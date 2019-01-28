package com.github.frapontillo.babylon.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.post_list_item.*

class PostViewHolder(override val containerView: View, private val onPostSelected: OnPostSelected) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(item: PostViewModel) {
        post_title.text = item.title
        post_body.text = item.body
        containerView.setOnClickListener { onPostSelected(item) }
    }
}
