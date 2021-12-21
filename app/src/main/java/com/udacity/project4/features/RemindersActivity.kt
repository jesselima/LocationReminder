package com.udacity.project4.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.project4.R
import com.udacity.project4.common.extensions.validateAuthentication

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