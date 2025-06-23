package com.kazanion.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kazanion.R
import com.kazanion.auth.AuthManager
import com.kazanion.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authManager: AuthManager
    private var phoneVerificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authManager = AuthManager.getInstance()

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val phoneNumber = binding.editTextPhoneNumber.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty()) {
                registerUser(email, password, phoneNumber)
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonVerifyPhone.setOnClickListener {
            val code = binding.editTextVerificationCode.text.toString()
            if (code.isNotEmpty() && phoneVerificationId != null) {
                verifyPhoneNumber(code)
            } else {
                Toast.makeText(context, "Lütfen doğrulama kodunu girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, password: String, phoneNumber: String) {
        lifecycleScope.launch {
            try {
                // Create user with email and password
                val user = authManager.createUserWithEmailAndPassword(email, password)
                
                // Send email verification
                authManager.sendEmailVerification()
                
                // Send phone verification
                phoneVerificationId = authManager.sendPhoneVerification(phoneNumber, requireActivity())
                
                // Show verification UI
                binding.verificationLayout.visibility = View.VISIBLE
                binding.buttonRegister.isEnabled = false
                
                Toast.makeText(
                    context,
                    "Doğrulama e-postası gönderildi ve telefon doğrulama kodu gönderildi",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyPhoneNumber(code: String) {
        lifecycleScope.launch {
            try {
                phoneVerificationId?.let { verificationId ->
                    authManager.verifyPhoneNumber(verificationId, code)
                    Toast.makeText(context, "Telefon numarası doğrulandı", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Doğrulama hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 