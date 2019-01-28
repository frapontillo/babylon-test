package com.github.frapontillo.babylon

import android.app.Activity
import android.content.Intent
import com.github.frapontillo.babylon.detail.EXTRA_POST_DATA
import com.github.frapontillo.babylon.detail.PostData
import com.github.frapontillo.babylon.detail.PostDetailActivity

class AndroidNavigator(private val activity: Activity) : Navigator {

    override fun goToPostDetail(postId: Int, title: String, body: String, authorId: Int) {
        goTo(PostDetailActivity::class.java) {
            putExtra(
                EXTRA_POST_DATA, PostData(
                    postId,
                    title,
                    body,
                    authorId
                )
            )
        }
    }

    private fun goTo(activityClass: Class<*>, intentModifier: Intent.() -> Unit) {
        Intent(activity, activityClass)
            .apply(intentModifier)
            .start()
    }

    private fun Intent.start() = activity.startActivity(this)

    override fun close() {
        activity.finish()
    }
}
