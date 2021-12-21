package com.udacity.project4.features.reminderslist

sealed class RemindersAction {
    object LoadRemindersError: RemindersAction()
    object NoRemindersFound: RemindersAction()
    object UpdateRemindersSuccess: RemindersAction()
    object UpdateRemindersError: RemindersAction()
    object DeleteRemindersSuccess: RemindersAction()
    object DeleteRemindersError: RemindersAction()
    object DeleteAllRemindersSuccess: RemindersAction()
    object DeleteAllRemindersError: RemindersAction()
}