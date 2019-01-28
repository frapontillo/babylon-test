package com.github.frapontillo.babylon

interface Navigator {

    fun goToPostDetail(postId: Int, title: String, body: String, authorId: Int)

    fun close()
}
