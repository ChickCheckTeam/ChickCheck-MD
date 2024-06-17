package com.example.chickcheckapp.presentation.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.data.remote.response.ScanHistoryItem
import com.example.chickcheckapp.databinding.FragmentProfileBinding
import com.example.chickcheckapp.presentation.adapter.HistoryAdapter
import com.example.chickcheckapp.utils.OnHistoryItemClickListener
import com.example.chickcheckapp.utils.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(), OnHistoryItemClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String = ""
    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().asLiveData().observe(viewLifecycleOwner) { user ->
            token = user.token
            getProfile()
            setupRecyclerView()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_home)
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.btnScanNow.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_scan)
        }
    }

    private fun setupRecyclerView() {
        binding.rvScanHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun getProfile() {
        with(binding) {
            viewModel.getProfile(token).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        containerProfileContent.alpha = 0f
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        containerProfileContent.alpha = 1f
                        tvProfileName.text = result.data.data.name
                        tvProfileEmail.text = result.data.data.email
                        tvProfileUsername.text = result.data.data.username
                        if (result.data.data.scanHistory.isEmpty()) {
                            tvNoDataHistory.visibility = View.VISIBLE
                            btnScanNow.visibility = View.VISIBLE
                            tvTotalHistory.text = getString(R.string.total_histories, "0" )
                        } else {
                            setHistoryData(result.data.data.scanHistory)
                            tvTotalHistory.text = getString(R.string.total_histories, result.data.data.scanHistory.size.toString())
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        containerProfileContent.alpha = 1f
                        if (result.error.isNotEmpty()) {
                            showSnackBar(result.error)
                        } else {
                            // Error on server (5xx, etc)
                            showSnackBar("Something went wrong. Please try again later.")
                        }
                    }
                }
            }
        }
    }

    private fun setHistoryData(history: List<ScanHistoryItem>) {
        historyAdapter.submitList(history)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Are you sure want to logout?")
            setPositiveButton("OK") { _, _ ->
                viewModel.logoutFromApi(token).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            viewModel.logout()
                            binding.progressBar.visibility = View.GONE
                            showSnackBar("Logout Success!")
                            findNavController().navigate(R.id.action_navigation_profile_to_navigation_login)
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            if (result.error.isNotEmpty()) {
                                showSnackBar(result.error)
                            } else {
                                // Error on server (5xx, etc)
                                showSnackBar("Something went wrong. Please try again later.")
                            }
                            Log.e("HomeFragment", result.error)
                        }

                        else -> {}
                    }
                }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onHistoryItemClick(article: ArticleData, imageUrl: String) {
        val action = ProfileFragmentDirections.actionNavigationProfileToResultFragment(
            article,
            imageUrl
        )
        findNavController().navigate(action)
    }
}