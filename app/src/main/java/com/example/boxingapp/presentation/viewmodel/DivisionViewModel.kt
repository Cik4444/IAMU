package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxingapp.data.model.Division
import com.example.boxingapp.data.repository.DivisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DivisionViewModel(
    private val repository: DivisionRepository
) : ViewModel() {

    private val _divisions = MutableStateFlow<List<Division>>(emptyList())
    val divisions: StateFlow<List<Division>> = _divisions.asStateFlow()

    private val _division = MutableStateFlow<Division?>(null)
    val division: StateFlow<Division?> = _division.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchAllDivisions()
    }

    fun fetchAllDivisions() {
        viewModelScope.launch {
            val result = repository.getAllDivisions()
            result.onSuccess {
                _divisions.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.localizedMessage ?: "Nepoznata greška"
            }
        }
    }

    fun fetchDivision(id: String) {
        viewModelScope.launch {
            val result = repository.getDivisionById(id)
            result.onSuccess {
                _division.value = it
                _error.value = null
            }.onFailure {
                _error.value = it.localizedMessage ?: "Nepoznata greška"
            }
        }
    }

    fun searchByDivisionId(id: String) {
        viewModelScope.launch {
            try {
                val allDivisions = _divisions.value
                _divisions.value = if (id.isBlank()) {
                    repository.getAllDivisions().getOrDefault(emptyList())
                } else {
                    allDivisions.filter { it.id == id.trim() }
                }
            } catch (e: Exception) {
                _error.value = "Greška: ${e.message}"
            }
        }
    }
}
