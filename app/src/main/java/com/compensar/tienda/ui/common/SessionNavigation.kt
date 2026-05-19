package com.compensar.tienda.ui.common

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.home.HomeProductActivity
import com.compensar.tienda.ui.home.HomeLoginActivity
import com.compensar.tienda.ui.profile.ProfileAdminActivity
import com.compensar.tienda.ui.profile.ProfileBuyerActivity
import com.compensar.tienda.ui.profile.ProfileSellerActivity
import com.compensar.tienda.ui.profile.ProfileShareActivity

object SessionNavigation {

    fun bindProfile(activity: AppCompatActivity) {
        bindProfileByRole(activity)
        bindSharedMenu(activity)
    }

    private fun bindProfileByRole(activity: AppCompatActivity) {
        val actionAccount = activity.findViewById<LinearLayout?>(R.id.actionAccount)
        val navProfile = activity.findViewById<LinearLayout?>(R.id.navProfile)

        val listener = {
            val intent = when (SessionManager.getRole(activity)) {
                1L -> Intent(activity, ProfileAdminActivity::class.java)
                2L -> Intent(activity, ProfileSellerActivity::class.java)
                3L -> Intent(activity, ProfileBuyerActivity::class.java)
                else -> Intent(activity, HomeLoginActivity::class.java)
            }

            activity.startActivity(intent)
        }

        actionAccount?.setOnClickListener { listener() }
        navProfile?.setOnClickListener { listener() }
    }

    private fun bindSharedMenu(activity: AppCompatActivity) {
        val adminMenu = activity.findViewById<TextView?>(R.id.btnMenu)
        val sellerMenu = activity.findViewById<TextView?>(R.id.actionMenu)

        val listener = {
            val intent = Intent(activity, ProfileShareActivity::class.java)
            activity.startActivity(intent)
        }

        adminMenu?.setOnClickListener { listener() }
        sellerMenu?.setOnClickListener { listener() }
    }

    fun showProfileShareDrawer(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profile_share)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val actionReturn = dialog.findViewById<TextView?>(R.id.actionReturn)
        val textName = dialog.findViewById<TextView>(R.id.textName)
        val textEmail = dialog.findViewById<TextView>(R.id.textEmail)
        val btnSettings = dialog.findViewById<LinearLayout>(R.id.btnSettings)
        val btnSupport = dialog.findViewById<LinearLayout>(R.id.btnSupport)
        val btnLogout = dialog.findViewById<LinearLayout>(R.id.btnLogout)

        textName.text = SessionManager.getFullName(activity)
        textEmail.text = SessionManager.getEmail(activity)

        actionReturn?.setOnClickListener {
            dialog.dismiss()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(activity, "Configuración", Toast.LENGTH_SHORT).show()
        }

        btnSupport.setOnClickListener {
            Toast.makeText(activity, "Soporte", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            dialog.dismiss()
            SessionManager.clear(activity)

            val intent = Intent(activity, HomeProductActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply {
                width = (activity.resources.displayMetrics.widthPixels * 0.82).toInt()
                height = WindowManager.LayoutParams.MATCH_PARENT
                gravity = Gravity.START or Gravity.TOP
                dimAmount = 0.45f
            }
        }
    }
}
