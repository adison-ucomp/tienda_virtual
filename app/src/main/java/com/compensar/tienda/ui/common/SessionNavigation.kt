package com.compensar.tienda.ui.common

import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
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
                else -> Intent(activity, ProfileBuyerActivity::class.java)
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
}
