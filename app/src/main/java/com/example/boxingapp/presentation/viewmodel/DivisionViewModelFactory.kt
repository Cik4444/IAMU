package com.example.boxingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxingapp.data.database.AppDatabase
import com.example.boxingapp.data.repository.DivisionRepository

class DivisionViewModelFactory(
    private val repository: DivisionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DivisionViewModel::class.java)) {
            return DivisionViewModel(repository) as T
        }
        throw IllegalArgumentException("Nepoznata klasa ViewModela")
    }
}





