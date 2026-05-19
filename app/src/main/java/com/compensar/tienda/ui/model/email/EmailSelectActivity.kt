package com.compensar.tienda.ui.model.email

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.domain.model.EmailModel
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.SSLSocketFactory

class EmailSelectActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var dataList: LinearLayout
    private lateinit var actionNew: LinearLayout
    private lateinit var actionTestConnection: Button

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("email")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_email_select)
        SessionNavigation.bindProfile(this)

        actionReturn = findViewById(R.id.actionReturn)
        dataList = findViewById(R.id.dataList)
        actionNew = findViewById(R.id.actionNew)
        actionTestConnection = findViewById(R.id.actionTestConnection)

        actionReturn.setOnClickListener { finish() }

        actionNew.setOnClickListener {
            val intent = Intent(this, EmailCreateActivity::class.java)
            startActivity(intent)
        }

        actionTestConnection.setOnClickListener {
            actionTestConnection()
        }

        loadDataBase()
    }

    override fun onResume() {
        super.onResume()
        loadDataBase()
    }

    private fun loadDataBase() {
        collection
            .get()
            .addOnSuccessListener { result ->
                dataList.removeAllViews()

                val items = result.documents.mapNotNull { document ->
                    document.toObject(EmailModel::class.java)
                }.sortedBy { it.register }

                items.forEach { data ->
                    dataList.addView(loadCard(data))
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun loadCard(data: EmailModel): CardView {
        val cardView = CardView(this).apply {
            radius = dp(18).toFloat()
            cardElevation = dp(6).toFloat()
            setCardBackgroundColor(getColor(R.color.white))

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dp(4), 0, dp(4), dp(16))
            }
        }

        val mainRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val txtRegister = TextView(this).apply {
            text = "Registro: ${data.register}"
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }
        textContainer.addView(txtRegister)

        val txtParam = TextView(this).apply {
            text = "Parámetro: ${data.param ?: ""}"
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }
        textContainer.addView(txtParam)

        val txtValue = TextView(this).apply {
            text = "Valor: ${getSafeValue(data)}"
            textSize = 14f
            setTextColor(getColor(R.color.black))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, 0)
        }
        textContainer.addView(txtValue)

        val buttonContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val btnEdit = ImageView(this).apply {
            setImageResource(R.drawable.ic_edit)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }

            setOnClickListener {
                val intent = Intent(this@EmailSelectActivity, EmailUpdateActivity::class.java)
                intent.putExtra("register", data.register)
                startActivity(intent)
            }
        }

        val btnQuit = ImageView(this).apply {
            setImageResource(R.drawable.ic_delete)
            layoutParams = LinearLayout.LayoutParams(dp(28), dp(28)).apply {
                setMargins(0, 0, 0, dp(16))
            }

            setOnClickListener {
                val intent = Intent(this@EmailSelectActivity, EmailDeleteActivity::class.java)
                intent.putExtra("register", data.register)
                startActivity(intent)
            }
        }

        buttonContainer.addView(btnEdit)
        buttonContainer.addView(btnQuit)

        mainRow.addView(textContainer)
        mainRow.addView(buttonContainer)

        cardView.addView(mainRow)

        return cardView
    }

    private fun actionTestConnection() {
        collection.get()
            .addOnSuccessListener { result ->
                val config = result.documents
                    .mapNotNull { document -> document.toObject(EmailModel::class.java) }
                    .associate { data ->
                        (data.param ?: "").trim().lowercase() to (data.value ?: "").trim()
                    }

                val host = config["host"].orEmpty()
                val port = config["port"]?.toIntOrNull()
                val security = config["security"].orEmpty().uppercase()
                val timeout = config["timeout"]?.toIntOrNull() ?: 10000

                if (host.isEmpty()) {
                    Toast.makeText(this, "Debes configurar el host SMTP", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                if (port == null || port <= 0) {
                    Toast.makeText(this, "Debes configurar un puerto SMTP válido", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                Toast.makeText(this, "Probando conexión SMTP...", Toast.LENGTH_SHORT).show()

                Thread {
                    try {
                        if (security.contains("SSL")) {
                            val socket = SSLSocketFactory.getDefault().createSocket(host, port)
                            socket.soTimeout = timeout
                            socket.close()
                        } else {
                            Socket().use { socket ->
                                socket.connect(InetSocketAddress(host, port), timeout)
                            }
                        }

                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Conexión SMTP exitosa",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (exception: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Error de conexión: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        exception.printStackTrace()
                    }
                }.start()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun getSafeValue(data: EmailModel): String {
        val param = data.param.orEmpty().trim().lowercase()
        val value = data.value.orEmpty()

        return if (param == "password" && value.isNotEmpty()) {
            "********"
        } else {
            value
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
