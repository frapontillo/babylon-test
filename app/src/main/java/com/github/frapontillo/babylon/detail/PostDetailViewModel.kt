package com.github.frapontillo.babylon.detail

import java.io.Serializable

sealed class PostDetailViewModel(open val postData: PostData) : Serializable {

    data class WithPostDataOnly(override val postData: PostData) : PostDetailViewModel(postData)

    data class WithEverything(override val postData: PostData, val userData: UserData, val commentsData: CommentsData) :
        PostDetailViewModel(postData)
}

data class PostData(val postId: Int, val title: String, val body: String, val authorId: Int) : Serializable
data class UserData(val username: String) : Serializable
data class CommentsData(val numberOfComments: Int) : Serializable
