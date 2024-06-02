package com.example.chickcheckapp.presentation.ui.signup

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chickcheckapp.R
import com.example.chickcheckapp.databinding.FragmentLoginBinding
import com.example.chickcheckapp.databinding.FragmentSignUpBinding

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

    }

    private fun formValidation() {
        with (binding) {
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

    private fun validConfirmPassword(): String? {
        val passwordText = binding.edSignUpPassword.text.toString()
        val confirmPasswordText = binding.edSignUpConfirmPassword.text.toString()
        if (confirmPasswordText != passwordText) {
            return "Password Does Not Match"
        }
        return null
    }

    private fun validPassword(): String? {
        val passwordText = binding.edSignUpPassword.text.toString()
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
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid Email Address"
        }
        return null
    }
}