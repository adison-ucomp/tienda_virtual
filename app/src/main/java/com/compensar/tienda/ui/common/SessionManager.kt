package com.compensar.tienda.ui.common

import android.content.Context
import com.compensar.tienda.model.UserModel

/**
 * Objeto singleton [SessionManager].
 *
 * Responsable de la logica asociada al utilidad comun de interfaz y sesion.
 */
object SessionManager {

    private const val PREFERENCES_NAME = "session_preferences"
    private const val KEY_REGISTER = "user_register"
    private const val KEY_NAMES = "user_names"
    private const val KEY_SURNAMES = "user_surnames"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_ROLE = "user_role"

    /**
     * Guarda informacion en almacenamiento local o remoto.
     */
    fun save(context: Context, user: UserModel) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_REGISTER, user.register)
            .putString(KEY_NAMES, user.names ?: "")
            .putString(KEY_SURNAMES, user.srnms ?: "")
            .putString(KEY_EMAIL, user.email ?: "")
            .putLong(KEY_ROLE, user.idRole)
            .apply()
    }

    /**
     * Actualiza informacion existente segun el flujo actual.
     */
    fun updateProfile(
        context: Context,
        names: String,
        surnames: String,
        email: String
    ) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAMES, names)
            .putString(KEY_SURNAMES, surnames)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getRegister(context: Context): Long {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getLong(KEY_REGISTER, 0)
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getNames(context: Context): String {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_NAMES, "") ?: ""
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getSurnames(context: Context): String {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_SURNAMES, "") ?: ""
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getFullName(context: Context): String {
        val names = getNames(context)
        val surnames = getSurnames(context)
        val fullName = "$names $surnames".trim()

        return fullName.ifEmpty { "Sin Informacion" }
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getEmail(context: Context): String {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getString(KEY_EMAIL, "")?.ifEmpty { "Sin Informacion" } ?: "Sin Informacion"
    }

    /**
     * Obtiene informacion requerida por la pantalla o helper.
     */
    fun getRole(context: Context): Long {
        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return preferences.getLong(KEY_ROLE, 0)
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
}

