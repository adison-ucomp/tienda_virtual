package com.compensar.tienda.ui.model.common

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

/**
 * Objeto singleton [FirebaseStorageImageHelper].
 *
 * Responsable de la logica asociada al helper comun para CRUD de modelos.
 */
object FirebaseStorageImageHelper {

    fun uploadFromUri(
        module: String,
        register: Long,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val path = buildPath(module, register)
        val reference = FirebaseStorage.getInstance().reference.child(path)

        reference.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }

                reference.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadFromBitmap(
        module: String,
        register: Long,
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val path = buildPath(module, register)
        val reference = FirebaseStorage.getInstance().reference.child(path)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val data = outputStream.toByteArray()

        reference.putBytes(data)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }

                reference.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun buildPath(module: String, register: Long): String {
        return "images/$module/${register}_${System.currentTimeMillis()}.jpg"
    }
}

