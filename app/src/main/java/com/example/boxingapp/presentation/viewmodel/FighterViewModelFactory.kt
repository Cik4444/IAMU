package com.example.boxingapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boxingapp.data.repository.FighterRepository
import android.content.Context
import com.example.boxingapp.presentation.viewmodel.FighterViewModel

class FighterViewModelFactory(
    private val repository: FighterRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FighterViewModel::class.java)) {
            return FighterViewModel(repository, context.applicationContext) as T
        }
        throw IllegalArgumentException("Nepoznata klasa ViewModela")
    }
}
