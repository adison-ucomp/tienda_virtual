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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
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
        val hasEpaycoReference = getEpaycoReference(uri).isNotBlank()
        val hasEpaycoResponse = uri.queryParameterNames.any { it.startsWith("x_") }

        if (isCustomResult || hasEpaycoReference || hasEpaycoResponse) {
            handleResult(uri)
            return true
        }

        return false
    }

    private fun handleResult(uri: Uri?) {
        if (uri == null || savedResult) return

        val isCustomResult = uri.scheme == "emptio" && uri.host == "epayco"
        val hasEpaycoReference = getEpaycoReference(uri).isNotBlank()
        val hasEpaycoResponse = uri.queryParameterNames.any { it.startsWith("x_") }

        if (!isCustomResult && !hasEpaycoReference && !hasEpaycoResponse) return

        savedResult = true
        webEpayco.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE
        txtReference.text = reference.ifBlank { "-" }
        txtTotal.text = "$ ${String.format("%,.0f", total)}"
        txtState.text = "CONSULTANDO"
        txtShipmentState.text = "CONSULTANDO"
        txtMessage.text = "Consultando el estado real de la transacción en ePayco..."

        val epaycoReference = getEpaycoReference(uri)
        if (epaycoReference.isNotBlank()) {
            requestEpaycoValidation(epaycoReference, uri)
            return
        }

        processValidatedPayment(
            validationJson = buildUriOnlyJson(uri),
            transactionData = buildUriTransactionData(uri),
            epaycoReference = getEpaycoReference(uri)
        )
    }

    private fun getEpaycoReference(uri: Uri): String {
        return uri.getQueryParameter("ref_payco")
            ?: uri.getQueryParameter("x_ref_payco")
            ?: uri.getQueryParameter("reference")
            ?: ""
    }

    private fun requestEpaycoValidation(epaycoReference: String, originalUri: Uri) {
        Thread {
            try {
                val connection = URL(EpaycoConfig.directValidationUrl(epaycoReference)).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 20000
                connection.setRequestProperty("Accept", "application/json")

                val stream = if (connection.responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                val response = BufferedReader(InputStreamReader(stream)).use { reader ->
                    reader.readText()
                }

                runOnUiThread {
                    if (connection.responseCode in 200..299) {
                        handleValidationResponse(response, epaycoReference, originalUri)
                    } else {
                        showLocalResult(
                            state = "ERROR",
                            shipmentState = "-",
                            message = "No se pudo consultar la transacción en ePayco. Código: ${connection.responseCode}."
                        )
                    }
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    showLocalResult(
                        state = "ERROR",
                        shipmentState = "-",
                        message = "No se pudo consultar la transacción en ePayco: ${exception.message}"
                    )
                }
            }
        }.start()
    }

    private fun handleValidationResponse(response: String, epaycoReference: String, originalUri: Uri) {
        try {
            val root = JSONObject(response)
            val wrapper = root.optJSONObject("data") ?: root
            val payload = wrapper.optJSONObject("payload") ?: wrapper
            val transactionData = payload.optJSONObject("data") ?: payload

            if (!root.optBoolean("success", true) && transactionData.length() == 0) {
                showLocalResult(
                    state = "ERROR",
                    shipmentState = "-",
                    message = root.optString("message", "No fue posible validar la transacción en ePayco.")
                )
                return
            }

            processValidatedPayment(
                validationJson = JSONObject().apply {
                    put("reference", reference)
                    put("epaycoReference", epaycoReference)
                    put("lookupUrl", EpaycoConfig.directValidationUrl(epaycoReference))
                    put("callbackUrl", originalUri.toString())
                    put("response", root)
                },
                transactionData = transactionData,
                epaycoReference = epaycoReference
            )
        } catch (exception: Exception) {
            showLocalResult(
                state = "ERROR",
                shipmentState = "-",
                message = "La respuesta de validación de ePayco no tiene formato válido: ${exception.message}"
            )
        }
    }

    private fun processValidatedPayment(
        validationJson: JSONObject,
        transactionData: JSONObject,
        epaycoReference: String
    ) {
        val transactionState = getTransactionStateText(transactionData)
        val internalState = normalizeTransactionState(transactionState, transactionData)
        val paymentMethod = getPaymentMethod(transactionData)
        val apiJson = buildGatewayJson(
            validationJson = validationJson,
            transactionData = transactionData,
            internalState = internalState,
            transactionState = transactionState,
            paymentMethod = paymentMethod,
            epaycoReference = epaycoReference
        )

        getPaymentId(paymentMethod) { idPayment ->
            getShopIdFromCart { idShop ->
                createTradeOrderAndPurchases(
                    apiJson = apiJson,
                    state = internalState,
                    transactionState = transactionState,
                    idGateway = 1L,
                    idPayment = idPayment,
                    idShop = idShop,
                    epaycoReference = epaycoReference
                )
            }
        }
    }

    private fun getTransactionStateText(data: JSONObject): String {
        return data.optString("x_transaction_state")
            .ifBlank { data.optString("x_response") }
            .ifBlank { data.optString("x_respuesta") }
            .ifBlank { data.optString("x_response_reason_text") }
            .ifBlank { "Pendiente" }
    }

    private fun getPaymentMethod(data: JSONObject): String {
        return data.optString("x_type_payment")
            .ifBlank { data.optString("x_payment_method") }
            .ifBlank { data.optString("x_franchise") }
            .ifBlank { "PSE" }
            .uppercase()
    }

    private fun normalizeTransactionState(transactionState: String, data: JSONObject): String {
        return when (transactionState.trim()) {
            "Pendiente" -> "Pendiente"
            "Aceptada" -> "Aceptada"
            "Rechazada" -> "Rechazada"
            "Fallida" -> "Fallida"
            else -> "Pendiente"
        }
    }

    private fun buildUriOnlyJson(uri: Uri): JSONObject {
        return JSONObject().apply {
            put("reference", reference)
            put("callbackUrl", uri.toString())
            put("data", buildUriTransactionData(uri))
        }
    }

    private fun buildUriTransactionData(uri: Uri): JSONObject {
        return JSONObject().apply {
            uri.queryParameterNames.sorted().forEach { key ->
                put(key, uri.getQueryParameter(key))
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
        transactionState: String,
        idGateway: Long,
        idPayment: Long,
        idShop: Long,
        epaycoReference: String
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
        val shipmentState = if (idShipment == 1L) "Pendiente" else "Rechazada"
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
                                            state = transactionState,
                                            reference = epaycoReference,
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
                                        state = transactionState,
                                        shipmentState = shipmentState,
                                        message = messageForState(transactionState, shipmentState, state == "APROBADO")
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

    private fun messageForState(transactionState: String, shipmentState: String, approved: Boolean): String {
        return if (approved) {
            "La transacción fue $transactionState correctamente. La orden quedó en estado $shipmentState."
        } else {
            "La transacción quedó en estado $transactionState. La orden quedó en estado $shipmentState."
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

    private fun buildGatewayJson(
        validationJson: JSONObject,
        transactionData: JSONObject,
        internalState: String,
        transactionState: String,
        paymentMethod: String,
        epaycoReference: String
    ): String {
        return JSONObject().apply {
            put("reference", reference)
            put("epaycoReference", epaycoReference)
            put("total", total)
            put("state", internalState)
            put("transactionState", transactionState)
            put("paymentMethod", paymentMethod)
            put("validation", validationJson)
            put("data", transactionData)
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
                        confirmation: '$confirmationUrl',
                        extra1: '$reference',
                        extra2: '$userRegister',
                        extra3: 'EMPTIO'
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
