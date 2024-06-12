package com.example.chickcheckapp.presentation.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.data.remote.response.DetectionResultResponse
import com.example.chickcheckapp.databinding.FragmentCameraxBinding
import com.example.chickcheckapp.presentation.ui.result.ResultFragment
import com.example.chickcheckapp.utils.Result
import com.example.chickcheckapp.utils.Utils
import com.example.chickcheckapp.utils.Utils.reduceFileSize
import com.example.chickcheckapp.utils.Utils.rotateImage
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Future

@AndroidEntryPoint
class CameraXFragment : Fragment() {
    private var _binding: FragmentCameraxBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CameraXViewModel by viewModels()
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var imageCapture: ImageCapture? = null
    private var token: String? = null
    private var isBackCamera: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.getSession().flowWithLifecycle(lifecycle).collect { user ->
                token = user.token
            }
        }
        if (checkPermission(Manifest.permission.CAMERA)) {
            startCamera()

        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()
        val cameraSelector: androidx.camera.core.CameraSelector =
            androidx.camera.core.CameraSelector.Builder()
                .requireLensFacing(if (isBackCamera) androidx.camera.core.CameraSelector.LENS_FACING_BACK else androidx.camera.core.CameraSelector.LENS_FACING_FRONT)
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
        val destination = Utils.createCustomTempFile(requireContext())
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(destination).build()
        binding.progressBar.visibility = View.VISIBLE
        imageCapture?.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(destination)
                    rotateImage(savedUri.path!!, binding.pvCamera)
                    sendImage(savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast(exception.message.toString())
                }

            })
    }

    private val orientationEventListener by lazy {
        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }

    private fun sendImage(uri: Uri) {
        val file = Utils.uriToFile(uri, requireContext()).reduceFileSize()

        viewModel.postDetection(file, token!!).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loadingBackground.visibility = View.VISIBLE
                    binding.tvScanLoadingText.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingBackground.visibility = View.GONE
                    binding.tvScanLoadingText.visibility = View.GONE
                    showToast(result.error)
                    Log.d(ResultFragment.TAG, "error: ${result.error}")
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingBackground.visibility = View.GONE
                    binding.tvScanLoadingText.visibility = View.GONE
                    val article : ArticleData = result.data.article
                    val action =
                        CameraXFragmentDirections.actionCameraXFragmentToResultFragment(
                            uri.toString(),
                            article
                        )
                    view?.findNavController()?.navigate(action)
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        binding.btnCapture.setOnClickListener {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                captureImage()
            } else {
                val listOfPermissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
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
    ) { uri ->
        if (uri != null) {
            sendImage(uri)
        } else {
            showToast("No image selected")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}