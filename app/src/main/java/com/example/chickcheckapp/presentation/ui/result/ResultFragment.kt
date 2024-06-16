package com.example.chickcheckapp.presentation.ui.result

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.databinding.FragmentResultBinding
import com.example.chickcheckapp.presentation.adapter.NearbyPlacesListAdapter
import com.example.chickcheckapp.utils.Disease
import com.example.chickcheckapp.utils.Result
import com.example.chickcheckapp.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
        val article = args.article
        setContent(article, uri)
        binding.btnBack.setOnClickListener {
            if (uri.isEmpty()) {
                findNavController().navigate(R.id.action_article_fragment_to_navigation_article)
            } else {
                requireActivity().finish()
            }
        }
        binding.btnScanAgain.setOnClickListener {
            binding.locationContent.visibility = View.GONE
            findNavController().navigate(R.id.action_resultFragment_to_cameraXFragment)
        }
        binding.ivDownArrowPlaces.setOnClickListener(this)
        binding.ivGeneralDownArrow.setOnClickListener(this)
        binding.ivIconTreatmentDownArrow.setOnClickListener(this)
        binding.ivIconPreventionDownArrow.setOnClickListener(this)
        binding.ivIconSymptomsDownArrow.setOnClickListener(this)
        binding.tvGeneralTitle.setOnClickListener(this)
        binding.tvLocationTitle.setOnClickListener(this)
        binding.tvTreatmentTitle.setOnClickListener(this)
        binding.tvPreventionTitle.setOnClickListener(this)
        binding.tvSymptomsTitle.setOnClickListener(this)

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
        if (uri.isNotEmpty() && article.title.lowercase() != "healthy") {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (isLocationEnabled()) {
                    getMyLocation()
                } else {
                    Utils.dialogAlertBuilder(
                        requireContext(),
                        "Turn on the location",
                        "Would you like to turn on the location to see nearby veterinarian?",
                     {
                        promptEnableLocation()
                    },{
                        hideNearbyPlaces()
                    }).create().show()
                }
            } else {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    private fun setNearbyPlaces(location: Location) {
        val adapter = NearbyPlacesListAdapter(location)
        val layoutManager = LinearLayoutManager(requireContext())
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

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
            numUpdates = 1
        }
        binding.progressBar.visibility = View.VISIBLE
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location: Location? = locationResult.lastLocation
                        if (location != null) {
                            Log.d(
                                TAG,
                                "requestNewLocationData: ${location.latitude}, ${location.longitude}"
                            )
                            setNearbyPlaces(location)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.location_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                Looper.myLooper()!!
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun promptEnableLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        getMyLocation()
        startActivity(intent)

    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(TAG, "getMyLocation: ${location.latitude}, ${location.longitude}")
                    setNearbyPlaces(location)
                } else {
                    requestNewLocationData()
                }
            }?.addOnFailureListener { e ->
                Log.e(TAG, "Error getting location: ${e.localizedMessage}")
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

    private fun setContent(article: ArticleData, uri: String) {
        Log.d(TAG, "setContent: $uri")
        if (uri.isEmpty()) {
            binding.tvYourPhoto.visibility = View.GONE
            binding.tvYourPhoto.visibility = View.GONE
            binding.btnScanAgain.visibility = View.GONE
            showToast("TEs")
            hideNearbyPlaces()
        } else {
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.ivYourPhoto)
        }
        binding.ivHeroImage.setImageResource(R.drawable.salmonella)
        binding.tvDesiaseName.text = article.title
        binding.tvToolbarTitle.text = article.title
        val content = Utils.parseJsonToDisease(article.content)
        setSubContent(content)
        when (article.title.lowercase()) {
            "healthy" -> {
                setHealthy()
            }

            "new castle disease" -> {
                binding.ivHeroImage.setImageResource(R.drawable.newcastle)

            }

            "salmonellosis" -> {
                binding.ivHeroImage.setImageResource(R.drawable.salmonella)

            }

            "coccidiosis" -> {
                binding.ivHeroImage.setImageResource(R.drawable.coccidiosis)
            }

        }
    }

    private fun setHealthy() {
        binding.ivHeroImage.setImageResource(R.drawable.healthy)
        binding.tvGeneralTitle.text = "Ciri-Ciri"
        binding.tvCause.visibility = View.GONE
        binding.tvPreventionTitle.text = "Strategi untuk peternakan ayam"
        binding.ivIconPrevention.setImageResource(R.drawable.strategy)
        binding.tvTreatmentTitle.text = "Sumber Nutrisi"
        binding.ivIconTreatment.setImageResource(R.drawable.nutrient)
        binding.tvSymptomsTitle.text = "Kondisi kandang ayam sehat"
        binding.ivIconSymptoms.setImageResource(R.drawable.condition)
        hideNearbyPlaces()
    }

    private fun setSubContent(content: Disease) {
        binding.tvCause.text = content.alternativeTitle
        binding.tvGeneralTitle.text = content.section[0].heading
        binding.tvGeneralInformation.text = content.section[0].content
        binding.tvPreventionTitle.text = content.section[2].heading
        binding.tvPreventionInformation.text = content.section[2].content
        binding.tvTreatmentTitle.text = content.section[3].heading
        binding.tvTreatmentInformation.text = content.section[3].content
        binding.tvSymptomsTitle.text = content.section[1].heading
        binding.tvSymptomsInformation.text = content.section[1].content
        addListContentToContainer(binding.listSymptomsContainer, content.section[1].listContent)
        addListContentToContainer(binding.listGeneralContainer, content.section[0].listContent)
        addListContentToContainer(binding.listPreventionContainer, content.section[2].listContent)
        addListContentToContainer(binding.listTreatmentContainer, content.section[3].listContent)
    }

    private fun addListContentToContainer(container: LinearLayout, listContent: List<String>) {
        if (listContent.isNotEmpty()) {
            listContent.forEach {
                val bullet = generateBullet()
                val tvString = generateTextView(it)
                val linearLayoutContainer = generateListLinearLayout(bullet, tvString)
                container.addView(linearLayoutContainer)
            }
        }
    }

    private fun hideNearbyPlaces() {
        binding.tvLocationTitle.visibility = View.GONE
        binding.locationContent.visibility = View.GONE
        binding.ivIconPlaces.visibility = View.GONE
        binding.ivDownArrowPlaces.visibility = View.GONE
        binding.itemDividerPlace.visibility = View.GONE
    }

    private fun generateListLinearLayout(bullet: ImageButton, textView: TextView): LinearLayout {
        val linearLayout = LinearLayout(requireContext())
        val linearLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayoutParams.setMargins(0, 8, 0, 0)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.layoutParams = linearLayoutParams
        linearLayout.gravity = Gravity.TOP
        linearLayout.addView(bullet)
        linearLayout.addView(textView)
        return linearLayout
    }

    private fun generateTextView(string: String): TextView {
        val textView = TextView(requireContext())
        textView.text = string
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.layoutParams = layoutParams
        return textView
    }

    private fun generateBullet(): ImageButton {
        val bullet = ImageButton(requireContext())
        bullet.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_bullet)
        val imageLayoutParams = ViewGroup.MarginLayoutParams(
            18,
            18
        )
        imageLayoutParams.setMargins(0, 8, 8, 0)
        bullet.layoutParams = imageLayoutParams
        return bullet
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_general_down_arrow -> {
                setSubContentVisible(0, binding.generalContent, binding.ivGeneralDownArrow)
            }

            R.id.tv_general_title -> {
                setSubContentVisible(0, binding.generalContent, binding.ivGeneralDownArrow)
            }

            R.id.iv_icon_treatment_down_arrow -> {
                setSubContentVisible(1, binding.treatmentContent, binding.ivIconTreatmentDownArrow)
            }

            R.id.tv_treatment_title -> {
                setSubContentVisible(1, binding.treatmentContent, binding.ivIconTreatmentDownArrow)
            }

            R.id.iv_icon_prevention_down_arrow -> {
                binding.ivIconPreventionDownArrow.animate()
                    .rotation(if (isExpanded[2]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(
                    2,
                    binding.preventionContent,
                    binding.ivIconPreventionDownArrow
                )
            }

            R.id.tv_prevention_title -> {
                setSubContentVisible(
                    2,
                    binding.preventionContent,
                    binding.ivIconPreventionDownArrow
                )
            }

            R.id.iv_icon_symptoms_down_arrow -> {
                binding.ivIconSymptomsDownArrow.animate()
                    .rotation(if (isExpanded[3]) 180f else 0f)
                    .setDuration(300).start()
                setSubContentVisible(3, binding.symptomsContent, binding.ivIconSymptomsDownArrow)
            }

            R.id.tv_symptoms_title -> {
                setSubContentVisible(3, binding.symptomsContent, binding.ivIconSymptomsDownArrow)
            }

            R.id.iv_down_arrow_places -> {
                setSubContentVisible(4, binding.locationContent, binding.ivDownArrowPlaces)
            }

            R.id.tv_location_title -> {
                setSubContentVisible(4, binding.locationContent, binding.ivDownArrowPlaces)
            }
        }
    }

    private fun rotateDownArrow(index: Int, arrow: ImageView) {
        arrow.animate().rotation(if (isExpanded[index]) 180f else 0f)
            .setDuration(300).start()
    }

    private fun setSubContentVisible(index: Int, view: ConstraintLayout, arrow: ImageView) {
        isExpanded[index] = !isExpanded[index]
        rotateDownArrow(index, arrow)
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