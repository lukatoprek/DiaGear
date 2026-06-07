package com.example.diagearandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.view.AddProductDialog
import com.example.diagearandroid.view.AdminLoginDialog
import com.example.diagearandroid.view.AdminProductScreen
import com.example.diagearandroid.view.EditProductDialog
import com.example.diagearandroid.view.HomeScreen
import com.example.diagearandroid.view.MainScreen
import com.example.diagearandroid.view.ProductDetailsScreen
import com.example.diagearandroid.viewmodel.AdminDialogState
import com.example.diagearandroid.viewmodel.AdminProductViewModel
import com.example.diagearandroid.viewmodel.ProductDetailsViewModel

object Routes {
    const val SCREEN_HOME = "homeScreen"
    const val SCREEN_ALL_PRODUCTS = "productScreen"
    const val SCREEN_ADMIN_ALL_PRODUCTS = "adminProductScreen"
    const val SCREEN_PRODUCT_DETAILS = "productDetails/{productId}"

    fun getProductDetailsPath(productId: String): String = "productDetails/$productId"
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
                        }
                        showAdminLoginDialog = false
                    }
                )
            }
        }

        composable(Routes.SCREEN_ALL_PRODUCTS) {
            MainScreen(repository = repository, navigation = navController)
        }

        composable(
            Routes.SCREEN_PRODUCT_DETAILS,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val viewModel: ProductDetailsViewModel = viewModel(
                factory = ProductDetailsViewModel.provideFactory(repository)
            )
            ProductDetailsScreen(
                productId = productId,
                viewModel = viewModel,
                onBackClicked = { navController.navigateUp() }
            )
        }

        composable(Routes.SCREEN_ADMIN_ALL_PRODUCTS) {
            val context = LocalContext.current
            val viewModel: AdminProductViewModel = viewModel(
                factory = AdminProductViewModel.provideFactory(repository, context)
            )
            val dialogState by viewModel.dialogState.collectAsState()
            val addForm by viewModel.addForm.collectAsState()
            val editForm by viewModel.editForm.collectAsState()

            AdminProductScreen(
                viewModel = viewModel,
                navigation = navController
            )

            when (val state = dialogState) {
                is AdminDialogState.Add -> AddProductDialog(
                    form = addForm,
                    onFormUpdate = { viewModel.updateAddForm(it) },
                    onDismiss = { viewModel.dismissDialog() },
                    onConfirm = { product -> viewModel.addProduct(product) }
                )
                is AdminDialogState.Edit -> EditProductDialog(
                    product = state.product,
                    form = editForm,
                    onFormUpdate = { viewModel.updateEditForm(it) },
                    onDismiss = { viewModel.dismissDialog() },
                    onConfirm = { product -> viewModel.updateProduct(product) }
                )
                is AdminDialogState.None -> Unit
            }
        }
    }
}
