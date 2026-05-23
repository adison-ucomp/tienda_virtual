package com.compensar.tienda.ui.common

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Clase de datos [CartItem].
 *
 * Responsable de la logica asociada al utilidad comun de interfaz y sesion.
 */
data class CartItem(
    val register: Long,
    val name: String,
    val detail: String,
    val price: Double,
    val storefire: String,
    var quantity: Int
)

object CartManager {
    private const val PREF = "buyer_cart_pref"
    private const val KEY = "items"

    fun add(context: Context, item: CartItem) {
        val items = getItems(context).toMutableList()
        val index = items.indexOfFirst { it.register == item.register }
        if (index >= 0) items[index].quantity += item.quantity else items.add(item)
        save(context, items)
    }

    fun setQuantity(context: Context, register: Long, quantity: Int) {
        val items = getItems(context).toMutableList()
        val index = items.indexOfFirst { it.register == register }
        if (index >= 0) {
            if (quantity <= 0) items.removeAt(index) else items[index].quantity = quantity
        }
        save(context, items)
    }

    fun getItems(context: Context): List<CartItem> {
        val raw = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val array = JSONArray(raw)
        val list = mutableListOf<CartItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(CartItem(
                register = obj.optLong("register"),
                name = obj.optString("name"),
                detail = obj.optString("detail"),
                price = obj.optDouble("price"),
                storefire = obj.optString("storefire"),
                quantity = obj.optInt("quantity", 1)
            ))
        }
        return list
    }

    fun subtotal(context: Context): Double = getItems(context).sumOf { it.price * it.quantity }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().remove(KEY).apply()
    }

    private fun save(context: Context, items: List<CartItem>) {
        val array = JSONArray()
        items.forEach {
            array.put(JSONObject().apply {
                put("register", it.register)
                put("name", it.name)
                put("detail", it.detail)
                put("price", it.price)
                put("storefire", it.storefire)
                put("quantity", it.quantity)
            })
        }
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }
}

