package com.example.boxingapp.data.mapper

import com.example.boxingapp.data.entity.DivisionEntity
import com.example.boxingapp.data.model.Division

fun Division.toEntity(): DivisionEntity {
    require(!id.isNullOrBlank()) { "Division id je null ili prazan!" }
    require(!name.isNullOrBlank()) { "Division name je null ili prazan!" }
    return DivisionEntity(
        id = this.id,
        name = this.name,
        weightKg = this.weight_kg ?: 0.0,
        weightLb = this.weight_lb ?: 0.0
    )
}



fun DivisionEntity.toModel(): Division {
    return Division(
        id = this.id,
        name = this.name,
        weight_kg = this.weightKg,
        weight_lb = this.weightLb
    )
}
