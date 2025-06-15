package com.example.boxingapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fighters",
    foreignKeys = [
        ForeignKey(
            entity = DivisionEntity::class,
            parentColumns = ["id"],
            childColumns = ["divisionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FighterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nationality: String,
    val gender: String?,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val totalBouts: Int,
    val divisionId: String,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false
)
