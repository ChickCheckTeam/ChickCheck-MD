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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chickcheckapp.R
import com.example.chickcheckapp.utils.Result
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.databinding.FragmentResultBinding
import com.example.chickcheckapp.presentation.adapter.NearbyPlacesListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val args: ResultFragmentArgs by navArgs()
    private val viewModel: ResultViewModel by viewModels()
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var isExpanded = arrayOf(false, false, false, false, false)

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
        setContent(data, uri)
        binding.btnBack.setOnClickListener {
            if (uri.isEmpty()) {
                findNavController().navigate(R.id.action_article_fragment_to_navigation_article)
            } else {
               requireActivity().finish()
            }
        }
        binding.btnScanAgain.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_cameraXFragment)
        }
        binding.ivDownArrowPlaces.setOnClickListener(this)
        binding.ivGeneralDownArrow.setOnClickListener(this)
        binding.ivIconTreatmentDownArrow.setOnClickListener(this)
        binding.ivIconPreventionDownArrow.setOnClickListener(this)
        binding.ivIconSymptomsDownArrow.setOnClickListener(this)

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (binding.collapsingToolbar.height + verticalOffset < 2 * ViewCompat.getMinimumHeight(
                    binding.collapsingToolbar
                )
            ) {
                binding.tvToolbarTitle.visibility = View.VISIBLE
            } else {
                binding.tvToolbarTitle.visibility = View.GONE
            }
        })
        if (uri.isNotEmpty() && data.title.lowercase() != "healthy") {
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
            hideNearbyPlaces()
        }

    }

    private fun setContent(data: DataItem, uri: String) {
        if (uri.isEmpty()) {
            binding.tvYourPhoto.visibility = View.GONE
            binding.tvYourPhoto.visibility = View.GONE
            binding.btnScanAgain.visibility = View.GONE
            hideNearbyPlaces()
        } else {
            binding.ivYourPhoto.setImageURI(uri.toUri())
        }
        binding.ivHeroImage.setImageResource(R.drawable.salmonella)
        binding.tvDesiaseName.text = data.title
        binding.tvToolbarTitle.text = data.title

        if (data.title.lowercase() == "healthy") {
            setHealthy(data)
        }
    }

    private fun setHealthy(data: DataItem) {
        binding.tvGeneralTitle.text = "Ciri-Ciri"
        binding.tvPreventionTitle.text = "Strategi untuk peternakan ayam"
        binding.ivIconPrevention.setImageResource(R.drawable.strategy)
        binding.tvTreatmentTitle.text = "Sumber Nutrisi"
        binding.ivIconTreatment.setImageResource(R.drawable.nutrient)
        binding.tvSymptomsTitle.text = "Kondisi kandang ayam sehat"
        binding.ivIconSymptoms.setImageResource(R.drawable.condition)
        hideNearbyPlaces()
    }

    private fun hideNearbyPlaces(){
        binding.tvLocationTitle.visibility = View.GONE
        binding.locationContent.visibility = View.GONE
        binding.ivIconPlaces.visibility = View.GONE
        binding.ivDownArrowPlaces.visibility = View.GONE
        binding.itemDividerPlace.visibility = View.GONE
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_general_down_arrow -> {
                isExpanded[0] = !isExpanded[0]
                binding.ivGeneralDownArrow.animate().rotation(if (isExpanded[0]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(0, binding.generalContent)
            }

            R.id.iv_icon_treatment_down_arrow -> {
                isExpanded[1] = !isExpanded[1]
                binding.ivIconTreatmentDownArrow.animate()
                    .rotation(if (isExpanded[1]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(1, binding.treatmentContent)
            }

            R.id.iv_icon_prevention_down_arrow -> {
                isExpanded[2] = !isExpanded[2]
                binding.ivIconPreventionDownArrow.animate()
                    .rotation(if (isExpanded[2]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(2, binding.preventionContent)
            }

            R.id.iv_icon_symptoms_down_arrow -> {
                isExpanded[3] = !isExpanded[3]
                binding.ivIconSymptomsDownArrow.animate()
                    .rotation(if (isExpanded[3]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(3, binding.symptomsContent)
            }

            R.id.iv_down_arrow_places -> {
                isExpanded[4] = !isExpanded[4]
                binding.ivDownArrowPlaces.animate().rotation(if (isExpanded[4]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(4, binding.locationContent)
            }
        }
    }

    private fun setSubContentVisible(index: Int, view: ConstraintLayout) {
        if (isExpanded[index]) {
            view.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null)
            }
        } else {
            view.apply {
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

    companion object {
        const val TAG = "ResultFragment"
    }

}