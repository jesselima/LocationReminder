package com.udacity.locationreminder.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    private val onBoardingSteps: List<OnBoardingStep>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = onBoardingSteps.size

    override fun createFragment(position: Int): Fragment {
        return OnBoardingStepFragment.newInstance(
            sectionNumber = position,
            onBoardingStep = onBoardingSteps[position]
        )
    }
}