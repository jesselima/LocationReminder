package com.udacity.locationreminder.features.addreminder.presentation

sealed class AddReminderAction {
    object AddReminderError: AddReminderAction()
    data class AddReminderSuccess(val id: Long) : AddReminderAction()
    object UpdateReminderSuccess: AddReminderAction()
    object UpdateReminderError: AddReminderAction()
    object InputErrorFieldTitle: AddReminderAction()
    object InputClearErrorFieldTitle: AddReminderAction()
    object InputErrorFieldLocationName: AddReminderAction()
    object InputClearErrorFieldLocationName: AddReminderAction()
    object InputErrorFieldDescription: AddReminderAction()
    object InputClearErrorFieldDescription: AddReminderAction()
    object InputErrorMissingLatLong: AddReminderAction()
    object InputClearErrorMissingLatLong: AddReminderAction()
    object ClearErrors: AddReminderAction()
}