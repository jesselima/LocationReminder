package com.udacity.project4.features.addreminder.presentation

import android.Manifest
import android.annotation.SuppressLint
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
import com.udacity.project4.R
import com.udacity.project4.BuildConfig
import com.udacity.project4.databinding.FragmentAddReminderBinding
import com.udacity.project4.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.geofence.GeofenceManager
import com.udacity.project4.features.RemindersActivity
import com.udacity.project4.common.ReminderConstants
import com.udacity.project4.common.extensions.ToastType
import com.udacity.project4.common.extensions.hideKeyboard
import com.udacity.project4.common.extensions.isPermissionNotGranted
import com.udacity.project4.common.extensions.showCustomDialog
import com.udacity.project4.common.extensions.showCustomToast
import com.udacity.project4.features.addreminder.mappers.ExpirationUnits
import com.udacity.project4.features.addreminder.mappers.mapInputUnitsExpirationValue
import com.udacity.project4.sharedpresentation.ReminderItemView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val PENDING_INTENT_REQUEST_CODE = 0
private const val CIRCULAR_RADIUS_DEFAULT = 50f
private const val TOAST_POSITION_ELEVATED = 350
private const val REMINDER_EXPIRATION_NEVER = -1L

@SuppressLint("UnspecifiedImmutableFlag")
class AddReminderFragment : Fragment() {

    private lateinit var binding: FragmentAddReminderBinding
    private val viewModel: AddReminderViewModel by sharedViewModel()
    private val geofenceManager: GeofenceManager by inject()

    private var _currentReminderData: ReminderItemView = ReminderItemView()
    private val args: AddReminderFragmentArgs by navArgs()

    private lateinit var geofenceClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            activity, PENDING_INTENT_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
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
        checkNavArgsUpdateViewModelData()
        setupAppBarTitle()
        geofenceClient = LocationServices.getGeofencingClient(requireActivity())
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

            reminderLocationName.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    viewModel.isLocationNameValid(text.toString())
                }
            }

            reminderTitle.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    viewModel.isTitleValid(text.toString())
                }
            }

            reminderDescription.doOnTextChanged { text, _, _, _ ->
                if (text?.isNotEmpty() == true) {
                    viewModel.isDescriptionValid(text.toString())
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
            (inputLayoutExpirationDuration.editText as? AutoCompleteTextView)?.apply {
                setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        resources.getStringArray(R.array.units_array)
                    )
                )
            }
            inputLayoutExpirationDuration.editText?.addTextChangedListener {
                inputLayoutExpirationDurationValue.isEnabled =
                    it.toString() != ExpirationUnits.NEVER.name
            }

            inputLayoutExpirationDuration.setOnClickListener { hideKeyboard() }
            autocompleteExpirationDuration.setOnClickListener { hideKeyboard() }
        }
    }

    private fun extractInputValues() {
        with(binding) {
            _currentReminderData.title = inputLayoutTitle.editText?.text.toString()
            _currentReminderData.locationName = inputLayoutLocationName.editText?.text.toString()
            _currentReminderData.description = inputLayoutDescription.editText?.text.toString()

            val isValid = expirationDurationEditText.text?.run { this.isEmpty().not() } ?: false
            if (isValid) {
                _currentReminderData.expiration = TimeUnit.DAYS.toMillis(
                    expirationDurationEditText.text.toString().toLong()
                )
            }
            _currentReminderData.isGeofenceEnable = isGeofenceEnableSwitch.isChecked
        }
        viewModel.setSelectedReminder(_currentReminderData)
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            with(binding) {
                state.selectedReminder?.latitude?.let { lat ->
                    state.selectedReminder.longitude?.let { lng ->
                        textSelectedLocation.text = String.format(
                            Locale.getDefault(), getString(R.string.lat_long_snippet), lat, lng
                        )
                        buttonSelectLocation.text =
                            getString(R.string.text_button_change_location)
                    }
                } ?: run {
                    buttonSelectLocation.text = getString(R.string.text_button_select_location)
                }

                state.selectedReminder?.locationName?.let {
                    reminderLocationName.setText(it)
                } ?: reminderLocationName.setText("")

                reminderTitle.setText(state.selectedReminder?.title)
                reminderDescription.setText(state.selectedReminder?.description)
                sliderCircularRadius.value = state.selectedReminder?.circularRadius ?: CIRCULAR_RADIUS_DEFAULT

                radioGroupTransitionType.check(
                    when(state.selectedReminder?.transitionType) {
                        Geofence.GEOFENCE_TRANSITION_EXIT -> R.id.radioButtonExit
                        else -> R.id.radioButtonEnter
                    }
                )

                isGeofenceEnableSwitch.isChecked = state.selectedReminder?.isGeofenceEnable ?: false

                state.selectedReminder?.expiration?.let {
                    if (it == REMINDER_EXPIRATION_NEVER) {
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
            with(binding) {
                when(action) {
                    is AddReminderAction.AddReminderError ->
                        context?.showCustomToast(
                            titleResId = R.string.message_saving_reminder_error,
                            toastType = ToastType.ERROR
                        )
                    is AddReminderAction.UpdateReminderError ->
                        context?.showCustomToast(
                            titleResId = R.string.message_update_reminder_error,
                            toastType = ToastType.ERROR
                        )
                    is AddReminderAction.AddReminderSuccess -> {
                        if (_currentReminderData.isGeofenceEnable) {
                            addGeofence(_currentReminderData.copy(id = action.id))
                        }
                        context?.showCustomToast(
                            titleResId = R.string.message_saving_reminder_success,
                            toastType = ToastType.SUCCESS,
                            offSetY = TOAST_POSITION_ELEVATED
                        )
                        navigateToReminderList()
                    }
                    is AddReminderAction.UpdateReminderSuccess -> {
                        if (_currentReminderData.isGeofenceEnable) {
                            addGeofence(_currentReminderData)
                        }
                        context?.showCustomToast(
                            titleResId = R.string.message_update_reminder_success,
                            toastType = ToastType.SUCCESS,
                            offSetY = TOAST_POSITION_ELEVATED
                        )
                        navigateToReminderList()
                    }
                    is AddReminderAction.InputErrorFieldTitle -> {
                        inputLayoutTitle.isErrorEnabled = true
                        inputLayoutTitle.error = getString(R.string.message_input_error_title)
                    }
                    is AddReminderAction.InputClearErrorFieldTitle ->
                        inputLayoutTitle.error = null
                    is AddReminderAction.InputErrorFieldLocationName -> {
                        inputLayoutLocationName.isErrorEnabled = true
                        inputLayoutLocationName.error =
                            getString(R.string.message_input_error_location)
                    }
                    is AddReminderAction.InputClearErrorFieldLocationName -> {
                        inputLayoutLocationName.error = null
                    }
                    is AddReminderAction.InputErrorFieldDescription -> {
                        inputLayoutDescription.isErrorEnabled = true
                        inputLayoutDescription.error =
                            getString(R.string.message_input_error_description)
                    }
                    is AddReminderAction.InputClearErrorFieldDescription ->
                        inputLayoutDescription.error = null
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
                    is AddReminderAction.InputClearErrorMissingLatLong -> {
                        binding.buttonSelectLocation.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                        binding.buttonSelectLocation.setStrokeColorResource(R.color.colorPrimary)
                    }
                    is AddReminderAction.ClearErrors -> {
                        inputLayoutExpirationDuration.error = null
                        inputLayoutLocationName.error = null
                        inputLayoutDescription.error = null
                    }
                    null -> Unit
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
            circularRadius = reminder.circularRadius,
            expiration = reminder.expiration,
            transitionType = reminder.transitionType,
            onAddGeofenceSuccess = ::onAddGeofenceSuccess,
            onAddGeofenceFailure = { reasonStringRes -> onAddGeofenceFailure(reasonStringRes)  }
        )
    }

    private fun isBackgroundPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            isPermissionNotGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
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
            (isPermissionNotGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    isPermissionNotGranted(Manifest.permission.ACCESS_FINE_LOCATION)).not()
        }
    }

    private fun onAddGeofenceSuccess() {
        context?.showCustomToast(
            titleResId = R.string.geofence_added,
            offSetY = TOAST_POSITION_ELEVATED,
            toastType = ToastType.INFO
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
            startActivity(Intent(activity?.applicationContext, RemindersActivity::class.java))
            activity?.finish()
        }
    }
}
