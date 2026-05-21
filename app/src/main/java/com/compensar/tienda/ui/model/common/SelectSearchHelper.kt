package com.compensar.tienda.ui.model.common

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compensar.tienda.R
import java.text.Normalizer

object SelectSearchHelper {
    fun bind(
        activity: AppCompatActivity,
        onSearch: (String) -> Unit,
        onClean: () -> Unit
    ) {
        val fieldSearch = activity.findViewById<EditText>(R.id.fieldSearch) ?: return
        val actionSearch = activity.findViewById<ImageView>(R.id.actionSearch) ?: return
        val actionClean = activity.findViewById<ImageView>(R.id.actionClean)

        actionClean?.visibility = View.GONE
        updateSearchState(fieldSearch, actionSearch)

        fun executeSearch() {
            val query = fieldSearch.text.toString().trim()

            if (query.isEmpty()) {
                Toast.makeText(activity, "Debe escribir algo para poder buscar", Toast.LENGTH_SHORT).show()
                return
            }

            onSearch(query)
            actionClean?.visibility = View.VISIBLE
        }

        actionSearch.setOnClickListener {
            executeSearch()
        }

        actionClean?.setOnClickListener {
            fieldSearch.text.clear()
            actionClean.visibility = View.GONE
            onClean()
            updateSearchState(fieldSearch, actionSearch)
        }

        fieldSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executeSearch()
                true
            } else {
                false
            }
        }

        fieldSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSearchState(fieldSearch, actionSearch)

                if (s.isNullOrBlank()) {
                    actionClean?.visibility = View.GONE
                    onClean()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun matches(data: Any, query: String): Boolean {
        val cleanQuery = query.trim()

        if (cleanQuery.isEmpty()) {
            return true
        }

        val parts = cleanQuery.split(":", limit = 2)

        return if (parts.size == 2) {
            val fieldName = normalizeField(parts[0])
            val fieldValue = normalizeValue(parts[1])

            if (fieldValue.isEmpty()) {
                true
            } else {
                values(data)
                    .filter { (key, _) -> normalizeField(key) == fieldName }
                    .any { (_, value) -> normalizeValue(value).contains(fieldValue) }
            }
        } else {
            val fieldValue = normalizeValue(cleanQuery)
            values(data).any { (_, value) -> normalizeValue(value).contains(fieldValue) }
        }
    }

    private fun updateSearchState(fieldSearch: EditText, actionSearch: ImageView) {
        actionSearch.alpha = if (fieldSearch.text.toString().trim().isEmpty()) 0.35f else 1f
    }

    private fun values(data: Any): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        var currentClass: Class<*>? = data.javaClass

        while (currentClass != null && currentClass != Any::class.java) {
            currentClass.declaredFields.forEach { field ->
                if (!field.name.contains("$")) {
                    field.isAccessible = true
                    val value = field.get(data)?.toString() ?: ""
                    val displayValue = if (normalizeField(field.name) == "state") {
                        when (value.lowercase()) {
                            "true" -> "Activo"
                            "false" -> "Apagado"
                            else -> value
                        }
                    } else {
                        value
                    }

                    result.add(field.name to value)
                    result.add(field.name to displayValue)
                    aliases(field.name).forEach { alias ->
                        result.add(alias to value)
                        result.add(alias to displayValue)
                    }
                }
            }

            currentClass = currentClass.superclass
        }

        return result
    }

    private fun aliases(fieldName: String): List<String> {
        return when (normalizeField(fieldName)) {
            "register" -> listOf("registro", "id")
            "name" -> listOf("nombre")
            "names" -> listOf("nombres")
            "srnms" -> listOf("apellidos")
            "email" -> listOf("correo")
            "password" -> listOf("clave", "contrasena")
            "detail" -> listOf("detalle")
            "state" -> listOf("estado")
            "model" -> listOf("modelo")
            "address" -> listOf("direccion")
            "label" -> listOf("etiqueta")
            "reference" -> listOf("referencia")
            "date" -> listOf("fecha")
            "hour" -> listOf("hora")
            "total" -> listOf("total")
            "amount" -> listOf("cantidad")
            "value" -> listOf("valor")
            "price" -> listOf("precio")
            "stock" -> listOf("existencias")
            "nit" -> listOf("nit")
            "company" -> listOf("empresa")
            "idrole" -> listOf("rol")
            "iduser" -> listOf("usuario")
            "idseller" -> listOf("vendedor")
            "idshop" -> listOf("tienda")
            "idcategory" -> listOf("categoria")
            "idproduct" -> listOf("producto")
            "idorder" -> listOf("orden", "referencia")
            "idgangway" -> listOf("pasarela")
            "idgateway" -> listOf("pasarela")
            "idubication" -> listOf("ubicacion")
            else -> emptyList()
        }
    }

    private fun normalizeField(value: String): String {
        return normalizeValue(value).replace("_", "").replace(" ", "")
    }

    private fun normalizeValue(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }
}
