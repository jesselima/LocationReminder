package com.udacity.locationreminder.features.reminderslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.shareddata.localdatasource.models.ReminderData
import com.udacity.locationreminder.shareddata.localdatasource.models.ResultData
import com.udacity.locationreminder.shareddata.localdatasource.models.mapToPresentationModel
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.sharedpresentation.ReminderItemView
import com.udacity.locationreminder.sharedpresentation.mapToDataModel
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
        _state.postValue(RemindersState())
    }

    fun getReminders() {
        viewModelScope.launch {
            _state.postValue(state.value?.copy(isLoading = true))
            when (val result = remindersLocalRepository.getReminders()) {
                is ResultData.Success<*> -> {
                    val dataList = ArrayList<ReminderItemView>()
                    dataList.addAll((result.data as List<ReminderData>).map { reminder ->
                        reminder.mapToPresentationModel()
                    })
                    if (dataList.isEmpty()) {
                        _action.postValue(RemindersAction.NoRemindersFound)
                        _state.postValue(
                            state.value?.copy(isLoading = false, reminders = emptyList())
                        )
                    } else {
                        _state.postValue(
                            state.value?.copy(isLoading = false, reminders = dataList)
                        )
                    }
                }
                is ResultData.Error ->  {
                    _state.postValue(state.value?.copy(isLoading = false))
                    _action.value = RemindersAction.LoadRemindersError
                }
            }
        }
    }

    fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean) {
        viewModelScope.launch {
            val result = remindersLocalRepository.updateGeofenceStatus(reminderId, isGeofenceEnable)
            if (result == RESULT_DATA_AFFECTED) {
                _action.postValue(RemindersAction.UpdateRemindersSuccess)
            } else {
                _action.postValue(RemindersAction.UpdateRemindersError)
            }
        }
    }

    fun deleteReminder(reminderItemView: ReminderItemView) {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteReminder(reminderItemView.mapToDataModel())
            if (result == RESULT_DATA_AFFECTED) {
                _action.postValue(RemindersAction.DeleteRemindersSuccess)
            } else {
                _action.postValue(RemindersAction.DeleteRemindersError)
            }
        }
    }

    fun deleteAllReminders() {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteAllReminders()
            if (result <= RESULT_NO_DATA_DELETED) {
                _action.postValue(RemindersAction.DeleteAllRemindersError)
            } else {
                _action.postValue(RemindersAction.DeleteAllRemindersSuccess)
            }
        }
    }
}