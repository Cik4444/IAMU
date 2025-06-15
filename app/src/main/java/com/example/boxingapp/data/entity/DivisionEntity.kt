package com.example.boxingapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "divisions")
data class DivisionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val weightKg: Double,
    val weightLb: Double
)