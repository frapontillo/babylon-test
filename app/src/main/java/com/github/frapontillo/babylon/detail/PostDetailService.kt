package com.github.frapontillo.babylon.detail

import com.github.frapontillo.babylon.common.network.Backend
import io.reactivex.Single

class PostDetailService(private val backend: Backend) {

    fun getUsername(userId: Int): Single<String> = backend.getAuthor(userId).map { it.username }

    fun getCommentsCount(postId: Int): Single<Int> = backend.getPostComments(postId).map { it.size }
}
