package com.udacity.project4.features.copyright

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.R
import com.udacity.project4.common.extensions.inflate
import com.udacity.project4.common.extensions.showWithFadeIn
import com.udacity.project4.databinding.LayoutItemListCopyrightBinding

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
		val binding = LayoutItemListCopyrightBinding.inflate(
			LayoutInflater.from(rootViewGroup.context),
			rootViewGroup,
			false
		)
		return rootViewGroup.inflate(R.layout.layout_item_list_copyright).run {
			showWithFadeIn()
			AuthorViewHolder(binding)
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val viewHolder = holder as AuthorViewHolder
		viewHolder.bindDataToView(copyrights[position])
	}

	override fun getItemCount() = copyrights.size
}