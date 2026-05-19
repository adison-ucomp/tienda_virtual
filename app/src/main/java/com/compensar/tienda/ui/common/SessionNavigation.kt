package com.compensar.tienda.ui.common

import android.content.Intent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.ui.profile.ProfileSellerActivity

object SessionNavigation {

    fun bindProfile(activity: AppCompatActivity) {
        val actionAccount = activity.findViewById<LinearLayout>(R.id.actionAccount)
        val navProfile = activity.findViewById<LinearLayout>(R.id.navProfile)

        actionAccount?.setOnClickListener {
            val intent = Intent(activity, ProfileSellerActivity::class.java)
            activity.startActivity(intent)
        }

        navProfile?.setOnClickListener {
            val intent = Intent(activity, ProfileSellerActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
