package com.udacity.project4.features.reminderslist

sealed class RemindersAction {
    object LoadRemindersError: RemindersAction()
    object NoRemindersFound: RemindersAction()
    object UpdateRemindersSuccess: RemindersAction()
    object ReviewLocationDeviceAndPermission: RemindersAction()
    object UpdateRemindersError: RemindersAction()
    object DeleteRemindersSuccess: RemindersAction()
    object DeleteRemindersError: RemindersAction()
    data class DeleteAllRemindersProcess(
        val isAccountRemoval: Boolean,
        val remindersDeleted: Int
    ): RemindersAction()
    data class RemoveGeofencesProcess(
        val remindersIds: List<String>
    ): RemindersAction()
}