package com.udacity.locationreminder.locationreminders.addreminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.locationreminder.BuildConfig
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentAddReminderBinding
import com.udacity.locationreminder.locationreminders.ReminderDescriptionActivity.Companion.ACTION_GEOFENCE_EVENT
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManager
import com.udacity.locationreminder.locationreminders.geofence.isAndroidOsEqualsOrGreaterThan
import com.udacity.locationreminder.locationreminders.geofence.isPermissionNotGranted
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.showDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import java.util.concurrent.TimeUnit

private const val PENDING_INTENT_REQUEST_CODE = 0

class AddReminderFragment : Fragment() {

    private lateinit var binding: FragmentAddReminderBinding
    private val addViewModel: AddReminderViewModel by sharedViewModel()
    private val geofenceManager: GeofenceManager by inject()

    private var _currentReminderData: ReminderItemView = ReminderItemView()
    private val args: AddReminderFragmentArgs by navArgs()

    private lateinit var geofenceClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            requireContext(),
            PENDING_INTENT_REQUEST_CODE,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java).apply {
                action = ACTION_GEOFENCE_EVENT
            },
            when {
                isAndroidOsEqualsOrGreaterThan(osVersion = Build.VERSION_CODES.M) -> {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                }
                else -> PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddReminderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("==>>> ", "AddReminderFragment.onViewCreated()")
        setupObservers()
        setupListeners()
        geofenceClient = LocationServices.getGeofencingClient(requireActivity())

        // TODO remove it from here
        _currentReminderData.locationName = args.lastSelectedLocation?.locationName
        _currentReminderData.latitude = args.lastSelectedLocation?.latitude
        _currentReminderData.longitude = args.lastSelectedLocation?.longitude
        if(args.lastSelectedLocation?.isPoi == true) {
            _currentReminderData.isPoi = true
            _currentReminderData.poiId = args.lastSelectedLocation?.poiId
        }

        addViewModel.setSelectedReminder(_currentReminderData)
    }

    private fun setupListeners() {
        with(binding) {
            buttonSelectLocation.setOnClickListener {
                findNavController().navigate(
                    AddReminderFragmentDirections.navigateToSelectLocation(_currentReminderData)
                )
            }

            saveReminderToolbar.setNavigationOnClickListener {
                findNavController().navigate(AddReminderFragmentDirections.navigateToReminderList())
            }

            binding.sliderCircularRadius.addOnChangeListener { _, value, _ ->
                _currentReminderData.circularRadius = value
                Log.d("==>>>", _currentReminderData.circularRadius.toString())
                binding.textCurrentCircularRadius.text = String.format(
                    getString(R.string.circular_radius_unit), value.toInt().toString()
                )
            }

            binding.inputLayoutExpirationDuration.editText?.setText(getString(R.string.units_days))
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.list_item,
                resources.getStringArray(R.array.units_array)
            )
            (binding.inputLayoutExpirationDuration.editText as? AutoCompleteTextView)?.setAdapter(adapter)

            binding.inputLayoutExpirationDuration.editText?.addTextChangedListener {
                Log.d("==>>>", it.toString())
                binding.expirationDurationValueTextInputLayout.isEnabled =
                    it.toString() != ExpirationUnits.NEVER.name
            }

            binding.expirationDurationValueTextInputLayout.editText?.addTextChangedListener {
                    durationNumber ->
                        _currentReminderData.expiration = mapInputUnitsExpirationValue(
                            unit = binding.inputLayoutExpirationDuration.editText?.text.toString(),
                            value = durationNumber.toString().toLong()
                        )
                        Log.d("==>>>", durationNumber.toString())
            }

            binding.radioGroupTransitionType.setOnCheckedChangeListener { group, checkedId ->
                _currentReminderData.transitionType = when (checkedId) {
                    R.id.radioButtonExit -> {
                        Geofence.GEOFENCE_TRANSITION_EXIT
                        Log.d("==>>>", "Exit")
                    }
                    else -> {
                        Geofence.GEOFENCE_TRANSITION_EXIT
                        Log.d("==>>>", "Enter")
                    }
                }
                Log.d("==>>>", _currentReminderData.toString())
            }

            binding.actionButtonSaveReminder.setOnClickListener {
                _currentReminderData.title = binding.inputLayoutTitle.editText?.text.toString()
                _currentReminderData.locationName = binding.inputLayoutLocationName.editText?.text.toString()
                _currentReminderData.description = binding.inputLayoutDescription.editText?.text.toString()
                addViewModel.setSelectedReminder(_currentReminderData)
                saveReminder(_currentReminderData)
            }
        }

        // TODO - Move the input validation logic to a UseCase
        //  The fragment should only call ViewModel methods, which will use cases to validate values
        //  Then return the result to the view model, which returns it to the fragment as action/state.
        binding.reminderTitle.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.inputLayoutTitle.error = null
        }

        binding.reminderLocationName.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.inputLayoutLocationName.error = null
        }

        binding.reminderDescription.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotBlank() == true) binding.inputLayoutDescription.error = null
        }
    }

    private fun mapInputUnitsExpirationValue(unit: String, value: Long): Long {
        return when (unit) {
            ExpirationUnits.HOURS.name -> TimeUnit.HOURS.toMillis(value)
            ExpirationUnits.DAYS.name -> TimeUnit.DAYS.toMillis(value)
            else -> -1L
        }
    }

    enum class ExpirationUnits {
        HOURS,
        DAYS,
        NEVER
    }

    private fun setupObservers() {
        addViewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
            reminder?.latitude?.let { lat ->
                reminder.longitude?.let { lng ->
                    binding.textSelectedLocation.text = String.format(
                        Locale.getDefault(), getString(R.string.lat_long_snippet), lat, lng
                    )
                    binding.buttonSelectLocation.text =
                        getString(R.string.text_button_change_location)
                }
            } ?: run {
                binding.buttonSelectLocation.text = getString(R.string.text_button_select_location)
            }

            reminder?.locationName?.let {
                binding.reminderLocationName.setText(it)
            } ?: binding.reminderLocationName.setText("")
        }

        addViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.actionButtonSaveReminder.isVisible = state.isLoading.not()
        }

        addViewModel.action.observe(viewLifecycleOwner) { action ->
            when(action) {
                is AddReminderAction.AddReminderError ->
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_error,
                        toastType = ToastType.ERROR
                    )
                is AddReminderAction.AddReminderSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_success,
                        toastType = ToastType.SUCCESS
                    )
                    addGeofence(_currentReminderData)
                }
                is AddReminderAction.InputErrorFieldTitle ->
                    binding.inputLayoutExpirationDuration.error = getString(R.string.message_input_error_title)
                is AddReminderAction.InputErrorFieldLocation ->
                    binding.inputLayoutLocationName.error = getString(R.string.message_input_error_location)
                is AddReminderAction.InputErrorFieldDescription ->
                    binding.inputLayoutDescription.error = getString(R.string.message_input_error_description)
                is AddReminderAction.InputErrorMissingLatLong ->
                    context?.showCustomToast(
                        titleResId = R.string.message_input_error_lat_long_missing,
                        toastType = ToastType.INFO,
                        durationToast = Toast.LENGTH_LONG
                    )
                is AddReminderAction.ClearErrors -> {
                    binding.inputLayoutExpirationDuration.error = null
                    binding.inputLayoutLocationName.error = null
                    binding.inputLayoutDescription.error = null
                }
            }
        }
    }

    private fun saveReminder(reminder: ReminderItemView) {
        if (isBackgroundPermissionGranted()) {
            addViewModel.saveReminder(reminder)
        }
    }

    private fun addGeofence(reminder: ReminderItemView) {
        geofenceManager.addGeofence(
            geofenceClient = geofenceClient,
            geofencePendingIntent = geofencePendingIntent,
            id = reminder.id.toString(),
            latitude = reminder.latitude,
            longitude = reminder.longitude,
            onAddGeofenceSuccess = { onAddGeofenceSuccess() },
            onAddGeofenceFailure = { reasonStringRes -> onAddGeofenceFailure(reasonStringRes)  }
        )
    }


    private fun isBackgroundPermissionGranted(): Boolean {
        return if (isPermissionNotGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            showDialog(
                context = requireContext(),
                title = getString(R.string.message_request_background_location_title),
                message = getString(R.string.message_request_background_location_description),
                positiveButtonText = getString(R.string.settings),
                positiveButtonAction = {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                },
            )
            false
        } else {
            true
        }
    }

    private fun onAddGeofenceSuccess() {
        context?.showCustomToast(titleResId = R.string.geofence_added)
        findNavController().navigate(
            AddReminderFragmentDirections.navigateToReminderList()
        )
    }

    private fun onAddGeofenceFailure(@StringRes reasonStringRes: Int) {
        context?.showCustomToast(
            titleText = String.format(
                getString(R.string.geofence_error),
                getString(reasonStringRes)
            ),
            toastType = ToastType.WARNING
        )
    }
}
