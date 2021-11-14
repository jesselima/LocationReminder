package com.udacity.locationreminder.locationreminders.reminderdetails

sealed class ReminderDetailsAction {
    object DeleteReminderSuccess: ReminderDetailsAction()
    object DeleteReminderError: ReminderDetailsAction()
}