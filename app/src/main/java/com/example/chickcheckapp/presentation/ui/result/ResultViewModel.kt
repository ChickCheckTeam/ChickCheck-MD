package com.example.chickcheckapp.presentation.ui.result

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.chickcheckapp.data.ChickCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(private val chickCheckRepository: ChickCheckRepository) : ViewModel() {
    fun findNearbyPlaces(location: Location) = chickCheckRepository.findNearbyPlaces(location)
}