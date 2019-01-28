package com.github.frapontillo.babylon.common

import android.app.Activity
import android.os.Bundle
import com.github.frapontillo.babylon.Lce
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Activity.getSerializableFromExtras(key: String) = intent?.extras?.getSerializable(key) as T

@Suppress("UNCHECKED_CAST")
fun <T, U> Bundle?.getLce(key: String): Lce<T, U>? {
    return this?.getSerializable(key) as Lce<T, U>?
}
