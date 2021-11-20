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

class RemindersListViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    // Todo improve this Party of live Data
    private var _state = MutableLiveData<RemindersState>()
    val state: LiveData<RemindersState> = _state

    private var _action = MutableLiveData<RemindersAction?>()
    val action: LiveData<RemindersAction?> = _action

    private var _remindersList = MutableLiveData<List<ReminderItemView>>()
    val remindersList: LiveData<List<ReminderItemView>> = _remindersList

    init {
        _state.value = RemindersState()
    }

    fun loadReminders() {
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
                        _state.value = state.value?.copy(isLoading = false)
                        _remindersList.value = emptyList()
                    } else {
                        _remindersList.value = dataList
                        _state.value = state.value?.copy(isLoading = false)
                    }
                }
                is ResultData.Error ->  {
                    _action.value = RemindersAction.LoadRemindersError
                    _action.value = null
                    _state.value = state.value?.copy(isLoading = false)
                }
            }
        }
    }

    fun updateGeofenceStatus(reminderId: Long, isGeofenceEnable: Boolean) {
        viewModelScope.launch {
            val result = remindersLocalRepository.updateGeofenceStatus(reminderId, isGeofenceEnable)
            if (result == 1) {
                _action.value = RemindersAction.UpdateRemindersSuccess
                _action.value = null
            } else {
                _action.value = RemindersAction.UpdateRemindersError
                _action.value = null
            }
        }
    }

    fun deleteReminder(reminderItemView: ReminderItemView) {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteReminder(reminderItemView.mapToDataModel())
            if (result == 1) {
                _action.value = RemindersAction.DeleteRemindersSuccess
                _action.value = null
            } else {
                _action.value = RemindersAction.DeleteRemindersError
                _action.value = null
            }
        }
    }
}