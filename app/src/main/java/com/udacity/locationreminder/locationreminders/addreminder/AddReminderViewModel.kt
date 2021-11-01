package com.udacity.locationreminder.locationreminders.addreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.mapToDataModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddReminderViewModel(
    private val remindersLocalRepository: RemindersLocalRepository
): ViewModel() {

    private var _selectedReminder = MutableLiveData<ReminderItemView?>()
    val selectedReminder: LiveData<ReminderItemView?> = _selectedReminder

    private var _state = MutableLiveData<AddReminderState>()
    val state: LiveData<AddReminderState> = _state

    private var _action = MutableLiveData<AddReminderAction?>()
    val action: LiveData<AddReminderAction?> = _action

    init {
        _state.value = AddReminderState()
    }

    fun setSelectedReminder(reminder: ReminderItemView?) {
        _selectedReminder.value = reminder
    }

    fun saveReminder() {
        if (isInputsValid().not()) return
        _state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            // Todo Remove this delay
            delay(1500)
            selectedReminder.value?.let {
                runCatching {
                    remindersLocalRepository.saveReminder(it.mapToDataModel())
                }.onSuccess {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderSuccess
                    _selectedReminder.value = null
                    _action.value = null
                }.onFailure {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderError
                }
            }
        }
    }

    private fun isInputsValid(): Boolean {
        var isFormValid = true
        if(_selectedReminder.value?.title.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldTitle
            isFormValid = false
        }
        if(_selectedReminder.value?.locationName.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldLocation
            isFormValid = false
        }
        if(_selectedReminder.value?.description.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldDescription
            isFormValid = false
        }

        if(selectedReminder.value?.latitude == null || selectedReminder.value?.longitude == null) {
            _action.value = AddReminderAction.InputErrorMissingLatLong
            _action.value = null
            isFormValid = false
        }
        return isFormValid
    }
}

