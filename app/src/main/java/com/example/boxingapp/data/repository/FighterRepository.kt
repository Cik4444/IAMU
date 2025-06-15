package com.example.boxingapp.data.repository

import com.example.boxingapp.data.api.BoxingApiService
import com.example.boxingapp.data.dao.DivisionDao
import com.example.boxingapp.data.dao.FighterDao
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

    suspend fun getFighters(name: String, divisionId: String?): List<Fighter> {
        return try {
            val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
            val response = apiService.getFighters(encodedName)

            if (response.isSuccessful) {
                val apiFighters = response.body() ?: emptyList()
                val fighters = apiFighters.map { sanitizeFighter(it) }

                val favoriteIds = fighterDao.getFavorites().map { it.id }

                val fightersToSave = fighters.map { fighter ->
                    fighter.copy(isFavorite = favoriteIds.contains(fighter.id))
                }

                val divisionsToSave = fightersToSave
                    .mapNotNull { it.division }
                    .distinctBy { it.id }

                if (divisionsToSave.isNotEmpty()) {
                    divisionDao.insertAll(divisionsToSave.map { it.toEntity() })
                }

                fighterDao.insertAll(fightersToSave.map { FighterMapper.toEntity(it) })

                fightersToSave
            } else {
                println("API ERROR ${response.code()} ${response.errorBody()?.string()}")
                getCachedFighters(name, divisionId)
            }
        } catch (e: Exception) {
            println("❌ Network greška: ${e.message}")
            getCachedFighters(name, divisionId)
        }
    }





    private suspend fun getCachedFighters(name: String, divisionId: String?): List<Fighter> {
        return withContext(Dispatchers.IO) {
            fighterDao.searchFightersJoined(name, divisionId)
                .map { mapWithDivision(it) }
        }
    }

    suspend fun toggleFavorite(fighterId: String, isFavorite: Boolean) {
        fighterDao.updateFavoriteStatus(fighterId, isFavorite)
    }

    suspend fun getFavorites(): List<Fighter> {
        return fighterDao.getFavoritesJoined().map { mapWithDivision(it) }
    }


    fun getFavoritesFlow(): Flow<List<Fighter>> =
        fighterDao.getFavoritesFlowJoined().map { list ->
            list.map { mapWithDivision(it) }
        }






}
