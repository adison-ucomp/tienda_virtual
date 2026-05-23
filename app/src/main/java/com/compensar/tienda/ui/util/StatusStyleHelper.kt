package com.compensar.tienda.ui.util

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.widget.TextView

/**
 * Objeto singleton [StatusStyleHelper].
 *
 * Responsable de la logica asociada al helper visual reutilizable.
 */
object StatusStyleHelper {
    fun applyShipment(textView: TextView, status: String) {
        apply(textView, status, colorForShipment(status))
    }

    fun applyTrade(textView: TextView, status: String) {
        apply(textView, status, colorForTrade(status))
    }

    fun colorForShipment(status: String): Int {
        val clean = status.uppercase().trim()
        return when {
            clean.contains("PENDIENT") -> Color.parseColor("#C79000")
            clean.contains("ENVI") -> Color.parseColor("#1E66F5")
            clean.contains("ENTREG") -> Color.parseColor("#1B8A3A")
            clean.contains("RECH") -> Color.parseColor("#D52D09")
            else -> Color.parseColor("#666666")
        }
    }

    fun colorForTrade(status: String): Int {
        val clean = status.uppercase().trim()
        return when {
            clean.contains("PENDIENT") -> Color.parseColor("#C79000")
            clean.contains("ACEPT") -> Color.parseColor("#1B8A3A")
            clean.contains("FALL") -> Color.parseColor("#D52D09")
            clean.contains("RECH") -> Color.parseColor("#D52D09")
            else -> Color.parseColor("#666666")
        }
    }

    private fun apply(textView: TextView, status: String, color: Int) {
        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 32f
            setColor(Color.TRANSPARENT)
            setStroke(1, Color.parseColor("#D8DDE8"))
        }

        textView.text = status
        textView.setTextColor(color)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.background = background
    }
}

