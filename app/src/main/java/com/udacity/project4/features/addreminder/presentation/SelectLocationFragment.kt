package com.udacity.project4.features.addreminder.presentation


import android.Manifest.permission
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.common.extensions.isPermissionGranted
import com.udacity.project4.common.extensions.openAppSettings
import com.udacity.project4.common.extensions.showCustomDialog
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.sharedpresentation.ReminderItemView
import java.util.*

private const val MAP_START_ZOOM = 15.0F
private val DEFAULT_LOCATION = LatLng(-23.5822877,-46.6530567)

class SelectLocationFragment : Fragment(), OnMapReadyCallback {

    private val currentClassName = SelectLocationFragment::class.java.simpleName

    private var selectedReminder: ReminderItemView? = null

    private lateinit var binding: FragmentSelectLocationBinding
    private var map: GoogleMap? = null

    private var currentLocationMarker: Marker? = null
    private var locationPermissionGranted: Boolean = false
    private var lastKnownLocation: Location? = null
    private var fusedLocation: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null

    private val args: SelectLocationFragmentArgs by navArgs()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                onPermissionAccepted()
            }
            permissions.getOrDefault(permission.ACCESS_COARSE_LOCATION, false) -> {
                // Precise location access granted.
                onPermissionAccepted()
            }
            !shouldShowRequestPermissionRationale(permission.ACCESS_COARSE_LOCATION) ||
            !shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION) -> {
                // access to the location was denied, the user has checked the Don't ask again.
                Log.d(currentClassName,"Called: shouldShowRequestPermissionRationale")
                showLocationPermissionDialog()
            }
            else -> {
                // No location access granted.
                showLocationPermissionDialog()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLocationBinding.inflate(layoutInflater)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.reminderMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        selectedReminder = args.lastSelectedLocation

        val googlePlayServicesStatus =  GoogleApiAvailability
            .getInstance()
            .isGooglePlayServicesAvailable(requireContext())

        if (googlePlayServicesStatus != ConnectionResult.SUCCESS) {
            val dialog = GoogleApiAvailability
                .getInstance().getErrorDialog(this, googlePlayServicesStatus, 10)
            dialog?.show()
        }
    }

    @TargetApi(29)
    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun showLocationPermissionDialog() {
        activity?.showCustomDialog(
            context = requireContext(),
            title = getString(R.string.message_permission_location_title),
            message = getString(R.string.message_show_map_location),
            positiveButtonText = getString(R.string.label_allow_now),
            positiveButtonAction = { openAppSettings() },
            negativeButtonText = resources.getString(R.string.label_select_manually)
        )
    }

    /**
     * This method is called only when permission is granted.
     */
    @SuppressLint("MissingPermission")
    private fun onPermissionAccepted() {
        locationPermissionGranted = true
        map?.isMyLocationEnabled = true
        getDeviceLocation()
    }

    private fun setupListeners() {
        binding.buttonGetSelectedLocation.setOnClickListener {
            findNavController().navigate(
                SelectLocationFragmentDirections.navigateToSaveReminderFragment(selectedReminder)
            )
        }

        binding.selectLocationToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.saveReminderFragment,false)
        }

        binding.selectLocationToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.normal_map -> {
                    map?.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }
                R.id.hybrid_map -> {
                    map?.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }
                R.id.satellite_map -> {
                    map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }
                R.id.terrain_map -> {
                    map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    true
                }
                R.id.map_local_style_dark -> {
                    setLocalMapStyle(map, R.raw.map_local_style_dark)
                    true
                }
                R.id.map_local_style_night-> {
                    setLocalMapStyle(map, R.raw.map_local_style_night)
                    true
                }
                R.id.map_local_style_night_highway_highlighted -> {
                    setLocalMapStyle(map, R.raw.map_local_style_night_highway_highlighted)
                    true
                }
                R.id.map_local_style_standard_highway_highlighted -> {
                    setLocalMapStyle(map, R.raw.map_local_style_standard_highway_highlighted)
                    true
                }
                else -> false
            }
        }
    }

    private fun initLocationService() {
        activity?.let {
            Places.initialize(it.applicationContext, BuildConfig.MAPS_API_KEY)
            placesClient = Places.createClient(requireContext())
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    /**
     * This method is called only when permission is granted.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val lat = selectedReminder?.latitude
        val lng = selectedReminder?.longitude

        if (lat != null && lng != null) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), MAP_START_ZOOM))
            setMarker(LatLng(lat, lng), selectedReminder?.locationName)
        } else {
            if (locationPermissionGranted) {
                try {
                    val locationResult = fusedLocation?.lastLocation
                    locationResult?.addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.result
                            lastKnownLocation?.let { location ->
                                setupMapUI()
                                map?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(location.latitude, location.longitude),
                                        MAP_START_ZOOM
                                    )
                                )
                                setMarker(LatLng(location.latitude, location.longitude), "My location")
                            }
                        } else {
                            Log.d(currentClassName, "Current location is null. Using defaults.")
                            Log.d(currentClassName, "Exception: %s", task.exception)
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    DEFAULT_LOCATION,
                                    MAP_START_ZOOM
                                )
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    Log.e("Exception: %s", e.message, e)
                }
            }
        }
    }

    /** At this the permission is already allowed by the user. Otherwise this method
     * would not be called .
     * */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        setupMapUI()
        initLocationService()
        binding.loadingMap.isGone = true
        map = googleMap
        map?.let { onClickSetMarker(it) }
        map?.let { setPoiClick(it) }

        if (isPermissionGranted(permission.ACCESS_FINE_LOCATION) &&
            isPermissionGranted(permission.ACCESS_COARSE_LOCATION)) {
            locationPermissionGranted = true
            map?.isMyLocationEnabled = true
            getDeviceLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun setupMapUI() {
        map?.uiSettings?.isCompassEnabled = true
        map?.uiSettings?.isIndoorLevelPickerEnabled = true
        map?.uiSettings?.isMapToolbarEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
        map?.uiSettings?.setAllGesturesEnabled(true)
    }

    private fun onClickSetMarker(mapWithMarker: GoogleMap) {
        mapWithMarker.setOnMapLongClickListener { latLng ->
            setMarker(latLng)
        }
        mapWithMarker.setOnMapClickListener { latLng ->
            setMarker(latLng)
        }
    }

    private fun setMarker(latLng: LatLng, locationName: String? = null) {
        binding.buttonGetSelectedLocation.isEnabled = true
        binding.buttonGetSelectedLocation.text = getString(R.string.action_set_location)

        currentLocationMarker?.remove()

        val snippet = String.format(
            Locale.getDefault(),
            getString(R.string.lat_long_snippet),
            latLng.latitude,
            latLng.longitude
        )

        currentLocationMarker = map?.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(locationName ?: getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
        )
        currentLocationMarker?.showInfoWindow()
        selectedReminder = selectedReminder?.copy(
            isPoi = false,
            poiId = null,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
        )
    }

    private fun setPoiClick(mapWithPoi: GoogleMap?) {
        mapWithPoi?.setOnPoiClickListener { poi ->
            binding.buttonGetSelectedLocation.isEnabled = true
            binding.buttonGetSelectedLocation.text = getString(R.string.action_set_location)
            currentLocationMarker?.remove()
            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                poi.latLng.latitude,
                poi.latLng.longitude
            )
            currentLocationMarker = mapWithPoi.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
                    .snippet(snippet)
            )
            currentLocationMarker?.showInfoWindow()
            selectedReminder = selectedReminder?.copy(
                isPoi = true,
                poiId = poi.placeId,
                latitude = poi.latLng.latitude,
                longitude = poi.latLng.longitude,
                locationName = poi.name.replace("\n", " ")
            )
        }
    }

    private fun setLocalMapStyle(map: GoogleMap?, resourceStyle: Int) {
        runCatching {
            val isMapStyleValid = map?.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(requireContext(), resourceStyle)
                )
            Log.d(currentClassName, "Map Style is valid: $isMapStyleValid")
        }.onFailure {
            Log.d(currentClassName, "Map Style could not be loaded")
        }
    }
}
