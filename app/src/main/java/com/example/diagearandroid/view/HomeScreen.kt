package com.example.diagearandroid.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diagearandroid.R
import com.example.diagearandroid.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigation: NavHostController,
    onAdminLoginClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Welcome to Dia Gear",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = onAdminLoginClicked) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Admin Login",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.background) // Gradient background
                        )
                    )
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(8.dp, shape = CircleShape)
                            .background(Color(0xFFF7F7FF), CircleShape)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.diagear_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Find Diabetic Products Covered by Croatian Health Insurance—All in One Place!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigation.navigate(Routes.SCREEN_ALL_PRODUCTS) },
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(8.dp, shape = CircleShape),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "View Products"
                    )
                    Text(
                        text = "View Products",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    )
}

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Admin Login") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; isUsernameError = false },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isUsernameError,
                    supportingText = {
                        if (isUsernameError) {
                            Text("Username is required")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; isPasswordError = false },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordError,
                    supportingText = {
                        if (isPasswordError) {
                            Text("Password is required")
                        }
                    }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isUsernameError = username.isEmpty()
                    isPasswordError = password.isEmpty()

                    if (isUsernameError || isPasswordError) {
                        errorMessage = "All fields are required"
                    } else if (username != "admin" || password != "admin") {
                        errorMessage = "Incorrect credentials"
                    } else {
                        errorMessage = ""
                        onLogin(username, password)
                    }
                }
            ) {
                Text("Login")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

