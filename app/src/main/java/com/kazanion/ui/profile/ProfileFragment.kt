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
import com.google.firebase.firestore.FirebaseFirestore

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
        // ViewModel initialize ediliyorsa burada yapılabilir
        // viewModel = ViewModelProvider(this).get(YourProfileViewModel::class.java)
        observeUser()
        setupClickListeners()
    }

    private fun observeUser() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val points = snapshot?.getLong("points") ?: 0
                    binding.textTotalPoints.text = points.toString()
                }
        }
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textUserName.text = user.displayName
                binding.textUserEmail.text = user.email
                // binding.textTotalPoints.text = user.points.toString() // Artık yukarıdan dinamik geliyor
            }
        }
    }

    private fun setupClickListeners() {
        binding.featureInvite?.setOnClickListener {
            showInviteDialog()
        }

        // Assuming featureRanking, featureFriends are valid IDs in your layout
        binding.featureRanking?.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_rankingFragment)
        }

        binding.featureFriends?.setOnClickListener {
            // Navigasyon grafiğinde action_profileFragment_to_friendsFragment tanımlı değil.
            // friendsFragment'ı navigasyon grafiğine ekledikten sonra bu satırı uncomment yapın.
            // findNavController().navigate(R.id.action_profileFragment_to_friendsFragment)
            showToast("Arkadaşlar özelliği tıklandı - Navigasyon eksik")
        }

        // featureSentiment layout'ta yok, ilgili satırı kaldırdık
        // binding.featureSentiment?.setOnClickListener { 
        //    // Sentiment sayfasına navigasyon kodunu buraya ekleyin
        // }
         binding.buttonEditProfile?.setOnClickListener { /* Handle Edit Profile Click */ }
         binding.featureLocation?.setOnClickListener { /* Handle Location Click */ }
         binding.featureWallet?.setOnClickListener { /* Handle Wallet Click */ }

         // Settings section click listeners (Assuming these IDs exist in layout)
         binding.settingNotifications?.setOnClickListener { /* Handle Notifications Setting Click */ }
         binding.settingLanguage?.setOnClickListener { /* Handle Language Setting Click */ }
         binding.settingPrivacy?.setOnClickListener { /* Handle Privacy Setting Click */ }
         binding.settingHelp?.setOnClickListener { /* Handle Help Setting Click */ }

         // Logout button click listener (Assuming this ID exists in layout)
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