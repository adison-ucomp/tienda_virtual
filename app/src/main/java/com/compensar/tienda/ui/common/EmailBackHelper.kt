package com.compensar.tienda.ui.common

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.compensar.tienda.BuildConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Objeto singleton [EmailBackHelper].
 *
 * Responsable de la logica asociada al utilidad comun de interfaz y sesion.
 */
object EmailBackHelper {

    private const val TAG = "PASSWORD_BACKEND"

    private val ENDPOINT: String
        get() = BuildConfig.API_BASE_URL.trim().trimEnd('/') + "/api/password/send-code"

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Envia informacion a un servicio externo o componente interno.
     */
    fun sendCode(
        email: String,
        code: String,
        name: String = "Usuario",
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        thread {
            var connection: HttpURLConnection? = null

            try {
                val payload = JSONObject().apply {
                    put("email", email)
                    put("code", code)
                    put("name", name)
                }

                Log.d(TAG, "endpoint: $ENDPOINT")
                Log.d(TAG, "email: $email")
                Log.d(TAG, "code length: ${code.length}")

                connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 20000
                    readTimeout = 20000
                    doInput = true
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("Accept", "application/json")
                }

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val statusCode = connection.responseCode
                val stream = if (statusCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                val response = stream.bufferedReader(Charsets.UTF_8).use(BufferedReader::readText)

                Log.d(TAG, "statusCode: $statusCode")
                Log.d(TAG, "response: $response")

                val json = JSONObject(response)
                val success = json.optBoolean("success", false)
                val message = json.optString("message", "Respuesta no válida del backend")

                if (statusCode in 200..299 && success) {
                    mainHandler.post { onSuccess() }
                } else {
                    mainHandler.post { onFailure(Exception(message)) }
                }
            } catch (exception: Exception) {
                Log.e(TAG, "No fue posible enviar el código al backend", exception)
                mainHandler.post { onFailure(exception) }
            } finally {
                connection?.disconnect()
            }
        }
    }
}

