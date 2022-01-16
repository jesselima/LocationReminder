package com.udacity.project4.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.udacity.project4.common.ReminderConstants
import com.udacity.project4.common.extensions.validateAuthentication
import com.udacity.project4.features.addreminder.presentation.AddReminderFragment
import com.udacity.project4.sharedpresentation.ReminderItemView

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
        fun newIntent(context: Context, reminderItemView: ReminderItemView): Intent {
            return Intent(context, ReminderEditorActivity::class.java).apply {
                putExtra(ReminderConstants.argsKeyReminder, reminderItemView)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddReminderFragment.REQUEST_TURN_DEVICE_LOCATION_ON) {
            val fragments = supportFragmentManager.fragments.first().childFragmentManager.fragments
            for (fragment in fragments) {
                if (fragment is AddReminderFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }
}