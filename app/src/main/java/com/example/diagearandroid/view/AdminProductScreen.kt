package com.example.diagearandroid.view

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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.runtime.collectAsState
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
import com.example.diagearandroid.model.Product
import com.example.diagearandroid.viewmodel.AdminProductViewModel
import com.example.diagearandroid.viewmodel.ProductForm

@Composable
fun AdminProductScreen(
    viewModel: AdminProductViewModel,
    navigation: NavHostController,
) {
    val products by viewModel.displayedProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    AdminProductScreenContent(
        products = products,
        isLoading = isLoading,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onCategorySelected = { viewModel.setCategory(it) },
        navigation = navigation,
        onAddProductClicked = { viewModel.showAddDialog() },
        onEditProductClicked = { product -> viewModel.showEditDialog(product) },
        deleteProduct = { product -> viewModel.deleteProduct(product) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreenContent(
    products: List<Product>,
    isLoading: Boolean,
    searchQuery: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    navigation: NavHostController,
    onAddProductClicked: () -> Unit,
    onEditProductClicked: (Product) -> Unit,
    deleteProduct: (Product) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                expandedHeight = 70.dp,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.diagear_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProductClicked) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProductSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
            FilteringChips(selectedCategory, onCategorySelected)

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
                    onEditClicked = { product -> onEditProductClicked(product) },
                    onDeleteClicked = { product -> deleteProduct(product) }
                )
            }
        }
    }
}

@Composable
fun EditProductDialog(
    product: Product,
    form: ProductForm,
    onFormUpdate: (ProductForm) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit,
) {
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
        title = { Text("Update Product") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ProductFormField("Product ID", form.productId, isProductIdError, "Product ID is required") {
                    onFormUpdate(form.copy(productId = it)); isProductIdError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Manufacturer", form.manufacturer, isManufacturerError, "Manufacturer is required") {
                    onFormUpdate(form.copy(manufacturer = it)); isManufacturerError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Name", form.name, isNameError, "Name is required") {
                    onFormUpdate(form.copy(name = it)); isNameError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Detailed Name", form.detailedName, isDetailedNameError, "Detailed Name is required") {
                    onFormUpdate(form.copy(detailedName = it)); isDetailedNameError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Category", form.category, isCategoryError, "Category is required") {
                    onFormUpdate(form.copy(category = it)); isCategoryError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Amount", form.amount, isAmountError, "Amount is required") {
                    onFormUpdate(form.copy(amount = it)); isAmountError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.price,
                    onValueChange = {
                        onFormUpdate(form.copy(price = it))
                        isPriceError = it.toDoubleOrNull() == null
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPriceError,
                    supportingText = { if (isPriceError) Text("Price must be a valid number") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Use Within", form.useWithin, isUseWithinError, "Use Within is required") {
                    onFormUpdate(form.copy(useWithin = it)); isUseWithinError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Image URL", form.image, isImageError, "Image URL is required") {
                    onFormUpdate(form.copy(image = it)); isImageError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Details", form.details, isDetailsError, "Details are required", maxLines = 3) {
                    onFormUpdate(form.copy(details = it)); isDetailsError = it.isEmpty()
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                isProductIdError = form.productId.isEmpty()
                isManufacturerError = form.manufacturer.isEmpty()
                isNameError = form.name.isEmpty()
                isDetailedNameError = form.detailedName.isEmpty()
                isCategoryError = form.category.isEmpty()
                isAmountError = form.amount.isEmpty()
                isPriceError = form.price.toDoubleOrNull() == null
                isUseWithinError = form.useWithin.isEmpty()
                isImageError = form.image.isEmpty()
                isDetailsError = form.details.isEmpty()

                if (!isProductIdError && !isManufacturerError && !isNameError && !isDetailedNameError &&
                    !isCategoryError && !isAmountError && !isPriceError && !isUseWithinError &&
                    !isImageError && !isDetailsError
                ) {
                    onConfirm(
                        Product(
                            id = product.id,
                            productId = form.productId,
                            manufacturer = form.manufacturer,
                            name = form.name,
                            detailedName = form.detailedName,
                            category = form.category,
                            amount = form.amount,
                            price = form.price.toDoubleOrNull() ?: 0.0,
                            useWithin = form.useWithin,
                            image = form.image,
                            details = form.details
                        )
                    )
                }
            }) { Text("Update") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddProductDialog(
    form: ProductForm,
    onFormUpdate: (ProductForm) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit,
) {
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                ProductFormField("Product ID", form.productId, isProductIdError, "Product ID is required") {
                    onFormUpdate(form.copy(productId = it)); isProductIdError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Manufacturer", form.manufacturer, isManufacturerError, "Manufacturer is required") {
                    onFormUpdate(form.copy(manufacturer = it)); isManufacturerError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Name", form.name, isNameError, "Name is required") {
                    onFormUpdate(form.copy(name = it)); isNameError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Detailed Name", form.detailedName, isDetailedNameError, "Detailed Name is required") {
                    onFormUpdate(form.copy(detailedName = it)); isDetailedNameError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Category", form.category, isCategoryError, "Category is required") {
                    onFormUpdate(form.copy(category = it)); isCategoryError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Amount", form.amount, isAmountError, "Amount is required") {
                    onFormUpdate(form.copy(amount = it)); isAmountError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.price,
                    onValueChange = {
                        onFormUpdate(form.copy(price = it))
                        isPriceError = it.toDoubleOrNull() == null
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isPriceError,
                    supportingText = { if (isPriceError) Text("Price must be a valid number") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Use Within", form.useWithin, isUseWithinError, "Use Within is required") {
                    onFormUpdate(form.copy(useWithin = it)); isUseWithinError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Image URL", form.image, isImageError, "Image URL is required") {
                    onFormUpdate(form.copy(image = it)); isImageError = it.isEmpty()
                }
                Spacer(modifier = Modifier.height(8.dp))
                ProductFormField("Details", form.details, isDetailsError, "Details are required", maxLines = 3) {
                    onFormUpdate(form.copy(details = it)); isDetailsError = it.isEmpty()
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                isProductIdError = form.productId.isEmpty()
                isManufacturerError = form.manufacturer.isEmpty()
                isNameError = form.name.isEmpty()
                isDetailedNameError = form.detailedName.isEmpty()
                isCategoryError = form.category.isEmpty()
                isAmountError = form.amount.isEmpty()
                isPriceError = form.price.toDoubleOrNull() == null
                isUseWithinError = form.useWithin.isEmpty()
                isImageError = form.image.isEmpty()
                isDetailsError = form.details.isEmpty()

                if (!isProductIdError && !isManufacturerError && !isNameError && !isDetailedNameError &&
                    !isCategoryError && !isAmountError && !isPriceError && !isUseWithinError &&
                    !isImageError && !isDetailsError
                ) {
                    onConfirm(
                        Product(
                            productId = form.productId,
                            manufacturer = form.manufacturer,
                            name = form.name,
                            detailedName = form.detailedName,
                            category = form.category,
                            amount = form.amount,
                            price = form.price.toDoubleOrNull() ?: 0.0,
                            useWithin = form.useWithin,
                            image = form.image,
                            details = form.details
                        )
                    )
                }
            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ProductFormField(
    label: String,
    value: String,
    isError: Boolean,
    errorMessage: String,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        supportingText = { if (isError) Text(errorMessage) },
        maxLines = maxLines
    )
}
