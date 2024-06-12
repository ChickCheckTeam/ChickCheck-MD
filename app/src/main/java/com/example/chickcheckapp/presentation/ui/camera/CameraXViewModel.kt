package com.example.chickcheckapp.presentation.ui.camera

import androidx.lifecycle.ViewModel
import com.example.chickcheckapp.data.ChickCheckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CameraXViewModel @Inject constructor(
    private val repository: ChickCheckRepository
) : ViewModel() {
    fun postDetection(file: File,token:String) = repository.postDetection(file,token)
    fun getSession() = repository.getSession()
}