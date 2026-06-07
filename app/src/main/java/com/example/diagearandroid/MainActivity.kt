package com.example.diagearandroid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.diagearandroid.model.FirestoreProductRepository
import com.example.diagearandroid.ui.theme.DiagearTheme
import com.example.diagearandroid.util.LocaleHelper
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            DiagearTheme() {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val productRepository = remember { FirestoreProductRepository() }
    NavigationController(productRepository)
}