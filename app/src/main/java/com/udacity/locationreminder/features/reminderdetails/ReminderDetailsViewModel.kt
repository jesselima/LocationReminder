package com.udacity.locationreminder.features.reminderdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.sharedpresentation.ReminderItemView
import com.udacity.locationreminder.sharedpresentation.mapToDataModel
import kotlinx.coroutines.launch

private const val RESULT_DATA_AFFECTED = 1

class ReminderDetailsViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
) : ViewModel() {

    private var _state = MutableLiveData<ReminderDetailsState>()
    val state: LiveData<ReminderDetailsState> = _state

    private var _action = MutableLiveData<ReminderDetailsAction>()
    val action: LiveData<ReminderDetailsAction> = _action

    init {
        _state.value = ReminderDetailsState()
    }

    fun deleteReminder(reminderItemView: ReminderItemView) {
        _state.postValue(state.value?.copy(isLoading = true))
        viewModelScope.launch {
            val result = remindersLocalRepository.deleteReminder(reminderItemView.mapToDataModel())
            if (result == RESULT_DATA_AFFECTED) {
                _action.postValue(ReminderDetailsAction.DeleteReminderSuccess)
            } else {
                _action.postValue(ReminderDetailsAction.DeleteReminderError)
            }
            _state.postValue(state.value?.copy(isLoading = false))
        }
    }
}