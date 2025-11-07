package com.example.babel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.babel.data.repository.ExploreRepository
import com.example.babel.repository.LocationWeatherRepository

class ExploreViewModelFactory(
    private val locationRepo: LocationWeatherRepository,
    private val exploreRepo: ExploreRepository,
    private val apiKey: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExploreViewModel(locationRepo, exploreRepo, apiKey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
