package com.udacity.locationreminder.locationreminders.reminderslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.locationreminder.locationreminders.ReminderItemView
import com.udacity.locationreminder.locationreminders.data.RemindersLocalRepository
import com.udacity.locationreminder.locationreminders.data.dto.ReminderData
import com.udacity.locationreminder.locationreminders.data.dto.Result
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

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadReminders() {
        viewModelScope.launch {
            _state.value = state.value?.copy(isLoading = true)
            when (val result = remindersLocalRepository.getReminders()) {
                is Result.Success<*> -> {
                    // Todo Improve this list result handling
                    val dataList = ArrayList<ReminderItemView>()
                    dataList.addAll((result.data as List<ReminderData>).map { reminder ->
                        ReminderItemView(
                            title = reminder.title,
                            description = reminder.description,
                            isPoi = reminder.title == null,
                            locationName = reminder.locationName,
                            latitude = reminder.latitude ?: 0.0,
                            longitude = reminder.longitude ?: 0.0,
                            id = reminder.id,
                            poiId = reminder.poiId
                        )
                    })
                    if (dataList.isEmpty()) {
                        _action.value = RemindersAction.NoRemindersFound
                        _state.value = state.value?.copy(isLoading = false)
                    } else {
                        // TODO improve me
                        //  Cannot find a setter for <androidx.recyclerview.widget.RecyclerView
                        //  android:liveData> that accepts parameter type 'java.util.List
                        _remindersList.value = dataList
                        _state.value = state.value?.copy(isLoading = false)
                    }
                }
                is Result.Error ->  {
                    _action.value = RemindersAction.LoadRemindersError
                    _action.value = null
                    _state.value = state.value?.copy(isLoading = false)
                }
            }
        }
    }
}