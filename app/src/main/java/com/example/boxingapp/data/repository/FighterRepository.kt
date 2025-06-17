package com.example.boxingapp.data.repository

import android.util.Log
import com.example.boxingapp.data.api.BoxingApiService
import com.example.boxingapp.data.dao.DivisionDao
import com.example.boxingapp.data.dao.FighterDao
import com.example.boxingapp.data.entity.FighterEntity
import com.example.boxingapp.data.mapper.FighterMapper
import com.example.boxingapp.data.mapper.toEntity
import com.example.boxingapp.data.mapper.toModel
import com.example.boxingapp.data.entity.FighterWithDivision
import com.example.boxingapp.data.model.Fighter
import com.example.boxingapp.util.sanitizeFighter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FighterRepository(
    private val apiService: BoxingApiService,
    private val fighterDao: FighterDao,
    private val divisionDao: DivisionDao
) {

    private suspend fun mapWithDivision(join: FighterWithDivision): Fighter {
        val base = FighterMapper.toModel(join)
        if (join.division != null) return base

        val division = divisionDao.getById(join.fighter.divisionId)
        return if (division != null) {
            base.copy(division = division.toModel())
        } else base
    }

    suspend fun getFighters(
        name: String,
        divisionId: String?,
        isConnected: Boolean = true
    ): List<Fighter> {
        if (!isConnected) {
            return getCachedFighters(name, divisionId)
        }

        return try {
            android.util.Log.d("FighterRepo", "getFighters query=$name division=$divisionId")

            // The API provides a single endpoint for listing fighters with an
            // optional name query. Retrofit handles URL encoding so we pass the
            // raw string directly.
            val response = apiService.getFighters(name)

            android.util.Log.d("FighterRepo", "API response code=${response.code()}")

            val body = response.body()
            if (response.isSuccessful && body != null) {
                val apiFighters = body
                val fighters = apiFighters.map { sanitizeFighter(it) }

                val fightersToSave = withContext(Dispatchers.IO) {
                    val favoriteIds = fighterDao.getFavorites().map { it.id }

                    val merged = fighters.map { fighter ->
                        fighter.copy(isFavorite = favoriteIds.contains(fighter.id))
                    }

                    val divisionsToSave = merged
                        .mapNotNull { it.division }
                        .distinctBy { it.id }

                    if (divisionsToSave.isNotEmpty()) {
                        divisionDao.insertAll(divisionsToSave.map { it.toEntity() })
                    }

                    fighterDao.insertAll(merged.map { FighterMapper.toEntity(it) })

                    merged
                }

                fightersToSave
            } else {
                android.util.Log.d(
                    "FighterRepo",
                    "API error code=${response.code()} body=${response.errorBody()?.string()}"
                )
                getCachedFighters(name, divisionId)
            }
        } catch (e: Exception) {
            android.util.Log.e("FighterRepo", "Network error: ${e.message}", e)
            getCachedFighters(name, divisionId)
        }
    }





    private suspend fun getCachedFighters(name: String, divisionId: String?): List<Fighter> {
        return withContext<List<Fighter>>(Dispatchers.IO) {
            val entities: List<FighterEntity> = fighterDao.searchFighters(name, divisionId)
            val divisionIds = entities.map { it.divisionId }.distinct()
            val divisions = divisionDao.getByIds(divisionIds).associateBy { it.id }
            entities.map { entity ->
                val base = FighterMapper.toModel(entity)
                val division = divisions[entity.divisionId]?.toModel()
                if (division != null) base.copy(division = division) else base
            }
        }
    }

    suspend fun toggleFavorite(fighterId: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            fighterDao.updateFavoriteStatus(fighterId, isFavorite)
        }
    }

    suspend fun getFavorites(): List<Fighter> {
        return withContext<List<Fighter>>(Dispatchers.IO) {
            val entities: List<FighterEntity> = fighterDao.getFavorites()
            val divisionIds = entities.map { it.divisionId }.distinct()
            val divisions = divisionDao.getByIds(divisionIds).associateBy { it.id }
            entities.map { entity ->
                val base = FighterMapper.toModel(entity)
                val division = divisions[entity.divisionId]?.toModel()
                if (division != null) base.copy(division = division) else base
            }
        }
    }


    fun getFavoritesFlow(): Flow<List<Fighter>> =
        fighterDao.getFavoritesFlow().map { entityList: List<FighterEntity> ->
            val divisionIds = entityList.map { it.divisionId }.distinct()
            val divisions = divisionDao.getByIds(divisionIds).associateBy { it.id }
            entityList.map { entity ->
                val base = FighterMapper.toModel(entity)
                val division = divisions[entity.divisionId]?.toModel()
                if (division != null) base.copy(division = division) else base
            }
        }






}
