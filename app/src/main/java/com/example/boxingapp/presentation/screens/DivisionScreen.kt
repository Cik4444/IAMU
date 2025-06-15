package com.example.boxingapp.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boxingapp.data.database.AppDatabase
import com.example.boxingapp.ui.viewmodel.DivisionViewModel
import com.example.boxingapp.ui.viewmodel.DivisionViewModelFactory
import com.example.boxingapp.data.api.RetrofitInstance

@Composable
fun DivisionScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val api = remember { RetrofitInstance.divisionApi }
    val repository = remember { com.example.boxingapp.data.repository.DivisionRepository(api, db.divisionDao()) }

    val viewModel: DivisionViewModel = viewModel(
        factory = DivisionViewModelFactory(repository)
    )


    val division by viewModel.division.collectAsState()
    val error by viewModel.error.collectAsState()
    var divisionId by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = divisionId,
            onValueChange = {
                divisionId = it
                viewModel.searchByDivisionId(it.text)
            },
            label = { Text("Unesi Division ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = { viewModel.fetchDivision(divisionId.text) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dohvati diviziju")
        }

        Spacer(modifier = Modifier.height(16.dp))

        division?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Naziv: ${it.name}", style = MaterialTheme.typography.headlineSmall)
                    Text("ID: ${it.id}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Težina: ${it.weight_kg} kg / ${it.weight_lb} lb",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Greška: $it", color = Color.Red)
        }
    }
}
