package com.kazanion.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kazanion.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    
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
        
        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        
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
        val email = savedEmail ?: currentUser?.email ?: ""
        binding.textViewEmail.text = email
        
        // Email doğrulama durumu
        isEmailVerified = currentUser?.isEmailVerified ?: false
        updateEmailVerificationStatus()
        
        // Telefon bilgisi (Firebase'dan gelmeyebilir)
        val phoneNumber = currentUser?.phoneNumber ?: ""
        binding.textViewPhone.text = if (phoneNumber.isNotEmpty()) phoneNumber else "+90 5XX XXX XX XX"
        
        // Telefon doğrulama durumu
        isPhoneVerified = !phoneNumber.isNullOrEmpty()
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
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        
        if (firebaseUser != null && !firebaseUser.isEmailVerified) {
            firebaseUser.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Doğrulama e-postası gönderildi. E-postanızı kontrol edin.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "E-posta gönderilirken hata oluştu: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(context, "E-posta zaten doğrulanmış veya kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 