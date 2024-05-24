package com.example.chickcheckapp.presentation.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.chickcheckapp.databinding.FragmentCameraxBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Future

class CameraXFragment : Fragment() {
    private var _binding : FragmentCameraxBinding? = null
    private val binding get() = _binding!!
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var imageCapture: ImageCapture? = null
    private var currentImage: Uri? = null
    private var isBackCamera: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraxBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkPermission(Manifest.permission.CAMERA)) {
            startCamera()
            binding.btnCapture.setOnClickListener {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    captureImage()
                }else{
                    val listOfPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    takePictureLauncher.launch(listOfPermissions)

                }
            }
            binding.btnGallery.setOnClickListener {
                launchGalleryIntent.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            binding.btnSwitchCamera.setOnClickListener {
                isBackCamera = !isBackCamera
                startCamera()
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector: androidx.camera.core.CameraSelector =
            androidx.camera.core.CameraSelector.Builder()
                .requireLensFacing(if(isBackCamera)androidx.camera.core.CameraSelector.LENS_FACING_BACK else androidx.camera.core.CameraSelector.LENS_FACING_FRONT)
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
            filename,".jpg",requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(destination).build()
        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    currentImage = outputFileResults.savedUri
                    val action = CameraXFragmentDirections.actionCameraXFragmentToAnalysisFragment(currentImage.toString())
                    view?.findNavController()?.navigate(action)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast(exception.message.toString())
                }

            })
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture?.addListener({
            val cameraProvider = cameraProviderFuture?.get()
            cameraProvider?.unbindAll()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

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
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val launchGalleryIntent = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){uri->
        if(uri!= null){
            val action = CameraXFragmentDirections.actionCameraXFragmentToAnalysisFragment(uri.toString())
            view?.findNavController()?.navigate(action)
        }else{
            showToast("No image selected")
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}