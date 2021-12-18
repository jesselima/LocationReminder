package com.udacity.locationreminder.features.addreminder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.features.addreminder.usecase.InputValidatorsUseCase
import com.udacity.locationreminder.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.locationreminder.sharedpresentation.ReminderItemView
import com.udacity.locationreminder.sharedpresentation.mapToDataModel
import kotlinx.coroutines.launch

private const val UPDATE_SUCCESS = 1

class AddReminderViewModel(
    private val remindersLocalRepository: RemindersLocalRepository,
    private val inputValidatorsUseCase: InputValidatorsUseCase
): ViewModel() {

    private var _state = MutableLiveData<AddReminderState>()
    val state: LiveData<AddReminderState> = _state

    private var _action = MutableLiveData<AddReminderAction>()
    val action: LiveData<AddReminderAction> = _action

    private var isFormValid: Boolean = false

    init {
        _state.value = AddReminderState()
        _action.value = AddReminderAction.ClearErrors
    }

    fun setSelectedReminder(reminder: ReminderItemView?) {
        reminder?.let { _state.value = state.value?.copy(selectedReminder = it) }
    }

    fun validateFieldsSaveOrUpdateReminder(isEditing: Boolean = false) {
        if (isLocationNameValid(state.value?.selectedReminder?.locationName) &&
            isTitleValid(state.value?.selectedReminder?.title) &&
            isDescriptionValid(state.value?.selectedReminder?.description) &&
            isLatLngValid()
        ) {
            state.value?.selectedReminder?.let {
                if (isEditing) {
                    updateReminder(it)
                } else {
                    saveReminder(it)
                }
            }
        }
    }

    private fun saveReminder(reminder: ReminderItemView) {
        if (isFormValid) {
            _action.value = AddReminderAction.ClearErrors
        } else {
            return
        }
        _state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.saveReminder(reminder.mapToDataModel())
            }.onSuccess { newReminderDatabaseId ->
                if (newReminderDatabaseId > 0) {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderSuccess(id = newReminderDatabaseId)
                } else {
                   setErrorActionAndState()
                }
            }.onFailure {
                setErrorActionAndState()
            }
        }
    }

    private fun setErrorActionAndState() {
        _state.value = state.value?.copy(isLoading = false)
        _action.value = AddReminderAction.AddReminderError
    }

    private fun updateReminder(reminder: ReminderItemView) {
        if (isFormValid.not()) return
        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.updateReminder(reminder.mapToDataModel())
            }.onSuccess { result ->
                if (result >= UPDATE_SUCCESS ) {
                    _action.value = AddReminderAction.UpdateReminderSuccess
                } else {
                    _action.value = AddReminderAction.UpdateReminderError
                }
            }.onFailure {
                _action.value = AddReminderAction.UpdateReminderError
            }
        }
    }

    fun isLocationNameValid(locationName: String?): Boolean {
        _state.value?.selectedReminder?.locationName = locationName
        inputValidatorsUseCase.isLocationNameValid(locationName).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldLocationName
            } else {
                _action.value = AddReminderAction.InputClearErrorFieldLocationName
            }
            isFormValid = this
            return this
        }
    }

    fun isTitleValid(title: String?): Boolean {
        _state.value?.selectedReminder?.title = title
        inputValidatorsUseCase.isTitleValid(title).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldTitle
            } else {
                _action.value = AddReminderAction.InputClearErrorFieldTitle
            }
            isFormValid = this
            return this
        }
    }

    fun isDescriptionValid(description: String?): Boolean {
        _state.value?.selectedReminder?.description = description
        inputValidatorsUseCase.isDescriptionValid(description).run {
            if (this.not()) {
                _action.value = AddReminderAction.InputErrorFieldDescription
            } else {
                _action.value = AddReminderAction.InputClearErrorFieldDescription
            }
            isFormValid = this
            return this
        }
    }

    fun isLatLngValid(): Boolean {
        val latitude = _state.value?.selectedReminder?.latitude
        val longitude = _state.value?.selectedReminder?.longitude
        return if (latitude == null && longitude == null) {
            _action.value = AddReminderAction.InputErrorMissingLatLong
            isFormValid = false
            false
        } else {
            _action.value = AddReminderAction.InputClearErrorMissingLatLong
            isFormValid = true
            true
        }
    }
}

