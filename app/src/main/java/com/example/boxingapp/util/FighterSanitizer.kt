package com.example.boxingapp.util

import com.example.boxingapp.data.model.Division
import com.example.boxingapp.data.model.Fighter
import com.example.boxingapp.data.model.Stats

fun sanitizeFighter(fighter: Fighter?): Fighter {
    if (fighter == null) {
        return Fighter(
            id = "nepoznat_id",
            name = "Nepoznato ime",
            nationality = "Nepoznata nacija",
            gender = "Nepoznat",
            division = Division("nepoznata", "Nepoznata", 0.0, 0.0),
            stats = Stats(0, 0, 0, 0)
        )
    }

    return Fighter(
        id = fighter.id ?: "nepoznat_id",
        name = fighter.name?.takeIf { it.isNotBlank() } ?: "Nepoznato ime",
        nationality = fighter.nationality?.takeIf { it.isNotBlank() } ?: "Nepoznata nacija",
        gender = fighter.gender ?: "Nepoznat",
        division = fighter.division ?: Division("nepoznata", "Nepoznata", 0.0, 0.0),
        stats = fighter.stats ?: Stats(0, 0, 0, 0)
    )
}
