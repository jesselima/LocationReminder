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

class SharedReminderViewModel(
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
        clearCurrentReminder()
        _selectedReminder.value = reminder
    }

    fun saveReminder(title: String?, name: String?, description: String?) {
        _selectedReminder.value = selectedReminder.value?.copy(
            title = title, locationName = name, description = description
        )

        if (isInputsValid(title, name, description).not()) return

        //  TODO Add location to GeoFences.

        _state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            delay(1500)
            selectedReminder.value?.let {
                runCatching {
                    remindersLocalRepository.saveReminder(it.mapToDataModel())
                }.onSuccess {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderSuccess
                }.onFailure {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderError
                }
            }
        }
    }

    private fun isInputsValid(title: String?, name: String?, description: String?): Boolean {
        var isFormValid = true
        if(title.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldTitle
            isFormValid = false
        }
        if(name.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldLocation
            isFormValid = false
        }
        if(description.isNullOrEmpty()) {
            _action.value = AddReminderAction.InputErrorFieldDescription
            isFormValid = false
        }

        if(selectedReminder.value?.latLng?.latitude == null || selectedReminder.value?.latLng?.longitude == null) {
            _action.value = AddReminderAction.InputErrorMissingLatLong
            _action.value = null
            isFormValid = false
        }
        return isFormValid
    }

    private fun clearCurrentReminder() {
        _selectedReminder.value = null
    }
}

