package com.example.chickcheckapp.presentation.ui.result

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.chickcheckapp.R
import com.example.chickcheckapp.utils.Result
import com.example.chickcheckapp.data.local.model.ArticleContent
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.databinding.FragmentResultBinding
import com.example.chickcheckapp.presentation.adapter.ArticleContentListAdapter
import com.example.chickcheckapp.presentation.adapter.NearbyPlacesListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val args: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    private var isPlacesExpanded = false
    private var fusedLocationClient: FusedLocationProviderClient? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = args.uriImage
        val data = args.data
        Log.d(TAG,"$uri , ${uri.isEmpty()}")
        setContent(data, uri)
        binding.btnBack.setOnClickListener {
            if(uri.isEmpty()){
                findNavController().navigate(R.id.action_article_fragment_to_navigation_article)
            }else{
                findNavController().navigate(R.id.action_resultFragment_to_cameraXFragment)
            }
        }
        binding.ivDownArrow.setOnClickListener{
            isPlacesExpanded = !isPlacesExpanded
            binding.ivDownArrow.animate().rotation(if (isPlacesExpanded) 180f else 0f)
                .setDuration(300).start()
            if (isPlacesExpanded) {
                binding.rvNearbyPlaces.apply {
                    visibility = View.VISIBLE
                    alpha = 0f
                    animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null)
                }
            } else {
                binding.rvNearbyPlaces.apply {
                    animate()
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }
                        })
                }
            }
        }
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (binding.collapsingToolbar.height + verticalOffset < 2 * ViewCompat.getMinimumHeight(binding.collapsingToolbar)) {
                binding.tvToolbarTitle.visibility = View.VISIBLE
            } else {
                binding.tvToolbarTitle.visibility = View.GONE
            }
        })
        if(uri.isNotEmpty() && data.title.lowercase() != "healthy"){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                getMyLocation()
            } else {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun setNearbyPlaces(location: Location) {
        val adapter = NearbyPlacesListAdapter(location)
        val layoutManager = LinearLayoutManager(requireContext())
        val itemDecorations = DividerItemDecoration(activity, layoutManager.orientation)
        binding.rvNearbyPlaces.addItemDecoration(itemDecorations)
        binding.rvNearbyPlaces.layoutManager = layoutManager
        binding.rvNearbyPlaces.adapter = adapter
        viewModel.findNearbyPlaces(location).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(result.error)
                    Log.d(TAG, "error: ${result.error}")
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(result.data.places)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(TAG, "getMyLocation: ${location.latitude}, ${location.longitude}")
                    setNearbyPlaces(location)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermission.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    }

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        } else {
            binding.layoutPlaces.visibility = View.GONE
        }

    }

    private fun setContent(data: DataItem, uri: String) {
        if (uri.isEmpty()) {
            binding.tvYourPhoto.visibility = View.GONE
            binding.tvYourPhoto.visibility = View.GONE
            binding.layoutPlaces.visibility = View.GONE
        } else {
            binding.ivYourPhoto.setImageURI(uri.toUri())
        }
        binding.ivHeroImage.setImageResource(R.drawable.salmonella)
        binding.tvDesiaseName.text = data.title
        binding.tvToolbarTitle.text = data.title
        val listContent = listOf(
            ArticleContent(
                icon = R.drawable.information,
                title = "General Information",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eleifend ipsum sapien. Duis vulputate ac nisl convallis dignissim. Sed et tincidunt nisi. Integer auctor egestas libero, eu euismod justo congue id. Donec porttitor porttitor leo, non iaculis diam viverra nec. Pellentesque ullamcorper nisi est, sit amet auctor nisl pulvinar eget."
            ),
            ArticleContent(
                icon = R.drawable.treatment,
                title = "Treatment",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eleifend ipsum sapien. Duis vulputate ac nisl convallis dignissim. Sed et tincidunt nisi. Integer auctor egestas libero, eu euismod justo congue id. Donec porttitor porttitor leo, non iaculis diam viverra nec. Pellentesque ullamcorper nisi est, sit amet auctor nisl pulvinar eget."
            ),
            ArticleContent(
                icon = R.drawable.prevention,
                title = "Prevention",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eleifend ipsum sapien. Duis vulputate ac nisl convallis dignissim. Sed et tincidunt nisi. Integer auctor egestas libero, eu euismod justo congue id. Donec porttitor porttitor leo, non iaculis diam viverra nec. Pellentesque ullamcorper nisi est, sit amet auctor nisl pulvinar eget."
            ),
            ArticleContent(
                icon = R.drawable.symptoms,
                title = "Symptoms",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eleifend ipsum sapien. Duis vulputate ac nisl convallis dignissim. Sed et tincidunt nisi. Integer auctor egestas libero, eu euismod justo congue id. Donec porttitor porttitor leo, non iaculis diam viverra nec. Pellentesque ullamcorper nisi est, sit amet auctor nisl pulvinar eget."
            ))
        binding.rvArticleContent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticleContent.adapter = ArticleContentListAdapter(listContent)
        if (data.title.lowercase() == "healthy") {
            setHealthy(data)
        }
    }

    private fun setHealthy(data: DataItem) {
//        binding.tvInformationTitle.text = "Ciri-Ciri"
//        binding.tvPreventionTitle.text = "Strategi untuk peternakan ayam"
//        binding.tvMedicationTitle.text = "Sumber Nutrisi"
//        binding.tvSymptomsTitle.text = "Kondisi kandang ayam sehat"
//        binding.tvLocationTitle.visibility = View.GONE
//        binding.tvLocationSubtitle.visibility = View.GONE
//        binding.rvNearbyPlaces.visibility = View.GONE
    }

    companion object {
        const val TAG = "ResultFragment"
    }
}