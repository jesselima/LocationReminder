package com.udacity.project4.features.reminderslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.common.extensions.ToastType
import com.udacity.project4.common.extensions.hasRequiredLocationPermissions
import com.udacity.project4.common.extensions.isDeviceLocationActivated
import com.udacity.project4.common.extensions.openAppSettings
import com.udacity.project4.common.extensions.openDeviceLocationsSettings
import com.udacity.project4.common.extensions.setup
import com.udacity.project4.common.extensions.showCustomDialog
import com.udacity.project4.common.extensions.showCustomToast
import com.udacity.project4.common.extensions.signOut
import com.udacity.project4.databinding.FragmentReminderListBinding
import com.udacity.project4.features.ReminderEditorActivity
import com.udacity.project4.geofence.GeofenceManager
import com.udacity.project4.sharedpresentation.ReminderItemView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PENDING_INTENT_REQUEST_CODE = 0

class ReminderListFragment : Fragment() {

    val viewModel: RemindersListViewModel by viewModel()
    private val geofenceManager: GeofenceManager by inject()

    private lateinit var binding: FragmentReminderListBinding

    private lateinit var geofenceClient: GeofencingClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderListBinding.inflate(layoutInflater)
        binding.refreshLayout.setOnRefreshListener { viewModel.getReminders() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupListeners()
        setupStateObservers()
        setupActionObservers()
        geofenceClient = LocationServices.getGeofencingClient(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        viewModel.getReminders()
        binding.noDataAnimation.playAnimation()
        binding.cardDeviceLocationStatus.isGone = context?.isDeviceLocationActivated() ?: false
        binding.cardPermissionLocationStatus.isGone = hasRequiredLocationPermissions()
    }

    private fun showLocationBackgroundPermissionRequiredSnackBar() {
        Snackbar.make(
            binding.reminderListMainLayout,
            R.string.message_geofence_requires_location_permission,
            Snackbar.LENGTH_LONG
        )
        .setAction(getString(R.string.enable_now)) { openAppSettings() }
        .setAnchorView(R.id.actionButtonAddReminder)
        .show()
    }

    private fun showDeviceLocationActiveRequiredSnackbar() {
        Snackbar.make(
            binding.reminderListMainLayout,
            R.string.device_location_is_disabled_title,
            Snackbar.LENGTH_LONG
        )
        .setAction(getString(R.string.enable_it)) { openDeviceLocationsSettings() }
        .setAnchorView(R.id.actionButtonAddReminder)
        .show()
    }

    private fun setupStateObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.noDataAnimation.isVisible = state?.reminders?.isEmpty() ?: false
            binding.noDataTextView.isVisible = state?.reminders?.isEmpty() ?: false
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun setupActionObservers() {
        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                RemindersAction.LoadRemindersError -> {
                    Snackbar.make(
                        binding.reminderListMainLayout,
                        R.string.message_loading_reminder_error,
                        Snackbar.LENGTH_LONG
                    )
                    .setAction(getString(R.string.dismiss)) { }
                    .setAnchorView(R.id.actionButtonAddReminder)
                    .show()
                    binding.noDataTextView.text = getText(R.string.message_no_reminders_found)
                    binding.noDataTextView.isVisible = true
                }
                RemindersAction.NoRemindersFound ->
                    binding.noDataTextView.isVisible = true
                RemindersAction.UpdateRemindersSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_update_reminder_success,
                        toastType = ToastType.SUCCESS
                    )
                    viewModel.getReminders()
                }
                RemindersAction.ReviewLocationDeviceAndPermission -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_update_reminder_error_review,
                        toastType = ToastType.SUCCESS
                    )
                }
                RemindersAction.UpdateRemindersError ->
                    context?.showCustomToast(
                        titleResId = R.string.message_update_reminder_error,
                        toastType = ToastType.ERROR
                    )
                is RemindersAction.DeleteRemindersSuccess -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_delete_reminder_success,
                    )
                    viewModel.getReminders()
                }
                is RemindersAction.DeleteRemindersError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_delete_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
                is RemindersAction.DeleteAllRemindersSuccess,
                is RemindersAction.DeleteAllRemindersError -> {
                    deleteAccount()
                }
                else -> binding.noDataTextView.isVisible = false
            }
        }
    }

    private fun setupListeners() {
        binding.buttonDeviceLocation.setOnClickListener {
            openDeviceLocationsSettings()
        }
        binding.buttonLocationPermission.setOnClickListener {
            openAppSettings()
        }
        binding.actionButtonAddReminder.setOnClickListener {
            findNavController().navigate(ReminderListFragmentDirections.navigateToSaveReminder())
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    activity?.showCustomDialog(
                        context = context,
                        title = getString(R.string.logout),
                        message = getString(R.string.logout_confirmation_message),
                        positiveButtonAction = { signOut() }
                    )
                    true
                }
                R.id.copyrights -> {
                    binding.noDataAnimation.pauseAnimation()
                    findNavController().navigate(
                        ReminderListFragmentDirections.navigateToCopyrights()
                    )
                    true
                }
                R.id.delete_account -> {
                    activity?.showCustomDialog(
                        context = context,
                        title = getString(R.string.delete_account_title),
                        message = getString(R.string.delete_account_confirmation_message),
                        negativeButtonText = getString(R.string.label_cancel),
                        positiveButtonText = getString(R.string.label_delete_account),
                        positiveButtonAction = {
                            viewModel.deleteAllReminders()
                        }
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteAccount() {
        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener {
            if (it.isSuccessful) {
                context?.showCustomToast(
                    titleResId = R.string.auth_account_deleted,
                    toastType = ToastType.INFO
                )
            }
            signOut()
        }
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter(
                onReminderItemClick = {
                    startActivity(ReminderEditorActivity.newIntent(requireContext(), it))
                },
                viewsResIdActions = listOf(
                    Pair(R.id.imageReminderStatus) { updateGeofenceStatus(it) },
                    Pair(R.id.imageDeleteReminder) { deleteReminder(it) }
                )
            )
        binding.remindersRecyclerView.setup(adapter)
    }

    private fun updateGeofenceStatus(reminder: ReminderItemView) {
        if(hasRequiredLocationPermissions().not() || context?.isDeviceLocationActivated() == false) {
            context?.showCustomToast(
                titleResId = R.string.message_update_reminder_error_review,
                toastType = ToastType.WARNING,
                durationToast = Toast.LENGTH_LONG
            )
        } else {
            reminder.id?.let {
                viewModel.updateGeofenceStatusOnDatabase(
                    reminderId = it,
                    isGeofenceEnable = reminder.isGeofenceEnable.not()
                )
                if (reminder.isGeofenceEnable) {
                    removeGeofence(reminder)
                } else {
                    addGeofence(reminder)
                }
            }
        }
    }

    private fun deleteReminder(reminder: ReminderItemView) {
        activity?.showCustomDialog(
            context = context,
            title = getString(R.string.delete_reminder_title),
            message = getString(R.string.delete_reminder_confirmation_message),
            negativeButtonText = getString(R.string.label_cancel),
            positiveButtonText = getString(R.string.label_delete),
            positiveButtonAction = {
                removeGeofence(reminder)
                viewModel.deleteReminder(reminder)
            }
        )
    }

    private fun addGeofence(reminder: ReminderItemView) {
        if (context?.isDeviceLocationActivated() == false) {
            showDeviceLocationActiveRequiredSnackbar()
            updateReminderGeofenceStatusOnDatabase(reminder.id)
            return
        }

        if (hasRequiredLocationPermissions().not()) {
            showLocationBackgroundPermissionRequiredSnackBar()
            updateReminderGeofenceStatusOnDatabase(reminder.id)
            return
        }

        geofenceManager.addGeofence(
            geofenceClient = geofenceClient,
            id = reminder.id.toString(),
            latitude = reminder.latitude,
            longitude = reminder.longitude,
            circularRadius = reminder.circularRadius,
            expiration = reminder.expiration,
            transitionType = reminder.transitionType,
            onAddGeofenceSuccess = { onAddGeofenceSuccess() },
            onAddGeofenceFailure = { reasonStringRes -> onAddGeofenceFailure(reasonStringRes)  }
        )
    }

    private fun updateReminderGeofenceStatusOnDatabase(id: Long?, isEnable: Boolean = false) {
        id?.let { reminderId ->
            viewModel.updateGeofenceStatusOnDatabase(reminderId, isEnable)
        }
    }

    private fun removeGeofence(reminder: ReminderItemView) {
        geofenceManager.removeGeofence(
            geofenceClient,
            reminder.id.toString(),
            onRemoveGeofenceFailure = { reasonStringRes -> geofenceFailure(reasonStringRes) },
            onRemoveGeofenceSuccess = { onRemoveGeofenceSuccess() }
        )
    }

    private fun onRemoveGeofenceSuccess() {
        context?.showCustomToast(
            titleResId = R.string.geofence_removed,
            toastType = ToastType.INFO
        )
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

    private fun onAddGeofenceSuccess() {
        context?.showCustomToast(titleResId = R.string.geofence_added)
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
