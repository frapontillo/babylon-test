package com.github.frapontillo.babylon.common

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.annotation.StringRes

@SuppressWarnings("SpreadOperator") // the only way to pass var args to Java
fun Resources.getHtml(@StringRes resId: Int, vararg formatArgs: Any): Spanned {
    val formattedText = getString(resId, *formatArgs)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(formattedText)
    }
}
