package com.compensar.tienda.worker

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.compensar.tienda.ui.home.EpaycoConfig
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class TradeStatusWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val db = FirebaseFirestore.getInstance()

    private val tag = "TRADE_STATUS_WORKER"

    private fun nowText(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    private fun nextExecutionText(): String {
        val nextTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(EpaycoConfig.CRON_MINUTES)
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(nextTime))
    }

    override fun doWork(): Result {
        val executedAt = nowText()
        Log.d(tag, "Iniciando validación programada de transacciones pendientes ePayco. Ejecutado en: $executedAt. Próxima ejecución estimada: ${nextExecutionText()}")
        try {
            val latch = java.util.concurrent.CountDownLatch(1)

            db.collection("trade")
                .whereEqualTo("idGateway", 1L)
                .get()
                .addOnSuccessListener { result ->
                    val documents = result.documents.filter {
                        it.getString("state").orEmpty().equals("Pendiente", ignoreCase = true)
                            && !it.getString("reference").isNullOrBlank()
                    }

                    Log.d(tag, "Transacciones pendientes encontradas: ${documents.size}. Hora consulta Firestore: ${nowText()}")

                    if (documents.isEmpty()) {
                        Log.d(tag, "No hay transacciones pendientes para validar. Próxima ejecución estimada: ${nextExecutionText()}")
                        latch.countDown()
                        return@addOnSuccessListener
                    }

                    val innerLatch = java.util.concurrent.CountDownLatch(documents.size)
                    documents.forEach { document ->
                        val reference = document.getString("reference").orEmpty()
                        validateAndUpdate(document.id, reference) {
                            innerLatch.countDown()
                        }
                    }
                    innerLatch.await(60, TimeUnit.SECONDS)
                    latch.countDown()
                }
                .addOnFailureListener { exception ->
                    Log.e(tag, "Error consultando transacciones pendientes", exception)
                    latch.countDown()
                }

            latch.await(90, TimeUnit.SECONDS)
            Log.d(tag, "Finalizó validación programada de transacciones pendientes ePayco. Ejecutado en: $executedAt. Finalizó en: ${nowText()}. Próxima ejecución estimada: ${nextExecutionText()}")
            schedule(applicationContext)
            return Result.success()
        } catch (exception: Exception) {
            Log.e(tag, "Error general en validación programada. Ejecutado en: $executedAt. Error en: ${nowText()}. Próxima ejecución estimada: ${nextExecutionText()}", exception)
            schedule(applicationContext)
            return Result.retry()
        }
    }

    private fun validateAndUpdate(
        tradeId: String,
        reference: String,
        onComplete: () -> Unit
    ) {
        Thread {
            try {
                val requestTime = nowText()
                val url = EpaycoConfig.directValidationUrl(reference)
                Log.d(tag, "Ejecutando petición a ePayco. Fecha y hora: $requestTime. tradeId=$tradeId reference=$reference url=$url")
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("Content-Type", "application/json")

                val stream = if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                val response = BufferedReader(InputStreamReader(stream)).use { it.readText() }
                Log.d(tag, "Respuesta ePayco recibida. Fecha y hora: ${nowText()}. tradeId=$tradeId reference=$reference code=${connection.responseCode}: $response")
                val root = JSONObject(response)
                val data = root.optJSONObject("data") ?: JSONObject()
                val transactionState = data.optString("x_transaction_state")
                    .ifBlank { data.optString("x_response") }
                    .ifBlank { data.optString("x_respuesta") }
                    .ifBlank { "Pendiente" }

                Log.d(tag, "Estado retornado por ePayco. Fecha y hora: ${nowText()}. reference=$reference state=$transactionState")

                if (!transactionState.equals("Pendiente", ignoreCase = true)) {
                    val shipmentId = if (transactionState.equals("Aceptada", ignoreCase = true)) 1L else 4L
                    db.collection("trade").document(tradeId).get()
                        .addOnSuccessListener { tradeDocument ->
                            val orderId = tradeDocument.getLong("idOrder") ?: 0L
                            val currentApi = tradeDocument.getString("api").orEmpty()
                            val apiJson = JSONObject().apply {
                                put("previous", currentApi)
                                put("validation", root)
                            }.toString()

                            val batch = db.batch()
                            batch.update(
                                db.collection("trade").document(tradeId),
                                mapOf(
                                    "api" to apiJson,
                                    "state" to transactionState
                                )
                            )
                            if (orderId > 0L) {
                                batch.update(
                                    db.collection("order").document(orderId.toString()),
                                    "idShipment",
                                    shipmentId
                                )
                            }
                            batch.commit().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(
                                        tag,
                                        "Transacción actualizada. Fecha y hora: ${nowText()}. tradeId=$tradeId state=$transactionState shipmentId=$shipmentId orderId=$orderId. Próxima ejecución estimada: ${nextExecutionText()}"
                                    )
                                } else {
                                    Log.e(tag, "Error actualizando transacción tradeId=$tradeId", task.exception)
                                }
                                onComplete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(tag, "Error consultando documento tradeId=$tradeId", exception)
                            onComplete()
                        }
                } else {
                    Log.d(tag, "La transacción sigue pendiente. Fecha y hora: ${nowText()}. tradeId=$tradeId reference=$reference. Próxima ejecución estimada: ${nextExecutionText()}")
                    onComplete()
                }
            } catch (exception: Exception) {
                Log.e(tag, "Error validando transacción. Fecha y hora: ${nowText()}. tradeId=$tradeId reference=$reference. Próxima ejecución estimada: ${nextExecutionText()}", exception)
                onComplete()
            }
        }.start()
    }

    companion object {
        private const val WORK_NAME = "trade_status_worker"

        fun schedule(context: Context) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val scheduledAt = System.currentTimeMillis()
            val nextTime = scheduledAt + TimeUnit.MINUTES.toMillis(EpaycoConfig.CRON_MINUTES)
            Log.d(
                WORK_NAME,
                "Programando próxima validación ePayco. Fecha y hora actual: ${formatter.format(Date(scheduledAt))}. " +
                    "Próxima ejecución: ${formatter.format(Date(nextTime))}. Intervalo: ${EpaycoConfig.CRON_MINUTES} minuto(s)"
            )
            val request = OneTimeWorkRequestBuilder<TradeStatusWorker>()
                .setInitialDelay(EpaycoConfig.CRON_MINUTES, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
