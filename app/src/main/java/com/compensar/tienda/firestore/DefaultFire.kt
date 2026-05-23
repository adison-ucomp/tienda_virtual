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
import com.google.firebase.firestore.Source

/**
 * Inicializa datos semilla en Firestore para el primer arranque de la app.
 *
 * Crea documentos faltantes por coleccion y evita sobreescribir datos existentes.
 */
class DefaultFire {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Ejecuta la carga de datos por defecto en orden de dependencias.
     */
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
            onSuccess = {
                db.collection("module").document("17")
                    .update(
                        mapOf(
                            "name" to "Transacciones",
                            "model" to "trade",
                            "detail" to "Administra las transacciones de pago"
                        )
                    )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onSuccess() }
            },
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
     * Se consulta la colección una sola vez contra servidor y luego se crea en lote
     * únicamente lo faltante. Esto evita hacer una petición por cada documento en
     * cada inicio de la app, que podía dejar el Splash muy lento o bloquear la
     * ejecución en el emulador/dispositivo.
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

        collection.get(Source.SERVER)
            .addOnSuccessListener { snapshot ->
                val existingRegisters = snapshot.documents
                    .mapNotNull { document -> document.id.toLongOrNull() }
                    .toSet()

                val missingData = data.filter { item ->
                    !existingRegisters.contains(getRegister(item))
                }

                if (missingData.isEmpty()) {
                    Log.d(
                        "DEFAULT_FIRE",
                        "Colección sin faltantes, no se sobrescribe: $collectionName"
                    )
                    onSuccess()
                    return@addOnSuccessListener
                }

                val batch = db.batch()

                missingData.forEach { item ->
                    val register = getRegister(item)
                    val document = collection.document(register.toString())
                    batch.set(document, item)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d(
                            "DEFAULT_FIRE",
                            "Documentos por defecto creados en $collectionName: ${missingData.size}"
                        )
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
