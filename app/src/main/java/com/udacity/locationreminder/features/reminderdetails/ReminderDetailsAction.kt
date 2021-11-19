package com.udacity.locationreminder.features.reminderdetails

sealed class ReminderDetailsAction {
    object DeleteReminderSuccess: ReminderDetailsAction()
    object DeleteReminderError: ReminderDetailsAction()
}