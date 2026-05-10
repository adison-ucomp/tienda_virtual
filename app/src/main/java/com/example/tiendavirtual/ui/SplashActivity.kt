package com.example.tiendavirtual.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendavirtual.R
import com.example.tiendavirtual.ui.platform.HomeProductActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeProductActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}