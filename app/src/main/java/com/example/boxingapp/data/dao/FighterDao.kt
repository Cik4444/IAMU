package com.example.boxingapp.data.dao

import androidx.room.*
import com.example.boxingapp.data.entity.FighterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FighterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fighters: List<FighterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fighter: FighterEntity)

    @Query("SELECT * FROM fighters")
    fun getAll(): Flow<List<FighterEntity>>

    @Query("SELECT * FROM fighters WHERE divisionId = :divisionId")
    fun getByDivision(divisionId: String): Flow<List<FighterEntity>>

    @Query("SELECT * FROM fighters WHERE name LIKE '%' || :query || '%'")
    fun searchByName(query: String): Flow<List<FighterEntity>>

    @Query("SELECT * FROM fighters WHERE isFavorite = 1")
    suspend fun getFavorites(): List<FighterEntity>


    @Query("UPDATE fighters SET isFavorite = :isFavorite WHERE id = :fighterId")
    suspend fun updateFavoriteStatus(fighterId: String, isFavorite: Boolean)

    @Query("DELETE FROM fighters")
    suspend fun clearAll()


    @Query("""
        SELECT * FROM fighters
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:divisionId IS NULL OR divisionId = :divisionId)
    """)
    suspend fun searchFighters(name: String?, divisionId: String?): List<FighterEntity>

    @Query("SELECT * FROM fighters WHERE isFavorite = 1")
    fun getFavoritesFlow(): Flow<List<FighterEntity>>


}
