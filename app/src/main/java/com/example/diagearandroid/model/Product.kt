package com.example.diagearandroid.model

import kotlinx.serialization.Serializable


@Serializable
data class Product(
    val id: String,
    var productId: String,
    var manufacturer: String,
    var name: String,
    var detailedName: String,
    var category: String,
    var amount: String,
    var price: Double,
    var useWithin: String,
    var image: String,
    var details: String
)