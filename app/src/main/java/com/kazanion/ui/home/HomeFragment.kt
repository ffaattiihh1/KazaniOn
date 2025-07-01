package com.kazanion.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kazanion.R
import com.kazanion.databinding.FragmentHomeBinding
import com.kazanion.model.Poll
import com.kazanion.network.LocationPoll
import com.kazanion.ui.locationpoll.LocationPollDetailBottomSheetFragment
import com.kazanion.viewmodels.HomeViewModel
import com.kazanion.viewmodels.AchievementViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var achievementViewModel: AchievementViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var activePollsAdapter: ActivePollsAdapter
    private lateinit var locationPollsAdapter: LocationPollsAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        achievementViewModel = ViewModelProvider(this).get(AchievementViewModel::class.java)

        setupUI()
        setupObservers()
        checkLocationPermission()
    }

    private fun setupUI() {
        setupRecyclerViews()
        setupClickListeners()
        binding.swipeRefreshLayout.setOnRefreshListener {
            val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("username", "yeni_kullanici") ?: "yeni_kullanici"
            viewModel.loadAllData(username, true)
        }
    }

    private fun setupClickListeners() {
        binding.topUserInfoSection.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        binding.buttonExploreOnMap.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapPollsFragment)
        }
        binding.walletCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_walletFragment)
        }
         binding.textViewTumunuGor.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_pollsFragment)
        }
    }

    private fun setupRecyclerViews() {
        storyAdapter = StoryAdapter(findNavController())
        binding.storiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }

        activePollsAdapter = ActivePollsAdapter(
            onItemClick = { poll -> openPollLink(poll) },
            onJoinClick = { poll -> openPollLink(poll) }
        )
        binding.recyclerViewActivePolls.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activePollsAdapter
        }

        locationPollsAdapter = LocationPollsAdapter(
            pollList = emptyList(),
            onItemClick = { poll, distance, address -> showPollDetails(poll, distance, address) }
        )
        binding.locationPollsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationPollsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitList(stories)
            binding.storiesRecyclerView.visibility = if (stories.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.activePolls.observe(viewLifecycleOwner) { polls ->
            android.util.Log.d("HomeFragment", "=== ACTIVE POLLS OBSERVER ===")
            android.util.Log.d("HomeFragment", "Received polls count: ${polls?.size ?: 0}")
            polls?.forEachIndexed { index, poll ->
                android.util.Log.d("HomeFragment", "Poll $index: ${poll.title} - Link: ${poll.link}")
            }
            
            activePollsAdapter.submitList(polls)
            val hasActivePolls = !polls.isNullOrEmpty()
            android.util.Log.d("HomeFragment", "hasActivePolls: $hasActivePolls")
            android.util.Log.d("HomeFragment", "Setting activePollsCard visibility to: ${if (hasActivePolls) "VISIBLE" else "GONE"}")
            binding.activePollsCard.visibility = if (hasActivePolls) View.VISIBLE else View.GONE
        }

        viewModel.locationPolls.observe(viewLifecycleOwner) { polls ->
            locationPollsAdapter.updateData(polls, currentLocation?.latitude, currentLocation?.longitude)
            val hasLocationPolls = !polls.isNullOrEmpty()
            binding.locationPollsCard.visibility = if (hasLocationPolls) View.VISIBLE else View.GONE
            binding.buttonExploreOnMap.visibility = if (hasLocationPolls) View.VISIBLE else View.GONE
        }

        viewModel.userBalance.observe(viewLifecycleOwner) { balance ->
            balance?.let {
                // Ana sayfa kullanıcı bilgileri
                val displayName = if (!it.firstName.isNullOrBlank()) {
                    it.firstName
                } else {
                    it.username
                }
                binding.textViewUserName.text = displayName
                binding.textViewBalance.text = String.format("₺%.2f", it.balance)
                binding.textViewPoints.text = "${it.points} Puan >"
                
                // Cüzdan kartı bilgileri
                binding.textViewWalletBalance.text = String.format("₺%.2f", it.balance)
                binding.textViewWalletPoints.text = "${it.points} puan"
            } ?: run {
                // Backend'den veri gelmezse Firebase'dan al
                loadFirebaseUserData()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }
        }
        
        // Achievement observers
        achievementViewModel.userRanking.observe(viewLifecycleOwner) { ranking ->
            updateUserBadge(ranking?.badge)
        }
    }

    private fun openPollLink(poll: Poll) {
        poll.link?.let { url ->
            if (url.isNotEmpty()) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Geçerli bir link bulunamadı.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Anket linki boş.", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(context, "Anket linki bulunamadı.", Toast.LENGTH_SHORT).show()
    }

    private fun showPollDetails(poll: LocationPoll, distance: String, address: String) {
        val bottomSheet = LocationPollDetailBottomSheetFragment.newInstance(poll, distance, address)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocationAndLoadData()
        }
    }

    private fun getCurrentLocationAndLoadData() {
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "yeni_kullanici") ?: "yeni_kullanici"
        val userId = sharedPreferences.getString("userId", null)?.toLongOrNull()
        
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = it
                }
                viewModel.loadAllData(username)
                
                // Load user ranking for badge
                userId?.let { 
                    android.util.Log.d("HomeFragment", "Loading user ranking for userId: $it")
                    achievementViewModel.loadUserRanking(it) 
                }
            }.addOnFailureListener {
                 viewModel.loadAllData(username)
                 userId?.let { achievementViewModel.loadUserRanking(it) }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
            val username = sharedPreferences.getString("username", "yeni_kullanici") ?: "yeni_kullanici"
            val userId = sharedPreferences.getString("userId", null)?.toLongOrNull()
            
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndLoadData()
            } else {
                Toast.makeText(
                    context,
                    "Konum izni olmadan konum bazlı anketler yüklenemez.",
                    Toast.LENGTH_LONG
                ).show()
                viewModel.loadAllData(username)
                userId?.let { achievementViewModel.loadUserRanking(it) }
            }
        }
    }

    private fun updateUserBadge(badge: String?) {
        android.util.Log.d("HomeFragment", "Updating user badge: $badge")
        
        when (badge) {
            "gold" -> {
                binding.imageViewUserBadge.setImageResource(R.drawable.ic_crown_gold)
                binding.imageViewUserBadge.visibility = View.VISIBLE
            }
            "silver" -> {
                binding.imageViewUserBadge.setImageResource(R.drawable.ic_crown_silver)
                binding.imageViewUserBadge.visibility = View.VISIBLE
            }
            "bronze" -> {
                binding.imageViewUserBadge.setImageResource(R.drawable.ic_crown_bronze)
                binding.imageViewUserBadge.visibility = View.VISIBLE
            }
            else -> {
                binding.imageViewUserBadge.visibility = View.GONE
            }
        }
    }

    private fun loadFirebaseUserData() {
        // Önce SharedPreferences'dan kontrol et
        val sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val savedDisplayName = sharedPreferences.getString("userDisplayName", "")
        val savedEmail = sharedPreferences.getString("userEmail", "")
        
        if (!savedDisplayName.isNullOrEmpty()) {
            android.util.Log.d("HomeFragment", "Loading from SharedPreferences...")
            android.util.Log.d("HomeFragment", "  Display Name: $savedDisplayName")
            android.util.Log.d("HomeFragment", "  Email: $savedEmail")
            
            val firstName = savedDisplayName.split(" ").firstOrNull() ?: savedEmail?.split("@")?.firstOrNull() ?: "Kullanıcı"
            
            // Ana sayfa bilgilerini SharedPreferences'dan güncelle
            binding.textViewUserName.text = firstName
            binding.textViewBalance.text = "₺0.00" // Default değer
            binding.textViewPoints.text = "0 Puan >" // Default değer
            
            // Cüzdan kartı bilgileri
            binding.textViewWalletBalance.text = "₺0.00"
            binding.textViewWalletPoints.text = "0 puan"
            
            android.util.Log.d("HomeFragment", "SharedPreferences user name set to: $firstName")
            return
        }
        
        // SharedPreferences'da bilgi yoksa Firebase'dan dene
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        
        if (currentUser != null) {
            android.util.Log.d("HomeFragment", "Loading Firebase user data...")
            android.util.Log.d("HomeFragment", "  Display Name: ${currentUser.displayName}")
            android.util.Log.d("HomeFragment", "  Email: ${currentUser.email}")
            
            val displayName = currentUser.displayName ?: ""
            val firstName = if (displayName.isNotEmpty()) {
                displayName.split(" ").firstOrNull() ?: currentUser.email?.split("@")?.firstOrNull() ?: "Kullanıcı"
            } else {
                currentUser.email?.split("@")?.firstOrNull() ?: "Kullanıcı"
            }
            
            // Ana sayfa bilgilerini Firebase'dan güncelle
            binding.textViewUserName.text = firstName
            binding.textViewBalance.text = "₺0.00" // Default değer
            binding.textViewPoints.text = "0 Puan >" // Default değer
            
            // Cüzdan kartı bilgileri
            binding.textViewWalletBalance.text = "₺0.00"
            binding.textViewWalletPoints.text = "0 puan"
            
            android.util.Log.d("HomeFragment", "Firebase user name set to: $firstName")
        } else {
            // Firebase user da yoksa default değerler
            binding.textViewUserName.text = "Kullanıcı"
            binding.textViewBalance.text = "₺0.00"
            binding.textViewPoints.text = "0 Puan >"
            binding.textViewWalletBalance.text = "₺0.00"
            binding.textViewWalletPoints.text = "0 puan"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
} 