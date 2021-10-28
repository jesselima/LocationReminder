package com.udacity.locationreminder.locationreminders.reminderslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.FragmentReminderListBinding
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
        binding.lifecycleOwner = this
        setupRecyclerView()
        setupActionListeners()
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
                    binding.animationNoLocationData.pauseAnimation()
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
        binding.animationNoLocationData.playAnimation()
    }

    private fun navigateToAddReminder() {
        binding.animationNoLocationData.pauseAnimation()
        findNavController().navigate(R.id.navigateToSaveReminder)
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }
        // binding.reminderssRecyclerView.setup(adapter)
    }


}
