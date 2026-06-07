package com.example.diagearandroid.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.diagearandroid.R
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.viewmodel.MapViewModel
import com.example.diagearandroid.viewmodel.ProductListViewModel

@Composable
fun MainScreen(repository: FirestoreProductRepository, navigation: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val productViewModel: ProductListViewModel = viewModel(
        factory = ProductListViewModel.provideFactory(repository)
    )
    val mapViewModel: MapViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_products)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_map)) }
                )
            }
        }
    ) { innerPadding ->
        // Only apply bottom padding — the inner TopAppBar owns the status bar area.
        // Applying top padding here too would double-apply the status bar inset.
        Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
            when (selectedTab) {
                0 -> ProductScreen(viewModel = productViewModel, navigation = navigation)
                else -> MapScreen(viewModel = mapViewModel)
            }
        }
    }
}
