package com.example.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NasaViewModelFactory(private val cachingManager: CachingManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NasaViewModel::class.java)) {
            return NasaViewModel(cachingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
