package com.example.tienda.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tienda.R
import com.example.tienda.admin.model.User
import android.content.Intent
import com.example.tienda.admin.ui.EditUserAdmin

class UserAdapter(

    private val listUsers: List<User>
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtId: TextView = itemView.findViewById(R.id.txtId)
        val txtRol: TextView = itemView.findViewById(R.id.txtRol)
        val txtFullName: TextView = itemView.findViewById(R.id.txtFullName)
        val txtEmail: TextView = itemView.findViewById(R.id.txtEmail)

        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = listUsers[position]

        holder.txtId.text = "ID: ${user.id}"
        holder.txtRol.text = user.rol
        holder.txtFullName.text = "${user.name} ${user.surnames}"
        holder.txtEmail.text = user.email

        // Edit button
        holder.btnEdit.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(context, EditUserAdmin::class.java)

            intent.putExtra("rol", user.rol)
            intent.putExtra("nombre", user.name)
            intent.putExtra("apellido", user.surnames)
            intent.putExtra("correo", user.email)

            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

}