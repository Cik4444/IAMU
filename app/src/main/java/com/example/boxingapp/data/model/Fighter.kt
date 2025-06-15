package com.example.boxingapp.data.model

data class Fighter(
    val id: String?,
    val name: String?,
    val nationality: String?,
    val stats: Stats?,
    val gender: String?,
    val division: Division?,
    val isFavorite: Boolean = false
)

