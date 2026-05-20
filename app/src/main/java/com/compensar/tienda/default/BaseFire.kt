package com.compensar.tienda.default

import com.google.firebase.firestore.FirebaseFirestore

open class BaseFire<T : Any>(
    collectionName: String,
    private val clazz: Class<T>,
    private val getRegister: (T) -> Long,
    private val withRegister: (T, Long) -> T
) {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(collectionName)

    fun create(
        data: T,
        onSuccess: (T) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val register = getRegister(data).takeIf { it > 0 } ?: System.currentTimeMillis()
        val dataToSave = withRegister(data, register)

        collection.document(register.toString())
            .set(dataToSave)
            .addOnSuccessListener {
                onSuccess(dataToSave)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAll(
        onSuccess: (List<T>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { document ->
                    document.toObject(clazz)
                }
                onSuccess(list)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getByRegister(
        register: Long,
        onSuccess: (T?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(register.toString())
            .get()
            .addOnSuccessListener { document ->
                onSuccess(document.toObject(clazz))
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun update(
        data: T,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val register = getRegister(data)

        if (register <= 0) {
            onFailure(Exception("El campo register es obligatorio para actualizar"))
            return
        }

        collection.document(register.toString())
            .set(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun delete(
        register: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        collection.document(register.toString())
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
