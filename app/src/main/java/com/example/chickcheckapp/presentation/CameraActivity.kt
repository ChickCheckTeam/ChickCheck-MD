package com.example.chickcheckapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chickcheckapp.databinding.ActivityCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



}