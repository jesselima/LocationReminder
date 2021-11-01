package com.udacity.locationreminder.locationreminders.addreminder


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.udacity.locationreminder.BuildConfig
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentSelectLocationBinding
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomToast
import java.util.Locale

private const val REQUEST_LOCATION_PERMISSION = 1
private const val MAP_START_ZOOM = 15.0F
private val DEFAULT_LOCATION = LatLng(-23.5822877,-46.6530567)

class SelectLocationFragment : Fragment(), OnMapReadyCallback {

    private val currentClassName = SelectLocationFragment::class.java.simpleName

    private lateinit var selectedReminder: ReminderItemView

    private lateinit var binding: FragmentSelectLocationBinding
    private var map: GoogleMap? = null

    private var currentLocationMarker: Marker? = null
    private var locationPermissionGranted: Boolean = false
    private var lastKnownLocation: Location? = null
    private var fusedLocation: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null

    private val args: SelectLocationFragmentArgs by navArgs()

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
        setupAppBarAndMenuListeners()
        selectedReminder = ReminderItemView(
            latitude = args.lastSelectedLocation?.latitude,
            longitude = args.lastSelectedLocation?.longitude,
            locationName = args.lastSelectedLocation?.locationName
        )
    }

    private fun setupAppBarAndMenuListeners() {
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

    private fun requestLocationPermissionsInitMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            locationPermissionGranted = true
            getDeviceLocation()
        }
    }

    private fun initLocationService() {
        activity?.let {
            Places.initialize(it.applicationContext, BuildConfig.MAPS_API_KEY)
            placesClient = Places.createClient(requireContext())
        }
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        val lat = selectedReminder.latitude
        val lng = selectedReminder.longitude

        if (lat != null && lng != null) {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), MAP_START_ZOOM))
            setMarker(LatLng(lat, lng), selectedReminder.locationName)
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
                            Log.e(currentClassName, "Exception: %s", task.exception)
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        initLocationService()
        binding.loadingMap.isGone = true
        map = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionsInitMyLocation()
        } else {
            locationPermissionGranted = true
            getDeviceLocation()
            map?.isMyLocationEnabled = true
            setupMapUI()
        }
    }

    /** At this the permission is already allowed by the user. Otherwise this method
     * would not be called .
     * */
    @SuppressLint("MissingPermission")
    private fun setupMapUI() {
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isCompassEnabled = true
        map?.uiSettings?.isIndoorLevelPickerEnabled = true
        map?.uiSettings?.isMapToolbarEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.uiSettings?.isMyLocationButtonEnabled = true
        map?.uiSettings?.setAllGesturesEnabled(true)
        map?.let { onClickSetMarker(it) }
        map?.let { setPoiClick(it) }
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
        selectedReminder = selectedReminder.copy(
            title = null,
            description = null,
            isPoi = false,
            poiId = null,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            locationName = null
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
                    .title(String.format(
                        getString(R.string.poi_lat_long_snippet),
                        getString(R.string.poi_dropped_pin),
                        poi.name))
                    .snippet(snippet)
            )
            currentLocationMarker?.showInfoWindow()
            selectedReminder = selectedReminder?.copy(
                title = null,
                description = null,
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isEmpty() && (grantResults.first() != PackageManager.PERMISSION_GRANTED)) {
                context?.showCustomToast(
                    titleResId = R.string.permission_denied_explanation,
                    toastType = ToastType.WARNING
                )
            } else {
                locationPermissionGranted = true
                getDeviceLocation()
                setupMapUI()
            }
        }
    }
}
