package com.compensar.tienda.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.firestore.DefaultFire
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.dashboard.DashboardAdminActivity
import com.compensar.tienda.ui.dashboard.DashboardSellerActivity
import com.compensar.tienda.ui.home.HomeProductActivity

class SplashActivity : AppCompatActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }*/

    private val defaultDataFire = DefaultFire()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        createDefaultData()
        goToHome()
    }

    private fun createDefaultData() {
        defaultDataFire.createDefault(
            onSuccess = {
                println("Datos por defecto creados correctamente")
            },
            onFailure = { exception ->
                exception.printStackTrace()
            }
        )
    }

    private fun goToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = when {
                SessionManager.getRegister(this) <= 0L -> {
                    Intent(this, HomeProductActivity::class.java)
                }
                SessionManager.getRole(this) == 1L -> {
                    Intent(this, DashboardAdminActivity::class.java)
                }
                SessionManager.getRole(this) == 2L -> {
                    Intent(this, DashboardSellerActivity::class.java)
                }
                SessionManager.getRole(this) == 3L -> {
                    Intent(this, HomeProductActivity::class.java)
                }
                else -> {
                    Intent(this, HomeProductActivity::class.java)
                }
            }
            startActivity(intent)
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 125)
    }
}
