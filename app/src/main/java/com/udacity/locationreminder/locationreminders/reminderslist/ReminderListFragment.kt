package com.udacity.locationreminder.locationreminders.reminderslist

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentReminderListBinding
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManager
import com.udacity.locationreminder.locationreminders.geofence.isAndroidOsEqualsOrGreaterThan
import com.udacity.locationreminder.locationreminders.ReminderEditorActivity
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.setup
import com.udacity.locationreminder.utils.showCustomDialog
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.signOut
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PENDING_INTENT_REQUEST_CODE = 0

class ReminderListFragment : Fragment() {

    val viewModel: RemindersListViewModel by viewModel()
    private val geofenceManager: GeofenceManager by inject()

    private lateinit var binding: FragmentReminderListBinding

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
        binding = FragmentReminderListBinding.inflate(layoutInflater)
        binding.refreshLayout.setOnRefreshListener { viewModel.loadReminders() }
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

    private fun setupObservers() {
        viewModel.remindersList.observe(viewLifecycleOwner) {
            binding.noDataAnimation.isVisible = it.isEmpty()
            binding.noDataTextView.isVisible = it.isEmpty()
            binding.refreshLayout.isRefreshing = false
        }
        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                RemindersAction.LoadRemindersError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_loading_reminder_error,
                        toastType = ToastType.ERROR
                    )
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
                    viewModel.loadReminders()
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
                    viewModel.loadReminders()
                }
                is RemindersAction.DeleteRemindersError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_delete_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
                else -> binding.noDataTextView.isVisible = false
            }
        }
    }
    private fun setupActionListeners() {
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
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadReminders()
        binding.noDataAnimation.playAnimation()
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
        viewModel.updateGeofenceStatus(
            reminderId = reminder.id,
            isGeofenceEnable = reminder.isGeofenceEnable.not()
        )
        if (reminder.isGeofenceEnable) {
            removeGeofence(reminder)
        } else {
            addGeofence(reminder)
        }
    }

    private fun deleteReminder(reminder: ReminderItemView) {
        removeGeofence(reminder)
        viewModel.deleteReminder(reminder)
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
