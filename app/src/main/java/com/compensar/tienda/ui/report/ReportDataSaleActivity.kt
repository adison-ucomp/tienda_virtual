package com.compensar.tienda.ui.report

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.model.SellerModel
import com.compensar.tienda.model.ShopModel
import com.compensar.tienda.model.UserModel
import com.compensar.tienda.ui.common.SessionManager
import com.compensar.tienda.ui.common.SessionNavigation
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase [ReportDataSaleActivity].
 *
 * Responsable de la logica asociada al pantalla de reportes.
 */
class ReportDataSaleActivity : AppCompatActivity() {

    private lateinit var containerSeller: LinearLayout
    private lateinit var spinnerSeller: Spinner
    private lateinit var spinnerShop: Spinner
    private lateinit var inputDateFrom: EditText
    private lateinit var inputDateTo: EditText
    private lateinit var actionSearch: TextView
    private lateinit var actionClean: TextView
    private lateinit var cardResult: CardView
    private lateinit var dataResult: LinearLayout
    private lateinit var textTotalProducts: TextView
    private lateinit var textTotalMoney: TextView
    private lateinit var cardPdf: CardView
    private lateinit var cardExcel: CardView

    private val db = FirebaseFirestore.getInstance()
    private val moneyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    private var sales: List<SaleData> = emptyList()
    private var reportRows: List<SaleReportRow> = emptyList()
    private var sellerOptions: List<FilterOption> = emptyList()
    private var shopOptions: List<FilterOption> = emptyList()

    private var isSellerSession: Boolean = false
    private var selectedSellerRegister: Long = 0L
    private var selectedShopRegister: Long = 0L
    private var totalProducts: Int = 0
    private var totalMoney: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_data_sale)
        SessionNavigation.bindProfile(this)

        initViews()
        initEvents()
        loadReportData()
    }

    private fun initViews() {
        containerSeller = findViewById(R.id.containerSeller)
        spinnerSeller = findViewById(R.id.spinnerSeller)
        spinnerShop = findViewById(R.id.spinnerShop)
        inputDateFrom = findViewById(R.id.inputDateFrom)
        inputDateTo = findViewById(R.id.inputDateTo)
        actionSearch = findViewById(R.id.actionSearch)
        actionClean = findViewById(R.id.actionClean)
        cardResult = findViewById(R.id.cardResult)
        dataResult = findViewById(R.id.dataResult)
        textTotalProducts = findViewById(R.id.textTotalProducts)
        textTotalMoney = findViewById(R.id.textTotalMoney)
        cardPdf = findViewById(R.id.cardPdf)
        cardExcel = findViewById(R.id.cardExcel)

        findViewById<TextView?>(R.id.actionReturn)?.setOnClickListener {
            finish()
        }

        isSellerSession = SessionManager.getRole(this) == 2L
        containerSeller.visibility = if (isSellerSession) {
            View.GONE
        } else {
            View.VISIBLE
        }

        configureInferiorNavbar()
        setExportEnabled(false)
    }

    private fun configureInferiorNavbar() {
        val adminInferior = findViewById<View?>(R.id.includeAdminInferior)
        val sellerInferior = findViewById<View?>(R.id.includeSellerInferior)

        if (isSellerSession) {
            adminInferior?.visibility = View.GONE
            sellerInferior?.visibility = View.VISIBLE
        } else {
            adminInferior?.visibility = View.VISIBLE
            sellerInferior?.visibility = View.GONE
        }
    }

    private fun initEvents() {
        spinnerSeller.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedSellerRegister = sellerOptions.getOrNull(position)?.register ?: 0L
                loadShopOptions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerShop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedShopRegister = shopOptions.getOrNull(position)?.register ?: 0L
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        actionSearch.setOnClickListener {
            generateReport()
        }

        actionClean.setOnClickListener {
            cleanReport()
        }

        cardPdf.setOnClickListener {
            if (reportRows.isEmpty()) {
                Toast.makeText(this, "Primero debes generar el reporte", Toast.LENGTH_SHORT).show()
            } else {
                exportPdf()
            }
        }

        cardExcel.setOnClickListener {
            if (reportRows.isEmpty()) {
                Toast.makeText(this, "Primero debes generar el reporte", Toast.LENGTH_SHORT).show()
            } else {
                exportExcel()
            }
        }
    }

    private fun loadReportData() {
        loadPurchases { purchases ->
            loadProducts { products ->
                loadShops { shops ->
                    loadSellers { sellers ->
                        loadUsers { users ->
                            loadOrders { orders ->
                                val productMap = products.associateBy { it.register }
                                val shopMap = shops.associateBy { it.register }
                                val sellerMap = sellers.associateBy { it.register }
                                val userMap = users.associateBy { it.register }
                                val orderMap = orders.associateBy { it.register }
                                val sessionRegister = SessionManager.getRegister(this)

                                sales = purchases.mapNotNull { purchase ->
                                    val product = productMap[purchase.idProduct] ?: return@mapNotNull null
                                    val shop = shopMap[product.idShop] ?: return@mapNotNull null
                                    val seller = sellerMap[shop.idSeller] ?: return@mapNotNull null
                                    val user = userMap[seller.idUser]
                                    val order = orderMap[purchase.idOrder]

                                    if (isSellerSession && seller.idUser != sessionRegister) {
                                        return@mapNotNull null
                                    }

                                    SaleData(
                                        sellerRegister = seller.register,
                                        sellerName = sellerName(seller, user),
                                        shopRegister = shop.register,
                                        shopName = shop.name ?: "Sin Informacion",
                                        products = purchase.amount ?: 0,
                                        total = purchase.total ?: 0.0,
                                        date = order?.date ?: ""
                                    )
                                }

                                loadSellerOptions()
                                loadShopOptions()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadPurchases(onSuccess: (List<PurchaseModel>) -> Unit) {
        db.collection("purchase")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(PurchaseModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadProducts(onSuccess: (List<ProductModel>) -> Unit) {
        db.collection("product")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(ProductModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadShops(onSuccess: (List<ShopModel>) -> Unit) {
        db.collection("shop")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(ShopModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadSellers(onSuccess: (List<SellerModel>) -> Unit) {
        db.collection("seller")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(SellerModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadUsers(onSuccess: (List<UserModel>) -> Unit) {
        db.collection("user")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(UserModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadOrders(onSuccess: (List<OrderModel>) -> Unit) {
        db.collection("order")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.documents.mapNotNull { it.toObject(OrderModel::class.java) })
            }
            .addOnFailureListener { exception ->
                showLoadError(exception)
                onSuccess(emptyList())
            }
    }

    private fun loadSellerOptions() {
        val options = mutableListOf(FilterOption(0L, getString(R.string.all)))
        val sellers = sales
            .distinctBy { it.sellerRegister }
            .sortedBy { it.sellerName }

        sellers.forEach { sale ->
            options.add(FilterOption(sale.sellerRegister, sale.sellerName))
        }

        sellerOptions = options

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sellerOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSeller.adapter = adapter
    }

    private fun loadShopOptions() {
        val filteredSales = sales.filter { sale ->
            selectedSellerRegister == 0L || sale.sellerRegister == selectedSellerRegister
        }

        val options = mutableListOf(FilterOption(0L, getString(R.string.all)))
        val shops = filteredSales
            .distinctBy { it.shopRegister }
            .sortedBy { it.shopName }

        shops.forEach { sale ->
            options.add(FilterOption(sale.shopRegister, sale.shopName))
        }

        shopOptions = options

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            shopOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerShop.adapter = adapter
    }

    private fun generateReport() {
        val dateFrom = inputDateFrom.text.toString().trim()
        val dateTo = inputDateTo.text.toString().trim()

        if ((dateFrom.isNotEmpty() && dateTo.isEmpty()) || (dateFrom.isEmpty() && dateTo.isNotEmpty())) {
            Toast.makeText(this, "Debes seleccionar la fecha desde y la fecha hasta", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredSales = sales.filter { sale ->
            val sellerValid = selectedSellerRegister == 0L || sale.sellerRegister == selectedSellerRegister
            val shopValid = selectedShopRegister == 0L || sale.shopRegister == selectedShopRegister
            val dateValid = if (dateFrom.isEmpty() && dateTo.isEmpty()) {
                true
            } else {
                sale.date.isNotEmpty() && sale.date >= dateFrom && sale.date <= dateTo
            }

            sellerValid && shopValid && dateValid
        }

        reportRows = if (isSellerSession) {
            filteredSales
                .groupBy { it.shopRegister }
                .map { (_, items) ->
                    val first = items.first()
                    SaleReportRow(
                        sellerName = first.sellerName,
                        shopName = first.shopName,
                        products = items.sumOf { it.products },
                        total = items.sumOf { it.total }
                    )
                }
                .sortedBy { it.shopName }
        } else {
            filteredSales
                .groupBy { "${it.sellerRegister}-${it.shopRegister}" }
                .map { (_, items) ->
                    val first = items.first()
                    SaleReportRow(
                        sellerName = first.sellerName,
                        shopName = first.shopName,
                        products = items.sumOf { it.products },
                        total = items.sumOf { it.total }
                    )
                }
                .sortedWith(compareBy({ it.sellerName }, { it.shopName }))
        }

        totalProducts = reportRows.sumOf { it.products }
        totalMoney = reportRows.sumOf { it.total }

        renderReport()
        setExportEnabled(reportRows.isNotEmpty())

        if (reportRows.isEmpty()) {
            Toast.makeText(this, "No se encontraron ventas con los filtros seleccionados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cleanReport() {
        inputDateFrom.text.clear()
        inputDateTo.text.clear()
        reportRows = emptyList()
        totalProducts = 0
        totalMoney = 0.0
        dataResult.removeAllViews()
        cardResult.visibility = View.GONE
        setExportEnabled(false)

        if (!isSellerSession && sellerOptions.isNotEmpty()) {
            spinnerSeller.setSelection(0)
        }

        if (shopOptions.isNotEmpty()) {
            spinnerShop.setSelection(0)
        }

        Toast.makeText(this, "Filtros limpiados", Toast.LENGTH_SHORT).show()
    }

    private fun renderReport() {
        dataResult.removeAllViews()
        cardResult.visibility = View.VISIBLE

        val header = if (isSellerSession) {
            listOf("Tienda", "Productos Vendidos", "Total")
        } else {
            listOf("Vendedor", "Tienda", "Productos Vendidos", "Total")
        }

        addTableRow(header, true)

        reportRows.forEach { row ->
            val values = if (isSellerSession) {
                listOf(
                    row.shopName,
                    row.products.toString(),
                    moneyFormat.format(row.total)
                )
            } else {
                listOf(
                    row.sellerName,
                    row.shopName,
                    row.products.toString(),
                    moneyFormat.format(row.total)
                )
            }

            addTableRow(values, false)
        }

        textTotalProducts.text = "Productos Vendidos: $totalProducts"
        textTotalMoney.text = "Total: ${moneyFormat.format(totalMoney)}"
    }

    private fun addTableRow(values: List<String>, isHeader: Boolean) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        values.forEach { value ->
            val textView = TextView(this).apply {
                text = value
                textSize = if (isHeader) 14f else 13f
                setTextColor(getColor(R.color.black))
                setTypeface(null, if (isHeader) Typeface.BOLD else Typeface.NORMAL)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(10), dp(10), dp(10))
                layoutParams = LinearLayout.LayoutParams(dp(150), ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            row.addView(textView)
        }

        dataResult.addView(row)

        val line = View(this).apply {
            setBackgroundColor(getColor(R.color.background_layout))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
            )
        }

        dataResult.addView(line)
    }

    private fun setExportEnabled(enabled: Boolean) {
        cardPdf.isEnabled = enabled
        cardPdf.isClickable = enabled
        cardPdf.alpha = if (enabled) 1f else 0.45f

        cardExcel.isEnabled = enabled
        cardExcel.isClickable = enabled
        cardExcel.alpha = if (enabled) 1f else 0.45f
    }

    private fun exportPdf() {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 12f
            color = Color.BLACK
        }

        val logo = BitmapFactory.decodeResource(resources, R.drawable.logo_app)
        val headerLogo = Bitmap.createScaledBitmap(logo, dp(42), dp(42), true)
        val footerLogo = Bitmap.createScaledBitmap(logo, dp(28), dp(28), true)
        val margin = 40f
        var y = 46f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 22f
        canvas.drawText("EMPTIO", margin, y, paint)
        canvas.drawBitmap(headerLogo, pageWidth - margin - headerLogo.width, 20f, paint)

        y = 105f
        paint.textSize = 18f
        val title = "Reporte de Ventas"
        canvas.drawText(title, (pageWidth - paint.measureText(title)) / 2f, y, paint)

        y += 34f
        val headers = if (isSellerSession) {
            listOf("Tienda", "Productos Vendidos", "Total")
        } else {
            listOf("Vendedor", "Tienda", "Productos Vendidos", "Total")
        }
        val widths = if (isSellerSession) {
            listOf(220f, 150f, 145f)
        } else {
            listOf(135f, 135f, 145f, 100f)
        }

        y = drawPdfTableRow(canvas, paint, margin, y, widths, headers, true)

        reportRows.forEach { row ->
            if (y > 700f) {
                return@forEach
            }

            val values = if (isSellerSession) {
                listOf(
                    row.shopName,
                    row.products.toString(),
                    moneyFormat.format(row.total)
                )
            } else {
                listOf(
                    row.sellerName,
                    row.shopName,
                    row.products.toString(),
                    moneyFormat.format(row.total)
                )
            }

            y = drawPdfTableRow(canvas, paint, margin, y, widths, values, false)
        }

        y += 24f
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 13f
        canvas.drawText("Productos Vendidos: $totalProducts", margin, y, paint)
        y += 22f
        canvas.drawText("Total: ${moneyFormat.format(totalMoney)}", margin, y, paint)

        drawPdfFooter(canvas, paint, pageWidth, pageHeight, footerLogo)
        document.finishPage(page)

        try {
            val fileName = "reporte_ventas_${fileDate()}.pdf"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                contentResolver.openOutputStream(uri)?.use { output ->
                    document.writeTo(output)
                }

                Toast.makeText(this, "PDF generado en Descargas", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            Toast.makeText(this, "No fue posible generar el PDF", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
            headerLogo.recycle()
            footerLogo.recycle()
        }
    }

    private fun drawPdfTableRow(
        canvas: Canvas,
        paint: Paint,
        xStart: Float,
        yStart: Float,
        widths: List<Float>,
        values: List<String>,
        isHeader: Boolean
    ): Float {
        val rowHeight = 30f
        var x = xStart

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.BLACK

        values.forEachIndexed { index, value ->
            val width = widths[index]
            canvas.drawRect(x, yStart, x + width, yStart + rowHeight, paint)

            paint.style = Paint.Style.FILL
            paint.typeface = Typeface.create(
                Typeface.DEFAULT,
                if (isHeader) Typeface.BOLD else Typeface.NORMAL
            )
            paint.textSize = if (isHeader) 10.5f else 10f
            paint.color = Color.BLACK
            canvas.drawText(truncatePdfText(value), x + 6f, yStart + 20f, paint)

            paint.style = Paint.Style.STROKE
            x += width
        }

        paint.style = Paint.Style.FILL
        return yStart + rowHeight
    }

    private fun drawPdfFooter(
        canvas: Canvas,
        paint: Paint,
        pageWidth: Int,
        pageHeight: Int,
        logo: Bitmap
    ) {
        val logoX = (pageWidth - logo.width) / 2f
        val logoY = pageHeight - 72f
        canvas.drawBitmap(logo, logoX, logoY, paint)

    }

    private fun truncatePdfText(value: String): String {
        return if (value.length > 20) {
            value.take(17) + "..."
        } else {
            value
        }
    }

    private fun exportExcel() {
        try {
            val fileName = "reporte_ventas_${fileDate()}.xls"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                    writer.write(buildExcelContent())
                }

                Toast.makeText(this, "Excel generado en Descargas", Toast.LENGTH_SHORT).show()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            Toast.makeText(this, "No fue posible generar el Excel", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildExcelContent(): String {
        val builder = StringBuilder()
        builder.append("<table>")

        if (isSellerSession) {
            builder.append("<tr><th>Tienda</th><th>Productos Vendidos</th><th>Total</th></tr>")

            reportRows.forEach { row ->
                builder.append("<tr>")
                builder.append("<td>${row.shopName}</td>")
                builder.append("<td>${row.products}</td>")
                builder.append("<td>${moneyFormat.format(row.total)}</td>")
                builder.append("</tr>")
            }
        } else {
            builder.append("<tr><th>Vendedor</th><th>Tienda</th><th>Productos Vendidos</th><th>Total</th></tr>")

            reportRows.forEach { row ->
                builder.append("<tr>")
                builder.append("<td>${row.sellerName}</td>")
                builder.append("<td>${row.shopName}</td>")
                builder.append("<td>${row.products}</td>")
                builder.append("<td>${moneyFormat.format(row.total)}</td>")
                builder.append("</tr>")
            }
        }

        builder.append("<tr>")
        builder.append("<td><b>Productos Vendidos</b></td>")
        builder.append("<td><b>$totalProducts</b></td>")
        builder.append("<td><b>Total</b></td>")
        builder.append("<td><b>${moneyFormat.format(totalMoney)}</b></td>")
        builder.append("</tr>")
        builder.append("</table>")

        return builder.toString()
    }

    private fun sellerName(seller: SellerModel, user: UserModel?): String {
        val userName = "${user?.names ?: ""} ${user?.srnms ?: ""}".trim()

        return seller.company
            ?.takeIf { it.isNotBlank() }
            ?: userName.takeIf { it.isNotBlank() }
            ?: seller.nit
            ?: "Sin Informacion"
    }

    private fun showLoadError(exception: Exception) {
        exception.printStackTrace()
        Toast.makeText(this, "No fue posible cargar la información", Toast.LENGTH_SHORT).show()
    }

    private fun fileDate(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private data class FilterOption(
        val register: Long,
        val name: String
    ) {
        override fun toString(): String {
            return name
        }
    }

    private data class SaleData(
        val sellerRegister: Long,
        val sellerName: String,
        val shopRegister: Long,
        val shopName: String,
        val products: Int,
        val total: Double,
        val date: String
    )

    private data class SaleReportRow(
        val sellerName: String,
        val shopName: String,
        val products: Int,
        val total: Double
    )
}

