package com.compensar.tienda.firestore

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Repositorio base para operaciones CRUD en Firestore.
 *
 * @param T tipo de entidad del documento.
 * @param collectionName nombre de la coleccion en Firestore.
 * @param clazz clase usada por Firestore para deserializar documentos.
 * @param getRegister funcion para leer el identificador `register` del modelo.
 * @param withRegister funcion para clonar el modelo asignando `register`.
 */
open class BaseFire<T : Any>(
    collectionName: String,
    private val clazz: Class<T>,
    private val getRegister: (T) -> Long,
    private val withRegister: (T, Long) -> T
) {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(collectionName)

    /**
     * Crea un documento.
     *
     * Si el `register` del modelo es menor o igual a cero, se genera con
     * `System.currentTimeMillis()` y se persiste ese valor en el documento.
     */
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

    /**
     * Consulta todos los documentos de la coleccion y los deserializa a [T].
     */
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

    /**
     * Consulta un documento puntual por su `register`.
     */
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

    /**
     * Actualiza un documento existente usando su `register` como id.
     */
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

    /**
     * Elimina un documento por su `register`.
     */
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
