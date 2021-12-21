package com.udacity.project4.features.addreminder.presentation

import com.udacity.project4.sharedpresentation.ReminderItemView

data class AddReminderState(
    val isLoading: Boolean = false,
    val selectedReminder: ReminderItemView? = null,
)
