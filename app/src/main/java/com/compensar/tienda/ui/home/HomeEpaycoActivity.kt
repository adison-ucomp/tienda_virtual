package com.compensar.tienda.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import com.compensar.tienda.model.EpaycoModel
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.CartManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONObject

class HomeEpaycoActivity : AppCompatActivity() {
    private lateinit var actionReturn: TextView
    private lateinit var webEpayco: WebView
    private lateinit var resultContainer: View
    private lateinit var txtReference: TextView
    private lateinit var txtState: TextView
    private lateinit var txtMessage: TextView
    private lateinit var txtTotal: TextView
    private lateinit var actionContinue: Button

    private val db = FirebaseFirestore.getInstance()
    private var orderRegister: Long = 0
    private var reference: String = ""
    private var total: Double = 0.0
    private var savedResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_epayco)
        SessionNavigation.bindProfile(this)

        readExtras(intent)
        initViews()
        initEvents()
        handleResult(intent?.data)

        if (!savedResult) {
            loadCheckout()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleResult(intent?.data)
    }

    private fun readExtras(intent: Intent?) {
        orderRegister = intent?.getLongExtra("orderRegister", 0L) ?: 0L
        reference = intent?.getStringExtra("reference").orEmpty()
        total = intent?.getDoubleExtra("total", 0.0) ?: 0.0
    }

    private fun initViews() {
        actionReturn = findViewById(R.id.actionReturn)
        webEpayco = findViewById(R.id.webEpayco)
        resultContainer = findViewById(R.id.resultContainer)
        txtReference = findViewById(R.id.txtReference)
        txtState = findViewById(R.id.txtState)
        txtMessage = findViewById(R.id.txtMessage)
        txtTotal = findViewById(R.id.txtTotal)
        actionContinue = findViewById(R.id.actionContinue)
    }

    private fun initEvents() {
        actionReturn.setOnClickListener { finish() }
        actionContinue.setOnClickListener {
            startActivity(Intent(this, BuyerShoppingActivity::class.java))
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadCheckout() {
        if (orderRegister <= 0 || reference.isBlank() || total <= 0.0) {
            showResult(
                state = "ERROR",
                message = "No fue posible cargar la información de la orden.",
                apiJson = buildResultJson("ERROR", "Datos de orden incompletos")
            )
            return
        }

        if (EpaycoConfig.PUBLIC_KEY == "EPAYCO_PUBLIC_KEY_AQUI") {
            showResult(
                state = "CONFIGURAR",
                message = "Debes configurar la llave pública de ePayco en EpaycoConfig.kt.",
                apiJson = buildResultJson("CONFIGURAR", "Llave pública pendiente")
            )
            return
        }

        resultContainer.visibility = View.GONE
        webEpayco.visibility = View.VISIBLE
        webEpayco.settings.javaScriptEnabled = true
        webEpayco.settings.domStorageEnabled = true
        webEpayco.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                return processUrl(uri)
            }

            @Suppress("DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val uri = url?.let { Uri.parse(it) } ?: return false
                return processUrl(uri)
            }
        }

        webEpayco.loadDataWithBaseURL(
            "https://checkout.epayco.co/",
            buildCheckoutHtml(),
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun processUrl(uri: Uri): Boolean {
        if (uri.scheme == "emptio" && uri.host == "epayco") {
            handleResult(uri)
            return true
        }
        return false
    }

    private fun handleResult(uri: Uri?) {
        if (uri == null || uri.scheme != "emptio" || uri.host != "epayco") {
            return
        }

        val rawState = uri.getQueryParameter("status")
            ?: uri.getQueryParameter("x_response")
            ?: uri.getQueryParameter("estado")
            ?: uri.getQueryParameter("state")
            ?: "PENDIENTE"

        val state = normalizeState(rawState)
        val apiJson = JSONObject().apply {
            put("reference", reference)
            put("order", orderRegister)
            put("total", total)
            put("state", state)
            put("uri", uri.toString())
        }.toString()

        showResult(
            state = state,
            message = messageForState(state),
            apiJson = apiJson
        )
    }

    private fun normalizeState(value: String): String {
        val clean = value.uppercase().trim()
        return when {
            clean.contains("ACEPT") || clean.contains("APPROV") || clean.contains("APROB") || clean == "OK" -> "APROBADO"
            clean.contains("RECH") || clean.contains("DECLIN") || clean.contains("DENIED") -> "RECHAZADO"
            clean.contains("CANCEL") -> "CANCELADO"
            clean.contains("FAIL") || clean.contains("ERROR") -> "FALLIDO"
            else -> clean.ifBlank { "PENDIENTE" }
        }
    }

    private fun messageForState(state: String): String {
        return when (state) {
            "APROBADO" -> "El pago fue aprobado correctamente."
            "RECHAZADO" -> "El pago fue rechazado por la pasarela."
            "CANCELADO" -> "El pago fue cancelado."
            "FALLIDO" -> "El pago no pudo ser procesado."
            "CONFIGURAR" -> "Configura la llave pública para iniciar el checkout."
            else -> "La transacción quedó en estado $state."
        }
    }

    private fun showResult(
        state: String,
        message: String,
        apiJson: String
    ) {
        savedResult = true
        webEpayco.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
        txtReference.text = reference.ifBlank { "-" }
        txtState.text = state
        txtMessage.text = message
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
        saveEpayco(apiJson, state)
    }

    private fun saveEpayco(apiJson: String, state: String) {
        if (orderRegister <= 0) return

        db.collection("epayco")
            .whereEqualTo("idOrder", orderRegister)
            .limit(1)
            .get()
            .addOnSuccessListener { existing ->
                val document = existing.documents.firstOrNull()
                if (document != null) {
                    val register = document.getLong("register") ?: return@addOnSuccessListener
                    db.collection("epayco").document(register.toString()).set(
                        EpaycoModel(
                            register = register,
                            api = apiJson,
                            state = state,
                            idOrder = orderRegister
                        )
                    )
                    return@addOnSuccessListener
                }

                db.collection("epayco")
                    .orderBy("register", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { result ->
                        val register = (result.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                        db.collection("epayco").document(register.toString()).set(
                            EpaycoModel(
                                register = register,
                                api = apiJson,
                                state = state,
                                idOrder = orderRegister
                            )
                        )
                    }
            }
    }

    private fun buildResultJson(state: String, message: String): String {
        return JSONObject().apply {
            put("reference", reference)
            put("order", orderRegister)
            put("total", total)
            put("state", state)
            put("message", message)
        }.toString()
    }

    private fun buildCheckoutHtml(): String {
        val amount = String.format(java.util.Locale.US, "%.0f", total)
        val testMode = if (EpaycoConfig.TEST_MODE) "true" else "false"
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <script type="text/javascript" src="${EpaycoConfig.CHECKOUT_URL}"></script>
                <style>
                    body { font-family: sans-serif; padding: 20px; text-align: center; }
                    button { background: #000; color: #fff; border: 0; border-radius: 14px; padding: 16px 24px; font-weight: bold; }
                </style>
            </head>
            <body>
                <h2>EMPTIO</h2>
                <p>Serás redirigido al checkout de ePayco.</p>
                <button onclick="openCheckout()">Pagar con ePayco</button>
                <script>
                    var handler = ePayco.checkout.configure({
                        key: '${EpaycoConfig.PUBLIC_KEY}',
                        test: $testMode
                    });
                    var data = {
                        name: 'Compra EMPTIO',
                        description: 'Orden $reference',
                        invoice: '$reference',
                        currency: 'cop',
                        amount: '$amount',
                        tax_base: '0',
                        tax: '0',
                        country: 'co',
                        lang: 'es',
                        external: 'false',
                        response: 'emptio://epayco/result?status=APROBADO',
                        confirmation: 'emptio://epayco/result?status=APROBADO'
                    };
                    function openCheckout() {
                        handler.open(data);
                    }
                    setTimeout(openCheckout, 700);
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}
