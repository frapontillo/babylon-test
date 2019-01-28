package com.github.frapontillo.babylon

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single

data class SchedulerPair(val subscribeOn: Scheduler, val observeOn: Scheduler)

fun <T> Observable<T>.applySchedulerPair(schedulerPair: SchedulerPair) =
    subscribeOn(schedulerPair.subscribeOn).observeOn(schedulerPair.observeOn)

fun <T> Single<T>.applySchedulerPair(schedulerPair: SchedulerPair) =
    subscribeOn(schedulerPair.subscribeOn).observeOn(schedulerPair.observeOn)
