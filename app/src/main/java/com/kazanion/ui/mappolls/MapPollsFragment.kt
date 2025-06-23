package com.kazanion.ui.mappolls

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kazanion.R
import com.kazanion.databinding.FragmentMapPollsBinding
import com.kazanion.network.ApiService
import com.kazanion.network.LocationPoll
import com.kazanion.ui.locationpoll.LocationPollDetailBottomSheetFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch

class MapPollsFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapPollsBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var apiService: ApiService
    private var currentLocation: Location? = null
    private lateinit var mapPollsAdapter: MapPollsAdapter
    private var polls: List<LocationPoll> = emptyList()
    private val markerPollMap = HashMap<Marker, LocationPoll>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapPollsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        apiService = ApiService.create()

        mapPollsAdapter = MapPollsAdapter(
            polls = polls,
            onItemClick = { poll, _, _ ->
                showPollDetails(poll)
            }
        )
        binding.pollsListView.adapter = mapPollsAdapter
        binding.pollsListView.layoutManager = LinearLayoutManager(context)

        binding.toggleButtonView.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonMapView -> {
                        binding.root.findViewById<View>(R.id.mapFragment).visibility = View.VISIBLE
                        binding.aboutText.visibility = View.VISIBLE
                        binding.pollsListView.visibility = View.GONE
                    }
                    R.id.buttonListView -> {
                        binding.root.findViewById<View>(R.id.mapFragment).visibility = View.GONE
                        binding.aboutText.visibility = View.GONE
                        binding.pollsListView.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.toggleButtonView.check(R.id.buttonMapView)

        if (checkLocationPermission()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }

        googleMap.setOnMarkerClickListener { marker ->
            markerPollMap[marker]?.let { poll ->
                showPollDetails(poll)
            }
            true
        }
    }

    private fun showPollDetails(poll: LocationPoll) {
        val distanceStr = if (currentLocation != null && poll.latitude != null && poll.longitude != null) {
            val pollLocation = Location("").apply {
                latitude = poll.latitude
                longitude = poll.longitude
            }
            val distance = currentLocation!!.distanceTo(pollLocation) / 1000
            String.format("%.1f km", distance)
        } else {
            null
        }

        val bottomSheet = LocationPollDetailBottomSheetFragment.newInstance(poll, distanceStr, poll.description)
        bottomSheet.setOnZoomToPollListener { latLng ->
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = it
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    loadLocationBasedPolls()
                }
            }
        }
    }

    private fun loadLocationBasedPolls() {
        Log.d("MapPollsFragment", "Loading location based polls...")
        lifecycleScope.launch {
            try {
                polls = apiService.getLocationBasedPolls()
                Log.d("MapPollsFragment", "Polls loaded: "+polls.size)
                mapPollsAdapter.updateData(polls, currentLocation?.latitude, currentLocation?.longitude)
                updateMapMarkers()
            } catch (e: Exception) {
                Log.e("MapPollsFragment", "Network error", e)
                Toast.makeText(context, "Ağ hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMapMarkers() {
        googleMap?.clear()
        markerPollMap.clear()
        for (poll in polls) {
            if (poll.latitude != null && poll.longitude != null) {
                val pollLocation = LatLng(poll.latitude, poll.longitude)
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(pollLocation)
                        .title(poll.title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
                if (marker != null) {
                    markerPollMap[marker] = poll
                }
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(context, "Konum izni gerekli", Toast.LENGTH_SHORT).show()
            }
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