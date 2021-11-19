package com.udacity.locationreminder.features.reminderslist

sealed class RemindersAction {
    object LoadRemindersError: RemindersAction()
    object NoRemindersFound: RemindersAction()
    object UpdateRemindersSuccess: RemindersAction()
    object UpdateRemindersError: RemindersAction()
    object DeleteRemindersSuccess: RemindersAction()
    object DeleteRemindersError: RemindersAction()
}