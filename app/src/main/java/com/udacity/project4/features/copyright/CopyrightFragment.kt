package com.udacity.project4.features.copyright

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentCopyrightsBinding

class CopyrightFragment : Fragment() {

	private lateinit var binding: FragmentCopyrightsBinding

	private val copyrightAdapter: CopyrightAdapter by lazy {
		CopyrightAdapter()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = FragmentCopyrightsBinding.inflate(layoutInflater)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.copyrightRecyclerView.adapter = copyrightAdapter
		copyrightAdapter.submitList(getCopyrightList().toMutableList())
		binding.copyrightTopAppBar.setNavigationOnClickListener {
			findNavController().popBackStack(R.id.reminderListFragment, false)
		}
	}

	private fun getCopyrightList() : List<Copyright> {
		return listOf(
			Copyright(
				imageResId = R.raw.animation_location_rounded,
				isAnimation = true,
				sourceName = "Lottie Files",
				authorName = "Edwin Nollen",
				link = "https://lottiefiles.com/1342-location"
			),
			Copyright(
				imageResId = R.raw.animation_location_pin,
				isAnimation = true,
				sourceName = "Lottie Files",
				authorName = "Vladislav Sholohov",
				link = "https://lottiefiles.com/58217-location-pin"
			),
			Copyright(
				imageResId = R.raw.animation_location_pin_alert,
				isAnimation = true,
				sourceName = "Lottie Files",
				authorName = "Pavlo Monakhov",
				link = "https://lottiefiles.com/18199-location-pin-on-a-map"
			),
			Copyright(
				imageResId = R.raw.animation_location_select,
				isAnimation = true,
				sourceName = "Lottie Files",
				authorName = "SAGATOV ART",
				link = "https://lottiefiles.com/39612-location-animation"
			),
			Copyright(
				imageResId = R.drawable.background_image_phone_pin_locations_thumbnail,
				isAnimation = false,
				sourceName = "Freepik",
				authorName = "rawpixel-com",
				link = "https://www.freepik.com/free-vector/covid-19-virus-tracking-app-new-normal-presentation_15229753.htm"
			),
		)
	}
}