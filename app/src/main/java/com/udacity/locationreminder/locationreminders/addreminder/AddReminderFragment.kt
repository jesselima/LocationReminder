package com.udacity.locationreminder.locationreminders.addreminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
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
import com.udacity.locationreminder.utils.hideKeyboard
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.showDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

private const val PENDING_INTENT_REQUEST_CODE = 0

class AddReminderFragment : Fragment() {

    private lateinit var binding: FragmentAddReminderBinding
    private val viewModel: AddReminderViewModel by sharedViewModel()
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
        setupNavigationListeners()
        setupObservers()
        setupListeners()
        geofenceClient = LocationServices.getGeofencingClient(requireActivity())
        checkNavArgsUpdateViewModelData()
    }

    private fun checkNavArgsUpdateViewModelData() {
        _currentReminderData.locationName = args.lastSelectedLocation?.locationName
        _currentReminderData.latitude = args.lastSelectedLocation?.latitude
        _currentReminderData.longitude = args.lastSelectedLocation?.longitude
        _currentReminderData.isPoi = args.lastSelectedLocation?.isPoi ?: false
        _currentReminderData.poiId = args.lastSelectedLocation?.poiId
        viewModel.setSelectedReminder(_currentReminderData)
    }

    private fun setupNavigationListeners() {
        binding.buttonSelectLocation.setOnClickListener {
            findNavController().navigate(
                AddReminderFragmentDirections.navigateToSelectLocation(_currentReminderData)
            )
        }

        binding.saveReminderToolbar.setNavigationOnClickListener {
            findNavController().navigate(AddReminderFragmentDirections.navigateToReminderList())
        }
    }

    private fun setupListeners() {
        with(binding) {

            binding.reminderTitle.doOnTextChanged { text, _, _, _ ->
                if (viewModel.isTitleValid(text.toString())) binding.inputLayoutTitle.error = null
            }

            binding.reminderLocationName.doOnTextChanged { text, _, _, _ ->
                if (viewModel.isLocationNameValid(text.toString())) {
                    binding.inputLayoutLocationName.error = null
                }
            }

            binding.reminderDescription.doOnTextChanged { text, _, _, _ ->
                if (viewModel.isDescriptionValid(text.toString())) {
                    binding.inputLayoutDescription.error = null
                }
            }

            sliderCircularRadius.addOnChangeListener { _, value, _ ->
                _currentReminderData.circularRadius = value
                textCurrentCircularRadius.text = String.format(
                    getString(R.string.circular_radius_unit), value.toInt().toString()
                )
            }

            inputLayoutExpirationDuration.editText?.setText(getString(R.string.units_days))
            (inputLayoutExpirationDuration.editText as? AutoCompleteTextView)?.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item,resources.getStringArray(
                    R.array.units_array)))
            inputLayoutExpirationDuration.editText?.addTextChangedListener {
                inputLayoutExpirationDurationValue.isEnabled =
                    it.toString() != ExpirationUnits.NEVER.name
            }

            inputLayoutExpirationDurationValue.editText?.addTextChangedListener { durationInput ->
                durationInput?.let { value ->
                    if(value.isNotEmpty()) {
                        _currentReminderData.expiration = mapInputUnitsExpirationValue(
                            unit = inputLayoutExpirationDuration.editText?.text.toString(),
                            value = value.toString().toLong()
                        )
                    }
                }
            }

            radioGroupTransitionType.setOnCheckedChangeListener { _, checkedId ->
                _currentReminderData.transitionType = when (checkedId) {
                    R.id.radioButtonEnter -> {
                        Geofence.GEOFENCE_TRANSITION_ENTER
                    }
                    R.id.radioButtonExit -> {
                        Geofence.GEOFENCE_TRANSITION_EXIT
                    }
                    else -> -1
                }
            }

            inputLayoutExpirationDuration.setOnClickListener { hideKeyboard() }
            autocompleteExpirationDuration.setOnClickListener { hideKeyboard() }

            actionButtonSaveReminder.setOnClickListener {
                extractInputValues()
                viewModel.setSelectedReminder(_currentReminderData)
                if(isBackgroundPermissionGranted()) viewModel.validateFieldsSaveReminder()
            }
        }
    }

    private fun extractInputValues() {
        _currentReminderData.title = binding.inputLayoutTitle.editText?.text.toString()
        _currentReminderData.locationName = binding.inputLayoutLocationName.editText?.text.toString()
        _currentReminderData.description = binding.inputLayoutDescription.editText?.text.toString()
    }

    private fun setupObservers() {
        viewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
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

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.actionButtonSaveReminder.isVisible = state.isLoading.not()
        }

        viewModel.action.observe(viewLifecycleOwner) { action ->
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
                    binding.inputLayoutTitle.error =
                        getString(R.string.message_input_error_title)
                is AddReminderAction.InputErrorFieldLocationName ->
                    binding.inputLayoutLocationName.error =
                        getString(R.string.message_input_error_location)
                is AddReminderAction.InputErrorFieldDescription ->
                    binding.inputLayoutDescription.error =
                        getString(R.string.message_input_error_description)
                is AddReminderAction.InputErrorMissingLatLong -> {
                    binding.buttonSelectLocation.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    )
                    binding.buttonSelectLocation.setStrokeColorResource(R.color.colorAccent)
                    context?.showCustomToast(
                        titleResId = R.string.message_input_error_lat_long_missing,
                        toastType = ToastType.INFO,
                        durationToast = Toast.LENGTH_LONG
                    )
                }
                is AddReminderAction.ClearErrors -> {
                    binding.inputLayoutExpirationDuration.error = null
                    binding.inputLayoutLocationName.error = null
                    binding.inputLayoutDescription.error = null
                }
            }
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
