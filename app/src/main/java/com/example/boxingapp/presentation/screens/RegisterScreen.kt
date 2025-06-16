package com.example.boxingapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.boxingapp.data.database.AppDatabase
import com.example.boxingapp.data.preferences.UserPreferenceRepository
import com.example.boxingapp.data.repository.UserRepository
import com.example.boxingapp.presentation.viewmodel.AuthViewModel
import com.example.boxingapp.presentation.viewmodel.AuthViewModelFactory

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            UserRepository(db.userDao()),
            UserPreferenceRepository(context)
        )
    )

    val loggedIn by viewModel.loggedInUser.collectAsState()
    val error by viewModel.error.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loggedIn) {
        if (loggedIn != null) {
            navController.navigate(NavRoutes.Home) {
                popUpTo(NavRoutes.Register) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Korisničko ime") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Lozinka") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.register(username, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Registriraj se")
        }
    }
}
