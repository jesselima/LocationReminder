package com.udacity.locationreminder.features.copyright

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.locationreminder.R
import com.udacity.locationreminder.common.extensions.inflate
import com.udacity.locationreminder.common.extensions.showWithFadeIn

class CopyrightAdapter(
	private var copyrights: MutableList<Copyright> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	@SuppressLint("NotifyDataSetChanged")
	fun submitList(copyrightsData: MutableList<Copyright>) {
		copyrights.clear()
		copyrights = copyrightsData
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(rootViewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return rootViewGroup.inflate(R.layout.layout_item_list_copyright).run {
			showWithFadeIn()
			AuthorViewHolder(this)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val viewHolder = holder as AuthorViewHolder
		viewHolder.bindDataToView(copyrights[position])
	}

	override fun getItemCount() = copyrights.size
}