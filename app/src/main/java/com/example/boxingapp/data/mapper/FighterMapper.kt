package com.example.boxingapp.data.mapper

import com.example.boxingapp.data.entity.FighterEntity
import com.example.boxingapp.data.entity.FighterWithDivision
import com.example.boxingapp.data.mapper.toModel as divisionEntityToModel
import com.example.boxingapp.data.model.Division
import com.example.boxingapp.data.model.Fighter
import com.example.boxingapp.data.model.Stats

object FighterMapper {


    fun toEntity(fighter: Fighter): FighterEntity {
        val stats = fighter.stats ?: Stats(0, 0, 0, 0)

        return FighterEntity(
            id = fighter.id ?: "",
            name = (fighter.name ?: "").ifBlank { "Nepoznato ime" },
            nationality = (fighter.nationality ?: "").ifBlank { "Nepoznata nacija" },
            gender = fighter.gender ?: "Nepoznat",
            divisionId = fighter.division?.id ?: "Nepoznata",
            wins = stats.wins ?: 0,
            losses = stats.losses ?: 0,
            draws = stats.draws ?: 0,
            totalBouts = stats.total_bouts ?: 0,
            isFavorite = fighter.isFavorite
        )
    }


    fun toModel(entity: FighterEntity): Fighter {
        return Fighter(
            id = entity.id,
            name = entity.name,
            isFavorite = entity.isFavorite,
            nationality = entity.nationality,
            gender = entity.gender?: "Nepoznat",
            division = Division(
                id = entity.divisionId,
                name = "Nepoznata divizija",
                weight_kg = 0.0,
                weight_lb = 0.0
            ),
            stats = Stats(
                wins = entity.wins,
                losses = entity.losses,
                draws = entity.draws,
                total_bouts = entity.totalBouts
            )
        )
    }

    fun toModel(fighterWithDivision: FighterWithDivision): Fighter {
        val entity = fighterWithDivision.fighter
        val division = fighterWithDivision.division?.let { divisionEntityToModel(it) } ?: Division(
            id = entity.divisionId,
            name = "Nepoznata divizija",
            weight_kg = 0.0,
            weight_lb = 0.0
        )

        return Fighter(
            id = entity.id,
            name = entity.name,
            isFavorite = entity.isFavorite,
            nationality = entity.nationality,
            gender = entity.gender ?: "Nepoznat",
            division = division,
            stats = Stats(
                wins = entity.wins,
                losses = entity.losses,
                draws = entity.draws,
                total_bouts = entity.totalBouts
            )
        )
    }
}
