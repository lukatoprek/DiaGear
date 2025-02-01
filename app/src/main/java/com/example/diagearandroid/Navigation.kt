package com.example.diagearandroid

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.Product
import com.example.diagearandroid.view.AdminLoginDialog
import com.example.diagearandroid.view.HomeScreen
import com.example.diagearandroid.view.ProductDetailsScreen
import com.example.diagearandroid.view.ProductScreen

object Routes {
    const val SCREEN_HOME = "homeScreen"
    const val SCREEN_ALL_PRODUCTS = "productScreen"
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
                            navController.navigate(Routes.SCREEN_ALL_PRODUCTS)
                            showAdminLoginDialog = false
                        } else {
                            showAdminLoginDialog = false
                        }
                    }
                )
            }
        }
        composable(Routes.SCREEN_ALL_PRODUCTS) {
            val products by produceState<List<Product>>(initialValue = emptyList()) {
                repository.getProducts(
                    onSuccess = { productList -> value = productList },
                    onFailure = { exception -> Log.e("Firestore", "Error fetching products", exception) }
                )
            }
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
                    onBackClicked = { navController.navigate(Routes.SCREEN_ALL_PRODUCTS) }
                )
            }
        }
    }
}