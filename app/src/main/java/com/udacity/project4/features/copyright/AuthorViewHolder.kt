package com.udacity.project4.features.copyright

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project4.databinding.LayoutItemListCopyrightBinding

class AuthorViewHolder(
	private val binding: LayoutItemListCopyrightBinding
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

	private var view: View = itemView
	private var copyright: Copyright? = null

	init {
		itemView.setOnClickListener(this)
	}

	override fun onClick(v: View?) {
		view.context.startActivity(
			Intent(Intent.ACTION_VIEW, Uri.parse(copyright?.link))
		)
	}

	fun bindDataToView(copyrightData: Copyright) {
		this.copyright = copyrightData
		binding.copyrightSource.text = copyrightData.sourceName
		binding.copyrightAuthorName.text = copyrightData.authorName

		if (copyright?.isAnimation == true) {
			binding.copyrightImage.setAnimation(copyrightData.imageResId)
		} else {
			binding.copyrightImage.setImageResource(copyrightData.imageResId)
		}
	}
}