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
import androidx.core.os.bundleOf
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
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManager
import com.udacity.locationreminder.locationreminders.geofence.isAndroidOsEqualsOrGreaterThan
import com.udacity.locationreminder.locationreminders.geofence.isPermissionNotGranted
import com.udacity.locationreminder.locationreminders.ReminderEditorActivity
import com.udacity.locationreminder.locationreminders.RemindersActivity
import com.udacity.locationreminder.utils.ReminderConstants
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.hideKeyboard
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.showCustomDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val PENDING_INTENT_REQUEST_CODE = 0
private const val CIRCULAR_RADIUS_DEFAULT = 50f
private const val TOAST_POSITION_ELEVATED = 300

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
                action = ReminderEditorActivity.ACTION_GEOFENCE_EVENT
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
        setupAppBarTitle()
    }

    private fun setupAppBarTitle() {
        binding.toolbar.title = if (args.isEditing) {
            getString(R.string.update_reminder)
        } else {
            getString(R.string.add_new_reminder)
        }
    }

    private fun checkNavArgsUpdateViewModelData() {
        args.lastSelectedLocation?.let { reminder ->
            _currentReminderData = reminder
            viewModel.setSelectedReminder(reminder)
        }
    }

    private fun setupNavigationListeners() {
        binding.buttonSelectLocation.setOnClickListener {
            findNavController().navigate(
                AddReminderFragmentDirections.navigateToSelectLocation(_currentReminderData)
            )
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        with(binding) {
            reminderTitle.doOnTextChanged { text, _, _, _ ->
                if (viewModel.isTitleValid(text.toString())) binding.inputLayoutTitle.error = null
            }

            reminderLocationName.doOnTextChanged { text, _, _, _ ->
                if (viewModel.isLocationNameValid(text.toString())) {
                    binding.inputLayoutLocationName.error = null
                }
            }

            reminderDescription.doOnTextChanged { text, _, _, _ ->
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
            setupExpirationUnitInputList()

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

            actionButtonSaveReminder.setOnClickListener {
                _currentReminderData.isGeofenceEnable = isGeofenceEnableSwitch.isChecked
                viewModel.setSelectedReminder(_currentReminderData)
                extractInputValues()
                if(isBackgroundPermissionGranted()) {
                    viewModel.validateFieldsSaveOrUpdateReminder(args.isEditing)
                }
            }
        }
    }

    private fun setupExpirationUnitInputList(selectedResId: Int = R.string.units_days) {
        with(binding) {
            inputLayoutExpirationDuration.editText?.setText(getString(selectedResId))
            (inputLayoutExpirationDuration.editText as? AutoCompleteTextView)?.setAdapter(
                ArrayAdapter(requireContext(), R.layout.list_item, resources.getStringArray(
                    R.array.units_array)))
            inputLayoutExpirationDuration.editText?.addTextChangedListener {
                inputLayoutExpirationDurationValue.isEnabled =
                    it.toString() != ExpirationUnits.NEVER.name
            }

            inputLayoutExpirationDuration.setOnClickListener { hideKeyboard() }
            autocompleteExpirationDuration.setOnClickListener { hideKeyboard() }
        }
    }

    private fun extractInputValues() {
        _currentReminderData.title = binding.inputLayoutTitle.editText?.text.toString()
        _currentReminderData.locationName = binding.inputLayoutLocationName.editText?.text.toString()
        _currentReminderData.description = binding.inputLayoutDescription.editText?.text.toString()
        viewModel.setSelectedReminder(_currentReminderData)
    }

    private fun setupObservers() {
        viewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
            with(binding) {
                reminder?.latitude?.let { lat ->
                    reminder.longitude?.let { lng ->
                        textSelectedLocation.text = String.format(
                            Locale.getDefault(), getString(R.string.lat_long_snippet), lat, lng
                        )
                        buttonSelectLocation.text =
                            getString(R.string.text_button_change_location)
                    }
                } ?: run {
                    buttonSelectLocation.text = getString(R.string.text_button_select_location)
                }

                reminder?.locationName?.let {
                    reminderLocationName.setText(it)
                } ?: reminderLocationName.setText("")

                reminderTitle.setText(reminder?.title)
                reminderDescription.setText(reminder?.description)
                sliderCircularRadius.value = reminder?.circularRadius ?: CIRCULAR_RADIUS_DEFAULT

                radioGroupTransitionType.check(
                    when(reminder?.transitionType) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> R.id.radioButtonEnter
                        else -> R.id.radioButtonExit
                    }
                )

                isGeofenceEnableSwitch.isChecked = reminder?.isGeofenceEnable ?: false

                reminder?.expiration?.let {
                    if (it == -1L) {
                        setupExpirationUnitInputList(R.string.units_never)
                        inputLayoutExpirationDurationValue.isEnabled = false
                    } else {
                        expirationDurationEditText.setText(
                            TimeUnit.MILLISECONDS.toDays(it).toString()
                        )
                    }
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.actionButtonSaveReminder.isVisible = state.isLoading.not()
        }

        viewModel.action.observe(viewLifecycleOwner) { action ->
            when(action) {
                is AddReminderAction.AddReminderError,
                is AddReminderAction.UpdateReminderError->
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_error,
                        toastType = ToastType.ERROR
                    )
                is AddReminderAction.AddReminderSuccess,
                is AddReminderAction.UpdateReminderSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_saving_reminder_success,
                        toastType = ToastType.SUCCESS,
                        offSetY = TOAST_POSITION_ELEVATED
                    )
                    if (_currentReminderData.isGeofenceEnable) {
                        addGeofence(_currentReminderData)
                    } else {
                        navigateToReminderList()
                    }
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
            activity?.showCustomDialog(
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
        navigateToReminderList()
    }

    private fun navigateToReminderList() {
        if (args.isEditing) {
            findNavController().navigate(
                R.id.navigateToReminderDetails,
                bundleOf(
                    ReminderConstants.argsKeyLastSelectedLocation to _currentReminderData,
                    ReminderConstants.argsKeyIsEditing to true,
                )
            )
        } else {
            startActivity(Intent(activity, RemindersActivity::class.java))
            activity?.finish()
        }
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
