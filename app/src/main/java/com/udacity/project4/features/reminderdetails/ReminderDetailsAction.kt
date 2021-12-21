package com.udacity.project4.features.reminderdetails

sealed class ReminderDetailsAction {
    object DeleteReminderSuccess: ReminderDetailsAction()
    object DeleteReminderError: ReminderDetailsAction()
    object GetReminderError: ReminderDetailsAction()
}