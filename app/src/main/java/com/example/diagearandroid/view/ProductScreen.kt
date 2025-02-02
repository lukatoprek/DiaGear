package com.example.diagearandroid.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diagearandroid.R
import com.example.diagearandroid.Routes
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product

@Composable
fun ProductScreen(repository: FirestoreProductRepository, navigation: NavHostController) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

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

    val filteredProducts = if (selectedCategory != null) {
        products.filter { it.category == selectedCategory }
    } else {
        products
    }

    ProductScreenContent(
        products = filteredProducts,
        isLoading = isLoading,
        navigation = navigation,
        selectedCategory = selectedCategory,
        onCategorySelected = { category -> selectedCategory = category }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    products: List<Product>,
    isLoading: Boolean,
    navigation: NavHostController,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.diagear_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilteringChips(selectedCategory,onCategorySelected)

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ProductList(
                    products = products,
                    modifier = Modifier.weight(1f)
                ) { product ->
                    navigation.navigate(Routes.getProductDetailsPath(product.id))
                }
            }
        }
    }
}

@Composable
fun FilteringChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All") },
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
        )

        FilterChip(
            selected = selectedCategory == "IIB",
            onClick = { onCategorySelected("IIB") },
            label = { Text("IIB") },
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
        )

        FilterChip(
            selected = selectedCategory == "IVD",
            onClick = { onCategorySelected("IVD") },
            label = { Text("IVD") },
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
        )

        FilterChip(
            selected = selectedCategory == "IIa",
            onClick = { onCategorySelected("IIa") },
            label = { Text("IIa") },
            modifier = Modifier
                .padding(start = 4.dp, end = 4.dp)
        )
    }
}