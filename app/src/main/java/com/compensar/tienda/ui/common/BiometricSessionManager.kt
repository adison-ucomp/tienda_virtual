package com.compensar.tienda.ui.common

import android.content.Context
import com.compensar.tienda.model.UserModel

/**
 * Objeto singleton [BiometricSessionManager].
 *
 * Responsable de la logica asociada al utilidad comun de interfaz y sesion.
 */
object BiometricSessionManager {

    private const val PREFERENCES_NAME = "biometric_preferences"
    private const val KEY_ENABLED = "biometric_enabled"
    private const val KEY_REGISTER = "biometric_user_register"
    private const val KEY_EMAIL = "biometric_user_email"
    private const val KEY_ROLE = "biometric_user_role"

    /**
     * Guarda informacion en almacenamiento local o remoto.
     */
    fun save(context: Context, user: UserModel) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, true)
            .putLong(KEY_REGISTER, user.register)
            .putString(KEY_EMAIL, user.email.orEmpty())
            .putLong(KEY_ROLE, user.idRole)
            .apply()
    }

    /**
     * Limpia estado temporal o datos persistidos.
     */
    fun clear(context: Context) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    /**
     * Ejecuta una parte del flujo funcional de esta clase.
     */
    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getRegister(context: Context): Long {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_REGISTER, 0L)
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getEmail(context: Context): String {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "").orEmpty()
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getRole(context: Context): Long {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_ROLE, 0L)
    }
}

