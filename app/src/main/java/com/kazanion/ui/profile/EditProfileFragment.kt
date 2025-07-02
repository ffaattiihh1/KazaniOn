package com.kazanion.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.kazanion.databinding.FragmentEditProfileBinding
import com.kazanion.network.ApiService

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var apiService: ApiService
    private var isEmailVerified = false
    private var isPhoneVerified = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        apiService = ApiService.create()
        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        
        // SharedPreferences'dan bilgileri yükle
        val savedDisplayName = sharedPreferences.getString("userDisplayName", "")
        val savedEmail = sharedPreferences.getString("userEmail", "")
        
        // Form alanlarını doldur
        if (!savedDisplayName.isNullOrEmpty()) {
            val nameParts = savedDisplayName.split(" ")
            binding.editTextFirstName.setText(nameParts.getOrNull(0) ?: "")
            binding.editTextLastName.setText(nameParts.getOrNull(1) ?: "")
        }
        
        // Email bilgisi
        binding.textViewEmail.text = savedEmail ?: ""
        
        // Email doğrulama durumu (backend'den gelecek)
        isEmailVerified = false // TODO: Backend'den kontrol et
        updateEmailVerificationStatus()
        
        // Telefon bilgisi (backend'den gelecek)
        binding.textViewPhone.text = "+90 5XX XXX XX XX"
        
        // Telefon doğrulama durumu (backend'den gelecek)
        isPhoneVerified = false // TODO: Backend'den kontrol et
        updatePhoneVerificationStatus()
        
        // Kullanıcı adı
        val username = sharedPreferences.getString("username", "") ?: ""
        binding.editTextUsername.setText(username)
    }

    private fun setupClickListeners() {
        binding.buttonSave.setOnClickListener {
            saveProfile()
        }
        
        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.buttonVerifyEmail.setOnClickListener {
            verifyEmail()
        }
        
        binding.buttonVerifyPhone.setOnClickListener {
            verifyPhone()
        }
        
        binding.textViewChangePhoto.setOnClickListener {
            // TODO: Fotoğraf değiştirme işlemi
            Toast.makeText(context, "Fotoğraf değiştirme özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
        }

        // ŞİFRE DEĞİŞTİRME BUTTON
        binding.buttonChangePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun saveProfile() {
        val firstName = binding.editTextFirstName.text.toString().trim()
        val lastName = binding.editTextLastName.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        
        if (firstName.isEmpty()) {
            binding.editTextFirstName.error = "Ad boş olamaz"
            return
        }
        
        if (username.isEmpty()) {
            binding.editTextUsername.error = "Kullanıcı adı boş olamaz"
            return
        }
        
        // SharedPreferences'a kaydet
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("userDisplayName", "$firstName $lastName")
            putString("username", username)
            apply()
        }
        
        // TODO: Backend'e kaydetme işlemi
        // apiService.updateUserProfile(userId, UpdateProfileRequest(firstName, lastName, username))
        
        Toast.makeText(context, "Profil başarıyla güncellendi", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun verifyEmail() {
        // TODO: Backend e-posta doğrulama sistemi
        Toast.makeText(context, "E-posta doğrulama özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
    }

    private fun verifyPhone() {
        // TODO: Telefon doğrulama işlemi
        Toast.makeText(context, "Telefon doğrulama özelliği yakında eklenecek", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmailVerificationStatus() {
        if (isEmailVerified) {
            binding.textViewEmailStatus.text = "Doğrulandı"
            binding.textViewEmailStatus.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            binding.buttonVerifyEmail.isEnabled = false
            binding.buttonVerifyEmail.text = "Doğrulandı"
        } else {
            binding.textViewEmailStatus.text = "Onaylanmadı"
            binding.textViewEmailStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
            binding.buttonVerifyEmail.isEnabled = true
            binding.buttonVerifyEmail.text = "Onayla"
        }
    }

    private fun updatePhoneVerificationStatus() {
        if (isPhoneVerified) {
            binding.textViewPhoneStatus.text = "Doğrulandı"
            binding.textViewPhoneStatus.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
            binding.buttonVerifyPhone.isEnabled = false
            binding.buttonVerifyPhone.text = "Doğrulandı"
        } else {
            binding.textViewPhoneStatus.text = "Onaylanmadı"
            binding.textViewPhoneStatus.setTextColor(requireContext().getColor(android.R.color.holo_red_dark))
            binding.buttonVerifyPhone.isEnabled = true
            binding.buttonVerifyPhone.text = "Onayla"
        }
    }

    // ŞİFRE DEĞİŞTİRME FONKSİYONU
    private fun changePassword() {
        val currentPassword = binding.editTextCurrentPassword.text.toString().trim()
        val newPassword = binding.editTextNewPassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPassword.text.toString().trim()

        // Validasyon kontrolleri
        if (currentPassword.isEmpty()) {
            binding.textInputLayoutCurrentPassword.error = "Mevcut şifrenizi girin"
            return
        }

        if (newPassword.isEmpty()) {
            binding.textInputLayoutNewPassword.error = "Yeni şifrenizi girin"
            return
        }

        if (newPassword.length < 6) {
            binding.textInputLayoutNewPassword.error = "Şifre en az 6 karakter olmalı"
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.textInputLayoutConfirmPassword.error = "Şifre tekrarını girin"
            return
        }

        if (newPassword != confirmPassword) {
            binding.textInputLayoutConfirmPassword.error = "Şifreler eşleşmiyor"
            return
        }

        if (currentPassword == newPassword) {
            binding.textInputLayoutNewPassword.error = "Yeni şifre eskisinden farklı olmalı"
            return
        }

        // Hataları temizle
        binding.textInputLayoutCurrentPassword.error = null
        binding.textInputLayoutNewPassword.error = null
        binding.textInputLayoutConfirmPassword.error = null

        // Loading göster
        binding.buttonChangePassword.isEnabled = false
        binding.buttonChangePassword.text = "Değiştiriliyor..."

        // Backend'e şifre değiştirme isteği gönder
        lifecycleScope.launch {
            try {
                val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
                val username = sharedPreferences.getString("username", "") ?: ""
                
                // TODO: Backend'e şifre değiştirme API'si eklenince burası düzenlenecek
                // val response = apiService.changePassword(ChangePasswordRequest(username, currentPassword, newPassword))
                
                // Şimdilik mock response - Backend'e API eklendikten sonra kaldırılacak
                kotlinx.coroutines.delay(2000) // Simulate network delay
                
                // Başarılı olduğunu simüle et
                Toast.makeText(context, "✅ Şifre başarıyla değiştirildi", Toast.LENGTH_LONG).show()
                
                // Form alanlarını temizle
                binding.editTextCurrentPassword.setText("")
                binding.editTextNewPassword.setText("")
                binding.editTextConfirmPassword.setText("")
                
            } catch (e: retrofit2.HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Mevcut şifre hatalı"
                    400 -> "Geçersiz şifre formatı"
                    500 -> "Sunucu hatası, lütfen daha sonra tekrar deneyin"
                    else -> "Şifre değiştirilemedi (${e.code()})"
                }
                Toast.makeText(context, "❌ $errorMessage", Toast.LENGTH_LONG).show()
            } catch (e: java.net.SocketTimeoutException) {
                Toast.makeText(context, "⏱️ Sunucu çok yavaş, lütfen tekrar deneyin", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "❌ Bağlantı hatası: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                // Loading'i kaldır
                if (_binding != null) {
                    binding.buttonChangePassword.isEnabled = true
                    binding.buttonChangePassword.text = "Şifreyi Değiştir"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 