package com.github.frapontillo.babylon.list

import java.io.Serializable

data class PostViewModel(
    val postId: Int,
    val title: String,
    val body: String,
    val authorId: Int
) : Serializable
