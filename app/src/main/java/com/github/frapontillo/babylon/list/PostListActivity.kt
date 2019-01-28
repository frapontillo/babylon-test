package com.github.frapontillo.babylon.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.frapontillo.babylon.AndroidNavigator
import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.R
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.common.createBackend
import com.github.frapontillo.babylon.common.getLce
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.error_view.*
import kotlinx.android.synthetic.main.post_list_activity.*

private const val KEY_VIEW_MODEL_STATE = "KEY_VIEW_MODEL_STATE"

class PostListActivity : AppCompatActivity(), PostListPresenter.View {

    private lateinit var presenter: PostListPresenter
    private lateinit var postListAdapter: PostListAdapter

    private var latestViewModel: Lce<PostListViewModel, Throwable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.post_list_activity)

        latestViewModel = savedInstanceState.getLce(KEY_VIEW_MODEL_STATE)

        presenter = PostListPresenter(
            this,
            PostListService(createBackend()),
            AndroidNavigator(this),
            SchedulerPair(Schedulers.io(), AndroidSchedulers.mainThread())
        )

        postListAdapter = PostListAdapter(layoutInflater, presenter::onPostSelected)

        post_list.apply {
            layoutManager = LinearLayoutManager(this@PostListActivity)
            adapter = postListAdapter
        }

        data_view.setOnRefreshListener {
            presenter.onRefresh()
        }

        error_retry.setOnClickListener { presenter.onRefresh() }
    }

    override fun onStart() {
        super.onStart()
        presenter.startPresenting(latestViewModel)
    }

    override fun show(postListViewModel: Lce<PostListViewModel, Throwable>) {
        when (postListViewModel) {
            is Lce.Loading -> showLoading()
            is Lce.Content -> showContent(postListViewModel)
            is Lce.Error -> showError(postListViewModel)
        }
    }

    private fun showLoading() {
        data_view.isRefreshing = true
        data_view.isVisible = true
    }

    private fun showContent(postListViewModel: Lce.Content<PostListViewModel, Throwable>) {
        data_view.isRefreshing = false
        data_view.isVisible = true
        error_view.isVisible = false

        postListAdapter.submitList(postListViewModel.content.postViewModels)
    }

    private fun showError(error: Lce.Error<PostListViewModel, Throwable>) {
        if (error.hasContent()) {
            showErrorWithContent(error)
        } else {
            showErrorOnly()
        }
    }

    private fun showErrorWithContent(error: Lce.Error<PostListViewModel, Throwable>) {
        showContent(Lce.Content(error.content!!))
        Snackbar.make(main_view, R.string.error_description_with_data, LENGTH_LONG).apply {
            setAction(R.string.error_retry) {
                presenter.onRefresh()
            }
            show()
        }
    }

    private fun showErrorOnly() {
        data_view.isVisible = false
        error_view.isVisible = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        latestViewModel = presenter.stateToSave()
        outState?.putSerializable(KEY_VIEW_MODEL_STATE, latestViewModel)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        presenter.stopPresenting()
        super.onStop()
    }
}
