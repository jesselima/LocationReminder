package com.udacity.project4.geofence

import android.content.Context
import androidx.core.os.bundleOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.R
import com.udacity.project4.common.ReminderConstants
import com.udacity.project4.common.extensions.ToastType
import com.udacity.project4.common.extensions.showCustomToast
import com.udacity.project4.common.notification.showOrUpdateNotification
import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

private const val INVALID_REQUEST_ID = -1
private const val EMPTY = ""
private const val enterEvent = Geofence.GEOFENCE_TRANSITION_ENTER
private const val exitEvent = Geofence.GEOFENCE_TRANSITION_EXIT

class GeofenceHandlerHelper(private val context: Context) : KoinComponent, CoroutineScope {

    private var coroutineJob: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private val remindersLocalRepository: RemindersLocalRepository by inject()

    fun onWorkReceived(geofencingEvent: GeofencingEvent?) {
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = handleGeofenceError(context, geofencingEvent.errorCode)
            context.showCustomToast(
                titleText = String.format(
                    context.resources.getString(R.string.geofence_error_generic_info),
                    errorMessage
                ),
                toastType = ToastType.WARNING
            )
            return
        }

        val transitionName = when (geofencingEvent?.geofenceTransition) {
            enterEvent -> context.resources.getString(R.string.label_enter_lowercase)
            exitEvent -> context.resources.getString(R.string.label_exit_lowercase)
            else -> EMPTY
        }

        val isGeofenceEventExitOrEnter = geofencingEvent?.geofenceTransition == enterEvent ||
                geofencingEvent?.geofenceTransition == exitEvent

        geofencingEvent?.triggeringGeofences?.let { geofences ->
            for (geofence in geofences) {
                geofence?.requestId?.let { id ->
                    if (isGeofenceEventExitOrEnter) {
                        CoroutineScope(coroutineContext).launch {
                            when (val result = remindersLocalRepository.getReminder(id)) {
                                is ResultData.Success<*> -> {
                                    val reminder = result.data as ReminderData
                                    if (id.toInt() != INVALID_REQUEST_ID) {

                                        val notificationMessage = String.format(
                                            context.resources.getString(R.string.notification_description),
                                            transitionName.uppercase(),
                                            reminder.locationName,
                                            reminder.title
                                        )

                                        context.showOrUpdateNotification(
                                            notificationId = id.toInt(),
                                            title = context.resources.getString(R.string.notification_title),
                                            text = notificationMessage,
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
        }
    }
}