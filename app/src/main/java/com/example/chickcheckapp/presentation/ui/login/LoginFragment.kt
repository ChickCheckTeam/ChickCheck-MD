package com.example.chickcheckapp.presentation.ui.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.local.model.UserModel
import com.example.chickcheckapp.databinding.FragmentLoginBinding
import com.example.chickcheckapp.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formValidation()
        login()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    private fun login() {
        with (binding) {
            btnLogin.setOnClickListener {
                if (isFormValid()) {
                    val email = edLoginEmail.text.toString()
                    val password = edLoginPassword.text.toString()

                    val login = viewModel.login(email, password)
                    login.observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                val user = UserModel(
                                    email,
                                    result.data.token,
                                    true
                                )
                                viewModel.saveSession(user)
                                progressBar.visibility = View.GONE
                                showSnackBar("Login Success!")
                                findNavController().navigate(R.id.action_navigation_login_to_navigation_home)
                            }
                            is Result.Error -> {
                                progressBar.visibility = View.GONE
                                showSnackBar("Username or Password is Incorrect!")
                                Log.e("LoginFragment", result.error)
                            }
                        }
                    }
                } else {
                    showErrorForm()
                }
            }
        }
    }

    private fun showErrorForm() {
        with (binding) {
            edEmailLayout.helperText = validEmail()
            edPasswordLayout.helperText = validPassword()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun isFormValid(): Boolean {
        with (binding) {
            val emailError = validEmail()
            val passwordError = validPassword()

            return emailError == null && passwordError == null
        }
    }

    private fun formValidation() {
        with (binding) {
            edLoginEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edEmailLayout.helperText = validEmail()
                }
            }
            edLoginPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edPasswordLayout.helperText = validPassword()
                }
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.edLoginPassword.text.toString()
        if (passwordText.isEmpty()) {
            return "Required"
        }
        if (passwordText.length < 8) {
            return "Minimum 8 Character Password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must Contain 1 Uppercase Character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must Contain 1 Lowercase Character"
        }
        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }
        return null
    }

    private fun validEmail(): String? {
        val emailText = binding.edLoginEmail.text.toString()
        if (emailText.isEmpty()) {
            return "Required"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}