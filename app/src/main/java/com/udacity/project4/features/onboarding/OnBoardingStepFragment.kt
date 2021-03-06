package com.udacity.project4.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.project4.R
import com.udacity.project4.common.EventObserver
import com.udacity.project4.databinding.FragmentOnBoardingBinding
import com.udacity.project4.common.extensions.AnimDuration
import com.udacity.project4.common.extensions.TranslationType
import com.udacity.project4.common.extensions.showAnimated

class OnBoardingStepFragment : Fragment() {

    private lateinit var onBoardingViewModel: OnBoardingViewModel
    private var _binding: FragmentOnBoardingBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBoardingViewModel = ViewModelProvider(this).get(OnBoardingViewModel::class.java).apply {
            setOnBoardingStep(arguments?.getInt(ARG_SECTION_NUMBER) ?: 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBoardingViewModel.onBoardingStepLiveData.observe(viewLifecycleOwner, EventObserver {
            binding.onBoardingStepImage.apply {
                showAnimated(
                    translationType = TranslationType.TRANSLATION_X,
                    animDuration = AnimDuration.TIME_500_MS,
                    values = floatArrayOf(0f)
                )
                setAnimation(it.animationResId)
            }
            binding.onBoardingStepTitle.apply {
                showAnimated(
                    translationType = TranslationType.TRANSLATION_X,
                    animDuration = AnimDuration.TIME_1000_MS
                )
                text = getString(it.title)
            }
            binding.onBoardingStepDescription.apply {
                showAnimated(
                    animationResId = R.anim.fade_in_with_interpolator,
                    translationType = TranslationType.TRANSLATION_X,
                    animDuration = AnimDuration.TIME_2000_MS,
                    values = floatArrayOf(70f)
                )
                text = getString(it.description)
            }
        })
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_ONBOARD_STEP_DATA = "intro_step"

        @JvmStatic
        fun newInstance(sectionNumber: Int, onBoardingStep: OnBoardingStep): OnBoardingStepFragment {
            return OnBoardingStepFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                    putParcelable(ARG_ONBOARD_STEP_DATA, onBoardingStep)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}