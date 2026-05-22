package com.compensar.tienda.firestore

import android.util.Log
import com.compensar.tienda.default.CategoryDefault
import com.compensar.tienda.default.ModuleDefault
import com.compensar.tienda.default.PaymentDefault
import com.compensar.tienda.default.ProductDefault
import com.compensar.tienda.default.RoleDefault
import com.compensar.tienda.default.SellerDefault
import com.compensar.tienda.default.ShopDefault
import com.compensar.tienda.default.UserDefault
import com.compensar.tienda.model.UbicationModel
import com.compensar.tienda.model.ShipmentModel
import com.google.firebase.firestore.FirebaseFirestore

class DefaultFire {

    private val db = FirebaseFirestore.getInstance()

    fun createDefault(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createModules(
            onSuccess = {
                createRoles(
                    onSuccess = {
                        createCategories(
                            onSuccess = {
                                createUbications(
                                    onSuccess = {
                                        createShipments(
                                            onSuccess = {
                                                createPayments(
                                                    onSuccess = {
                                                        createUsers(
                                                            onSuccess = {
                                                                createSellers(
                                                                    onSuccess = {
                                                                        createShops(
                                                                            onSuccess = {
                                                                                createProducts(
                                                                                    onSuccess = onSuccess,
                                                                                    onFailure = onFailure
                                                                                )
                                                                            },
                                                                            onFailure = onFailure
                                                                        )
                                                                    },
                                                                    onFailure = onFailure
                                                                )
                                                            },
                                                            onFailure = onFailure
                                                        )
                                                    },
                                                    onFailure = onFailure
                                                )
                                            },
                                            onFailure = onFailure
                                        )
                                    },
                                    onFailure = onFailure
                                )
                            },
                            onFailure = onFailure
                        )
                    },
                    onFailure = onFailure
                )
            },
            onFailure = onFailure
        )
    }


    private fun createModules(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "module",
            data = ModuleDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createRoles(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "role",
            data = RoleDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createCategories(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "category",
            data = CategoryDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createUbications(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "ubication",
            data = listOf(
                UbicationModel(register = 1, name = "Casa"),
                UbicationModel(register = 2, name = "Oficina"),
                UbicationModel(register = 3, name = "Apartamento"),
                UbicationModel(register = 4, name = "Hotel"),
                UbicationModel(register = 5, name = "Otro")
            ),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createShipments(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "shipment",
            data = listOf(
                ShipmentModel(register = 1, name = "Pendiente"),
                ShipmentModel(register = 2, name = "Enviado"),
                ShipmentModel(register = 3, name = "Entregado"),
                ShipmentModel(register = 4, name = "Rechazado")
            ),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createPayments(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "payment",
            data = PaymentDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createUsers(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "user",
            data = UserDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }



    private fun createSellers(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "seller",
            data = SellerDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }


    private fun createShops(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "shop",
            data = ShopDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    private fun createProducts(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createMissingDocuments(
            collectionName = "product",
            data = ProductDefault.getAll(),
            getRegister = { it.register },
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    /**
     * Crea solamente los documentos por defecto que no existen.
     *
     * Antes se usaba batch.set(...) en cada inicio de la app, por eso Firestore
     * volvía a sobrescribir la información por defecto. Con este método primero
     * se consulta cada documento y solo se crea cuando no existe.
     */
    private fun <T : Any> createMissingDocuments(
        collectionName: String,
        data: List<T>,
        getRegister: (T) -> Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (data.isEmpty()) {
            onSuccess()
            return
        }

        val collection = db.collection(collectionName)
        var completed = 0
        var finishedWithError = false

        fun completeOne() {
            if (finishedWithError) return

            completed++

            if (completed == data.size) {
                onSuccess()
            }
        }

        fun fail(exception: Exception) {
            if (finishedWithError) return

            finishedWithError = true
            onFailure(exception)
        }

        data.forEach { item ->
            val register = getRegister(item)
            val document = collection.document(register.toString())

            document.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Log.d(
                            "DEFAULT_FIRE",
                            "Documento existente, no se sobrescribe: $collectionName/$register"
                        )
                        completeOne()
                    } else {
                        document.set(item)
                            .addOnSuccessListener {
                                Log.d(
                                    "DEFAULT_FIRE",
                                    "Documento por defecto creado: $collectionName/$register"
                                )
                                completeOne()
                            }
                            .addOnFailureListener { exception ->
                                fail(exception)
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    fail(exception)
                }
        }
    }
}
