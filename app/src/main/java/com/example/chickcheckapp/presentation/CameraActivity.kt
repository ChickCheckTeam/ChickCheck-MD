package com.example.chickcheckapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.chickcheckapp.databinding.PreviewLayoutBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.ArrayList

class CameraActivity : AppCompatActivity() {
    private var _binding: PreviewLayoutBinding? = null
    private val binding get() = _binding!!
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var imageCapture: ImageCapture? = null
    private var currentImage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PreviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (checkPermission(Manifest.permission.CAMERA)) {
            startCamera()
            binding.btnCapture.setOnClickListener {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    captureImage()
                }else{
                    val listOfPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    takePictureLauncher.launch(listOfPermissions)

                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector: androidx.camera.core.CameraSelector =
            androidx.camera.core.CameraSelector.Builder()
                .requireLensFacing(androidx.camera.core.CameraSelector.LENS_FACING_BACK)
                .build()
        preview.setSurfaceProvider(binding.pvCamera.surfaceProvider)
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .build()
        val imageAnalyzer = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .setTargetRotation(binding.pvCamera.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(binding.pvCamera.display.rotation)
            .build()

        cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer, imageCapture)
    }

    private fun captureImage() {
        val filename = "${System.currentTimeMillis()}"
        val destination = File.createTempFile(
            filename,".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(destination).build()
        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    currentImage = outputFileResults.savedUri
                    binding.ivCapturedImage.setImageURI(currentImage)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast(exception.message.toString())
                }

            })
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture?.addListener({
            val cameraProvider = cameraProviderFuture?.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showToast("Permission required")
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = result.all { it.value }

            if (allGranted) {
                captureImage()
            } else {
                showToast("Storage permission Required")
            }
        }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}