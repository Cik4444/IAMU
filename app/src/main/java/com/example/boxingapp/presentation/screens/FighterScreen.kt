package com.example.boxingapp.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.boxingapp.data.model.Division
import com.example.boxingapp.data.repository.DivisionRepository
import com.example.boxingapp.presentation.screens.NavRoutes
import com.example.boxingapp.presentation.viewmodel.FighterViewModel
import com.example.boxingapp.ui.viewmodel.DivisionViewModel
import com.example.boxingapp.ui.viewmodel.DivisionViewModelFactory
import com.google.gson.Gson
import java.net.URLEncoder
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.boxingapp.data.database.AppDatabase
import com.example.boxingapp.data.repository.FighterRepository
import com.example.boxingapp.ui.viewmodel.FighterViewModelFactory


@Composable
fun provideDivisionRepository(): DivisionRepository {
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = com.example.boxingapp.data.database.AppDatabase.getInstance(context).divisionDao()
    val api = RetrofitInstance.divisionApi
    return DivisionRepository(api, dao)
}

@Composable
fun provideFighterRepository(): FighterRepository {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val api = RetrofitInstance.api
    return FighterRepository(
        apiService = api,
        fighterDao = db.fighterDao(),
        divisionDao = db.divisionDao()
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisionDropdown(
    divisions: List<Division>,
    selectedId: String?,
    onDivisionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentName = divisions.find { it.id == selectedId }?.name?.takeIf { !it.isNullOrBlank() } ?: "Sve divizije"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = currentName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Filter po diviziji") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Sve divizije") },
                onClick = {
                    onDivisionSelected(null)
                    expanded = false
                }
            )
            divisions.forEach { division ->
                DropdownMenuItem(
                    text = { Text(division.name?.takeIf { !it.isNullOrBlank() } ?: "Nepoznata divizija") },
                    onClick = {
                        onDivisionSelected(division.id)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun FighterScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)

    val fighterViewModel: FighterViewModel = viewModel(
        factory = FighterViewModelFactory(
            FighterRepository(
                apiService = RetrofitInstance.api,
                fighterDao = db.fighterDao(),
                divisionDao = db.divisionDao()
            )
        )
    )

    val divisionViewModel: DivisionViewModel = viewModel(
        factory = DivisionViewModelFactory(
            DivisionRepository(
                api = RetrofitInstance.divisionApi,
                dao = AppDatabase.getInstance(context).divisionDao()
            )
        )
    )

    val divisions by divisionViewModel.divisions.collectAsState()
    val fighters by fighterViewModel.filteredFighters.collectAsState()
    val favorites by fighterViewModel.favorites.collectAsState()
    val error by fighterViewModel.error.collectAsState()
    val selectedDivisionId by fighterViewModel.selectedDivisionId.collectAsState()
    val query by fighterViewModel.query.collectAsState()

    LaunchedEffect(Unit) {
        divisionViewModel.fetchAllDivisions()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        DivisionDropdown(
            divisions = divisions,
            selectedId = selectedDivisionId,
            onDivisionSelected = { id -> fighterViewModel.onDivisionSelected(id) }
        )

        OutlinedTextField(
            value = query,
            onValueChange = { fighterViewModel.onQueryChanged(it) },
            label = { Text("Pretraži borca") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        Button(
            onClick = {
                fighterViewModel.onQueryChanged("")
                fighterViewModel.onDivisionSelected(null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resetiraj filtere")
        }

        error?.let {
            Text("Greška: $it", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fighters, key = { it.id ?: "" }) { fighter ->
                val isFavorite = favorites.any { it.id == fighter.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val json = URLEncoder.encode(Gson().toJson(fighter), "UTF-8")
                            navController.navigate("${NavRoutes.FighterDetail}/$json")
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fighter.name ?: "Nepoznato ime",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Divizija: ${fighter.division?.name?.takeIf { !it.isNullOrBlank() } ?: "Nepoznato"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Rekord: " +
                                        "${fighter.stats?.wins ?: 0}-" +
                                        "${fighter.stats?.losses ?: 0}-" +
                                        "${fighter.stats?.draws ?: 0}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        IconButton(
                            onClick = {
                                Log.d(
                                    "FAVORITE",
                                    "${if (isFavorite) "Uklanjam iz favorita" else "Dodajem u favorite"}: ${fighter.name} (ID: ${fighter.id})"
                                )
                                fighterViewModel.toggleFavorite(fighter)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorit"
                            )
                        }
                    }
                }
            }
        }

    }
}



