package com.example.chickcheckapp.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.data.remote.response.RecentHistoryResponse
import com.example.chickcheckapp.data.remote.response.ScanDataItem
import com.example.chickcheckapp.data.remote.response.ScanHistoryItem
import com.example.chickcheckapp.databinding.FragmentHomeBinding
import com.example.chickcheckapp.presentation.adapter.HistoryAdapter
import com.example.chickcheckapp.presentation.adapter.RecentHistoryAdapter
import com.example.chickcheckapp.utils.OnHistoryItemClickListener
import com.example.chickcheckapp.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnHistoryItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var token: String = ""
    private val historyAdapter: RecentHistoryAdapter by lazy {
        RecentHistoryAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_profile)
        }

        binding.btnScan.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_scan)
        }

        viewModel.getSession().asLiveData().observe(viewLifecycleOwner) { user ->
            token = user.token
            getProfile()
            getRecentHistory()
        }

        setupRecyclerView()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    private fun setupRecyclerView() {
        binding.rvScanHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun getProfile() {
        viewModel.getProfile(token).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.containerHomeContent.alpha = 0f
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvWelcome.text = getString(
                        R.string.welcome_title,
                        result.data.data.username
                    )
                    binding.containerHomeContent.alpha = 1f
                    if (result.data.data.scanHistory.isEmpty()) {
                        binding.containerEmptyScan.visibility = View.VISIBLE
                    } else {
                        binding.containerEmptyScan.visibility = View.GONE
                    }
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.containerHomeContent.alpha = 1f
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

    private fun setRecentHistoryData(history: List<ScanDataItem>) {
        historyAdapter.submitList(history)
    }

    private fun getRecentHistory() {
        viewModel.getRecentHistory(token).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val response = result.data
                    setRecentHistoryData(response.data)
                    Log.d("TAG", "getRecentHistory: ${result.data}")
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
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

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHistoryItemClick(article: ArticleData, imageUrl: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToResultFragment(
            article,
            imageUrl
        )
        findNavController().navigate(action)
    }
}