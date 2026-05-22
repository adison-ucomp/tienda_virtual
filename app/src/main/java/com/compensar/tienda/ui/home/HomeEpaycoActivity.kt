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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private var userRegister: Long = 0
    private var reference: String = ""
    private var address: String = ""
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
        userRegister = intent?.getLongExtra("userRegister", 0L) ?: 0L
        reference = intent?.getStringExtra("reference").orEmpty()
        address = intent?.getStringExtra("address").orEmpty()
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
        if (reference.isBlank() || total <= 0.0 || userRegister <= 0 || address.isBlank()) {
            showResult(
                state = "ERROR",
                message = "No fue posible cargar la información para iniciar el pago.",
                apiJson = buildResultJson("ERROR", "Datos de pago incompletos"),
                idOrder = 0L
            )
            return
        }

        if (CartManager.getItems(this).isEmpty()) {
            showResult(
                state = "ERROR",
                message = "No hay productos pendientes para pagar.",
                apiJson = buildResultJson("ERROR", "Carrito vacío"),
                idOrder = 0L
            )
            return
        }

        if (EpaycoConfig.PUBLIC_KEY == "EPAYCO_PUBLIC_KEY_AQUI") {
            showResult(
                state = "CONFIGURAR",
                message = "Debes configurar la llave pública de ePayco en el archivo .env.local.",
                apiJson = buildResultJson("CONFIGURAR", "Llave pública pendiente"),
                idOrder = 0L
            )
            return
        }

        val amountMessage = EpaycoConfig.validateAmount(total)
        if (amountMessage != null) {
            showResult(
                state = "VALOR_NO_PERMITIDO",
                message = amountMessage,
                apiJson = buildResultJson("VALOR_NO_PERMITIDO", amountMessage),
                idOrder = 0L
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
        val isCustomResult = uri.scheme == "emptio" && uri.host == "epayco"
        val isWebResult = uri.host?.contains("emptio", ignoreCase = true) == true &&
            uri.path?.contains("epayco", ignoreCase = true) == true

        if (isCustomResult || isWebResult) {
            handleResult(uri)
            return true
        }
        return false
    }

    private fun handleResult(uri: Uri?) {
        if (uri == null || savedResult) return

        val isCustomResult = uri.scheme == "emptio" && uri.host == "epayco"
        val isWebResult = uri.host?.contains("emptio", ignoreCase = true) == true &&
            uri.path?.contains("epayco", ignoreCase = true) == true

        if (!isCustomResult && !isWebResult) return

        val rawState = uri.getQueryParameter("status")
            ?: uri.getQueryParameter("x_response")
            ?: uri.getQueryParameter("x_response_reason_text")
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

        savedResult = true
        webEpayco.visibility = View.GONE

        if (state == "APROBADO") {
            createOrderAfterApproved(apiJson)
            return
        }

        showResult(
            state = state,
            message = messageForState(state),
            apiJson = apiJson,
            idOrder = 0L
        )
    }

    private fun createOrderAfterApproved(apiJson: String) {
        val items = CartManager.getItems(this)
        if (items.isEmpty()) {
            showResult(
                state = "ERROR",
                message = "El pago fue aprobado, pero no hay productos en el carrito para generar la orden.",
                apiJson = buildResultJson("ERROR", "Carrito vacío después del pago"),
                idOrder = 0L
            )
            return
        }

        txtReference.text = reference.ifBlank { "-" }
        txtState.text = "PROCESANDO"
        txtMessage.text = "Pago aprobado. Generando orden de compra..."
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
        resultContainer.visibility = View.VISIBLE

        db.collection("order")
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { orderResult ->
                orderRegister = (orderResult.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val orderData = mapOf(
                    "register" to orderRegister,
                    "address" to address,
                    "reference" to reference,
                    "total" to total,
                    "date" to date,
                    "hour" to hour,
                    "idShop" to 0L,
                    "idShipment" to 1L,
                    "idUser" to userRegister
                )

                db.collection("purchase")
                    .orderBy("register", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { purchaseResult ->
                        val firstPurchaseRegister = (purchaseResult.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                        db.runBatch { batch ->
                            batch.set(db.collection("order").document(orderRegister.toString()), orderData)

                            items.forEachIndexed { index, item ->
                                val purchaseRegister = firstPurchaseRegister + index
                                val productRef = db.collection("product").document(item.register.toString())
                                val reservationRef = db.collection("cart_reservation").document("${userRegister}_${item.register}")
                                batch.set(
                                    db.collection("purchase").document(purchaseRegister.toString()),
                                    mapOf(
                                        "register" to purchaseRegister,
                                        "amount" to item.quantity,
                                        "value" to item.price,
                                        "total" to item.price * item.quantity,
                                        "idProduct" to item.register,
                                        "idGangway" to 0L,
                                        "idUser" to userRegister,
                                        "idOrder" to orderRegister
                                    )
                                )
                                batch.update(
                                    productRef,
                                    mapOf(
                                        "stock" to FieldValue.increment(-item.quantity.toLong()),
                                        "reserved" to FieldValue.increment(-item.quantity.toLong())
                                    )
                                )
                                batch.delete(reservationRef)
                            }
                        }.addOnSuccessListener {
                            CartManager.clear(this)
                            val finalJson = JSONObject(apiJson).apply {
                                put("order", orderRegister)
                                put("generatedOrder", true)
                            }.toString()
                            showResult(
                                state = "APROBADO",
                                message = "El pago fue aprobado correctamente y la orden fue generada.",
                                apiJson = finalJson,
                                idOrder = orderRegister
                            )
                        }.addOnFailureListener { exception ->
                            showResult(
                                state = "ERROR_ORDEN",
                                message = "El pago fue aprobado, pero no se pudo generar la orden: ${exception.message}",
                                apiJson = buildResultJson("ERROR_ORDEN", exception.message ?: "Error generando orden"),
                                idOrder = 0L
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        showResult(
                            state = "ERROR_ORDEN",
                            message = "El pago fue aprobado, pero no se pudieron generar las compras: ${exception.message}",
                            apiJson = buildResultJson("ERROR_ORDEN", exception.message ?: "Error generando compras"),
                            idOrder = 0L
                        )
                    }
            }
            .addOnFailureListener { exception ->
                showResult(
                    state = "ERROR_ORDEN",
                    message = "El pago fue aprobado, pero no se pudo generar la orden: ${exception.message}",
                    apiJson = buildResultJson("ERROR_ORDEN", exception.message ?: "Error generando orden"),
                    idOrder = 0L
                )
            }
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
            "APROBADO" -> "El pago fue aprobado correctamente y la orden fue generada."
            "RECHAZADO" -> "El pago fue rechazado por la pasarela. No se generó la orden de compra."
            "CANCELADO" -> "El pago fue cancelado. No se generó la orden de compra."
            "FALLIDO" -> "El pago no pudo ser procesado. No se generó la orden de compra."
            "CONFIGURAR" -> "Configura la llave pública para iniciar el checkout."
            else -> "La transacción quedó en estado $state. No se generó la orden de compra."
        }
    }

    private fun showResult(
        state: String,
        message: String,
        apiJson: String,
        idOrder: Long
    ) {
        savedResult = true
        webEpayco.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
        txtReference.text = reference.ifBlank { "-" }
        txtState.text = state
        txtMessage.text = message
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
        saveEpayco(apiJson, state, idOrder)
    }

    private fun saveEpayco(apiJson: String, state: String, idOrder: Long) {
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
                        idOrder = idOrder
                    )
                )
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar la respuesta de ePayco", Toast.LENGTH_LONG).show()
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
        val amount = String.format(Locale.US, "%.0f", total)
        val testMode = if (EpaycoConfig.TEST_MODE) "true" else "false"
        val responseUrl = "https://www.emptio.com/epayco/result"
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
                        description: 'Compra $reference',
                        invoice: '$reference',
                        currency: 'COP',
                        amount: '$amount',
                        tax_base: '$amount',
                        tax: '0',
                        country: 'co',
                        lang: 'es',
                        external: 'false',
                        response: '$responseUrl',
                        confirmation: '$responseUrl'
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
