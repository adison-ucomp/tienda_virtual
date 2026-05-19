package com.compensar.tienda.ui.common

/**
 * El envío SMTP directo desde Android quedó reemplazado por el backend PHP.
 * Se conserva este helper solamente como compatibilidad temporal si alguna clase antigua lo invoca.
 */
object SmtpEmailHelper {
    fun sendPasswordRestoreEmail(
        toEmail: String,
        resetLink: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        onFailure(
            UnsupportedOperationException(
                "SMTP directo desde Android fue deshabilitado. Usa EmailBackHelper.sendCode()."
            )
        )
    }
}
