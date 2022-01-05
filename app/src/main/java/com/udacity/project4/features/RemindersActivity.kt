package com.udacity.project4.features

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.udacity.project4.common.extensions.validateAuthentication
import com.udacity.project4.features.addreminder.presentation.AddReminderFragment

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)
    }

    override fun onResume() {
        super.onResume()
        validateAuthentication()
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
