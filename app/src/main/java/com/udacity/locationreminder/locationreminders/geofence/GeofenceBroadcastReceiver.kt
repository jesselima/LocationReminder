package com.udacity.locationreminder.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.locationreminder.R
import com.udacity.locationreminder.locationreminders.ReminderDescriptionActivity
import com.udacity.locationreminder.utils.ReminderConstants
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.showOrUpdateNotification

private const val EMPTY = ""

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val tag = GeofenceBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ReminderDescriptionActivity.ACTION_GEOFENCE_EVENT) {

            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = handleGeofenceError(context, geofencingEvent.errorCode)
                Log.d(tag,"Geofence error: $errorMessage")
                context.showCustomToast(
                    titleText = context.resources.getString(R.string.geofence_error_generic_info),
                    toastType = ToastType.WARNING
                )
                return
            }

            val transition = when(geofencingEvent.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER ->
                    context.resources.getString(R.string.notification_text_transition_type_enter)
                Geofence.GEOFENCE_TRANSITION_EXIT ->
                    context.resources.getString(R.string.notification_text_transition_type_exit)
                else -> EMPTY
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                with(geofencingEvent.triggeringGeofences) {
                    if (isNotEmpty()) {
                        forEach {
                            context.showOrUpdateNotification(
                                notificationId = it.requestId.toInt(),
                                title =  context.resources.getString(R.string.notification_title),
                                text = String.format(
                                    context.resources.getString(R.string.notification_description),
                                    transition
                                ),
                                channelId = ReminderConstants.channelIdReminders,
                                data = bundleOf(
                                    ReminderConstants.argsKeyReminderId to it.requestId.toInt()
                                )
                            )
                        }
                    } else {
                        return
                    }
                }
            }
        }
    }
}
