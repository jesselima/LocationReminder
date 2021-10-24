package com.udacity.locationreminder.locationreminders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.locationreminder.R
import com.udacity.locationreminder.utils.validateAuthentication

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
}
