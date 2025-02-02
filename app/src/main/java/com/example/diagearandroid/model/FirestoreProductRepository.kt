package com.example.diagearandroid.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirestoreProductRepository{

    private val firestore = Firebase.firestore

    fun getProducts(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("DiaGear")
            .get()
            .addOnSuccessListener { result ->
                val products = result.documents.mapNotNull { it.toObject(Product::class.java) }
                onSuccess(products)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getProductById(id: String, onSuccess: (Product?) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("DiaGear")
            .document(id)
            .get()
            .addOnSuccessListener { result ->
                val product = result.toObject(Product::class.java)
                onSuccess(product)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val productDatabaseId = generateRandomStringId()
        product.id = productDatabaseId
        firestore.collection("DiaGear")
            .document(productDatabaseId)
            .set(product.copy(id = productDatabaseId))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("DiaGear")
            .document(product.id)
            .set(product)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteProduct(product: Product, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("DiaGear")
            .document(product.id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    private fun generateRandomStringId(length: Int = 20): String
    {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return(1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}