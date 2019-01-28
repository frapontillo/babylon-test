package com.github.frapontillo.babylon

import io.reactivex.Observable
import io.reactivex.Single
import java.io.Serializable

sealed class Lce<T, U>(open val content: T? = null) : Serializable {

    data class Loading<T, U>(override val content: T? = null) : Lce<T, U>()
    data class Content<T, U>(override val content: T) : Lce<T, U>()
    data class Error<T, U>(val error: U, override val content: T? = null) : Lce<T, U>()

    fun hasContent() = content != null
}

fun <T> Single<T>.mapToLce(previousContent: T?): Observable<Lce<T, Throwable>> {
    return this.toObservable().mapToLce(previousContent)
}

fun <T> Observable<T>.mapToLce(previousContent: T?): Observable<Lce<T, Throwable>> {
    return this
        .map { Lce.Content<T, Throwable>(it) as Lce<T, Throwable> }
        .startWith(Lce.Loading(previousContent))
        .onErrorReturn { Lce.Error(it, previousContent) }
}
