package com.github.frapontillo.babylon.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.frapontillo.babylon.R

class PostListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onPostSelected: OnPostSelected
) : ListAdapter<PostViewModel, PostViewHolder>(PostViewModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = layoutInflater.inflate(R.layout.post_list_item, parent, false)
        return PostViewHolder(view, onPostSelected)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class PostViewModelDiffCallback : DiffUtil.ItemCallback<PostViewModel>() {
        override fun areItemsTheSame(oldItem: PostViewModel, newItem: PostViewModel): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: PostViewModel, newItem: PostViewModel): Boolean = oldItem == newItem
    }
}
