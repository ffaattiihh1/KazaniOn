package com.kazanion.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kazanion.R
import com.kazanion.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kazanion.MainActivity
import com.kazanion.utils.NotificationPermissionHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    // CACHE - Sayfa değişiminde hızlı açılması için
    companion object {
        private var userDataLoaded = false
        private var cachedDisplayName: String? = null
        private var cachedEmail: String? = null
        private var cachedUserId: Long? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // HER ZAMAN YENİDEN YÜKLE - EMAIL SORUNU ÇÖZÜLENE KADAR
        android.util.Log.d("ProfileFragment", "🔄 Her zaman fresh data yükleniyor...")
        loadUserData()
        setupClickListeners()
        setupNotificationSwitch()
    }

    private fun loadUserData() {
        // Direkt Firebase/SharedPreferences'dan veri yükle
        loadFirebaseUserData()
    }
    
    private fun loadFirebaseUserData() {
        // SharedPreferences'dan kullanıcı verilerini al
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        
        // DEBUG: Tüm SharedPreferences'ları listele
        android.util.Log.d("ProfileFragment", "🔍 SharedPreferences DEBUG:")
        sharedPreferences.all.forEach { (key, value) ->
            android.util.Log.d("ProfileFragment", "   $key = $value")
        }
        
        val savedDisplayName = sharedPreferences.getString("userDisplayName", "")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val mainEmail = sharedPreferences.getString("email", "")
        val username = sharedPreferences.getString("username", "")
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val userId = sharedPreferences.getString("userId", null)?.toLongOrNull() ?: generateUserId()
        
        // EMAIL DEBUG
        android.util.Log.d("ProfileFragment", "📧 EMAIL DEBUG:")
        android.util.Log.d("ProfileFragment", "   userEmail = '$userEmail'")
        android.util.Log.d("ProfileFragment", "   email = '$mainEmail'")
        
        // İsim Soyisim (birden fazla kaynaktan dene)
        val fullName = when {
            !savedDisplayName.isNullOrEmpty() -> savedDisplayName
            !firstName.isNullOrEmpty() && !lastName.isNullOrEmpty() -> "$firstName $lastName"
            !firstName.isNullOrEmpty() -> firstName
            else -> "İsim Soyisim"
        }
        
        // Email (TÜM OLASILIKLARı KONTROL ET)
        val email = when {
            !userEmail.isNullOrEmpty() -> {
                android.util.Log.d("ProfileFragment", "✅ userEmail kullanılıyor: '$userEmail'")
                userEmail
            }
            !mainEmail.isNullOrEmpty() -> {
                android.util.Log.d("ProfileFragment", "✅ email kullanılıyor: '$mainEmail'")
                mainEmail
            }
            !username.isNullOrEmpty() && username.contains("@") -> {
                android.util.Log.d("ProfileFragment", "✅ username email formatında: '$username'")
                username
            }
            else -> {
                android.util.Log.d("ProfileFragment", "⚠️ Email bulunamadı, default kullanılıyor")
                "email@example.com"
            }
        }
        
        // Username (@ ile)
        val usernameDisplay = if (!username.isNullOrEmpty()) {
            "@$username"
        } else {
            "@kullaniciadi"
        }
        
        android.util.Log.d("ProfileFragment", "📝 Final values: fullName='$fullName', email='$email', username='$usernameDisplay'")
        
        // UI'ı güncelle
        binding.textUserName.text = fullName
        binding.textUserEmail.text = email
        binding.textUsername.text = usernameDisplay
        binding.textTotalPoints.text = "0" // Default değer
        
        // CACHE'E KAYDET - Bir sonraki açılış hızlı olsun!
        userDataLoaded = true
        cachedDisplayName = fullName
        cachedEmail = email
        cachedUserId = userId
        
        // 6 haneli ID sistemi - önce backend'den publicId kontrol et
        val savedPublicId = sharedPreferences.getString("publicId", null)
        val displayId = if (!savedPublicId.isNullOrEmpty()) {
            savedPublicId.toLongOrNull() ?: generate6DigitId(userId)
        } else {
            generate6DigitId(userId)
        }
        updateUserIdDisplay(displayId)
    }
    
    private fun generateUserId(): Long {
        // Eğer userId yoksa, yeni bir tane oluştur ve kaydet
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val newUserId = System.currentTimeMillis() % 1000000 // Son 6 hane
        sharedPreferences.edit().putString("userId", newUserId.toString()).apply()
        return newUserId
    }
    
    private fun generate6DigitId(originalId: Long): Long {
        // 6 haneli ID sistemi - 100000 ile 999999 arasında
        val id6Digit = (originalId % 900000) + 100000
        return id6Digit
    }
    
    private fun updateUserIdDisplay(userId: Long) {
        val userIdText = "ID: #$userId"
        binding.textUserId.text = userIdText
        
        // ID'ye tıklandığında kopyala
        binding.textUserId.setOnClickListener {
            copyUserIdToClipboard(userId.toString())
        }
    }
    
    private fun copyUserIdToClipboard(userId: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Kullanıcı ID", userId)
        clipboard.setPrimaryClip(clip)
        
        // Kopyalandı bildirimi
        showToast("Kullanıcı ID kopyalandı: #$userId")
        
        // Visual feedback - ID background'ını kısa süre değiştir
        binding.textUserId.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                binding.textUserId.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
            }
    }

    private fun setupClickListeners() {
        // Arkadaşlar davet et
        binding.featureFriends?.setOnClickListener {
            showInviteDialog()
        }

        // İstatistikler
        binding.featureStatistics?.setOnClickListener {
            showToast("İstatistikler özelliği yakında eklenecek")
        }

        // Profili düzenle
        binding.buttonEditProfile?.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            } catch (e: Exception) {
                // Navigation action bulunamazsa EditProfileFragment'ı açmaya çalış
                showToast("Profil düzenleme sayfası açılamıyor")
            }
        }

        // Settings section click listeners
        binding.settingNotifications?.setOnClickListener {
            handleNotificationSettings()
        }
        
        binding.settingLanguage?.setOnClickListener {
            showToast("Dil ayarları yakında eklenecek")
        }
        
        binding.settingPrivacy?.setOnClickListener {
            showToast("Gizlilik ayarları yakında eklenecek")
        }
        
        binding.settingHelp?.setOnClickListener {
            showToast("Yardım ve destek yakında eklenecek")
        }

        // Logout button
        binding.buttonLogout?.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun showInviteDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_invite, null) as View
        dialog.setContentView(dialogView)

        dialogView.findViewById<Button>(R.id.btnWhatsApp)?.setOnClickListener {
            shareViaWhatsApp()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCopyLink)?.setOnClickListener {
            copyInviteLink()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnAppStore)?.setOnClickListener {
            openAppStore()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun shareViaWhatsApp() {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                setPackage("com.whatsapp")
                putExtra(Intent.EXTRA_TEXT, "Kazanion'a katıl ve birlikte kazanalım! ${getInviteLink()}")
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                 showToast("WhatsApp uygulaması bulunamadı.")
            }
        } catch (e: Exception) {
            showToast("WhatsApp ile paylaşılırken bir hata oluştu: ${e.message}")
        }
    }

    private fun copyInviteLink() {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Kazanion Davet Linki", getInviteLink())
        clipboard.setPrimaryClip(clip)
        showToast("Davet linki kopyalandı")
    }

    private fun openAppStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${requireContext().packageName}")
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // Play Store yüklü değilse web tarayıcıda aç
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                }
                if (webIntent.resolveActivity(requireContext().packageManager) != null) {
                     startActivity(webIntent)
                } else {
                    showToast("Uygulama mağazası veya web tarayıcı bulunamadı.")
                }
            }
        } catch (e: Exception) {
            showToast("Uygulama mağazası açılırken bir hata oluştu: ${e.message}")
        }
    }

    private fun getInviteLink(): String {
        // Kullanıcı ID'sini al ve davet linkinde kullan
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null) ?: "unknown"
        return "https://kazanion.com/invite/$userId"
    }

    // showToast fonksiyonu sınıf içinde tanımlı
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun handleNotificationSettings() {
        val notificationHelper = NotificationPermissionHelper(requireActivity())
        
        if (notificationHelper.isNotificationPermissionGranted()) {
            // Bildirimler zaten açık - Kapatma seçeneği sunalım
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Bildirim Ayarları")
                .setMessage("Bildirimler aktif. Kapatmak ister misiniz?")
                .setPositiveButton("Ayarları Aç") { _, _ ->
                    // Android sistem ayarlarını aç
                    notificationHelper.openNotificationSettings(requireContext())
                }
                .setNegativeButton("İptal", null)
                .show()
        } else {
            // Bildirimler kapalı - Açma seçeneği sunalım
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Bildirimler Kapalı")
                .setMessage("Önemli duyuruları kaçırmamak için bildirimleri açmak ister misiniz?")
                .setPositiveButton("Bildirimleri Aç") { _, _ ->
                    requestNotificationPermission()
                }
                .setNegativeButton("İptal", null)
                .show()
        }
    }

    private fun requestNotificationPermission() {
        val notificationHelper = NotificationPermissionHelper(requireActivity())
        notificationHelper.requestNotificationPermission(requireActivity()) { granted ->
            if (granted) {
                showToast("Bildirimler etkinleştirildi! 🔔")
                // Switch'i güncelle
                binding.switchNotifications.isChecked = true
            } else {
                showToast("Bildirim izni verilmedi. Ayarlardan manuel olarak açabilirsiniz.")
                // Switch'i kapat
                binding.switchNotifications.isChecked = false
            }
        }
    }
    
    private fun setupNotificationSwitch() {
        // Notification channel oluştur
        createNotificationChannel()
        
        // Switch durumunu kontrol et
        val notificationHelper = NotificationPermissionHelper(requireActivity())
        val isEnabled = notificationHelper.isNotificationPermissionGranted()
        binding.switchNotifications.isChecked = isEnabled
        
        android.util.Log.d("ProfileFragment", "🔔 Notification switch durumu: $isEnabled")
        
        // Switch listener ekle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            android.util.Log.d("ProfileFragment", "🔄 Switch değişti: $isChecked")
            
            if (isChecked) {
                // Bildirimleri aç
                if (!notificationHelper.isNotificationPermissionGranted()) {
                    // İzin yok, iste
                    requestNotificationPermission()
                } else {
                    // İzin var, sadece kaydet
                    saveNotificationEnabled(true)
                    showToast("Bildirimler açıldı! 🔔")
                }
            } else {
                // Bildirimleri kapat
                saveNotificationEnabled(false)
                showToast("Bildirimler kapatıldı")
                
                // Sistem ayarlarına yönlendir
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Sistem Ayarları")
                    .setMessage("Bildirimleri tamamen kapatmak için sistem ayarlarından da kapatmanız önerilir.")
                    .setPositiveButton("Ayarları Aç") { _, _ ->
                        notificationHelper.openNotificationSettings(requireContext())
                    }
                    .setNegativeButton("Tamam", null)
                    .show()
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channelId = "kazanion_notifications"
            val channelName = "KazaniOn Bildirimleri"
            val channelDescription = "Anket ve duyuru bildirimleri"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            
            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
            
            android.util.Log.d("ProfileFragment", "📱 Notification channel oluşturuldu: $channelId")
        }
    }
    
    private fun saveNotificationEnabled(enabled: Boolean) {
        try {
            val sharedPref = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("notification_enabled", enabled)
                apply()
            }
            android.util.Log.d("ProfileFragment", "💾 Notification ayarı kaydedildi: $enabled")
        } catch (e: Exception) {
            android.util.Log.e("ProfileFragment", "❌ Notification ayarı kaydedilemedi", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 