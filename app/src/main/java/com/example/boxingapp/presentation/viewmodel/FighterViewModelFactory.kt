package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxingapp.data.repository.FighterRepository
import com.example.boxingapp.presentation.viewmodel.FighterViewModel

class FighterViewModelFactory(
    private val repository: FighterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FighterViewModel::class.java)) {
            return FighterViewModel(repository) as T
        }
        throw IllegalArgumentException("Nepoznata klasa ViewModela")
    }
}
