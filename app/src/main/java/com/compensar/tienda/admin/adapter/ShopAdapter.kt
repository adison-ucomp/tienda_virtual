package com.compensar.tienda.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.compensar.tienda.R
import com.compensar.tienda.admin.model.Shop
import android.content.Intent
import com.compensar.tienda.admin.ui.EditShopAdmin

class ShopAdapter(
    private val listShops: List<Shop>
) : RecyclerView.Adapter<ShopAdapter.ShopViewHolder>() {

    class ShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtNit: TextView = itemView.findViewById(R.id.txtNit)
        val txtCompany: TextView = itemView.findViewById(R.id.txtCompany)
        val txtAdress: TextView = itemView.findViewById(R.id.txtAdress)
        val txtId: TextView = itemView.findViewById(R.id.txtId)
        val txtFullName: TextView = itemView.findViewById(R.id.txtFullName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)

        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shops, parent, false)

        return ShopViewHolder(view)

    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {

        val tienda = listShops[position]

        holder.txtNit.text = "NIT: ${tienda.nit}"
        holder.txtCompany.text = tienda.company
        holder.txtAdress.text = tienda.adress

        holder.txtId.text = "ID: ${tienda.id}"
        holder.txtFullName.text = "${tienda.name} ${tienda.surnames}"
        holder.txtEmail.text = tienda.email

        holder.btnEdit.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, EditShopAdmin::class.java)

            intent.putExtra("nit", tienda.nit)
            intent.putExtra("company", tienda.company)
            intent.putExtra("adress", tienda.adress)

            intent.putExtra("id", tienda.id)
            intent.putExtra("name", tienda.name)
            intent.putExtra("surnames", tienda.surnames)
            intent.putExtra("email", tienda.email)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {

        return listShops.size

    }

}