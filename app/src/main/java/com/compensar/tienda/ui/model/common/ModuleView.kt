package com.compensar.tienda.ui.model.common

import android.widget.TextView
import com.compensar.tienda.model.ModuleModel
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Objeto singleton [ModuleView].
 *
 * Responsable de la logica asociada al helper comun para CRUD de modelos.
 */
object ModuleView {

    fun bindTitle(
        title: TextView,
        action: String,
        model: String,
        defaultName: String
    ) {
        title.text = "$action $defaultName"

        FirebaseFirestore.getInstance()
            .collection("module")
            .whereEqualTo("model", model)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val module = result.documents
                    .firstOrNull()
                    ?.toObject(ModuleModel::class.java)

                val name = module?.name?.takeIf { it.isNotBlank() } ?: defaultName
                title.text = "$action $name"
            }
    }
}

