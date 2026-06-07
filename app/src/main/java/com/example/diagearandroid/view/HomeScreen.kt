package com.example.diagearandroid.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diagearandroid.R
import com.example.diagearandroid.Routes
import com.example.diagearandroid.util.LocaleHelper

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
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    LanguageSelector()
                },
                actions = {
                    IconButton(onClick = onAdminLoginClicked) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.admin_login),
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
                            .background(Color.White, CircleShape)
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.diagear_logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.home_tagline),
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
                        contentDescription = stringResource(R.string.home_view_products)
                    )
                    Text(
                        text = stringResource(R.string.home_view_products),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    )
}

/**
 * Top-bar language switcher. Collapsed, it shows the active language's flag emoji; tapping
 * opens a dropdown of the supported languages. Picking one persists the choice and recreates
 * the activity so every screen re-renders in the new language.
 */
@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val current = LocaleHelper.getCurrentLanguage(context)
    val selectorDescription = stringResource(R.string.cd_language_selector)

    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.semantics { contentDescription = selectorDescription }
        ) {
            Text(
                text = flagFor(current),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LanguageMenuItem(
                flag = flagFor(LocaleHelper.ENGLISH),
                label = stringResource(R.string.language_english),
                selected = current == LocaleHelper.ENGLISH
            ) {
                expanded = false
                selectLanguage(context, LocaleHelper.ENGLISH)
            }
            LanguageMenuItem(
                flag = flagFor(LocaleHelper.CROATIAN),
                label = stringResource(R.string.language_croatian),
                selected = current == LocaleHelper.CROATIAN
            ) {
                expanded = false
                selectLanguage(context, LocaleHelper.CROATIAN)
            }
        }
    }
}

@Composable
private fun LanguageMenuItem(
    flag: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = flag, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = label,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        },
        trailingIcon = if (selected) {
            { Icon(Icons.Default.Check, contentDescription = null) }
        } else null,
        onClick = onClick
    )
}

private fun flagFor(tag: String): String =
    if (tag == LocaleHelper.CROATIAN) "🇭🇷" else "🇬🇧"

private fun selectLanguage(context: Context, tag: String) {
    if (LocaleHelper.getCurrentLanguage(context) == tag) return
    LocaleHelper.setLanguage(context, tag)
    context.findActivity()?.recreate()
}

private fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
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

    val allFieldsRequired = stringResource(R.string.error_all_fields_required)
    val incorrectCredentials = stringResource(R.string.error_incorrect_credentials)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.admin_login)) },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; isUsernameError = false },
                    label = { Text(stringResource(R.string.field_username)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isUsernameError,
                    supportingText = {
                        if (isUsernameError) {
                            Text(stringResource(R.string.error_username_required))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; isPasswordError = false },
                    label = { Text(stringResource(R.string.field_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isPasswordError,
                    supportingText = {
                        if (isPasswordError) {
                            Text(stringResource(R.string.error_password_required))
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
                        errorMessage = allFieldsRequired
                    } else if (username != "admin" || password != "admin") {
                        errorMessage = incorrectCredentials
                    } else {
                        errorMessage = ""
                        onLogin(username, password)
                    }
                }
            ) {
                Text(stringResource(R.string.action_login))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
