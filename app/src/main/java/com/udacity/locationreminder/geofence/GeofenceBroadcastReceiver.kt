package com.udacity.locationreminder.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.ReminderConstants
import com.udacity.locationreminder.common.extensions.ToastType
import com.udacity.locationreminder.common.extensions.showCustomToast
import com.udacity.locationreminder.common.notification.showOrUpdateNotification
import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

private const val EMPTY = ""
private const val INVALID_REQUEST_ID = -1
private const val enterEvent = Geofence.GEOFENCE_TRANSITION_ENTER
private const val exitEvent = Geofence.GEOFENCE_TRANSITION_EXIT

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val tag = GeofenceBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("===>>", "GeofenceBroadcastReceiver#onReceive called!")

        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            Log.d("===>>", "geofencingEvent.hasError ${geofencingEvent.errorCode}")
            val errorMessage = handleGeofenceError(context, geofencingEvent.errorCode)
                Log.d(tag,"Geofence error: $errorMessage")
                context.showCustomToast(
                    titleText = context.resources.getString(R.string.geofence_error_generic_info),
                    toastType = ToastType.WARNING
                )
            return
        }
        Log.d("===>>", "geofencingEvent requestId ${geofencingEvent?.triggeringGeofences?.first()?.requestId}")
        Log.d("===>>", "geofencingEvent triggeringLocation.latitude ${geofencingEvent?.triggeringLocation?.latitude}")
        Log.d("===>>", "geofencingEvent triggeringLocation.longitude ${geofencingEvent?.triggeringLocation?.longitude}")
        Log.d("===>>", "geofencingEvent geofenceTransition ${geofencingEvent?.geofenceTransition}")
        Log.d("===>>", "geofencingEvent triggeringLocation ${geofencingEvent?.triggeringLocation}")

        geofencingEvent?.triggeringGeofences?.first()?.requestId?.let { id ->

            val transitionName = when(geofencingEvent.geofenceTransition) {
                enterEvent -> context.resources.getString(R.string.label_enter_lowercase)
                exitEvent -> context.resources.getString(R.string.label_exit_lowercase)
                else -> EMPTY
            }

            val isExitOrEnter = geofencingEvent.geofenceTransition == enterEvent ||
                    geofencingEvent.geofenceTransition == exitEvent

            if (isExitOrEnter) NotificationHelper().onReceive(context, id, transitionName)
        }
    }

    class NotificationHelper : KoinComponent {
        private val applicationScope = CoroutineScope(Dispatchers.Default)
        private val remindersLocalRepository: RemindersLocalRepository by inject()

        fun onReceive(context: Context, id: String, transitionName: String) {
            applicationScope.launch {
                when(val result = remindersLocalRepository.getReminder(id)) {
                    is ResultData.Success<*> -> {
                        val reminder = result.data as ReminderData
                        if (id.toInt() != INVALID_REQUEST_ID) {
                            context.showOrUpdateNotification(
                                notificationId = id.toInt(),
                                title =  context.resources.getString(R.string.notification_title),
                                text = String.format(context.resources.getString(R.string.notification_description),
                                    transitionName.uppercase(), reminder.locationName, reminder.title
                                ),
                                channelId = ReminderConstants.channelIdReminders,
                                data = bundleOf(
                                    ReminderConstants.argsKeyReminderId to id.toInt()
                                )
                            )
                        }
                    }
                    is ResultData.Error -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}
