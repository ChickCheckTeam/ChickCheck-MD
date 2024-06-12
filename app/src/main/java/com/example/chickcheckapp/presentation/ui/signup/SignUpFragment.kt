package com.example.chickcheckapp.presentation.ui.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chickcheckapp.R
import com.example.chickcheckapp.databinding.FragmentSignUpBinding
import com.example.chickcheckapp.utils.Result
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formValidation()
        registerUser()

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_signup_to_navigation_login)
        }
    }

    private fun registerUser() {
        with (binding) {
            btnSignUp.setOnClickListener {
                if (isFormValid()) {
                    val name = edSignUpName.text.toString()
                    val username = edSignUpUsername.text.toString()
                    val email = edSignUpEmail.text.toString()
                    val password = edSignUpPassword.text.toString()
                    val confirmPassword = edSignUpConfirmPassword.text.toString()

                    val register = viewModel.registerUser(name, username, email, password, confirmPassword)

                    register.observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                progressBar.visibility = View.GONE
                                showSnackBar("Account Created Successfully!")
                                findNavController().navigate(R.id.action_navigation_signup_to_navigation_login)
                            }
                            is Result.Error -> {
                                progressBar.visibility = View.GONE
                                if (result.error.isNotEmpty()) {
                                    showSnackBar(result.error)
                                } else {
                                    // Error on server (5xx, etc)
                                    showSnackBar("Something went wrong. Please try again later.")
                                }
                                Log.e("SignUpFragment", result.error)
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
            edSignUpNameLayout.helperText = validName()
            edSignUpUsernameLayout.helperText = validUsername()
            edSignUpEmailLayout.helperText = validEmail()
            edSignUpPasswordLayout.helperText = validPassword()
            edSignUpConfirmPasswordLayout.helperText = validConfirmPassword()
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun isFormValid(): Boolean {
        with (binding) {
            val nameError = validName()
            val usernameError = validUsername()
            val emailError = validEmail()
            val passwordError = validPassword()
            val confirmPasswordError = validConfirmPassword()

            return nameError == null && usernameError == null && emailError == null && passwordError == null && confirmPasswordError == null
        }
    }

    private fun formValidation() {
        with (binding) {
            edSignUpName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edSignUpNameLayout.helperText = validName()
                }
            }
            edSignUpUsername.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edSignUpUsernameLayout.helperText = validUsername()
                }
            }
            edSignUpEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edSignUpEmailLayout.helperText = validEmail()
                }
            }
            edSignUpPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edSignUpPasswordLayout.helperText = validPassword()
                }
            }
            edSignUpConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    edSignUpConfirmPasswordLayout.helperText = validConfirmPassword()
                }
            }
        }
    }

    private fun validName(): String? {
        val nameText = binding.edSignUpName.text.toString()
        if (nameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validUsername(): String? {
        val usernameText = binding.edSignUpUsername.text.toString()
        if (usernameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validConfirmPassword(): String? {
        val passwordText = binding.edSignUpPassword.text.toString()
        val confirmPasswordText = binding.edSignUpConfirmPassword.text.toString()
        if (confirmPasswordText.isEmpty()) {
            return "Required"
        }
        if (confirmPasswordText != passwordText) {
            return "Password Does Not Match"
        }
        return null
    }

    private fun validPassword(): String? {
        val passwordText = binding.edSignUpPassword.text.toString()
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
        val emailText = binding.edSignUpEmail.text.toString()
        if (emailText.isEmpty()) {
            return "Required"
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }
}