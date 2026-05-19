package com.compensar.tienda.ui.model.common

import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreRelationLabelHelper {

    private val db = FirebaseFirestore.getInstance()

    fun load(
        target: TextView,
        collectionName: String,
        register: Long,
        labelFields: List<String>
    ) {
        if (register <= 0) {
            target.text = "Sin Informacion"
            return
        }

        db.collection(collectionName)
            .document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                target.text = buildLabel(document, labelFields)
            }
            .addOnFailureListener {
                target.text = "Sin Informacion"
            }
    }

    private fun buildLabel(
        document: DocumentSnapshot,
        labelFields: List<String>
    ): String {
        if (!document.exists()) {
            return "Sin Informacion"
        }

        val label = labelFields
            .mapNotNull { field -> document.get(field)?.toString()?.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" ")

        return label.ifEmpty { "Sin Informacion" }
    }
}
