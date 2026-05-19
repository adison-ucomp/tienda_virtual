package com.compensar.tienda.ui.common

import android.os.Handler
import android.os.Looper
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

object SmtpEmailHelper {

    private val db = FirebaseFirestore.getInstance()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun sendPasswordRestoreEmail(
        toEmail: String,
        resetLink: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("email")
            .get()
            .addOnSuccessListener { result ->
                val config = result.documents.associate { document ->
                    val param = document.getString("param").orEmpty().trim().lowercase()
                    val value = document.getString("value").orEmpty().trim()
                    param to value
                }

                val host = config["host"].orEmpty()
                val port = config["port"].orEmpty()
                val username = config["username"].orEmpty()
                val password = config["password"].orEmpty()
                val security = config["security"].orEmpty().uppercase()
                val from = config["from"].orEmpty().ifEmpty { username }
                val auth = config["auth"].orEmpty().ifEmpty { "true" }
                val timeout = config["timeout"].orEmpty().ifEmpty { "10000" }

                if (host.isEmpty() || port.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    onFailure(Exception("Faltan datos SMTP: host, port, username o password"))
                    return@addOnSuccessListener
                }

                thread {
                    try {
                        val properties = Properties().apply {
                            put("mail.smtp.host", host)
                            put("mail.smtp.port", port)
                            put("mail.smtp.auth", auth)
                            put("mail.smtp.connectiontimeout", timeout)
                            put("mail.smtp.timeout", timeout)
                            put("mail.smtp.writetimeout", timeout)

                            if (security.contains("SSL")) {
                                put("mail.smtp.ssl.enable", "true")
                            }

                            if (security.contains("TLS")) {
                                put("mail.smtp.starttls.enable", "true")
                            }
                        }

                        val session = Session.getInstance(
                            properties,
                            object : Authenticator() {
                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                    return PasswordAuthentication(username, password)
                                }
                            }
                        )

                        val message = MimeMessage(session).apply {
                            setFrom(InternetAddress(from))
                            setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                            subject = "Recuperación de contraseña - EMPTIO"
                            setText(
                                """
                                Hola,

                                Recibimos una solicitud para restablecer tu contraseña.

                                Abre el siguiente enlace desde este dispositivo para crear una nueva contraseña:

                                $resetLink

                                Si no solicitaste este cambio, puedes ignorar este mensaje.

                                EMPTIO Colombia
                                """.trimIndent()
                            )
                        }

                        Transport.send(message)

                        mainHandler.post {
                            onSuccess()
                        }
                    } catch (exception: Exception) {
                        mainHandler.post {
                            onFailure(exception)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
