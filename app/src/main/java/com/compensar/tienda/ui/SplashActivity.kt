package com.compensar.tienda.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.data.remote.fire.DefaultFire
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
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 125)
    }
}