package com.udacity.locationreminder.locationreminders.reminderdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.mapToDataModel
import kotlinx.coroutines.launch

class ReminderDescriptionViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    private var _state = MutableLiveData<ReminderDetailsState>()
    val state: LiveData<ReminderDetailsState> = _state

    private var _action = MutableLiveData<ReminderDetailsAction?>()
    val action: LiveData<ReminderDetailsAction?> = _action

    init {
        _state.value = ReminderDetailsState()
    }

    fun updateReminder(reminderId: Long, isGeofenceEnable: Boolean) {
        viewModelScope.launch {
            val result = remindersLocalRepository.updateReminder(reminderId, isGeofenceEnable)
            if (result == 1) {
                _action.value = ReminderDetailsAction.UpdateReminderDatabaseSuccess
                _action.value = null
            } else {
                _action.value = ReminderDetailsAction.UpdateReminderDatabaseError
                _action.value = null
            }
        }
    }

    fun deleteReminder(reminderItemView: ReminderItemView) {
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteReminder(reminderItemView.mapToDataModel())
            if (result == 1) {
                _action.value = ReminderDetailsAction.DeleteReminderSuccess
                _action.value = null
            } else {
                _action.value = ReminderDetailsAction.DeleteReminderError
                _action.value = null
            }
        }
    }
}