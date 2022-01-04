package com.udacity.project4.features.reminderslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.shareddata.localdatasource.models.ReminderData
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.models.mapToPresentationModel
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.sharedpresentation.ReminderItemView
import com.udacity.project4.sharedpresentation.mapToDataModel
import kotlinx.coroutines.launch

private const val RESULT_NO_DATA_DELETED = 0
private const val RESULT_DATA_AFFECTED = 1

class RemindersListViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    private var _state = MutableLiveData<RemindersState>()
    val state: LiveData<RemindersState> = _state

    private var _action = MutableLiveData<RemindersAction>()
    val action: LiveData<RemindersAction> = _action

    init {
        _state.value = RemindersState()
    }

    fun getReminders() {
        viewModelScope.launch {
            _state.value = state.value?.copy(isLoading = true)
            when (val result = remindersLocalRepository.getReminders()) {
                is ResultData.Success<*> -> {
                    val dataList = ArrayList<ReminderItemView>()
                    dataList.addAll((result.data as List<ReminderData>).map { reminder ->
                        reminder.mapToPresentationModel()
                    })
                    if (dataList.isEmpty()) {
                        _action.value = RemindersAction.NoRemindersFound
                        _state.value = state.value?.copy(isLoading = false, reminders = emptyList())
                    } else {
                        _state.value = state.value?.copy(isLoading = false, reminders = dataList)
                    }
                }
                is ResultData.Error ->  {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = RemindersAction.LoadRemindersError
                }
            }
        }
    }

    fun updateGeofenceStatusOnDatabase(reminderId: Long, isGeofenceEnable: Boolean, isCancelling: Boolean = false) {
        viewModelScope.launch {
            val result = remindersLocalRepository.updateGeofenceStatus(reminderId, isGeofenceEnable)
            if (result == RESULT_DATA_AFFECTED) {
                if (isCancelling) {
                    _action.value = RemindersAction.ReviewLocationDeviceAndPermission
                } else {
                    _action.value = RemindersAction.UpdateRemindersSuccess
                }
            } else {
                _action.value = RemindersAction.UpdateRemindersError
            }
        }
    }

    fun deleteReminder(reminderItemView: ReminderItemView) {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteReminder(reminderItemView.mapToDataModel())
            if (result == RESULT_DATA_AFFECTED) {
                _action.value = RemindersAction.DeleteRemindersSuccess
            } else {
                _action.value = RemindersAction.DeleteRemindersError
            }
        }
    }

    fun deleteAllReminders() {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteAllReminders()
            if (result <= RESULT_NO_DATA_DELETED) {
                _action.value = RemindersAction.DeleteAllRemindersError
            } else {
                _action.value = RemindersAction.DeleteAllRemindersSuccess
            }
        }
    }
}