package com.example.diagearandroid.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.diagearandroid.model.Product
import com.example.diagearandroid.model.ProductRepository
import kotlinx.coroutines.launch

@Composable
fun ProductScreen(repository: ProductRepository) {
    val scope = rememberCoroutineScope()
    val products by repository.getProducts().collectAsState(emptyList())

    ProductScreenContent(
        products = products,
        addProduct = {scope.launch {repository.addProduct(it)}},
        updateProduct = {scope.launch {repository.updateProduct(it)}},
        deleteProduct = {scope.launch { repository.deleteProduct(it) }},
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    products: List<Product>,
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    deleteProduct: (Product) -> Unit,
){
    val scope = rememberCoroutineScope()
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dia O Gear")}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {/*TODO*/}){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ){
        innerPadding -> ProductList(products = products, modifier = Modifier.padding(innerPadding))
        {
                product -> selectedProduct = product
        }
    }
}