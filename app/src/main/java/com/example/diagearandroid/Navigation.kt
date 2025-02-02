package com.example.diagearandroid

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product
import com.example.diagearandroid.view.AddProductDialog
import com.example.diagearandroid.view.AdminLoginDialog
import com.example.diagearandroid.view.AdminProductScreen
import com.example.diagearandroid.view.EditProductDialog
import com.example.diagearandroid.view.HomeScreen
import com.example.diagearandroid.view.ProductDetailsScreen
import com.example.diagearandroid.view.ProductScreen
import kotlinx.coroutines.launch

object Routes {
    const val SCREEN_HOME = "homeScreen"
    const val SCREEN_ALL_PRODUCTS = "productScreen"
    const val SCREEN_ADMIN_ALL_PRODUCTS = "adminProductScreen"
    const val SCREEN_PRODUCT_DETAILS = "productDetails/{productId}"

    fun getProductDetailsPath(productId: String?): String{
        if(productId != null){
            return "productDetails/$productId"
        }
        return "productDetails/0"
    }
}

@Composable
fun NavigationController(repository: FirestoreProductRepository) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SCREEN_HOME) {
        composable(Routes.SCREEN_HOME) {
            var showAdminLoginDialog by remember { mutableStateOf(false) }
            HomeScreen(
                navigation = navController,
                onAdminLoginClicked = { showAdminLoginDialog = true }
            )
            if (showAdminLoginDialog) {
                AdminLoginDialog(
                    onDismiss = { showAdminLoginDialog = false },
                    onLogin = { username, password ->
                        if (username == "admin" && password == "admin") {
                            navController.navigate(Routes.SCREEN_ADMIN_ALL_PRODUCTS)
                            showAdminLoginDialog = false
                        } else {
                            showAdminLoginDialog = false
                        }
                    }
                )
            }
        }
        composable(Routes.SCREEN_ALL_PRODUCTS) {
            ProductScreen(repository = repository, navigation = navController)
        }
        composable(
            Routes.SCREEN_PRODUCT_DETAILS,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailsScreen(
                    productId = productId,
                    repository = repository,
                    onBackClicked = { navController.navigateUp()}
                )
            }
        }
        composable(Routes.SCREEN_ADMIN_ALL_PRODUCTS) {
            var showAddProductDialog by remember { mutableStateOf(false) }
            var showEditProductDialog by remember { mutableStateOf(false) }
            var clickedEditProduct by remember {mutableStateOf<Product>(Product())}
            val scope = rememberCoroutineScope()

            AdminProductScreen(
                scope = scope,
                repository = repository,
                navigation = navController,
                onAddProductClicked = {showAddProductDialog = true},
                onEditProductClicked = {product ->
                    clickedEditProduct = product
                    showEditProductDialog = true
                }
            )

            if(showEditProductDialog)
            {
                EditProductDialog(
                    product = clickedEditProduct,
                    onDismiss = {showEditProductDialog = false},
                    onConfirm = { newProduct ->
                        scope.launch {
                        repository.updateProduct(
                            newProduct,
                            onSuccess = {
                                showEditProductDialog = false
                                navController.navigate(Routes.SCREEN_ADMIN_ALL_PRODUCTS)
                            },
                            onFailure = {exception ->
                                Log.e("Firestore","Error updating product with id: ${newProduct.id}", exception)
                                showEditProductDialog = false
                            }
                            )
                        }
                    }
                )
            }
            if(showAddProductDialog)
            {
                AddProductDialog(
                    onDismiss = { showAddProductDialog = false },
                    onConfirm = { newProduct ->
                        scope.launch {
                            repository.addProduct(
                                newProduct,
                                onSuccess = {
                                    showAddProductDialog = false
                                    navController.navigate(Routes.SCREEN_ADMIN_ALL_PRODUCTS)
                                },
                                onFailure = { exception ->
                                    Log.e("Firestore","Error adding new product to database.", exception)
                                    showAddProductDialog = false
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}