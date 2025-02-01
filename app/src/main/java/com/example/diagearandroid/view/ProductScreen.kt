package com.example.diagearandroid.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.diagearandroid.Routes
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product
import kotlinx.coroutines.launch

@Composable
fun ProductScreen(repository: FirestoreProductRepository, navigation: NavHostController) {
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch products asynchronously
    LaunchedEffect(Unit) {
        repository.getProducts(
            onSuccess = { productList ->
                products = productList
                isLoading = false
            },
            onFailure = { exception ->
                Log.e("ProductScreen", "Error fetching products", exception)
                isLoading = false
            }
        )
    }

    ProductScreenContent(
        products = products,
        isLoading = isLoading,
        navigation = navigation,
        addProduct = { product ->
            scope.launch {
                repository.addProduct(
                    product = product,
                    onSuccess = {
                        // Refresh the product list after adding a new product
                        repository.getProducts(
                            onSuccess = { productList -> products = productList },
                            onFailure = { exception -> Log.e("ProductScreen", "Error fetching products", exception) }
                        )
                    },
                    onFailure = { exception -> Log.e("ProductScreen", "Error adding product", exception) }
                )
            }
        },
        updateProduct = { product ->
            scope.launch {
                repository.updateProduct(
                    product = product,
                    onSuccess = {
                        // Refresh the product list after updating a product
                        repository.getProducts(
                            onSuccess = { productList -> products = productList },
                            onFailure = { exception -> Log.e("ProductScreen", "Error fetching products", exception) }
                        )
                    },
                    onFailure = { exception -> Log.e("ProductScreen", "Error updating product", exception) }
                )
            }
        },
        deleteProduct = { product ->
            scope.launch {
                repository.deleteProduct(
                    product = product,
                    onSuccess = {
                        // Refresh the product list after deleting a product
                        repository.getProducts(
                            onSuccess = { productList -> products = productList },
                            onFailure = { exception -> Log.e("ProductScreen", "Error fetching products", exception) }
                        )
                    },
                    onFailure = { exception -> Log.e("ProductScreen", "Error deleting product", exception) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    products: List<Product>,
    isLoading: Boolean,
    navigation: NavHostController,
    addProduct: (Product) -> Unit,
    updateProduct: (Product) -> Unit,
    deleteProduct: (Product) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dia O Gear") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (isLoading) {
            // Show a loading indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Show the product list
            ProductList(
                products = products,
                modifier = Modifier.padding(innerPadding)
            ) { product ->
                navigation.navigate(Routes.getProductDetailsPath(product.id))
            }
        }
    }
}