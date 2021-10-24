package com.udacity.locationreminder.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.udacity.locationreminder.BuildConfig
import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseFragment
import com.udacity.locationreminder.databinding.FragmentSelectLocationBinding
import com.udacity.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomToast
import org.koin.android.ext.android.inject
import java.util.*

private const val REQUEST_LOCATION_PERMISSION = 1
private const val MAP_START_ZOOM = 14.0F
private val DEFAULT_LOCATION = LatLng(-23.5822877,-46.6530567)

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val currentClassName = SelectLocationFragment::class.java.simpleName

    override val _viewModel: SaveReminderViewModel by inject()

    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap

    private var currentLocationMarker: Marker? = null
    private var locationPermissionGranted: Boolean = false
    private var lastKnownLocation: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var placesClient: PlacesClient? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectLocationBinding.inflate(layoutInflater)
        val mapFragment = childFragmentManager.findFragmentById(R.id.reminderMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        requestLocationPermissionsInitMyLocation()
    }

    private fun setupListeners() {
        binding.selectLocationToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(R.id.saveReminderFragment,false)
        }
        binding.selectLocationToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.normal_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }
                R.id.hybrid_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }
                R.id.satellite_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }
                R.id.terrain_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_TERRAIN
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

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient?.lastLocation
                locationResult?.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        lastKnownLocation?.let { location ->
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude,location.longitude), MAP_START_ZOOM)
                            )
                        }
                    } else {
                        Log.d(currentClassName, "Current location is null. Using defaults.")
                        Log.e(currentClassName, "Exception: %s", task.exception)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, MAP_START_ZOOM))
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionsInitMyLocation()
            return
        } else {
            locationPermissionGranted = true
            getDeviceLocation()
            map.isMyLocationEnabled = true
        }
        onClickSetMarker(map)
        setPoiClick(map)
        map.isMyLocationEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isIndoorLevelPickerEnabled = true
        map.uiSettings.isMapToolbarEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)
    }

    private fun onClickSetMarker(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            setMarker(latLng)
        }
        map.setOnMapClickListener { latLng ->
            setMarker(latLng)
        }
    }

    private fun setMarker(latLng: LatLng) {
        currentLocationMarker?.remove()
        val snippet = String.format(
            Locale.getDefault(),
            getString(R.string.lat_long_snippet),
            latLng.latitude,
            latLng.longitude
        )

        currentLocationMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
        )
        currentLocationMarker?.showInfoWindow()
    }

    private fun setPoiClick(map: GoogleMap) {
        currentLocationMarker?.remove()
        map.setOnPoiClickListener { poi ->
            currentLocationMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            currentLocationMarker?.showInfoWindow()
            val data = "Name: ${poi.name} - Lat/Lng: ${poi.latLng.latitude} - ${poi.latLng.longitude} - Id: ${poi.placeId}"
        }
    }

    private fun setLocalMapStyle(map: GoogleMap, resourceStyle: Int) {
        runCatching {
            val isMapStyleValid = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(requireContext(), resourceStyle)
                )
            Log.d(currentClassName, "Map Style is valid: $isMapStyleValid")
        }.onFailure {
            Log.d(currentClassName, "Map Style could not be loaded")
        }
    }

    private fun requestLocationPermissionsInitMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            activity?.let{
                Places.initialize(it.applicationContext, BuildConfig.MAPS_API_KEY)
                placesClient = Places.createClient(requireContext())
            }
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            locationPermissionGranted = true
            getDeviceLocation()
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
                context?.showCustomToast(titleResId = R.string.location_permission_allowed)
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(
            context,
            "Title: ${marker.title} - Lat/Lng: ${marker.position.latitude} - ${marker.position.longitude} - tag: ${marker.tag}",
            Toast.LENGTH_LONG
        ).show()
        return false
    }
}
