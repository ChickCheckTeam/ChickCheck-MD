package com.example.chickcheckapp.presentation.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import com.example.chickcheckapp.R
import com.example.chickcheckapp.databinding.FragmentProfileBinding
import com.example.chickcheckapp.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String = ""

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

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}