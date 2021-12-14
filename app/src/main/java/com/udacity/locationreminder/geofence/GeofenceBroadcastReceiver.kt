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

private const val EMPTY = ""
private const val INVALID_REQUEST_ID = -1

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val tag = GeofenceBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("===>>", "GeofenceBroadcastReceiver#onReceive called!")
        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            Log.e("===>>", "geofencingEvent.hasError ${geofencingEvent.errorCode}")
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

        val transition = when(geofencingEvent?.geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER ->
                    context.resources.getString(R.string.notification_text_transition_type_enter)
                Geofence.GEOFENCE_TRANSITION_EXIT ->
                    context.resources.getString(R.string.notification_text_transition_type_exit)
                else -> EMPTY
            }

        if (geofencingEvent?.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofencingEvent?.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            val requestId = geofencingEvent.triggeringGeofences.first()?.requestId?.toInt()
                ?: INVALID_REQUEST_ID

            if (requestId != INVALID_REQUEST_ID) {
                context.showOrUpdateNotification(
                    notificationId = requestId,
                    title =  context.resources.getString(R.string.notification_title),
                    text = String.format(
                        context.resources.getString(R.string.notification_description),
                        transition.uppercase()
                    ),
                    channelId = ReminderConstants.channelIdReminders,
                    data = bundleOf(
                        ReminderConstants.argsKeyReminderId to requestId
                    )
                )
            }
        }
    }
}
