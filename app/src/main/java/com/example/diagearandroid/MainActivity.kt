package com.example.diagearandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.model.ProductRepository
import com.example.diagearandroid.ui.theme.DiaGearTheme
import com.example.diagearandroid.view.ProductScreen
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            DiaGearTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val productRepository = remember { FirestoreProductRepository() }
    ProductScreen(productRepository)
}