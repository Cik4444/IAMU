package com.example.boxingapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * Represents a Fighter together with its Division from a JOIN query.
 */
data class FighterWithDivision(
    @Embedded val fighter: FighterEntity,
    @Embedded(prefix = "division_") val division: DivisionFields?,
)

data class DivisionFields(
    @ColumnInfo(name = "division_id") val id: String?,
    @ColumnInfo(name = "division_name") val name: String?,
    @ColumnInfo(name = "division_weightKg") val weightKg: Double?,
    @ColumnInfo(name = "division_weightLb") val weightLb: Double?,
)
