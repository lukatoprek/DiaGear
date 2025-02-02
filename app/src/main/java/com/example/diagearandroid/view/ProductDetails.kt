package com.example.diagearandroid.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    repository: FirestoreProductRepository,
    onBackClicked: () -> Unit
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(productId) {
        repository.getProductById(
            id = productId,
            onSuccess = { fetchedProduct ->
                product = fetchedProduct
                isLoading = false
            },
            onFailure = { exception ->
                Log.e("ProductDetailsScreen", "Error fetching product", exception)
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Product Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            )
        },
        content = { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (product != null) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.onSurface)
                    ) {
                        ImageView(
                            imageUrl = product!!.image,
                            description = product!!.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    )
                    {
                        Column (
                            modifier = Modifier.padding(16.dp)
                        ){
                            Text(
                                text = product!!.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ProductInfoRow(label = "Category", value = product!!.category)
                            ProductInfoRow(label = "Manufacturer", value = product!!.manufacturer)
                            ProductInfoRow(label = "HZZO ID", value = product!!.productId)
                            ProductInfoRow(label = "Amount", value = product!!.amount)
                            ProductInfoRow(label = "Price", value = "${product!!.price}€")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    {
                        Column (
                            modifier = Modifier.padding(16.dp)
                        ){
                            Text(
                                text = "Additional Information",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = product!!.detailedName,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Details",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = product!!.details,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Product not found", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
fun ProductInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
