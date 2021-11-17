package com.udacity.locationreminder.locationreminders.addreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.mapToDataModel
import kotlinx.coroutines.launch

private const val UPDATE_SUCCESS = 1

class AddReminderViewModel(
    private val remindersLocalRepository: RemindersLocalRepository,
    private val inputValidatorsUseCase: InputValidatorsUseCase
): ViewModel() {

    private var _selectedReminder = MutableLiveData<ReminderItemView?>()
    val selectedReminder: LiveData<ReminderItemView?> = _selectedReminder

    private var _state = MutableLiveData<AddReminderState>()
    val state: LiveData<AddReminderState> = _state

    private var _action = MutableLiveData<AddReminderAction?>()
    val action: LiveData<AddReminderAction?> = _action

    private var isFormValid: Boolean = false

    init {
        _state.value = AddReminderState()
    }

    fun setSelectedReminder(reminder: ReminderItemView?) {
        _selectedReminder.value = reminder
    }

    fun validateFieldsSaveOrUpdateReminder(isEditing: Boolean = false) {
        if (isTitleValid(_selectedReminder.value?.title) &&
            isLocationNameValid(_selectedReminder.value?.locationName) &&
            isDescriptionValid(_selectedReminder.value?.description) &&
            isLatLngValid()
        ) {
            _selectedReminder.value?.let {
                if (isEditing) {
                    updateReminder(it)
                } else {
                    saveReminder(it)
                }
            }
        }
    }

    private fun saveReminder(reminder: ReminderItemView) {
        if (isFormValid.not()) return
        _state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.saveReminder(reminder.mapToDataModel())
            }.onSuccess {
                _state.value = state.value?.copy(isLoading = false)
                _action.value = AddReminderAction.AddReminderSuccess
                _action.value = null
            }.onFailure {
                _state.value = state.value?.copy(isLoading = false)
                _action.value = AddReminderAction.AddReminderError
                _action.value = null
            }
        }
    }

    private fun updateReminder(reminder: ReminderItemView) {
        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.updateReminder(reminder.mapToDataModel())
            }.onSuccess { result ->
                if (result >= UPDATE_SUCCESS )
                    _action.value = AddReminderAction.UpdateReminderSuccess
                    _action.value = null

            }.onFailure {
                _action.value = AddReminderAction.UpdateReminderError
                _action.value = null
            }
        }
    }

    fun isTitleValid(title: String?): Boolean {
        _selectedReminder.value?.title = title
        inputValidatorsUseCase.validateTitle(title).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldTitle
                _action.value = null
            }
            isFormValid = this
            return this
        }
    }

    fun isLocationNameValid(locationName: String?): Boolean {
        _selectedReminder.value?.locationName = locationName
        inputValidatorsUseCase.validateLocationName(locationName).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldLocationName
                _action.value = null
            }
            isFormValid = this
            return this
        }
    }

    fun isDescriptionValid(description: String?): Boolean {
        _selectedReminder.value?.description = description
        inputValidatorsUseCase.validateDescription(description).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldDescription
                _action.value = null
            }
            isFormValid = this
            return this
        }
    }

    private fun isLatLngValid(): Boolean {
        inputValidatorsUseCase.validateLatLng(
            selectedReminder.value?.latitude, selectedReminder.value?.longitude
        ).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorMissingLatLong
                _action.value = null
            }
            return this
        }
    }
}

