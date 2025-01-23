package com.example.diagearandroid.model

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirestoreProductRepository : ProductRepository{

    private val firestore = Firebase.firestore

    override fun getProducts() = flow {
        firestore.collection("DiaGear").snapshots.collect { querySnapshot ->

            val products = querySnapshot.documents.map { documentSnapshot ->
                documentSnapshot.data<Product>()
            }
            emit(products)
        }
    }

    override fun getProductById(id: String) = flow {
        firestore.collection("DiaGear").document(id).snapshots.collect { documentSnapshot ->
            emit(documentSnapshot.data<Product>())
        }
    }

    override suspend fun addProduct(product: Product) {
        val productDatabaseId = generateRandomStringId()
        firestore.collection("DiaGear")
            .document(productDatabaseId)
            .set(product.copy(id = productDatabaseId))
    }

    override suspend fun updateProduct(product: Product) {
        firestore.collection("DiaGear").document(product.id).set(product)
    }

    override suspend fun deleteProduct(product: Product) {
        firestore.collection("DiaGear").document(product.id).delete()
    }

    private fun generateRandomStringId(length: Int = 20): String
    {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return(1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}