package com.compensar.tienda.ui.model.common

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class SelectOption(
    val register: Long,
    val label: String,
    val selectable: Boolean = true
) {
    override fun toString(): String = label
}

object FirestoreSelectHelper {

    private val db = FirebaseFirestore.getInstance()

    fun load(
        context: Context,
        spinner: Spinner,
        collectionName: String,
        labelFields: List<String>,
        selectedId: Long = 0,
        filterField: String? = null,
        filterValue: Any? = null
    ) {
        var query: Query = db.collection(collectionName)

        if (filterField != null && filterValue != null) {
            query = query.whereEqualTo(filterField, filterValue)
        }

        query.get()
            .addOnSuccessListener { result ->
                val options = result.documents.mapNotNull { document ->
                    val register = document.getLong("register")
                        ?: document.id.toLongOrNull()
                        ?: 0L

                    if (register <= 0) {
                        null
                    } else {
                        val label = labelFields
                            .mapNotNull { field -> document.get(field)?.toString()?.trim() }
                            .filter { it.isNotEmpty() }
                            .joinToString(" ")

                        SelectOption(
                            register = register,
                            label = if (label.isEmpty()) {
                                "Registro: $register"
                            } else {
                                "$register - $label"
                            }
                        )
                    }
                }.sortedBy { it.register }

                val finalOptions = if (options.isEmpty()) {
                    listOf(
                        SelectOption(
                            register = 0,
                            label = "Sin Informacion",
                            selectable = false
                        )
                    )
                } else {
                    options
                }

                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    finalOptions
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                if (selectedId > 0) {
                    val index = finalOptions.indexOfFirst { it.register == selectedId }
                    if (index >= 0) {
                        spinner.setSelection(index)
                    }
                }
            }
            .addOnFailureListener {
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    listOf(
                        SelectOption(
                            register = 0,
                            label = "Sin Informacion",
                            selectable = false
                        )
                    )
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
    }

    fun getSelectedId(spinner: Spinner): Long? {
        val option = spinner.selectedItem as? SelectOption
        return if (option != null && option.selectable && option.register > 0) {
            option.register
        } else {
            null
        }
    }
}
