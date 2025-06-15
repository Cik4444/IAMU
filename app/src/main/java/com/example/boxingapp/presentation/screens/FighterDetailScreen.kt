package com.example.boxingapp.presentation.screens

import android.R.style
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.boxingapp.data.model.Fighter

@Composable
fun FighterDetailScreen(fighter: Fighter) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = fighter.name ?: "Nepoznato",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(label = "Nacionalnost", value = fighter.nationality ?: "Nepoznata")
        DetailRow(label = "Spol", value = fighter.gender ?: "Nepoznat")

        DetailRow(
            label = "Divizija",
            value = fighter.division?.name?.takeIf { !it.isNullOrBlank() } ?: "Nepoznata"
        )

        DetailRow(
            label = "Težina",
            value = fighter.division?.weight_kg?.takeIf { it != null && it > 0 }?.let { "$it kg" } ?: "Nepoznata"
        )

        DetailRow(
            label = "Rekord",
            value = buildString {
                val wins = fighter.stats?.wins ?: 0
                val losses = fighter.stats?.losses ?: 0
                val draws = fighter.stats?.draws ?: 0
                append("$wins pobjeda, $losses poraza, $draws neodlučeno")
            }
        )

        DetailRow(
            label = "Ukupno borbi",
            value = fighter.stats?.total_bouts?.toString() ?: "Nepoznato"
        )
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
