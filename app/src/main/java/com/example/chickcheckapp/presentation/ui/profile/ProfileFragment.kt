package com.example.chickcheckapp.presentation.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ScanHistoryItem
import com.example.chickcheckapp.databinding.FragmentProfileBinding
import com.example.chickcheckapp.presentation.adapter.HistoryAdapter
import com.example.chickcheckapp.presentation.ui.home.HomeFragmentDirections
import com.example.chickcheckapp.utils.OnHistoryItemClickListener
import com.example.chickcheckapp.utils.Result
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
    }

    private fun setupRecyclerView() {
        binding.rvScanHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun getProfile() {
        with (binding) {
            viewModel.getProfile(token).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        containerProfileContent.alpha = 0f
                    }
                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        containerProfileContent.alpha = 1f
                        tvProfileName.text = result.data.data?.name ?: "User"
                        tvProfileEmail.text = result.data.data?.email ?: "useremail@gmail.com"
                        tvProfileUsername.text = result.data.data?.username ?: "username"
                        if (result.data.data?.scanHistory?.size == 0) {
                            tvNoDataHistory.visibility = View.VISIBLE
                        } else {
                            result.data.data?.scanHistory?.let { setHistoryData(it) }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onHistoryItemClick(diseaseName: String, imageUrl: String) {
        viewModel.getArticle(token).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    result.data.forEach { articleData ->
                        if (articleData.title == diseaseName) {
                            val action = ProfileFragmentDirections.actionNavigationProfileToResultFragment(
                                imageUrl,
                                articleData
                            )
                            findNavController().navigate(action)
                        }
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.e("HomeFragment", result.error)
                    if (result.error.isNotEmpty()) {
                        showSnackBar(result.error)
                    } else {
                        // Error on server (5xx, etc)
                        showSnackBar("Something went wrong. Please try again later.")
                    }
                }

                else -> {}
            }
        }
    }
}