package com.udacity.project4.features.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityOnBoardingBinding
import com.udacity.project4.features.RemindersActivity
import com.udacity.project4.common.extensions.AnimDuration
import com.udacity.project4.common.extensions.TranslationType
import com.udacity.project4.common.extensions.showAnimated

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
                    binding.onboardingButtonStart.showAnimated(
                        translationType = TranslationType.TRANSLATION_X,
                        animDuration = AnimDuration.TIME_2000_MS
                    )
                    binding.onboardingButtonStart.text = getString(R.string.start)
                    binding.onboardingButtonStart.setOnClickListener {
                        startActivity(Intent(applicationContext, RemindersActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                } else {
                    binding.onboardingButtonStart.visibility = View.GONE
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