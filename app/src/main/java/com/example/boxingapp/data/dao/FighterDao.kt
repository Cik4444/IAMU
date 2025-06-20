package com.example.boxingapp.data.dao

import androidx.room.*
import com.example.boxingapp.data.entity.FighterEntity
import com.example.boxingapp.data.entity.FighterWithDivision
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmSuppressWildcards

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

    @Transaction
    @Query(
        """
        SELECT f.*, d.id AS division_id, d.name AS division_name,
               d.weightKg AS division_weightKg, d.weightLb AS division_weightLb
        FROM fighters f
        LEFT JOIN divisions d ON f.divisionId = d.id
        WHERE f.isFavorite = 1
        """
    )
    suspend fun getFavoritesJoined(): List<FighterWithDivision>

    @Query("UPDATE fighters SET isFavorite = :isFavorite WHERE id = :fighterId")
    suspend fun updateFavoriteStatus(fighterId: String, isFavorite: Boolean)

    @Query("DELETE FROM fighters")
    suspend fun clearAll()

    @JvmSuppressWildcards
    @Query("SELECT * FROM fighters WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<FighterEntity>


    @Query("""
        SELECT * FROM fighters
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:divisionId IS NULL OR divisionId = :divisionId)
    """)
    suspend fun searchFighters(name: String?, divisionId: String?): List<FighterEntity>
  


    @Query(
        """
        SELECT f.*, d.id AS division_id, d.name AS division_name,
               d.weightKg AS division_weightKg, d.weightLb AS division_weightLb
        FROM fighters f
        LEFT JOIN divisions d ON f.divisionId = d.id
        WHERE (:name IS NULL OR f.name LIKE '%' || :name || '%')
          AND (:divisionId IS NULL OR f.divisionId = :divisionId)
        """
    )
    suspend fun searchFightersWithDivision(name: String?, divisionId: String?): List<FighterWithDivision>



    @Transaction
    @Query(
        """
        SELECT f.*, d.id AS division_id, d.name AS division_name,
               d.weightKg AS division_weightKg, d.weightLb AS division_weightLb
        FROM fighters f
        LEFT JOIN divisions d ON f.divisionId = d.id
        WHERE (:name IS NULL OR f.name LIKE '%' || :name || '%')
          AND (:divisionId IS NULL OR f.divisionId = :divisionId)
        """
    )
    suspend fun searchFightersJoined(name: String?, divisionId: String?): List<FighterWithDivision>

    @Query("SELECT * FROM fighters WHERE isFavorite = 1")
    fun getFavoritesFlow(): Flow<List<FighterEntity>>
 


    @Query(
        """
        SELECT f.*, d.id AS division_id, d.name AS division_name,
               d.weightKg AS division_weightKg, d.weightLb AS division_weightLb
        FROM fighters f
        LEFT JOIN divisions d ON f.divisionId = d.id
        WHERE f.isFavorite = 1
        """
    )
    fun getFavoritesFlowWithDivision(): Flow<List<FighterWithDivision>>



    @Transaction
    @Query(
        """
        SELECT f.*, d.id AS division_id, d.name AS division_name,
               d.weightKg AS division_weightKg, d.weightLb AS division_weightLb
        FROM fighters f
        LEFT JOIN divisions d ON f.divisionId = d.id
        WHERE f.isFavorite = 1
        """
    )
    fun getFavoritesFlowJoined(): Flow<List<FighterWithDivision>>


}
