package com.udacity.locationreminder.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.ActivityReminderDescriptionBinding

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reminder = intent.extras?.getSerializable(EXTRA_REMINDER)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        // TODO: Add the implementation of the reminder details
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
