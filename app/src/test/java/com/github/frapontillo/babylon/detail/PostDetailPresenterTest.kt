package com.github.frapontillo.babylon.detail

import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.common.network.Backend
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class PostDetailPresenterTest {

    private val view: PostDetailPresenter.View = mock()
    private val backend: Backend = mock()
    private val postDetailService = PostDetailService(backend)
    private val schedulerPair = SchedulerPair(Schedulers.trampoline(), Schedulers.trampoline())
    private val initialPartialData =
        PostDetailViewModel.WithPostDataOnly(PostData(1, "The best article", "It is what it is", 5))

    private lateinit var postDetailPresenter: PostDetailPresenter

    @Before
    fun setUp() {
        postDetailPresenter = PostDetailPresenter(
            view,
            postDetailService,
            schedulerPair
        )

        whenever(backend.getAuthor(5)).thenReturn(Single.just(backendAuthor))
        whenever(backend.getPostComments(1)).thenReturn(Single.just(backendComments(3)))
    }

    @Test
    fun `show initial data, then loading, then full data`() {
        postDetailPresenter.startPresenting(Lce.Content(initialPartialData))

        verify(view).show(Lce.Content(initialPartialData))
        verify(view).show(Lce.Loading(initialPartialData))
        verify(view).show(
            Lce.Content(
                PostDetailViewModel.WithEverything(
                    initialPartialData.postData,
                    UserData("macgyver"),
                    CommentsData(3)
                )
            )
        )
    }

    @Test
    fun `show initial data, then loading, then error with initial data`() {
        val someException = Exception()
        whenever(backend.getAuthor(5)).thenReturn(Single.error(someException))

        postDetailPresenter.startPresenting(Lce.Content(initialPartialData))

        verify(view).show(Lce.Content(initialPartialData))
        verify(view).show(Lce.Loading(initialPartialData))
        verify(view).show(Lce.Error(someException, initialPartialData))
    }

    @Test
    fun `after refresh, show loading, then new data with more comments`() {
        postDetailPresenter.startPresenting(Lce.Content(initialPartialData))
        verify(view, times(3)).show(any())

        whenever(backend.getPostComments(1)).thenReturn(Single.just(backendComments(4)))
        postDetailPresenter.onRefresh()

        verify(view).show(Lce.Loading(initialPartialData))
        val expectedFullData = PostDetailViewModel.WithEverything(
            initialPartialData.postData,
            UserData("macgyver"),
            CommentsData(4)
        )
        verify(view).show(Lce.Content(expectedFullData))
    }

    @Test
    fun `after refresh, show loading, then error with previous data`() {
        postDetailPresenter.startPresenting(Lce.Content(initialPartialData))
        verify(view, times(3)).show(any())

        val someException = Exception()
        whenever(backend.getPostComments(1)).thenReturn(Single.error(someException))
        postDetailPresenter.onRefresh()

        val expectedFullData = PostDetailViewModel.WithEverything(
            initialPartialData.postData,
            UserData("macgyver"),
            CommentsData(3)
        )
        verify(view).show(Lce.Loading(expectedFullData))
        verify(view).show(Lce.Error(someException, content = expectedFullData))
    }

    @Test
    fun `get latest known data to save in cache`() {
        postDetailPresenter.startPresenting(Lce.Content(initialPartialData))

        val stateToSave = postDetailPresenter.stateToSave()
        val expectedStateToSave = Lce.Content<PostDetailViewModel, Throwable>(
            PostDetailViewModel.WithEverything(
                initialPartialData.postData,
                UserData("macgyver"),
                CommentsData(3)
            )
        )
        assertThat(stateToSave).isEqualTo(expectedStateToSave)
    }

    @Test
    fun `show cached data and do not hit the network`() {
        val initialFullData = Lce.Content<PostDetailViewModel, Throwable>(
            PostDetailViewModel.WithEverything(
                initialPartialData.postData,
                UserData("macgyver"),
                CommentsData(3)
            )
        )
        postDetailPresenter.startPresenting(initialFullData)

        verify(view).show(initialFullData)
        verify(backend, never()).getAuthor(5)
        verify(backend, never()).getPostComments(1)
    }
}
