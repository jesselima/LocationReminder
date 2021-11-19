package com.udacity.locationreminder.features.reminderslist

import com.udacity.locationreminder.R
import com.udacity.locationreminder.sharedpresentation.BaseRecyclerViewAdapter
import com.udacity.locationreminder.sharedpresentation.ReminderItemView

class RemindersListAdapter(
    onReminderItemClick: (selectedReminderView: ReminderItemView) -> Unit,
    viewsResIdActions: List<Pair<Int, ((selectedReminderView: ReminderItemView) -> Unit)>?>
) : BaseRecyclerViewAdapter<ReminderItemView>(onReminderItemClick, viewsResIdActions) {
    override fun getLayoutRes(viewType: Int) = R.layout.item_list_reminder
}