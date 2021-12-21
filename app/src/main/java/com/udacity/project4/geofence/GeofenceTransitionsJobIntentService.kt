package com.udacity.project4.geofence

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private  val geofenceHandlerHelper: GeofenceHandlerHelper by inject()

    companion object {
        private const val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        val geofencingEvent: GeofencingEvent? = GeofencingEvent.fromIntent(intent)
        geofencingEvent?.let { geofenceHandlerHelper.onWorkReceived(it) }
    }
}