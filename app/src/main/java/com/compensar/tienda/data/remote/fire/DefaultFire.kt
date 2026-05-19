package com.compensar.tienda.data.remote.fire

import com.compensar.tienda.data.remote.fire.default.CategoryDefault
import com.compensar.tienda.data.remote.fire.default.PaymentDefault
import com.compensar.tienda.data.remote.fire.default.ProductDefault
import com.compensar.tienda.data.remote.fire.default.RoleDefault
import com.compensar.tienda.data.remote.fire.default.UserDefault
import com.google.firebase.firestore.FirebaseFirestore

class DefaultFire {

    private val db = FirebaseFirestore.getInstance()

    fun createDefault(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        createRoles(
            onSuccess = {
                createCategories(
                    onSuccess = {
                        createPayments(
                            onSuccess = {
                                createUsers(
                                    onSuccess = {
                                        createProducts(
                                            onSuccess = {
                                                onSuccess()
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

    private fun createRoles(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        RoleDefault.getAll().forEach { role ->
            val document = db.collection("role")
                .document(role.register.toString())

            batch.set(document, role)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun createCategories(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        CategoryDefault.getAll().forEach { category ->
            val document = db.collection("category")
                .document(category.register.toString())

            batch.set(document, category)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun createPayments(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        PaymentDefault.getAll().forEach { payment ->
            val document = db.collection("payment")
                .document(payment.register.toString())

            batch.set(document, payment)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun createUsers(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        UserDefault.getAll().forEach { user ->
            val document = db.collection("user")
                .document(user.register.toString())

            batch.set(document, user)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun createProducts(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        ProductDefault.getAll().forEach { product ->
            val document = db.collection("product")
                .document(product.register.toString())

            batch.set(document, product)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
