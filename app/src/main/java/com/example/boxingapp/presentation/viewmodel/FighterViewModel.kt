package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxingapp.data.model.Fighter
import com.example.boxingapp.data.repository.FighterRepository
import com.example.boxingapp.util.isInternetAvailable
import android.content.Context
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FighterViewModel(
    private val repository: FighterRepository,
    private val context: Context,
    private val loadOnInit: Boolean = true
) : ViewModel() {

    val favorites: StateFlow<List<Fighter>> = repository.getFavoritesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedDivisionId = MutableStateFlow<String?>(null)
    val selectedDivisionId: StateFlow<String?> = _selectedDivisionId

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _fighters = MutableStateFlow<List<Fighter>>(emptyList())
    val filteredFighters: StateFlow<List<Fighter>> = combine(
        _fighters, _selectedDivisionId, _query
    ) { fighters, divisionId, query ->
        fighters.filter { fighter ->
            val divisionIdMatches = divisionId.isNullOrEmpty() || fighter.division?.id == divisionId
            val nameMatches = query.isEmpty() || (fighter.name ?: "").contains(query, ignoreCase = true)
            divisionIdMatches && nameMatches
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        if (loadOnInit) {
            reloadFighters()
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        reloadFighters()
    }

    fun onDivisionSelected(id: String?) {
        _selectedDivisionId.value = id
        reloadFighters()
    }

    fun isFavorite(fighter: Fighter): Boolean {
        return favorites.value.any { it.id == fighter.id }
    }

    fun toggleFavorite(fighter: Fighter) {
        val currentlyFavorite = favorites.value.any { it.id == fighter.id }
        viewModelScope.launch {
            repository.toggleFavorite(fighter.id ?: "", !currentlyFavorite)
        }
    }

   /* fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                println("❌ Greška pri dohvaćanju favorita: ${e.message}")
            }
        }
    }*/

    private fun reloadFighters() {
        viewModelScope.launch {
            try {
                val fighters = repository.getFighters(
                    name = _query.value.trim(),
                    divisionId = _selectedDivisionId.value,
                    isConnected = isInternetAvailable(context)
                )
                _fighters.value = fighters
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Nepoznata greška"
                println("❌ Greška pri dohvaćanju boraca: ${e.message}")
            }
        }
    }
}
