package com.compensar.tienda.ui.common

import android.content.Context
import com.compensar.tienda.model.OrderModel
import com.compensar.tienda.model.ProductModel
import com.compensar.tienda.model.PurchaseModel
import com.compensar.tienda.model.SellerModel
import com.compensar.tienda.model.ShipmentModel
import com.compensar.tienda.model.ShopModel
import com.compensar.tienda.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

object SellerDataHelper {

    private val db = FirebaseFirestore.getInstance()

    data class SellerData(
        val seller: SellerModel?,
        val shops: List<ShopModel>,
        val products: List<ProductModel>,
        val purchases: List<PurchaseModel>,
        val orders: List<OrderModel>,
        val users: List<UserModel>,
        val shipments: List<ShipmentModel>
    )

    data class SellerOrder(
        val order: OrderModel,
        val purchases: List<PurchaseModel>,
        val user: UserModel?,
        val shipment: ShipmentModel?
    )

    fun loadSellerData(
        context: Context,
        onSuccess: (SellerData) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userRegister = SessionManager.getRegister(context)

        db.collection("seller").get()
            .addOnSuccessListener { sellerSnapshot ->
                val sellers = sellerSnapshot.documents.mapNotNull { it.toObject(SellerModel::class.java) }
                val currentSeller = sellers.firstOrNull { it.idUser == userRegister }

                db.collection("shop").get()
                    .addOnSuccessListener { shopSnapshot ->
                        val allShops = shopSnapshot.documents.mapNotNull { it.toObject(ShopModel::class.java) }
                        val sellerShops = allShops.filter { it.idSeller == currentSeller?.register }
                        val shopRegisters = sellerShops.map { it.register }.toSet()

                        db.collection("product").get()
                            .addOnSuccessListener { productSnapshot ->
                                val allProducts = productSnapshot.documents.mapNotNull { it.toObject(
                                    ProductModel::class.java) }
                                val sellerProducts = allProducts.filter { it.idShop in shopRegisters }
                                val productRegisters = sellerProducts.map { it.register }.toSet()

                                db.collection("purchase").get()
                                    .addOnSuccessListener { purchaseSnapshot ->
                                        val allPurchases = purchaseSnapshot.documents.mapNotNull { it.toObject(
                                            PurchaseModel::class.java) }
                                        val sellerPurchases = allPurchases.filter { it.idProduct in productRegisters }

                                        db.collection("order").get()
                                            .addOnSuccessListener { orderSnapshot ->
                                                val allOrders = orderSnapshot.documents.mapNotNull { it.toObject(
                                                    OrderModel::class.java) }
                                                val orderRegisters = sellerPurchases.map { it.idOrder }.toSet()
                                                val sellerOrders = allOrders.filter { it.register in orderRegisters }

                                                db.collection("user").get()
                                                    .addOnSuccessListener { userSnapshot ->
                                                        val allUsers = userSnapshot.documents.mapNotNull { it.toObject(
                                                            UserModel::class.java) }

                                                        db.collection("shipment").get()
                                                            .addOnSuccessListener { shipmentSnapshot ->
                                                                val allShipments = shipmentSnapshot.documents.mapNotNull {
                                                                    it.toObject(ShipmentModel::class.java)
                                                                }

                                                                onSuccess(
                                                                    SellerData(
                                                                        seller = currentSeller,
                                                                        shops = sellerShops,
                                                                        products = sellerProducts,
                                                                        purchases = sellerPurchases,
                                                                        orders = sellerOrders,
                                                                        users = allUsers,
                                                                        shipments = allShipments
                                                                    )
                                                                )
                                                            }
                                                            .addOnFailureListener { onFailure(it) }
                                                    }
                                                    .addOnFailureListener { onFailure(it) }
                                            }
                                            .addOnFailureListener { onFailure(it) }
                                    }
                                    .addOnFailureListener { onFailure(it) }
                            }
                            .addOnFailureListener { onFailure(it) }
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun buildSellerOrders(data: SellerData): List<SellerOrder> {
        val purchasesByOrder = data.purchases.groupBy { it.idOrder }

        return data.orders.mapNotNull { order ->
            val purchases = purchasesByOrder[order.register].orEmpty()

            if (purchases.isEmpty()) {
                null
            } else {
                SellerOrder(
                    order = order,
                    purchases = purchases,
                    user = data.users.firstOrNull { it.register == order.idUser },
                    shipment = data.shipments.firstOrNull { it.register == order.idShipment }
                )
            }
        }
    }

    fun formatCurrency(value: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return formatter.format(value)
    }

    fun getLastSixMonths(): List<String> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -5)

        val months = mutableListOf<String>()

        repeat(6) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            months.add("%04d-%02d".format(year, month))
            calendar.add(Calendar.MONTH, 1)
        }

        return months
    }

    fun getOrderMonth(order: OrderModel): String {
        val date = order.date.orEmpty()

        return if (date.length >= 7) {
            date.substring(0, 7)
        } else {
            ""
        }
    }

    fun getUserFullName(user: UserModel?): String {
        val names = user?.names.orEmpty()
        val surnames = user?.srnms.orEmpty()
        val fullName = "$names $surnames".trim()

        return fullName.ifEmpty { "Sin información" }
    }
}
