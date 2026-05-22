package com.compensar.tienda.ui.common

import android.content.Context
import com.compensar.tienda.model.UserModel

object BiometricSessionManager {

    private const val PREFERENCES_NAME = "biometric_preferences"
    private const val KEY_ENABLED = "biometric_enabled"
    private const val KEY_REGISTER = "biometric_user_register"
    private const val KEY_EMAIL = "biometric_user_email"
    private const val KEY_ROLE = "biometric_user_role"

    fun save(context: Context, user: UserModel) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, true)
            .putLong(KEY_REGISTER, user.register)
            .putString(KEY_EMAIL, user.email.orEmpty())
            .putLong(KEY_ROLE, user.idRole)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun getRegister(context: Context): Long {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_REGISTER, 0L)
    }

    fun getEmail(context: Context): String {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "").orEmpty()
    }

    fun getRole(context: Context): Long {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_ROLE, 0L)
    }
}
