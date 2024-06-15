package com.example.chickcheckapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.databinding.ActivityCameraBinding
import com.example.chickcheckapp.presentation.ui.camera.CameraXFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_camerax)

        val articleData = intent.getParcelableExtra<ArticleData>("ARTICLE_DATA")
        val imageUrl = intent.getStringExtra("IMAGE_URL")

        if (articleData != null && imageUrl != null) {
            // Navigate to result fragment with article data and image URL
            val action = CameraXFragmentDirections.actionCameraXFragmentToResultFragment(
                imageUrl,
                articleData
            )
            navController.navigate(action)
        }

    }
}