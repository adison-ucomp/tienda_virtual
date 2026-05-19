package com.compensar.tienda.ui.model.common

import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.compensar.tienda.R

object ImagePreviewHelper {

    fun addPreviewToRow(
        activity: AppCompatActivity,
        row: LinearLayout,
        url: String?
    ) {
        if (url.isNullOrBlank()) return

        val imageView = ImageView(activity).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundResource(R.drawable.bg_button)
            layoutParams = LinearLayout.LayoutParams(
                dp(activity, 72),
                dp(activity, 72)
            ).apply {
                setMargins(0, 0, dp(activity, 14), 0)
            }
        }

        Glide.with(activity)
            .load(url)
            .centerCrop()
            .into(imageView)

        row.addView(imageView)
    }

    fun addPreviewToCard(
        activity: AppCompatActivity,
        cardView: CardView,
        url: String?
    ) {
        if (url.isNullOrBlank()) return

        val content = cardView.getChildAt(0) as? LinearLayout ?: return

        if (content.findViewWithTag<ImageView>("imagePreviewDelete") != null) return

        val imageView = ImageView(activity).apply {
            tag = "imagePreviewDelete"
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundResource(R.drawable.bg_button)
            layoutParams = LinearLayout.LayoutParams(
                dp(activity, 120),
                dp(activity, 120)
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setMargins(0, dp(activity, 18), 0, dp(activity, 18))
            }
        }

        Glide.with(activity)
            .load(url)
            .centerCrop()
            .into(imageView)

        val insertIndex = if (content.childCount > 1) 1 else content.childCount
        content.addView(imageView, insertIndex)
    }

    private fun dp(activity: AppCompatActivity, value: Int): Int {
        return (value * activity.resources.displayMetrics.density).toInt()
    }
}
