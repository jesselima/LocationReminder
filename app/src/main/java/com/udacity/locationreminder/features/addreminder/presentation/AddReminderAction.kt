package com.udacity.locationreminder.features.addreminder.presentation

sealed class AddReminderAction {
    object AddReminderError: AddReminderAction()
    object AddReminderSuccess: AddReminderAction()
    object UpdateReminderSuccess: AddReminderAction()
    object UpdateReminderError: AddReminderAction()
    object InputErrorFieldTitle: AddReminderAction()
    object InputErrorFieldLocationName: AddReminderAction()
    object InputErrorFieldDescription: AddReminderAction()
    object InputErrorMissingLatLong: AddReminderAction()
    object ClearErrors: AddReminderAction()
}