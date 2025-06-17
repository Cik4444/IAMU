package com.example.boxingapp.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.boxingapp.presentation.viewmodel.FighterViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import com.example.boxingapp.data.api.RetrofitInstance

@Composable
fun FavoritesScreen(
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = com.example.boxingapp.data.database.AppDatabase.getInstance(context)
    val viewModel: FighterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = com.example.boxingapp.presentation.viewmodel.FighterViewModelFactory(
            com.example.boxingapp.data.repository.FighterRepository(
                apiService = RetrofitInstance.api,
                fighterDao = db.fighterDao(),
                divisionDao = db.divisionDao()
            ),
            context
        )
    )

    val favorites by viewModel.favorites.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Omiljeni borci", style = MaterialTheme.typography.headlineMedium)

        if (favorites.isEmpty()) {
            Text("Nema dodanih favorita", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favorites) { fighter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val json = URLEncoder.encode(Gson().toJson(fighter), "UTF-8")
                                navController.navigate("${NavRoutes.FighterDetail}/$json")
                            },
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = fighter.name ?: "Nepoznato ime",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Divizija: ${fighter.division?.name ?: "Nepoznato"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val wins = fighter.stats?.wins ?: 0
                            val losses = fighter.stats?.losses ?: 0
                            val draws = fighter.stats?.draws ?: 0
                            Text(
                                text = "Rekord: $wins-$losses-$draws",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

