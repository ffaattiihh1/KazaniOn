package com.kazanion.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kazanion.R
import com.kazanion.databinding.FragmentLoginBinding
import com.kazanion.network.ApiService
import com.kazanion.network.LoginRequest
import com.kazanion.network.LoginResponse

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = ApiService.create()

        // Check if already logged in
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            if (findNavController().currentDestination?.id == R.id.loginFragment) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            val usernameOrEmail = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()

            if (usernameOrEmail.isNotEmpty() && password.isNotEmpty()) {
                login(usernameOrEmail, password)
            } else {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }



    private fun login(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        lifecycleScope.launch {
            try {
                val loginResponse = apiService.login(loginRequest)
                if (loginResponse.success && loginResponse.user != null) {
                    // Save login state and user info to phone
                    val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("token", loginResponse.token)
                        putString("userId", loginResponse.user.id?.toString())
                        putString("username", loginResponse.user.username)
                        putString("email", loginResponse.user.email)
                        putString("firstName", loginResponse.user.firstName ?: "")
                        putString("lastName", loginResponse.user.lastName ?: "")
                        putString("phoneNumber", loginResponse.user.phoneNumber ?: "")
                        putString("birthDate", loginResponse.user.birthDate ?: "")
                        putInt("points", loginResponse.user.points ?: 0)
                        putFloat("balance", (loginResponse.user.balance ?: 0.0).toFloat())
                        apply()
                    }
                    
                    val firstName = loginResponse.user.firstName ?: username
                    Toast.makeText(context, "Hoş geldin $firstName!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to home
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Giriş başarısız: ${loginResponse.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Bağlantı hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 