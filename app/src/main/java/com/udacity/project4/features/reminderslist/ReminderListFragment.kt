package com.udacity.project4.features.reminderslist

import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.common.extensions.ToastType
import com.udacity.project4.common.extensions.isPermissionGranted
import com.udacity.project4.common.extensions.setup
import com.udacity.project4.common.extensions.showCustomDialog
import com.udacity.project4.common.extensions.showCustomToast
import com.udacity.project4.common.extensions.signOut
import com.udacity.project4.databinding.FragmentReminderListBinding
import com.udacity.project4.features.ReminderEditorActivity
import com.udacity.project4.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.geofence.GeofenceManager
import com.udacity.project4.sharedpresentation.ReminderItemView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PENDING_INTENT_REQUEST_CODE = 0

@SuppressLint("UnspecifiedImmutableFlag")
class ReminderListFragment : Fragment() {

    val viewModel: RemindersListViewModel by viewModel()
    private val geofenceManager: GeofenceManager by inject()

    private lateinit var binding: FragmentReminderListBinding

    private lateinit var geofenceClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            activity,
            PENDING_INTENT_REQUEST_CODE,
            Intent(activity, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

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
        setupActionListeners()
        setupObservers()
        geofenceClient = LocationServices.getGeofencingClient(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        viewModel.getReminders()
        binding.noDataAnimation.playAnimation()
        checkLocationDeviceStatus()
        checkPermissionsAndLocationEnabled()
    }

    private fun checkLocationDeviceStatus() {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isProviderEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        binding.cardDeviceLocationStatus.isGone = isProviderEnabled
    }

    private fun checkPermissionsAndLocationEnabled() {
        if(hasRequiredPermissions().not()) {
            showLocationBackgroundPermissionRequiredSnackBar()
        }
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

    private fun hasRequiredPermissions(): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isPermissionGranted(ACCESS_BACKGROUND_LOCATION)
        } else {
            (isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun openAppSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.noDataAnimation.isVisible = state?.reminders?.isEmpty() ?: false
            binding.noDataTextView.isVisible = state?.reminders?.isEmpty() ?: false
            binding.refreshLayout.isRefreshing = false
        }

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

    private fun setupActionListeners() {
        binding.buttonActivateDeviceLocation.setOnClickListener {
            context?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
        binding.reminderssRecyclerView.setup(adapter)
    }

    private fun updateGeofenceStatus(reminder: ReminderItemView) {
        if(hasRequiredPermissions().not()) {
            showLocationBackgroundPermissionRequiredSnackBar()
        } else {
            viewModel.updateGeofenceStatus(
                reminderId = reminder.id ?: 0L,
                isGeofenceEnable = reminder.isGeofenceEnable.not()
            )
            if (reminder.isGeofenceEnable) {
                removeGeofence(reminder)
            } else {
                addGeofence(reminder)
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
        geofenceManager.addGeofence(
            geofenceClient = geofenceClient,
            geofencePendingIntent = geofencePendingIntent,
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
