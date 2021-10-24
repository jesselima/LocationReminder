package com.udacity.locationreminder.copyright

data class Copyright(
	val imageResId: Int,
	val sourceName: String,
	val authorName: String? = null,
	val link: String? = null,
	val isAnimation: Boolean = false
)
