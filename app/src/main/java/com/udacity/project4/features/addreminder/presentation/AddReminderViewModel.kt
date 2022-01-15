package com.udacity.project4.features.addreminder.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.features.addreminder.domain.usecase.InputValidatorsUseCase
import com.udacity.project4.shareddata.localdatasource.models.ResultData
import com.udacity.project4.shareddata.localdatasource.repository.RemindersLocalRepository
import com.udacity.project4.sharedpresentation.ReminderItemView
import com.udacity.project4.sharedpresentation.mapToDataModel
import kotlinx.coroutines.launch

private const val UPDATE_SUCCESS = 1
private const val INVALID_ID = 0

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

    fun validateFieldsAndSaveOrUpdateReminder(shouldSaveWithoutGeofence: Boolean = false) {
        if (isLocationNameValid(state.value?.selectedReminder?.locationName) &&
            isTitleValid(state.value?.selectedReminder?.title) &&
            isDescriptionValid(state.value?.selectedReminder?.description) &&
            isLatLngValid()
        ) {
            if (shouldSaveWithoutGeofence) {
                proceedToSaveReminder(isEditing = false, shouldSaveWithoutGeofence)
            } else {
                _action.value = AddReminderAction.FormIsValid
            }
        }
    }

    fun proceedToSaveReminder(isEditing: Boolean, shouldSaveWithoutGeofence: Boolean = false) {
        state.value?.selectedReminder?.let {
            if (isEditing) {
                updateReminder(it)
            } else {
                if (shouldSaveWithoutGeofence) {
                    saveReminder(it.copy(isGeofenceEnable = false))
                } else {
                    saveReminder(reminder = it)
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
            when (val result = remindersLocalRepository.saveReminder(reminder.mapToDataModel())) {
                is ResultData.Success<*> -> {
                    _state.value = state.value?.copy(isLoading = false)
                    if (result.data as Long > INVALID_ID) {
                        _action.value = AddReminderAction.AddReminderSuccess(id = (result.data))
                        _action.value = AddReminderAction.ClearErrors
                    }
                }
                else ->  {
                    _state.value = state.value?.copy(isLoading = false)
                    _action.value = AddReminderAction.AddReminderError
                }
            }
        }
    }

    private fun updateReminder(reminder: ReminderItemView) {
        if (isFormValid.not()) return
        viewModelScope.launch {
            when (val result = remindersLocalRepository.updateReminder(reminder.mapToDataModel())) {
                is ResultData.Success<*> -> {
                    if (result.data as Int == UPDATE_SUCCESS) {
                        _action.value = AddReminderAction.UpdateReminderSuccess
                    }
                }
                else -> {
                    _action.value = AddReminderAction.UpdateReminderError
                }
            }
        }
    }

    fun updateGeofenceStatusOnDatabase(reminderId: Long, isGeofenceEnable: Boolean) {
        viewModelScope.launch {
            when (val result = remindersLocalRepository.updateGeofenceStatus(reminderId, isGeofenceEnable)) {
                is ResultData.Success<*> -> {
                    if (result.data as Int == UPDATE_SUCCESS) {
                        _action.value = AddReminderAction.StatusUpdatedSuccess
                    }
                }
                is ResultData.Error -> {
                    _action.value = AddReminderAction.StatusUpdateError
                }
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

