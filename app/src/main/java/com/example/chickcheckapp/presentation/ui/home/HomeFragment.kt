package com.example.chickcheckapp.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chickcheckapp.R
import com.example.chickcheckapp.databinding.FragmentHomeBinding
import com.example.chickcheckapp.utils.Result
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            token = user.token
            if (user.token.isEmpty()) {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_login)
            } else {
                findNavController().popBackStack(R.id.navigation_login, true)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            logout()
        }
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
                            binding.progressBar.visibility = View.GONE
                            viewModel.logout()
                            showSnackBar("Logout Success!")
                            findNavController().navigate(R.id.action_navigation_home_to_navigation_login)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showSnackBar(result.error)
                            Log.e("HomeFragment", result.error)
                        }
                    }
                }
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}