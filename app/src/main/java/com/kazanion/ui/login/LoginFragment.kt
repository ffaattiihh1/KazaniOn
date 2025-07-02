package com.kazanion.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import com.kazanion.MainActivity
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

        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }



    private fun login(username: String, password: String) {
        // Loading göster
        binding.buttonLogin.isEnabled = false
        binding.buttonLogin.text = "Giriş yapılıyor..."
        
        val loginRequest = LoginRequest(username, password)
        lifecycleScope.launch {
            try {
                val loginResponse = apiService.login(loginRequest)
                if (loginResponse.success && loginResponse.user != null) {
                    // Save login state and user info to phone
                    val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                    
                    // DisplayName oluştur - İsim Soyisim
                    val firstName = loginResponse.user.firstName ?: ""
                    val lastName = loginResponse.user.lastName ?: ""
                    val displayName = when {
                        firstName.isNotEmpty() && lastName.isNotEmpty() -> "$firstName $lastName"
                        firstName.isNotEmpty() -> firstName
                        else -> loginResponse.user.username
                    }
                    
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", true)
                        putString("token", loginResponse.token)
                        putString("userId", loginResponse.user.id?.toString())
                        putString("publicId", loginResponse.user.publicId ?: "000000")  // 6 haneli ID
                        putString("username", loginResponse.user.username)
                        putString("email", loginResponse.user.email)
                        putString("userDisplayName", displayName)  // FULL NAME KAYDEDİLDİ
                        putString("userEmail", loginResponse.user.email)  // EMAIL AYRICA KAYDEDİLDİ
                        putString("firstName", firstName)
                        putString("lastName", lastName)
                        putString("phoneNumber", loginResponse.user.phoneNumber ?: "")
                        putString("birthDate", loginResponse.user.birthDate ?: "")
                        putInt("points", loginResponse.user.points ?: 0)
                        putFloat("balance", (loginResponse.user.balance ?: 0.0).toFloat())
                        apply()
                    }
                    
                    Toast.makeText(context, "Hoş geldin $firstName!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to home
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    
                    // Bildirim izni iste
                    (activity as? MainActivity)?.onLoginSuccess()
                } else {
                    // DAHA NET HATA MESAJLARI
                    val errorMessage = when {
                        loginResponse.message.contains("invalid", true) -> "E-posta veya şifre hatalı"
                        loginResponse.message.contains("credential", true) -> "E-posta veya şifre hatalı"
                        loginResponse.message.contains("password", true) -> "Şifre hatalı"
                        loginResponse.message.contains("email", true) -> "E-posta adresi bulunamadı"
                        loginResponse.message.contains("user", true) -> "Kullanıcı bulunamadı"
                        else -> "E-posta veya şifre hatalı"
                    }
                    Toast.makeText(context, "❌ $errorMessage", Toast.LENGTH_LONG).show()
                }
            } catch (e: retrofit2.HttpException) {
                // HTTP hataları için özel mesajlar
                val errorMessage = when (e.code()) {
                    401 -> "E-posta veya şifre hatalı"
                    404 -> "Kullanıcı bulunamadı"
                    400 -> "Geçersiz bilgiler"
                    500 -> "Sunucu hatası, lütfen daha sonra tekrar deneyin"
                    else -> "Giriş yapılamadı (${e.code()})"
                }
                Toast.makeText(context, "❌ $errorMessage", Toast.LENGTH_LONG).show()
            } catch (e: java.net.SocketTimeoutException) {
                Toast.makeText(context, "⏱️ Sunucu çok yavaş, lütfen tekrar deneyin", Toast.LENGTH_LONG).show()
            } catch (e: java.net.UnknownHostException) {
                Toast.makeText(context, "🔌 İnternet bağlantınızı kontrol edin", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Bağlantı hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                // Loading'i kaldır
                if (_binding != null) {
                    binding.buttonLogin.isEnabled = true
                    binding.buttonLogin.text = "Giriş Yap"
                }
            }
        }
    }



    private fun showForgotPasswordDialog() {
        val editText = EditText(requireContext())
        editText.hint = "E-posta adresinizi girin"

        AlertDialog.Builder(requireContext())
            .setTitle("Şifremi Unuttum")
            .setMessage("E-posta adresinizi girin, şifrenizi size göndereceğiz.")
            .setView(editText)
            .setPositiveButton("Gönder") { _, _ ->
                val email = editText.text.toString().trim()
                if (email.isNotEmpty()) {
                    resetPassword(email)
                } else {
                    Toast.makeText(context, "Lütfen e-posta adresinizi girin", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            try {
                val request = mapOf("email" to email)
                val response = apiService.forgotPassword(request)
                
                if (response["success"] == true) {
                    Toast.makeText(context, response["message"].toString(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, response["message"].toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Şifre sıfırlama hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 