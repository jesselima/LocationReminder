package com.udacity.locationreminder.locationreminders.reminderdetails

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.ActivityReminderDescriptionBinding
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.locationreminder.locationreminders.geofence.GeofenceManager
import com.udacity.locationreminder.locationreminders.geofence.isAndroidOsEqualsOrGreaterThan
import com.udacity.locationreminder.locationreminders.reminderslist.PENDING_INTENT_REQUEST_CODE
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomDialog
import com.udacity.locationreminder.utils.showCustomToast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val FORMAT_LAT_LNG_DECIMALS = "%.4f"
private const val ROTATION_FLIP_HORIZONTAL = 180f

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding

    val viewModel: ReminderDescriptionViewModel by viewModel()
    private val geofenceManager: GeofenceManager by inject()

    private lateinit var geofenceClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            this,
            PENDING_INTENT_REQUEST_CODE,
            Intent(this, GeofenceBroadcastReceiver::class.java).apply {
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

    private var _currentReminderData: ReminderItemView = ReminderItemView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        setupListeners()
        setupObservers()
        geofenceClient = LocationServices.getGeofencingClient(this)
    }

    override fun onResume() {
        super.onResume()
        val reminder = intent.extras?.getSerializable(EXTRA_REMINDER) as ReminderItemView?
        updateUI(reminder)
    }

    private fun setupObservers() {
        viewModel.action.observe(this) { action ->
            when (action) {
                ReminderDetailsAction.DeleteReminderSuccess -> {
                    showCustomToast(
                        titleResId = R.string.message_delete_reminder_success,
                        toastType = ToastType.INFO
                    )
                    finish()
                }
                ReminderDetailsAction.DeleteReminderError -> {
                    showCustomToast(
                        titleResId = R.string.message_delete_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
                ReminderDetailsAction.UpdateReminderDatabaseSuccess -> {
                    showCustomToast(
                        titleResId = R.string.message_update_reminder_success,
                        toastType = ToastType.SUCCESS
                    )
                }
                ReminderDetailsAction.UpdateReminderDatabaseError -> {
                    showCustomToast(
                        titleResId = R.string.message_update_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
            }
        }
    }

    private fun setupListeners() {
        binding.reminderDetailsToolbar.setNavigationOnClickListener { finish() }

        binding.isGeofenceEnableSwitch.setOnCheckedChangeListener { _ , isChecked ->
            _currentReminderData.isGeofenceEnable = isChecked
            toggleImageReminderGeofenceStatus(isChecked)
            updateReminderOnLocalDatabase(_currentReminderData)
            updateGeofenceStatus(_currentReminderData)
        }

        binding.buttonDeleteReminderAndGeofence.setOnClickListener {
            showCustomDialog(
                context = this,
                title = getString(R.string.message_delete_reminder),
                message = getString(R.string.message_delete_reminder_description),
                positiveButtonText = getString(R.string.text_button_delete_reminder),
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
                isGeofenceEnableSwitch.isChecked = reminder.isGeofenceEnable
                textCurrentCircularRadius.text = String.format(
                    getString(R.string.circular_radius_unit),
                    reminder.circularRadius.toInt().toString()
                )
                reminderLocationCoordinatesLat.text =
                    String.format(FORMAT_LAT_LNG_DECIMALS, reminder.latitude)
                reminderLocationCoordinatesLong.text =
                    String.format(FORMAT_LAT_LNG_DECIMALS, reminder.longitude)

                if (reminder.transitionType ==  Geofence.GEOFENCE_TRANSITION_EXIT) {
                    imageViewGeofenceTriggerDirection.rotation = ROTATION_FLIP_HORIZONTAL
                }
            }

            toggleImageReminderGeofenceStatus(reminder.isGeofenceEnable)
        } ?: showCustomToast(
            titleResId = R.string.message_reminder_details_error,
            toastType = ToastType.ERROR
        )
    }

    private fun toggleImageReminderGeofenceStatus(isGeofenceEnable: Boolean) {
        binding.imageReminderGeofenceStatus.setImageResource(
            if (isGeofenceEnable) R.drawable.ic_map_alert_bg_transparent_enable
            else R.drawable.ic_map_alert_bg_transparent_disable
        )
    }

    private fun updateGeofenceStatus(reminder: ReminderItemView) {
        if (reminder.isGeofenceEnable) {
            removeGeofence(reminder)
        } else {
            addGeofence(reminder)
        }
    }

    private fun updateReminderOnLocalDatabase(reminder: ReminderItemView) {
        viewModel.updateReminder(
            reminderId = reminder.id,
            isGeofenceEnable = reminder.isGeofenceEnable.not()
        )
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
            onAddGeofenceFailure = { reasonStringRes -> geofenceFailure(reasonStringRes)  }
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

    private fun onAddGeofenceSuccess() {
        showCustomToast(titleResId = R.string.geofence_added)
    }

    private fun onRemoveGeofenceSuccess() {
        showCustomToast(titleResId = R.string.geofence_removed, toastType = ToastType.INFO)
    }

    private fun geofenceFailure(@StringRes reasonStringRes: Int) {
        showCustomToast(
            titleText = String.format(
                getString(R.string.geofence_error),
                getString(reasonStringRes)
            ),
            toastType = ToastType.WARNING
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private const val EXTRA_REMINDER = "EXTRA_REMINDER"
        internal const val ACTION_GEOFENCE_EVENT =
            "ReminderDescriptionActivity.geofences.action.ACTION_GEOFENCE_EVENT"

        fun newIntent(context: Context, reminderItemView: ReminderItemView): Intent {
            return Intent(context, ReminderDescriptionActivity::class.java).apply {
                putExtra(EXTRA_REMINDER, reminderItemView)
            }
        }
    }
}
