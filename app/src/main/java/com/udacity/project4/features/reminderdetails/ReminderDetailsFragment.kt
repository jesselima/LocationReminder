package com.udacity.project4.features.reminderdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.common.ReminderConstants
import com.udacity.project4.common.extensions.ToastType
import com.udacity.project4.common.extensions.showCustomDialog
import com.udacity.project4.common.extensions.showCustomToast
import com.udacity.project4.databinding.FragmentReminderDetailsBinding
import com.udacity.project4.geofence.GeofenceManager
import com.udacity.project4.sharedpresentation.ReminderItemView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val FORMAT_LAT_LNG_DECIMALS = "%.4f"
private const val ROTATION_FLIP_HORIZONTAL = 180f

/**
 * Fragment that displays the reminder details after the user clicks on the notification or
 * when click on a reminder on the main screen list.
 */
class ReminderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentReminderDetailsBinding

    val viewModel: ReminderDetailsViewModel by viewModel()
    private val geofenceManager: GeofenceManager by inject()

    private lateinit var geofenceClient: GeofencingClient

    private var _currentReminderData: ReminderItemView = ReminderItemView()
    private val args: ReminderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
        geofenceClient = LocationServices.getGeofencingClient(requireContext())

        val reminderItemView = activity?.intent?.extras
            ?.getSerializable(ReminderConstants.argsKeyReminder) as ReminderItemView?

        val idFromNotification = activity?.intent?.extras
            ?.getInt(ReminderConstants.argsKeyReminderId)

        when {
            args.isEditing || args.isFromList -> {
                updateUI(args.lastSelectedLocation)
            }
            reminderItemView != null -> {
                updateUI(reminderItemView)
            }
            idFromNotification != null -> {
                viewModel.getReminder(idFromNotification.toString())
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = state.isLoading
            binding.layoutReminderDetails.isVisible = state.isLoading.not()
            state?.reminderItemView?.let { reminderItemView ->
                updateUI(reminderItemView)
            }
        }
        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                ReminderDetailsAction.DeleteReminderSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_delete_reminder_success,
                        toastType = ToastType.INFO
                    )
                    activity?.finish()
                }
                ReminderDetailsAction.DeleteReminderError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_delete_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
                ReminderDetailsAction.GetReminderError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_get_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
            }
        }
    }

    private fun setupListeners() {
        binding.reminderDetailsToolbar.setNavigationOnClickListener { activity?.finish() }

        binding.buttonEditReminderAndGeofence.setOnClickListener { view ->
            view.findNavController().navigate(R.id.navigateToAddReminder, bundleOf(
                ReminderConstants.argsKeyLastSelectedLocation to _currentReminderData,
                ReminderConstants.argsKeyIsEditing to true
            ))
        }

        binding.buttonDeleteReminderAndGeofence.setOnClickListener {
            activity?.showCustomDialog(
                context = context,
                title = getString(R.string.message_delete_reminder),
                message = getString(R.string.message_delete_reminder_description),
                positiveButtonText = getString(R.string.label_delete),
                negativeButtonText = getString(R.string.label_cancel),
                positiveButtonAction = {
                    deleteReminder(_currentReminderData)
                },
            )
        }
    }

    private fun updateUI(reminder: ReminderItemView?) {
        reminder?.let {
            _currentReminderData = reminder
            // update UI
            with(binding) {
                reminderTitle.text = reminder.title
                reminderDescription.text = reminder.description
                reminderLocationName.text = reminder.locationName

                if (reminder.isGeofenceEnable) {
                    imageReminderGeofenceStatusDisabled.isVisible = false
                    isGeofenceEnableAnimation.playAnimation()
                    textGeofenceStatus.text = getString(R.string.label_geofence_is_enable)
                    textGeofenceStatus.setTextColor(ContextCompat.getColor(
                        requireContext(), (R.color.colorPrimary))
                    )
                } else {
                    imageReminderGeofenceStatusDisabled.isVisible = true
                    isGeofenceEnableAnimation.isVisible = false
                    textGeofenceStatus.text = getString(R.string.label_geofence_is_disable)
                    textGeofenceStatus.setTextColor(
                        ContextCompat.getColor(requireContext(), (R.color.colorSecondaryLight))
                    )
                }

                textCurrentCircularRadius.text = String.format(
                    getString(R.string.circular_radius_unit_format),
                    reminder.circularRadius.toInt().toString(),
                    when (reminder.transitionType) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> {
                            getString(R.string.label_enter_lowercase).uppercase()
                        }
                        else -> {
                            getString(R.string.label_exit_lowercase).uppercase()
                        }
                    }
                )

                reminderLocationCoordinatesLat.text =
                    String.format(FORMAT_LAT_LNG_DECIMALS, reminder.latitude)
                reminderLocationCoordinatesLong.text =
                    String.format(FORMAT_LAT_LNG_DECIMALS, reminder.longitude)

                if (reminder.transitionType ==  Geofence.GEOFENCE_TRANSITION_EXIT) {
                    imageViewGeofenceTriggerDirection.contentDescription =
                        getString(R.string.content_description_alert_exit)
                    imageViewGeofenceTriggerDirection.rotation = ROTATION_FLIP_HORIZONTAL
                } else {
                    imageViewGeofenceTriggerDirection.contentDescription =
                        getString(R.string.content_description_alert_enter)
                }
            }

        } ?: context?.showCustomToast(
            titleResId = R.string.message_reminder_details_error,
            toastType = ToastType.ERROR
        )
    }

    private fun deleteReminder(reminder: ReminderItemView) {
        removeGeofence(reminder)
        viewModel.deleteReminder(reminder)
    }

    private fun removeGeofence(reminder: ReminderItemView) {
        geofenceManager.removeGeofences(
            geofenceClient,
            listOf(reminder.id.toString()),
            onRemoveGeofenceFailure = { reasonStringRes -> geofenceFailure(reasonStringRes) },
            onRemoveGeofenceSuccess = { onRemoveGeofenceSuccess() }
        )
    }

    private fun onRemoveGeofenceSuccess() {
        context?.showCustomToast(titleResId = R.string.geofence_removed, toastType = ToastType.INFO)
    }

    private fun geofenceFailure(@StringRes reasonStringRes: Int) {
        context?.showCustomToast(
            titleText = String.format(
                getString(R.string.geofence_error),
                getString(reasonStringRes)
            ),
            toastType = ToastType.WARNING
        )
    }
}
