package me.onvo.onvo.test


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import me.onvo.onvo.theme.ThemeManager
import me.onvo.onvo.theme.ThemeMode
import me.onvo.onvo.theme.ThemeViewModel
import onvo.composeapp.generated.resources.Res
import onvo.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

import org.koin.compose.koinInject
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel,
    onSettingClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val currentTheme by themeViewModel.themeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }

    Logger.a("SettingsScreen") { "hi loggg 111111" }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                Image(painterResource(Res.drawable.compose_multiplatform), null)

            }
            // Theme Setting
            item {
                ThemeSettingItem(
                    currentTheme = currentTheme,
                    onClick = { showThemeDialog = true }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // Other Settings
            items(listOf("Account", "Privacy", "Notifications", "Appearance", "Help")) { setting ->
                SettingItem(
                    title = setting,
                    onClick = { onSettingClick(setting) }
                )
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingItem(
                    title = "Logout",
                    onClick = onLogout
                )
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onThemeSelected = { theme ->
                themeViewModel.setThemeMode(theme)
//                themeManager.setThemeMode(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
private fun ThemeSettingItem(
    currentTheme: ThemeMode,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text("Theme") },
        supportingContent = {
            Text(
                when (currentTheme) {
                    ThemeMode.LIGHT -> "Light"
                    ThemeMode.DARK -> "Dark"
                    ThemeMode.SYSTEM -> "System Default"
                }
            )
        },
        leadingContent = {
            Icon(
                imageVector = when (currentTheme) {
                    ThemeMode.LIGHT -> Icons.Default.LightMode
                    ThemeMode.DARK -> Icons.Default.DarkMode
                    ThemeMode.SYSTEM -> Icons.Default.Brightness4
                },
                contentDescription = "Theme"
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentTheme == mode,
                            onClick = { onThemeSelected(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (mode) {
                                ThemeMode.LIGHT -> "Light"
                                ThemeMode.DARK -> "Dark"
                                ThemeMode.SYSTEM -> "System Default"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun SettingItem(
    title: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}