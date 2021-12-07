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
    }

    fun setSelectedReminder(reminder: ReminderItemView?) {
        reminder?.let {  _state.postValue(state.value?.copy(selectedReminder = it)) }
    }

    fun validateFieldsSaveOrUpdateReminder(isEditing: Boolean = false) {
        if (isTitleValid(state.value?.selectedReminder?.title) &&
            isLocationNameValid(state.value?.selectedReminder?.locationName) &&
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
        if (isFormValid.not()) return
        _state.postValue(state.value?.copy(isLoading = true))

        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.saveReminder(reminder.mapToDataModel())
            }.onSuccess {
                if (it > 0) {
                    _state.postValue(state.value?.copy(isLoading = false))
                    _action.postValue(AddReminderAction.AddReminderSuccess)
                } else {
                   setErrorActionAndState()
                }
            }.onFailure {
                setErrorActionAndState()
            }
        }
    }

    private fun setErrorActionAndState() {
        _state.postValue(state.value?.copy(isLoading = false))
        _action.postValue(AddReminderAction.AddReminderError)
    }

    private fun updateReminder(reminder: ReminderItemView) {
        viewModelScope.launch {
            runCatching {
                remindersLocalRepository.updateReminder(reminder.mapToDataModel())
            }.onSuccess { result ->
                if (result >= UPDATE_SUCCESS ) {
                    _action.postValue(AddReminderAction.UpdateReminderSuccess)
                } else {
                    _action.postValue(AddReminderAction.UpdateReminderError)
                }
            }.onFailure {
                _action.postValue(AddReminderAction.UpdateReminderError)
            }
        }
    }

    fun isTitleValid(title: String?): Boolean {
        state.value?.selectedReminder?.title = title
        inputValidatorsUseCase.isTitleValid(title).run {
            if (this.not()) {
                _action.postValue(AddReminderAction.InputErrorFieldTitle)
            }
            isFormValid = this
            return this
        }
    }

    fun isLocationNameValid(locationName: String?): Boolean {
        state.value?.selectedReminder?.locationName = locationName
        inputValidatorsUseCase.isLocationNameValid(locationName).run {
            if (this.not()) {
                _action.postValue(AddReminderAction.InputErrorFieldLocationName)
            }
            isFormValid = this
            return this
        }
    }

    fun isDescriptionValid(description: String?): Boolean {
        state.value?.selectedReminder?.description = description
        inputValidatorsUseCase.isDescriptionValid(description).run {
            if (this.not()) {
                _action.postValue(AddReminderAction.InputErrorFieldDescription)
            }
            isFormValid = this
            return this
        }
    }

    fun isLatLngValid(): Boolean {
        val latitude = _state.value?.selectedReminder?.latitude
        val longitude = _state.value?.selectedReminder?.longitude
        return if (latitude != null && longitude != null) {
            isFormValid = true
            true
        } else {
            _action.postValue(AddReminderAction.InputErrorMissingLatLong)
            isFormValid = false
            false
        }
    }
}

