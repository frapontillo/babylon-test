package com.github.frapontillo.babylon.detail

import com.github.frapontillo.babylon.common.network.Author
import com.github.frapontillo.babylon.common.network.PostComment

val backendAuthor = Author("macgyver")

val backendComments = listOf(PostComment(1), PostComment(2), PostComment(3))

fun backendComments(number: Int): List<PostComment> = (0 until number).map { PostComment(it + 1) }
