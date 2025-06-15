package com.example.boxingapp.data.dao

import androidx.room.*
import com.example.boxingapp.data.entity.DivisionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DivisionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(divisions: List<DivisionEntity>)

    @Query("SELECT * FROM divisions")
    fun getAll(): Flow<List<DivisionEntity>>

    @Query("DELETE FROM divisions")
    suspend fun clearAll()

    @Query("SELECT * FROM divisions WHERE id = :id")
    suspend fun getById(id: String): DivisionEntity?

    @Query("SELECT id FROM divisions")
    suspend fun getAllIds(): List<String>

}