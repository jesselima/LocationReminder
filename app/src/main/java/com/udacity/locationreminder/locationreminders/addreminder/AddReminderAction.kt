package com.udacity.locationreminder.locationreminders.addreminder

sealed class AddReminderAction {
    object AddReminderError: AddReminderAction()
    object AddReminderSuccess: AddReminderAction()
    object InputErrorFieldTitle: AddReminderAction()
    object InputErrorFieldLocation: AddReminderAction()
    object InputErrorFieldDescription: AddReminderAction()
    object InputErrorMissingLatLong: AddReminderAction()
    object ClearErrors: AddReminderAction()
}