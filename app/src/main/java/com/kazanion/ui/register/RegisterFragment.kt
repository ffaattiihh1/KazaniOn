package com.kazanion.ui.register

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kazanion.MainActivity
import com.kazanion.R
import com.kazanion.databinding.FragmentRegisterBinding

import com.kazanion.network.ApiService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiService: ApiService
    
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
        apiService = ApiService.create()
        
        // Geri tuşu davranışını düzelt - sadece login sayfasına git
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigation ile login'e git
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        
        setupUI()
        setupCityDropdown()
        
        binding.buttonRegister.setOnClickListener {
            handleRegisterClick()
        }

        binding.buttonBackToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
    
    private fun setupUI() {
        // Normal kayıt için UI
        binding.passwordGroup.visibility = View.VISIBLE
    }
    
    private fun setupCityDropdown() {
        // Şehir listesini al
        val cities = resources.getStringArray(R.array.cities_turkey)
        
        // Adapter oluştur
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cities
        )
        
        // AutoCompleteTextView'e adapter'ı bağla
        binding.autoCompleteCity.setAdapter(adapter)
        
        // İlk seçimi yapma (index 0 = "Şehir Seçiniz")
        binding.autoCompleteCity.setText(cities[0], false)
    }
    
    private fun handleRegisterClick() {
        val firstName = binding.editTextAd.text.toString().trim()
        val lastName = binding.editTextSoyad.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        val email = binding.editTextEmail.text.toString().trim()
        val phoneNumber = binding.editTextPhoneNumber.text.toString().trim()
        val selectedCity = binding.autoCompleteCity.text.toString().trim()
        val day = binding.editTextDay.text.toString().trim()
        val month = binding.editTextMonth.text.toString().trim()
        val year = binding.editTextYear.text.toString().trim()
        
        // Temel validasyonlar
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()) {
            Toast.makeText(context, "Ad, soyad, kullanıcı adı ve e-posta gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Username validasyonu
        if (username.length < 3) {
            Toast.makeText(context, "Kullanıcı adı en az 3 karakter olmalı", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!username.matches(Regex("^[a-zA-Z0-9._]+$"))) {
            Toast.makeText(context, "Kullanıcı adı sadece harf, rakam, nokta ve alt çizgi içerebilir", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Şehir validasyonu
        if (selectedCity.isEmpty() || selectedCity == "Şehir Seçiniz") {
            Toast.makeText(context, "Lütfen şehrinizi seçiniz", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Doğum tarihi validasyonu
        if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(context, "Doğum tarihi gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        val birthDay = day.toIntOrNull()
        val birthMonth = month.toIntOrNull()
        val birthYear = year.toIntOrNull()
        
        if (birthDay == null || birthMonth == null || birthYear == null ||
            birthDay !in 1..31 || birthMonth !in 1..12 || birthYear !in 1900..Calendar.getInstance().get(Calendar.YEAR)) {
            Toast.makeText(context, "Geçerli doğum tarihi girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Email validasyonu
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Geçerli e-posta girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Şifre validasyonu
        val password = binding.editTextPassword.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Şifre gerekli", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password != confirmPassword) {
            Toast.makeText(context, "Şifreler farklı", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(context, "Şifre 6+ karakter", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Kayıt işlemini gerçekleştir
        registerNormalUser(firstName, lastName, username, email, phoneNumber, selectedCity, password, "$birthDay/$birthMonth/$birthYear")
    }
    
    private fun registerNormalUser(firstName: String, lastName: String, username: String, email: String, phoneNumber: String, city: String, password: String, birthDate: String) {
        lifecycleScope.launch {
            try {
                val userData = com.kazanion.network.CreateUserRequest(
                    username = username,
                    email = email,
                    passwordHash = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    birthDate = birthDate,
                    points = 0,
                    balance = 0.0,
                    city = city
                )
                
                // Backend'e kullanıcı oluştur
                val response = apiService.createUser(userData)
                
                // Eğer response başarısızsa hata fırlat
                if (response.success == false) {
                    throw Exception(response.message ?: "Kayıt başarısız")
                }
                
                // Backend'den dönen user id'yi al
                val backendUserId = response.id.toString()
                val publicId = response.publicId ?: "000000"  // 6 haneli public ID
                
                // DisplayName oluştur - İsim Soyisim
                val displayName = when {
                    firstName.isNotEmpty() && lastName.isNotEmpty() -> "$firstName $lastName"
                    firstName.isNotEmpty() -> firstName
                    else -> username
                }
                
                // Login state'i ve kullanıcı bilgilerini telefona kaydet
                val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("isLoggedIn", true)
                    putString("userId", backendUserId)
                    putString("publicId", publicId)  // 6 haneli ID'yi kaydet
                    putString("username", username)
                    putString("email", email)
                    putString("userDisplayName", displayName)  // FULL NAME KAYDEDİLDİ
                    putString("userEmail", email)  // EMAIL AYRICA KAYDEDİLDİ
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("phoneNumber", phoneNumber)
                    putString("city", city)
                    putString("birthDate", birthDate)
                    putInt("points", 0)
                    putFloat("balance", 0.0f)
                    apply()
                }
                
                Toast.makeText(context, "Hesap başarıyla oluşturuldu! Hoş geldin $firstName!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                
                // Bildirim izni iste
                (activity as? MainActivity)?.onLoginSuccess()
                
            } catch (e: Exception) {
                android.util.Log.e("RegisterFragment", "User registration failed: ${e.message}", e)
                
                val errorMessage = when {
                    e.message?.contains("Bu kullanıcı adı zaten kullanılıyor") == true -> "Bu kullanıcı adı zaten alınmış, başka bir tane deneyin"
                    e.message?.contains("Bu e-posta adresi zaten kayıtlı") == true -> "Bu e-posta adresi zaten kayıtlı"
                    e.message?.contains("HTTP 400") == true -> "Geçersiz veriler gönderildi"
                    e.message?.contains("HTTP 500") == true -> "Sunucu hatası" 
                    e.message?.contains("timeout") == true -> "Bağlantı zaman aşımı - Backend'e ulaşılamıyor"
                    e.message?.contains("Unable to resolve host") == true -> "DNS çözümlemesi başarısız - İnternet bağlantısı kontrol edin"
                    e.message?.contains("Failed to connect") == true -> "Backend sunucusuna bağlanılamıyor (Port: 8081)"
                    e.message?.contains("ConnectException") == true -> "Backend sunucusu çalışmıyor veya erişilemiyor"
                    else -> "Kayıt başarısız: ${e.message}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 