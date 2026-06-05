package com.example.diagearandroid.model

data class Pharmacy(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val address: String?
)
