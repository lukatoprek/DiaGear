package com.example.diagearandroid.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AdminProductScreen(
    scope: CoroutineScope,
    repository: FirestoreProductRepository,
    navigation: NavHostController,
    onAddProductClicked: () -> Unit,
    onEditProductClicked: (Product) -> Unit,
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    fun refreshProducts() {
        scope.launch {
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
    }

    LaunchedEffect(Unit) {
        refreshProducts()
    }

    val filteredProducts = if (selectedCategory != null) {
        products.filter { it.category == selectedCategory }
    } else {
        products
    }

    AdminProductScreenContent(
        products = filteredProducts,
        isLoading = isLoading,
        navigation = navigation,
        deleteProduct = { product ->
            scope.launch {
                repository.deleteProduct(
                    product = product,
                    onSuccess = {
                        refreshProducts()
                    },
                    onFailure = { exception -> Log.e("ProductScreen", "Error deleting product", exception) }
                )
            }
        },
        onAddProductClicked = onAddProductClicked,
        onEditProductClicked = onEditProductClicked,
        selectedCategory = selectedCategory,
        onCategorySelected = { category -> selectedCategory = category }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreenContent(
    products: List<Product>,
    isLoading: Boolean,
    navigation: NavHostController,
    deleteProduct: (Product) -> Unit,
    onAddProductClicked: () -> Unit,
    onEditProductClicked: (Product) -> Unit,
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
                    modifier = Modifier.weight(1f),
                    productClicked = { product ->
                        navigation.navigate(Routes.getProductDetailsPath(product.id))
                    },
                    onEditClicked = { product -> onEditProductClicked(product)
                        Log.d("ProductScreen", "Edit clicked for product: ${product.name}")
                    },
                    onDeleteClicked = { product ->
                        deleteProduct(product)
                    }
                )
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit,
) {
    var productId by remember { mutableStateOf(product.productId) }
    var manufacturer by remember { mutableStateOf(product.manufacturer) }
    var name by remember { mutableStateOf(product.name) }
    var detailedName by remember { mutableStateOf(product.detailedName) }
    var category by remember { mutableStateOf(product.category) }
    var amount by remember { mutableStateOf(product.amount) }
    var price by remember { mutableDoubleStateOf(product.price) }
    var useWithin by remember { mutableStateOf(product.useWithin) }
    var image by remember { mutableStateOf(product.image) }
    var details by remember { mutableStateOf(product.details) }

    var isProductIdError by remember { mutableStateOf(false) }
    var isManufacturerError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isDetailedNameError by remember { mutableStateOf(false) }
    var isCategoryError by remember { mutableStateOf(false) }
    var isAmountError by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }
    var isUseWithinError by remember { mutableStateOf(false) }
    var isImageError by remember { mutableStateOf(false) }
    var isDetailsError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = productId,
                    onValueChange = { productId = it; isProductIdError = it.isEmpty() },
                    label = { Text("Product ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isProductIdError,
                    supportingText = {
                        if (isProductIdError) {
                            Text("Product ID is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it; isManufacturerError = it.isEmpty() },
                    label = { Text("Manufacturer") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isManufacturerError,
                    supportingText = {
                        if (isManufacturerError) {
                            Text("Manufacturer is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; isNameError = it.isEmpty() },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text("Name is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = detailedName,
                    onValueChange = { detailedName = it; isDetailedNameError = it.isEmpty() },
                    label = { Text("Detailed Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDetailedNameError,
                    supportingText = {
                        if (isDetailedNameError) {
                            Text("Detailed Name is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it; isCategoryError = it.isEmpty() },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isCategoryError,
                    supportingText = {
                        if (isCategoryError) {
                            Text("Category is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; isAmountError = it.isEmpty() },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isAmountError,
                    supportingText = {
                        if (isAmountError) {
                            Text("Amount is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price.toString(),
                    onValueChange = { price = it.toDouble(); isPriceError = it.isEmpty() || it.toDoubleOrNull() == null },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPriceError,
                    supportingText = {
                        if (isPriceError) {
                            Text("Price must be a valid number")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = useWithin,
                    onValueChange = { useWithin = it; isUseWithinError = it.isEmpty() },
                    label = { Text("Use Within") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isUseWithinError,
                    supportingText = {
                        if (isUseWithinError) {
                            Text("Use Within is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = image,
                    onValueChange = { image = it; isImageError = it.isEmpty() },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isImageError,
                    supportingText = {
                        if (isImageError) {
                            Text("Image URL is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it; isDetailsError = it.isEmpty() },
                    label = { Text("Details") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDetailsError,
                    supportingText = {
                        if (isDetailsError) {
                            Text("Details are required")
                        }
                    },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProductIdError = productId.isEmpty()
                    isManufacturerError = manufacturer.isEmpty()
                    isNameError = name.isEmpty()
                    isDetailedNameError = detailedName.isEmpty()
                    isCategoryError = category.isEmpty()
                    isAmountError = amount.isEmpty()
                    isPriceError = price.toString().isEmpty()
                    isUseWithinError = useWithin.isEmpty()
                    isImageError = image.isEmpty()
                    isDetailsError = details.isEmpty()

                    if (!isProductIdError && !isManufacturerError && !isNameError && !isDetailedNameError &&
                        !isCategoryError && !isAmountError && !isPriceError && !isUseWithinError &&
                        !isImageError && !isDetailsError
                    ) {
                        val newProduct = Product(
                            id = product.id,
                            productId = productId,
                            manufacturer = manufacturer,
                            name = name,
                            detailedName = detailedName,
                            category = category,
                            amount = amount,
                            price = price,
                            useWithin = useWithin,
                            image = image,
                            details = details
                        )
                        onConfirm(newProduct)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit,
) {
    var productId by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var detailedName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("0.0") }
    var useWithin by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    var isProductIdError by remember { mutableStateOf(false) }
    var isManufacturerError by remember { mutableStateOf(false) }
    var isNameError by remember { mutableStateOf(false) }
    var isDetailedNameError by remember { mutableStateOf(false) }
    var isCategoryError by remember { mutableStateOf(false) }
    var isAmountError by remember { mutableStateOf(false) }
    var isPriceError by remember { mutableStateOf(false) }
    var isUseWithinError by remember { mutableStateOf(false) }
    var isImageError by remember { mutableStateOf(false) }
    var isDetailsError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = productId,
                    onValueChange = { productId = it; isProductIdError = it.isEmpty() },
                    label = { Text("Product ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isProductIdError,
                    supportingText = {
                        if (isProductIdError) {
                            Text("Product ID is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it; isManufacturerError = it.isEmpty() },
                    label = { Text("Manufacturer") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isManufacturerError,
                    supportingText = {
                        if (isManufacturerError) {
                            Text("Manufacturer is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; isNameError = it.isEmpty() },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isNameError,
                    supportingText = {
                        if (isNameError) {
                            Text("Name is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = detailedName,
                    onValueChange = { detailedName = it; isDetailedNameError = it.isEmpty() },
                    label = { Text("Detailed Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDetailedNameError,
                    supportingText = {
                        if (isDetailedNameError) {
                            Text("Detailed Name is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it; isCategoryError = it.isEmpty() },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isCategoryError,
                    supportingText = {
                        if (isCategoryError) {
                            Text("Category is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; isAmountError = it.isEmpty() },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isAmountError,
                    supportingText = {
                        if (isAmountError) {
                            Text("Amount is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it; isPriceError = it.isEmpty() || it.toDoubleOrNull() == null },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPriceError,
                    supportingText = {
                        if (isPriceError) {
                            Text("Price must be a valid number")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = useWithin,
                    onValueChange = { useWithin = it; isUseWithinError = it.isEmpty() },
                    label = { Text("Use Within") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isUseWithinError,
                    supportingText = {
                        if (isUseWithinError) {
                            Text("Use Within is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = image,
                    onValueChange = { image = it; isImageError = it.isEmpty() },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isImageError,
                    supportingText = {
                        if (isImageError) {
                            Text("Image URL is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it; isDetailsError = it.isEmpty() },
                    label = { Text("Details") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isDetailsError,
                    supportingText = {
                        if (isDetailsError) {
                            Text("Details are required")
                        }
                    },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProductIdError = productId.isEmpty()
                    isManufacturerError = manufacturer.isEmpty()
                    isNameError = name.isEmpty()
                    isDetailedNameError = detailedName.isEmpty()
                    isCategoryError = category.isEmpty()
                    isAmountError = amount.isEmpty()
                    isPriceError = price.isEmpty() || price.toDoubleOrNull() == null
                    isUseWithinError = useWithin.isEmpty()
                    isImageError = image.isEmpty()
                    isDetailsError = details.isEmpty()

                    if (!isProductIdError && !isManufacturerError && !isNameError && !isDetailedNameError &&
                        !isCategoryError && !isAmountError && !isPriceError && !isUseWithinError &&
                        !isImageError && !isDetailsError
                    ) {
                        val newProduct = Product(
                            productId = productId,
                            manufacturer = manufacturer,
                            name = name,
                            detailedName = detailedName,
                            category = category,
                            amount = amount,
                            price = price.toDoubleOrNull() ?: 0.0,
                            useWithin = useWithin,
                            image = image,
                            details = details
                        )
                        onConfirm(newProduct)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}