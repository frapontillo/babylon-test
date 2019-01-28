package com.github.frapontillo.babylon.common.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Backend {

    @GET("posts")
    fun getPosts(): Single<List<Post>>

    @GET("comments")
    fun getPostComments(@Query("postId") postId: Int): Single<List<PostComment>>

    @GET("users/{id}")
    fun getAuthor(@Path("id") id: Int): Single<Author>
}
