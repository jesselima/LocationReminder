package com.udacity.locationreminder.locationreminders.reminderdetails

sealed class ReminderDetailsAction {
    object UpdateReminderDatabaseSuccess: ReminderDetailsAction()
    object UpdateReminderDatabaseError: ReminderDetailsAction()
    object DeleteReminderSuccess: ReminderDetailsAction()
    object DeleteReminderError: ReminderDetailsAction()
}