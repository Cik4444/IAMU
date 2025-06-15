package com.example.boxingapp.data.repository

import com.example.boxingapp.data.dao.DivisionDao
import com.example.boxingapp.data.mapper.toEntity
import com.example.boxingapp.data.mapper.toModel
import com.example.boxingapp.data.model.Division
import com.example.boxingapp.data.remote.DivisionApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DivisionRepository(
    private val api: DivisionApiService,
    private val dao: DivisionDao
) {
    suspend fun getAllDivisions(): Result<List<Division>> {
        return try {
            val response = api.getAllDivisions()
            if (response.isSuccessful) {
                val divisions = response.body() ?: emptyList()
                dao.insertAll(divisions.map { it.toEntity() })
                Result.success(divisions)
            } else {
                println("🌐 API greška: ${response.code()}")
                getLocalDivisions()
            }
        } catch (e: Exception) {
            println("📴 Offline fallback: ${e.message}")
            getLocalDivisions()
        }
    }

    private suspend fun getLocalDivisions(): Result<List<Division>> {
        return withContext(Dispatchers.IO) {
            val divisions = dao.getAll().first().map { it.toModel() }
            Result.success(divisions)
        }
    }


    suspend fun getDivisionById(id: String): Result<Division> {
        return try {
            val response = api.getDivisionById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    dao.insertAll(listOf(it.toEntity()))
                    Result.success(it)
                } ?: Result.failure(Exception("Prazan odgovor"))
            } else {
                getLocalDivisionById(id)
            }
        } catch (e: Exception) {
            getLocalDivisionById(id)
        }
    }

    private suspend fun getLocalDivisionById(id: String): Result<Division> {
        return withContext(Dispatchers.IO) {
            val local = dao.getById(id)
            local?.let { Result.success(it.toModel()) }
                ?: Result.failure(Exception("Divizija nije pronađena lokalno"))
        }
    }
}