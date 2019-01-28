package com.github.frapontillo.babylon.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.frapontillo.babylon.AndroidNavigator
import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.R
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.common.createBackend
import com.github.frapontillo.babylon.common.getHtml
import com.github.frapontillo.babylon.common.getLce
import com.github.frapontillo.babylon.common.getSerializableFromExtras
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.post_detail_activity.*

const val EXTRA_POST_DATA = "EXTRA_POST_DATA"

private const val KEY_VIEW_MODEL_STATE = "KEY_VIEW_MODEL_STATE"

class PostDetailActivity : AppCompatActivity(), PostDetailPresenter.View {

    private val navigator = AndroidNavigator(this)

    private lateinit var presenter: PostDetailPresenter
    private lateinit var latestViewModel: Lce<PostDetailViewModel, Throwable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.post_detail_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        presenter = PostDetailPresenter(
            this,
            PostDetailService(createBackend()),
            SchedulerPair(Schedulers.io(), AndroidSchedulers.mainThread())
        )

        val defaultPostData: PostData = getSerializableFromExtras(EXTRA_POST_DATA)

        latestViewModel = savedInstanceState.getLce(KEY_VIEW_MODEL_STATE)
            ?: Lce.Loading<PostDetailViewModel, Throwable>(PostDetailViewModel.WithPostDataOnly(defaultPostData))

        data_view.setOnRefreshListener { presenter.onRefresh() }
    }

    override fun onStart() {
        super.onStart()
        presenter.startPresenting(latestViewModel)
    }

    override fun show(postDetailViewModel: Lce<PostDetailViewModel, Throwable>) {
        showPartialContent(postDetailViewModel.content!!.postData)

        when (postDetailViewModel) {
            is Lce.Loading -> showLoading()
            is Lce.Error -> showError()
            is Lce.Content -> showRemainingContent(postDetailViewModel.content as PostDetailViewModel.WithEverything)
        }
    }

    private fun showPartialContent(postData: PostData) {
        post_title.text = postData.title
        post_body.text = postData.body
    }

    private fun showLoading() {
        data_view.isRefreshing = true
    }

    private fun showError() {
        data_view.isRefreshing = false
        Snackbar.make(main_view, R.string.error_description_with_data, LENGTH_LONG).apply {
            setAction(R.string.error_retry) { presenter.onRefresh() }
            show()
        }
    }

    private fun showRemainingContent(content: PostDetailViewModel.WithEverything) {
        data_view.isRefreshing = false

        post_author.text = resources.getHtml(R.string.author, content.userData.username)

        val numberOfComments = content.commentsData.numberOfComments
        post_comments.text = resources.getQuantityString(R.plurals.comments, numberOfComments, numberOfComments)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            navigator.close()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(KEY_VIEW_MODEL_STATE, presenter.stateToSave())
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        presenter.stopPresenting()
        super.onStop()
    }
}
