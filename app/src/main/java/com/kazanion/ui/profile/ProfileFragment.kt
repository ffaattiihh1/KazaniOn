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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kazanion.R
import com.kazanion.databinding.FragmentProfileBinding
import com.kazanion.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

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
        
        // Username'i SharedPreferences'dan al
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "yeni_kullanici") ?: "yeni_kullanici"
        
        android.util.Log.d("ProfileFragment", "=== PROFILE FRAGMENT INIT ===")
        android.util.Log.d("ProfileFragment", "SharedPreferences username: $username")
        android.util.Log.d("ProfileFragment", "All SharedPreferences keys:")
        sharedPreferences.all.forEach { (key, value) ->
            android.util.Log.d("ProfileFragment", "  $key = $value")
        }
        
        // ViewModel'i username ile initialize et
        android.util.Log.d("ProfileFragment", "Calling viewModel.initializeWithUsername($username)")
        viewModel.initializeWithUsername(username)
        
        observeUser()
        setupClickListeners()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textUserName.text = user.displayName
                binding.textUserEmail.text = user.email
                binding.textTotalPoints.text = user.points.toString()
            } ?: run {
                // Backend'den veri gelmezse Firebase'dan al
                loadFirebaseUserData()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Loading state gösterilebilir
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                android.util.Log.e("ProfileFragment", "ViewModel error: $it")
                showToast(it)
                // Error olduğunda da Firebase'dan dene
                android.util.Log.d("ProfileFragment", "API failed, trying fallback...")
                loadFirebaseUserData()
            }
        }
    }
    
    private fun loadFirebaseUserData() {
        android.util.Log.d("ProfileFragment", "=== LOADING FIREBASE/FALLBACK DATA ===")
        
        // Önce SharedPreferences'dan kontrol et
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val savedDisplayName = sharedPreferences.getString("userDisplayName", "")
        val savedEmail = sharedPreferences.getString("userEmail", "")
        
        android.util.Log.d("ProfileFragment", "SharedPreferences fallback:")
        android.util.Log.d("ProfileFragment", "  savedDisplayName: '$savedDisplayName'")
        android.util.Log.d("ProfileFragment", "  savedEmail: '$savedEmail'")
        
        if (!savedDisplayName.isNullOrEmpty()) {
            android.util.Log.d("ProfileFragment", "Using SharedPreferences data")
            android.util.Log.d("ProfileFragment", "  Display Name: $savedDisplayName")
            android.util.Log.d("ProfileFragment", "  Email: $savedEmail")
            
            // Profil bilgilerini SharedPreferences'dan güncelle
            binding.textUserName.text = savedDisplayName
            binding.textUserEmail.text = savedEmail ?: ""
            binding.textTotalPoints.text = "0" // Default değer
            
            android.util.Log.d("ProfileFragment", "SharedPreferences user data loaded: $savedDisplayName")
            return
        }
        
        // SharedPreferences'da bilgi yoksa Firebase'dan dene
        android.util.Log.d("ProfileFragment", "SharedPreferences empty, trying Firebase...")
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        
        android.util.Log.d("ProfileFragment", "Firebase current user: ${currentUser?.uid}")
        
        if (currentUser != null) {
            android.util.Log.d("ProfileFragment", "Using Firebase user data")
            android.util.Log.d("ProfileFragment", "  Display Name: ${currentUser.displayName}")
            android.util.Log.d("ProfileFragment", "  Email: ${currentUser.email}")
            
            val displayName = currentUser.displayName ?: "Kullanıcı"
            val email = currentUser.email ?: ""
            
            // Profil bilgilerini Firebase'dan güncelle
            binding.textUserName.text = displayName
            binding.textUserEmail.text = email
            binding.textTotalPoints.text = "0" // Default değer
            
            android.util.Log.d("ProfileFragment", "Firebase user data loaded: $displayName")
        } else {
            // Firebase user da yoksa default değerler
            android.util.Log.d("ProfileFragment", "No Firebase user, using defaults")
            binding.textUserName.text = "Kullanıcı"
            binding.textUserEmail.text = ""
            binding.textTotalPoints.text = "0"
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
            showToast("Bildirim ayarları yakında eklenecek")
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
        // TODO: Gerçek davet linkini oluştur
        // ViewModel kullanılıyorsa aşağıdaki satırı uncomment yapıp ViewModel'ı doğru initialize edin
        // return "https://kazanion.com/invite/${viewModel.getCurrentUser()?.id}"
        return "https://kazanion.com/invite/user_id_placeholder"
    }

    // showToast fonksiyonu sınıf içinde tanımlı
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 