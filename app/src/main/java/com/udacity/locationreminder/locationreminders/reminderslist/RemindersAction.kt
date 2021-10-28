package com.udacity.locationreminder.locationreminders.reminderslist

sealed class RemindersAction {
    object LoadRemindersError: RemindersAction()
}