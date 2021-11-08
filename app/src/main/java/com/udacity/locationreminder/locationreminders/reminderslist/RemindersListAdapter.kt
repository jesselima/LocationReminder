package com.udacity.locationreminder.locationreminders.reminderslist

import com.udacity.locationreminder.R
import com.udacity.locationreminder.base.BaseRecyclerViewAdapter
import com.udacity.locationreminder.locationreminders.ReminderItemView

class RemindersListAdapter(
    onReminderItemClick: (selectedReminderView: ReminderItemView) -> Unit,
    viewsResIdActions: List<Pair<Int, ((selectedReminderView: ReminderItemView) -> Unit)>?>
) : BaseRecyclerViewAdapter<ReminderItemView>(onReminderItemClick, viewsResIdActions) {
    override fun getLayoutRes(viewType: Int) = R.layout.item_list_reminder
}