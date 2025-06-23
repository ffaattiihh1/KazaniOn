package com.kazanion.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kazanion.R
import com.kazanion.databinding.FragmentRegisterBinding
import com.kazanion.model.Result
import com.kazanion.viewmodel.RegisterViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel

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
        registerViewModel = RegisterViewModel()

        binding.buttonRegister.setOnClickListener {
            val name = binding.editTextAd.text.toString().trim()
            val surname = binding.editTextSoyad.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val phoneNumber = binding.editTextPhoneNumber.text.toString().trim()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Basic email validation (you might want a more robust one)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Geçerli bir e-posta adresi girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Basic phone number validation (you might want a more robust one)
            if (phoneNumber.length < 10) { // Example: minimum 10 digits
                Toast.makeText(context, "Geçerli bir telefon numarası girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // You might want to add password strength validation here (e.g., min length, special chars)
            if (password.length < 6) {
                Toast.makeText(context, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For now, only email and password are passed to registerViewModel. You may need to update your ViewModel
            // to accept name, surname, and phoneNumber if you intend to send them to your backend.
            registerViewModel.register(name, surname, email, phoneNumber, password)
        }

        binding.buttonBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Observe the StateFlow registerResult
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.registerResult.collectLatest { result: Result<Unit> ->
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        is Result.Error -> {
                            Toast.makeText(context, "Kayıt başarısız: ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 