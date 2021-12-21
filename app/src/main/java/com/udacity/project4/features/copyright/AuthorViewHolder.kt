package com.udacity.project4.features.copyright

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_item_list_copyright.view.*

class AuthorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

	private var view: View = itemView
	private var copyright: Copyright? = null

	init {
		itemView.setOnClickListener(this)
	}

	override fun onClick(v: View?) {
		view.context.startActivity(
			Intent(
				Intent.ACTION_VIEW,
				Uri.parse(copyright?.link)
			)
		)
	}

	fun bindDataToView(copyrightData: Copyright) {
		this.copyright = copyrightData
		view.copyrightSource.text = copyrightData.sourceName
		view.copyrightAuthorName.text = copyrightData.authorName

		val isAnimation = copyright?.isAnimation ?: false
		if (isAnimation) {
			view.copyrightImage.setAnimation(copyrightData.imageResId)
		} else {
			view.copyrightImage.setImageResource(copyrightData.imageResId)
		}
	}
}