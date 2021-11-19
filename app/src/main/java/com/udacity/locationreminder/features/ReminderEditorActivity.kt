package com.udacity.locationreminder.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.ReminderConstants
import com.udacity.locationreminder.common.extensions.validateAuthentication
import com.udacity.locationreminder.sharedpresentation.ReminderItemView

class ReminderEditorActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_editor)
    }

    override fun onResume() {
        super.onResume()
        validateAuthentication()
    }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "ReminderDescriptionActivity.geofences.action.ACTION_GEOFENCE_EVENT"

        fun newIntent(context: Context, reminderItemView: ReminderItemView): Intent {
            return Intent(context, ReminderEditorActivity::class.java).apply {
                putExtra(ReminderConstants.argsKeyReminder, reminderItemView)
            }
        }
    }
}