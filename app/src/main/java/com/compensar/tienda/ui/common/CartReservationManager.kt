package com.compensar.tienda.ui.common

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.max

object CartReservationManager {
    private val db = FirebaseFirestore.getInstance()

    private fun reservationId(userRegister: Long, productRegister: Long): String = "${userRegister}_${productRegister}"

    fun reserveQuantity(
        context: Context,
        item: CartItem,
        newQuantity: Int,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val userRegister = SessionManager.getRegister(context)
        if (userRegister <= 0) {
            onError("Debes iniciar sesión para usar el carrito")
            return
        }

        val productRef = db.collection("product").document(item.register.toString())
        val reservationRef = db.collection("cart_reservation").document(reservationId(userRegister, item.register))

        db.runTransaction { transaction ->
            val productSnapshot = transaction.get(productRef)
            if (!productSnapshot.exists()) throw Exception("Producto no encontrado")

            val stock = (productSnapshot.getLong("stock") ?: 0L).toInt()
            val reservedStock = (productSnapshot.getLong("reservedStock") ?: 0L).toInt()
            val reservationSnapshot = transaction.get(reservationRef)
            val currentUserReserved = if (reservationSnapshot.exists()) {
                (reservationSnapshot.getLong("quantity") ?: 0L).toInt()
            } else 0

            val safeNewQuantity = max(0, newQuantity)
            val availableForUser = stock - reservedStock + currentUserReserved

            if (safeNewQuantity > availableForUser) {
                throw Exception("Stock disponible: ${max(0, availableForUser)}")
            }

            val diff = safeNewQuantity - currentUserReserved
            transaction.update(productRef, "reservedStock", FieldValue.increment(diff.toLong()))

            if (safeNewQuantity <= 0) {
                transaction.delete(reservationRef)
            } else {
                transaction.set(reservationRef, mapOf(
                    "idUser" to userRegister,
                    "idProduct" to item.register,
                    "quantity" to safeNewQuantity,
                    "name" to item.name,
                    "price" to item.price,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
            }
            safeNewQuantity
        }.addOnSuccessListener { finalQuantity ->
            if (finalQuantity <= 0) {
                CartManager.setQuantity(context, item.register, 0)
            } else {
                val exists = CartManager.getItems(context).any { it.register == item.register }
                if (exists) {
                    CartManager.setQuantity(context, item.register, finalQuantity)
                } else {
                    CartManager.add(context, item.copy(quantity = finalQuantity))
                }
            }
            onSuccess(finalQuantity)
        }.addOnFailureListener { exception ->
            onError(exception.message ?: "No fue posible reservar stock")
        }
    }

    fun releaseCart(context: Context) {
        val userRegister = SessionManager.getRegister(context)
        if (userRegister <= 0) return
        CartManager.getItems(context).forEach { item ->
            val productRef = db.collection("product").document(item.register.toString())
            val reservationRef = db.collection("cart_reservation").document(reservationId(userRegister, item.register))
            db.runTransaction { transaction ->
                val snapshot = transaction.get(reservationRef)
                val quantity = if (snapshot.exists()) (snapshot.getLong("quantity") ?: 0L).toInt() else 0
                if (quantity > 0) {
                    transaction.update(productRef, "reservedStock", FieldValue.increment(-quantity.toLong()))
                    transaction.delete(reservationRef)
                }
            }
        }
    }
}
