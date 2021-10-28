package com.udacity.locationreminder.locationreminders.reminderslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentReminderListBinding
import com.udacity.locationreminder.utils.ToastType
import com.udacity.locationreminder.utils.setup
import com.udacity.locationreminder.utils.showCustomToast
import com.udacity.locationreminder.utils.showDialog
import com.udacity.locationreminder.utils.signOut
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : Fragment() {

    val viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentReminderListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderListBinding.inflate(layoutInflater)
        binding.refreshLayout.setOnRefreshListener { viewModel.loadReminders() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupActionListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.remindersList.observe(viewLifecycleOwner) {
            binding.noDataAnimation.isVisible = it.isEmpty()
            binding.noDataTextView.isVisible = it.isEmpty()
            binding.refreshLayout.isRefreshing = false
        }
        viewModel.action.observe(viewLifecycleOwner) { action ->
            when (action) {
                RemindersAction.LoadRemindersError -> {
                    context?.showCustomToast(
                        titleResId = R.string.message_loading_reminder_error,
                        toastType = ToastType.ERROR
                    )
                }
            }
        }
    }
    private fun setupActionListeners() {
        binding.actionButtonAddReminder.setOnClickListener {
            findNavController().navigate(ReminderListFragmentDirections.navigateToSaveReminder())
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    showDialog(
                        context = context,
                        title = getString(R.string.logout),
                        message = getString(R.string.logout_confirmation_message),
                        positiveButtonAction = { signOut() }
                    )
                    true
                }
                R.id.copyrights -> {
                    binding.noDataAnimation.pauseAnimation()
                    findNavController().navigate(
                        ReminderListFragmentDirections.navigateToCopyrights()
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadReminders()
        binding.noDataAnimation.playAnimation()
    }

    private fun setupRecyclerView() {
        // Todo improve this recycler view Setup
        val adapter = RemindersListAdapter {
        }
         binding.reminderssRecyclerView.setup(adapter)
    }
}
