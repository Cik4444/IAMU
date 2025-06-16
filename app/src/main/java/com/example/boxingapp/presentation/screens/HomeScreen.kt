package com.example.boxingapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.boxingapp.data.database.AppDatabase
import com.example.boxingapp.data.preferences.UserPreferenceRepository
import com.example.boxingapp.data.repository.UserRepository
import com.example.boxingapp.presentation.viewmodel.AuthViewModel
import com.example.boxingapp.presentation.viewmodel.AuthViewModelFactory

@Composable
fun HomeScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            UserRepository(db.userDao()),
            UserPreferenceRepository(context)
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Boxing Info",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Istraži svijet profesionalnog boksa",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { navController.navigate(NavRoutes.FighterList) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pretraži borce")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate(NavRoutes.Favorites) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Favoriti")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tamna tema")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate(NavRoutes.Login) {
                    popUpTo(NavRoutes.Home) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Odjava")
        }
    }
}
