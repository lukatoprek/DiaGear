package com.example.diagearandroid.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diagearandroid.model.Product
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ProductList(products: List<Product>, modifier: Modifier = Modifier, productClicked: (Product) -> Unit) {
    LazyColumn (
        modifier = modifier
    ){
        items(products, key = {it.id}) { product->
            ProductCard(product = product){
                productClicked(product)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}