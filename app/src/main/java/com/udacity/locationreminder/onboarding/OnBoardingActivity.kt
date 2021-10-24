package com.udacity.locationreminder.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.udacity.locationreminder.R
import com.udacity.locationreminder.databinding.ActivityOnBoardingBinding
import com.udacity.locationreminder.locationreminders.RemindersActivity
import com.udacity.locationreminder.utils.AnimDuration
import com.udacity.locationreminder.utils.TranslationType
import com.udacity.locationreminder.utils.showAnimated

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val viewModel = OnBoardingViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingPagerAdapter(
            viewModel.onBoardingSteps, this
        )
        val viewPager: ViewPager2 = binding.onBoardingViewPager
        viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, viewPager) { _, _ ->
            /* Use "tab" and "position" to set tabs texts */
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                if(position + 1 == viewModel.onBoardingSteps.size) {
                    binding.onboardingButtonSkip.showAnimated(
                        translationType = TranslationType.TRANSLATION_X,
                        animDuration = AnimDuration.TIME_2000_MS
                    )
                    binding.onboardingButtonSkip.text = getString(R.string.start)
                    binding.onboardingButtonSkip.setOnClickListener {
                        startActivity(Intent(applicationContext, RemindersActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } else {
                    binding.onboardingButtonSkip.visibility = View.GONE
                }
                super.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d("", (state == ViewPager2.SCROLL_STATE_DRAGGING).toString())
            }

        })

    }
}