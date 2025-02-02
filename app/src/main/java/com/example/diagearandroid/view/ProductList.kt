package com.example.diagearandroid.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diagearandroid.model.Product

@Composable
fun ProductList(
    products: List<Product>,
    modifier: Modifier = Modifier,
    productClicked: (Product) -> Unit,
) {
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

@Composable
fun ProductList(
    products: List<Product>,
    modifier: Modifier = Modifier,
    productClicked: (Product) -> Unit,
    onEditClicked: (Product) -> Unit,
    onDeleteClicked: (Product) -> Unit,
) {
    LazyColumn (
        modifier = modifier
    ){
        items(products, key = {it.id}) { product->
            ProductCard(
                product = product,
                clicked = {productClicked(product)},
                onEditClicked = {onEditClicked(product)},
                onDeleteClicked = {onDeleteClicked(product)},
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}