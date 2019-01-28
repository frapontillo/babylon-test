package com.github.frapontillo.babylon.list

import com.github.frapontillo.babylon.common.network.Backend
import io.reactivex.Single

class PostListService(private val backend: Backend) {

    fun getPosts(): Single<PostListViewModel> {
        return backend
            .getPosts()
            .map { list -> list.sortedByDescending { it.id } }
            .map { list ->
                PostListViewModel(
                    list.map { post -> PostViewModel(post.id, post.title, post.body, post.userId) }
                )
            }
    }
}
