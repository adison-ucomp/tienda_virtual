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
import com.compensar.tienda.model.TradeModel
import com.compensar.tienda.ui.buyer.BuyerShoppingActivity
import com.compensar.tienda.ui.common.CartManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeEpaycoActivity : AppCompatActivity() {
    private var actionReturn: View? = null
    private lateinit var webEpayco: WebView
    private lateinit var resultContainer: View
    private lateinit var txtReference: TextView
    private lateinit var txtState: TextView
    private lateinit var txtShipmentState: TextView
    private lateinit var txtMessage: TextView
    private lateinit var txtTotal: TextView
    private lateinit var actionContinue: Button

    private val db = FirebaseFirestore.getInstance()
    private var userRegister: Long = 0
    private var reference: String = ""
    private var address: String = ""
    private var total: Double = 0.0
    private var savedResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_epayco)
        SessionNavigation.bindProfile(this)
        SessionNavigation.applyBuyerInferiorVisibility(this)

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
        txtShipmentState = findViewById(R.id.txtShipmentState)
        txtMessage = findViewById(R.id.txtMessage)
        txtTotal = findViewById(R.id.txtTotal)
        actionContinue = findViewById(R.id.actionContinue)
    }

    private fun initEvents() {
        actionReturn?.setOnClickListener { finish() }
        actionContinue.setOnClickListener {
            startActivity(Intent(this, BuyerShoppingActivity::class.java))
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadCheckout() {
        if (reference.isBlank() || total <= 0.0 || userRegister <= 0 || address.isBlank()) {
            showLocalResult(
                state = "ERROR",
                shipmentState = "-",
                message = "No fue posible cargar la información para iniciar el pago."
            )
            return
        }

        if (CartManager.getItems(this).isEmpty()) {
            showLocalResult(
                state = "ERROR",
                shipmentState = "-",
                message = "No hay productos pendientes para pagar."
            )
            return
        }

        if (EpaycoConfig.PUBLIC_KEY == "EPAYCO_PUBLIC_KEY_AQUI") {
            showLocalResult(
                state = "CONFIGURAR",
                shipmentState = "-",
                message = "Debes configurar la llave pública de ePayco en el archivo .env.local."
            )
            return
        }

        val amountMessage = EpaycoConfig.validateAmount(total)
        if (amountMessage != null) {
            showLocalResult(
                state = "VALOR_NO_PERMITIDO",
                shipmentState = "-",
                message = amountMessage
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

        if (isCustomResult) {
            handleResult(uri)
            return true
        }

        return false
    }

    private fun handleResult(uri: Uri?) {
        if (uri == null || savedResult) return

        val isCustomResult = uri.scheme == "emptio" && uri.host == "epayco"

        if (!isCustomResult) return

        savedResult = true
        webEpayco.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
        txtReference.text = reference.ifBlank { "-" }
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
        txtState.text = "PROCESANDO"
        txtShipmentState.text = "PROCESANDO"
        txtMessage.text = "Procesando respuesta de la pasarela..."

        val state = normalizeState(uri)
        val paymentMethod = uri.getQueryParameter("x_payment_method")
            ?: uri.getQueryParameter("x_franchise")
            ?: "PSE"
        val apiJson = buildGatewayJson(uri, state, paymentMethod)

        getPaymentId(paymentMethod) { idPayment ->
            getShopIdFromCart { idShop ->
                createTradeOrderAndPurchases(
                    apiJson = apiJson,
                    state = state,
                    idGateway = 1L,
                    idPayment = idPayment,
                    idShop = idShop
                )
            }
        }
    }

    private fun getPaymentId(paymentMethod: String, onComplete: (Long) -> Unit) {
        db.collection("payment")
            .get()
            .addOnSuccessListener { result ->
                val cleanMethod = paymentMethod.trim().uppercase()
                val payment = result.documents.firstOrNull { document ->
                    val name = document.getString("name").orEmpty().trim().uppercase()
                    name == cleanMethod || cleanMethod.contains(name) || name.contains(cleanMethod)
                }
                onComplete(payment?.getLong("register") ?: 3L)
            }
            .addOnFailureListener { onComplete(3L) }
    }

    private fun getShopIdFromCart(onComplete: (Long) -> Unit) {
        val firstProduct = CartManager.getItems(this).firstOrNull()
        if (firstProduct == null) {
            onComplete(0L)
            return
        }

        db.collection("product")
            .document(firstProduct.register.toString())
            .get()
            .addOnSuccessListener { document ->
                onComplete(document.getLong("idShop") ?: 0L)
            }
            .addOnFailureListener { onComplete(0L) }
    }

    private fun createTradeOrderAndPurchases(
        apiJson: String,
        state: String,
        idGateway: Long,
        idPayment: Long,
        idShop: Long
    ) {
        val items = CartManager.getItems(this)
        if (items.isEmpty()) {
            showLocalResult(
                state = "ERROR",
                shipmentState = "-",
                message = "No hay productos en el carrito para generar la orden."
            )
            return
        }

        val idShipment = if (state == "APROBADO") 1L else 4L
        val shipmentState = if (idShipment == 1L) "PENDIENTE" else "RECHAZADO"
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val hour = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        db.collection("trade")
            .orderBy("register", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { tradeResult ->
                val tradeRegister = (tradeResult.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                db.collection("order")
                    .orderBy("register", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { orderResult ->
                        val orderRegister = (orderResult.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                        db.collection("purchase")
                            .orderBy("register", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { purchaseResult ->
                                val firstPurchaseRegister = (purchaseResult.documents.firstOrNull()?.getLong("register") ?: 0L) + 1L
                                val finalJson = JSONObject(apiJson).apply {
                                    put("trade", tradeRegister)
                                    put("order", orderRegister)
                                    put("shipment", shipmentState)
                                }.toString()

                                db.runBatch { batch ->
                                    batch.set(
                                        db.collection("trade").document(tradeRegister.toString()),
                                        TradeModel(
                                            register = tradeRegister,
                                            api = finalJson,
                                            state = state,
                                            idGateway = idGateway,
                                            idOrder = orderRegister
                                        )
                                    )

                                    batch.set(
                                        db.collection("order").document(orderRegister.toString()),
                                        mapOf(
                                            "register" to orderRegister,
                                            "address" to address,
                                            "reference" to reference,
                                            "total" to total,
                                            "date" to date,
                                            "hour" to hour,
                                            "idTrade" to tradeRegister,
                                            "idShop" to idShop,
                                            "idPayment" to idPayment,
                                            "idShipment" to idShipment,
                                            "idUser" to userRegister
                                        )
                                    )

                                    items.forEachIndexed { index, item ->
                                        val purchaseRegister = firstPurchaseRegister + index
                                        batch.set(
                                            db.collection("purchase").document(purchaseRegister.toString()),
                                            mapOf(
                                                "register" to purchaseRegister,
                                                "amount" to item.quantity,
                                                "value" to item.price,
                                                "total" to item.price * item.quantity,
                                                "idProduct" to item.register,
                                                "idUser" to userRegister,
                                                "idOrder" to orderRegister
                                            )
                                        )

                                        if (state == "APROBADO") {
                                            val productRef = db.collection("product").document(item.register.toString())
                                            val reservationRef = db.collection("cart_reservation").document("${userRegister}_${item.register}")
                                            batch.update(
                                                productRef,
                                                mapOf(
                                                    "stock" to FieldValue.increment(-item.quantity.toLong()),
                                                    "reserved" to FieldValue.increment(-item.quantity.toLong())
                                                )
                                            )
                                            batch.delete(reservationRef)
                                        }
                                    }
                                }.addOnSuccessListener {
                                    if (state == "APROBADO") {
                                        CartManager.clear(this)
                                    }
                                    showResult(
                                        state = state,
                                        shipmentState = shipmentState,
                                        message = messageForState(state, shipmentState)
                                    )
                                }.addOnFailureListener { exception ->
                                    showLocalResult(
                                        state = "ERROR_ORDEN",
                                        shipmentState = "-",
                                        message = "No se pudo guardar la transacción y la orden: ${exception.message}"
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                showLocalResult("ERROR_ORDEN", "-", "No se pudo calcular la compra: ${exception.message}")
                            }
                    }
                    .addOnFailureListener { exception ->
                        showLocalResult("ERROR_ORDEN", "-", "No se pudo calcular la orden: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                showLocalResult("ERROR_TRADE", "-", "No se pudo guardar la transacción: ${exception.message}")
            }
    }

    private fun normalizeState(uri: Uri): String {
        uri.getQueryParameter("emptio_state")?.let { serverState ->
            val cleanServerState = serverState.uppercase().trim()
            if (cleanServerState == "APROBADO" || cleanServerState == "RECHAZADO") {
                return cleanServerState
            }
        }

        val code = uri.getQueryParameter("x_cod_response")
            ?: uri.getQueryParameter("x_cod_respuesta")
            ?: uri.getQueryParameter("x_cod_transaction_state")

        if (code == "1") return "APROBADO"
        if (code in listOf("2", "3", "4", "6", "7", "8", "9", "10", "11")) return "RECHAZADO"

        val raw = uri.getQueryParameter("x_response")
            ?: uri.getQueryParameter("x_respuesta")
            ?: uri.getQueryParameter("x_transaction_state")
            ?: uri.getQueryParameter("x_response_reason_text")
            ?: uri.getQueryParameter("status")
            ?: uri.getQueryParameter("estado")
            ?: uri.getQueryParameter("state")
            ?: "RECHAZADO"

        val clean = raw.uppercase().trim()
        return when {
            clean.contains("ACEPT") || clean.contains("APPROV") || clean.contains("APROB") || clean == "OK" -> "APROBADO"
            clean.contains("RECH") || clean.contains("DECLIN") || clean.contains("DENIED") -> "RECHAZADO"
            clean.contains("CANCEL") || clean.contains("ABANDON") -> "RECHAZADO"
            clean.contains("FAIL") || clean.contains("ERROR") || clean.contains("FONDOS") -> "RECHAZADO"
            clean.contains("PEND") -> "RECHAZADO"
            else -> clean.ifBlank { "RECHAZADO" }
        }
    }

    private fun messageForState(state: String, shipmentState: String): String {
        return if (state == "APROBADO") {
            "La transacción fue aprobada correctamente. La orden quedó en estado $shipmentState."
        } else {
            "La transacción quedó en estado $state. La orden quedó en estado $shipmentState."
        }
    }

    private fun showLocalResult(state: String, shipmentState: String, message: String) {
        savedResult = true
        webEpayco.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
        showResult(state, shipmentState, message)
    }

    private fun showResult(state: String, shipmentState: String, message: String) {
        txtReference.text = reference.ifBlank { "-" }
        txtState.text = state
        txtShipmentState.text = shipmentState
        txtMessage.text = message
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
    }

    private fun buildGatewayJson(uri: Uri, state: String, paymentMethod: String): String {
        val data = JSONObject()
        uri.queryParameterNames.sorted().forEach { key ->
            data.put(key, uri.getQueryParameter(key))
        }
        return JSONObject().apply {
            put("reference", reference)
            put("total", total)
            put("state", state)
            put("paymentMethod", paymentMethod)
            put("url", uri.toString())
            put("data", data)
        }.toString()
    }

    private fun buildCheckoutHtml(): String {
        val amount = String.format(Locale.US, "%.0f", total)
        val testMode = if (EpaycoConfig.TEST_MODE) "true" else "false"
        val responseUrl = EpaycoConfig.RESPONSE_URL
        val confirmationUrl = EpaycoConfig.CONFIRMATION_URL
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
                        confirmation: '$confirmationUrl'
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
