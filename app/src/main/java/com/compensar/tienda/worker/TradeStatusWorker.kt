package com.compensar.tienda.worker

import android.content.Context
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
import java.util.concurrent.TimeUnit

class TradeStatusWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val db = FirebaseFirestore.getInstance()

    override fun doWork(): Result {
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

                    if (documents.isEmpty()) {
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
                .addOnFailureListener {
                    latch.countDown()
                }

            latch.await(90, TimeUnit.SECONDS)
            schedule(applicationContext)
            return Result.success()
        } catch (_: Exception) {
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
                val connection = URL(EpaycoConfig.directValidationUrl(reference)).openConnection() as HttpURLConnection
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
                val root = JSONObject(response)
                val data = root.optJSONObject("data") ?: JSONObject()
                val transactionState = data.optString("x_transaction_state")
                    .ifBlank { data.optString("x_response") }
                    .ifBlank { data.optString("x_respuesta") }
                    .ifBlank { "Pendiente" }

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
                            batch.commit().addOnCompleteListener { onComplete() }
                        }
                        .addOnFailureListener { onComplete() }
                } else {
                    onComplete()
                }
            } catch (_: Exception) {
                onComplete()
            }
        }.start()
    }

    companion object {
        private const val WORK_NAME = "trade_status_worker"

        fun schedule(context: Context) {
            val request = OneTimeWorkRequestBuilder<TradeStatusWorker>()
                .setInitialDelay(EpaycoConfig.CRON_MINUTES, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }
}
