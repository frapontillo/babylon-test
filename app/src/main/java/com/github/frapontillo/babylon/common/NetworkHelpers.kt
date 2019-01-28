package com.github.frapontillo.babylon.common

import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val rxJavaCallAdapter: CallAdapter.Factory by lazy {
    RxJava2CallAdapterFactory.create()
}

val gsonConverter: Converter.Factory by lazy {
    GsonConverterFactory.create()
}

inline fun <reified T> createBackend(): T {
    val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(rxJavaCallAdapter)
        .addConverterFactory(gsonConverter)
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .build()

    return retrofit.create(T::class.java)
}
