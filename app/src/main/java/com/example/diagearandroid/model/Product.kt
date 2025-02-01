package com.example.diagearandroid.model

data class Product(
    val id: String = "",
    var productId: String = "",
    var manufacturer: String = "",
    var name: String = "",
    var detailedName: String = "",
    var category: String = "",
    var amount: String = "",
    var price: Double = 0.0,
    var useWithin: String = "",
    var image: String = "",
    var details: String = ""
)