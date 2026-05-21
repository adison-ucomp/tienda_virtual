package com.compensar.tienda.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.compensar.tienda.R
import com.compensar.tienda.ui.common.SessionNavigation
import com.compensar.tienda.ui.report.ReportDataSaleActivity

class AdminReportDataActivity : AppCompatActivity() {

    private lateinit var cardReportSale: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_report_data)
        SessionNavigation.bindProfile(this)

        cardReportSale = findViewById(R.id.cardReportSale)

        cardReportSale.setOnClickListener {
            val intent = Intent(this, ReportDataSaleActivity::class.java)
            startActivity(intent)
        }
    }
}
