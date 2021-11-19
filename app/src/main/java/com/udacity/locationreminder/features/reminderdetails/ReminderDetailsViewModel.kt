package com.udacity.locationreminder.features.reminderdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.sharedpresentation.ReminderItemView
import com.udacity.locationreminder.sharedpresentation.mapToDataModel
import kotlinx.coroutines.launch

class ReminderDetailsViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    private var _state = MutableLiveData<ReminderDetailsState>()
    val state: LiveData<ReminderDetailsState> = _state

    private var _action = MutableLiveData<ReminderDetailsAction?>()
    val action: LiveData<ReminderDetailsAction?> = _action

    init {
        _state.value = ReminderDetailsState()
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