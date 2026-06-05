package com.example.diagearandroid.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirestoreProductRepository {

    private val firestore = Firebase.firestore

    suspend fun getProducts(): List<Product> = suspendCancellableCoroutine { cont ->
        firestore.collection("DiaGear").get()
            .addOnSuccessListener { result ->
                cont.resume(result.documents.mapNotNull { it.toObject(Product::class.java) })
            }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    suspend fun getProductById(id: String): Product? = suspendCancellableCoroutine { cont ->
        firestore.collection("DiaGear").document(id).get()
            .addOnSuccessListener { result ->
                cont.resume(result.toObject(Product::class.java))
            }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    suspend fun addProduct(product: Product): Unit = suspendCancellableCoroutine { cont ->
        val newId = generateRandomStringId()
        val withId = product.copy(id = newId)
        firestore.collection("DiaGear").document(newId).set(withId)
            .addOnSuccessListener { cont.resume(Unit) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    suspend fun updateProduct(product: Product): Unit = suspendCancellableCoroutine { cont ->
        firestore.collection("DiaGear").document(product.id).set(product)
            .addOnSuccessListener { cont.resume(Unit) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    suspend fun deleteProduct(product: Product): Unit = suspendCancellableCoroutine { cont ->
        firestore.collection("DiaGear").document(product.id).delete()
            .addOnSuccessListener { cont.resume(Unit) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }

    private fun generateRandomStringId(length: Int = 20): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { allowedChars.random() }.joinToString("")
    }
}
