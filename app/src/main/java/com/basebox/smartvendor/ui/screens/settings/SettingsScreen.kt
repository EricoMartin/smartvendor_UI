package com.basebox.smartvendor.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.basebox.smartvendor.ui.screens.components.SettingsRow
import com.basebox.smartvendor.ui.viewmodels.AuthViewModel
import com.basebox.smartvendor.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val settings by viewModel.settingsState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val languageOptions = listOf("English", "Pidgin", "Yoruba", "Hausa", "Igbo")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        // Account Section
        Text(
            "ACCOUNT",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SettingsRow(
            icon = Icons.Default.AccountCircle,
            title = "Account",
            onClick = { /* Navigate to Account */ })

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // More Section
        Text(
            "MORE",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SettingsRow(
            icon = Icons.Default.Security,
            title = "Privacy & Security",
            onClick = { /* Navigate to Privacy */ })
        SettingsRow(
            icon = Icons.Default.Star,
            title = "Rate the app",
            onClick = { /* Open Play Store */ })
        SettingsRow(
            icon = Icons.Default.Phone,
            title = "Support",
            onClick = { /* Navigate to Support */ })

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        // Sign Out Button
        SettingsRow(
            icon = Icons.Default.ExitToApp,
            title = "Sign Out",
            onClick = {
                authViewModel.signOut()
            },
            iconColor = Color.Red // Make the icon and text red for emphasis
        )
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            // This is the TextField that shows the selected option
            TextField(
                value = settings.language,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Language") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor() // This is important
            )
            // This is the content of the dropdown menu
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            viewModel.updateLanguage(option)
                            expanded = false // Close the menu after selection
                        }
                    )
                }
            }
        }

//        Spacer(Modifier.height(16.dp))
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("Cloud Sync")
//            Spacer(Modifier.weight(1f))
//            Switch(checked = settings.syncEnabled, onCheckedChange = { viewModel.toggleSync(it) })
//        }
//        Spacer(Modifier.height(20.dp))
//        Button(onClick = { viewModel.exportData() }) { Text("Export CSV") }
    }

}
