package com.example.boxingapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Represents a Fighter along with its associated Division loaded from Room.
 */
data class FighterWithDivision(
    @Embedded val fighter: FighterEntity,
    @Relation(
        parentColumn = "divisionId",
        entityColumn = "id"
    )
    val division: DivisionEntity?
)
