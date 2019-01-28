package com.github.frapontillo.babylon.common

import com.github.frapontillo.babylon.Lce
import io.reactivex.subjects.BehaviorSubject

val <T, U> BehaviorSubject<Lce<T, U>>.lastContentOrNull: T?
    get() = value?.content
