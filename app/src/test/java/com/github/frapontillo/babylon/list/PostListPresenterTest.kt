package com.github.frapontillo.babylon.list

import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.Navigator
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.common.network.Backend
import com.github.frapontillo.babylon.common.network.Post
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

class PostListPresenterTest {

    private val view: PostListPresenter.View = mock()
    private val backend: Backend = mock()
    private val postListService = PostListService(backend)
    private val navigator: Navigator = mock()
    private val schedulers = SchedulerPair(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var postListPresenter: PostListPresenter

    @Before
    fun setUp() {
        postListPresenter = PostListPresenter(
            view,
            postListService,
            navigator,
            schedulers
        )

        whenever(backend.getPosts()).thenReturn(Single.just(backendPosts))
    }

    @Test
    fun `show loading, then data`() {
        postListPresenter.startPresenting()

        verify(view).show(Lce.Loading())
        verify(view).show(
            Lce.Content(
                PostListViewModel(
                    listOf(
                        PostViewModel(2, "Title 2", "Body 2", 1),
                        PostViewModel(1, "Title 1", "Body 1", 1)
                    )
                )
            )
        )
    }

    @Test
    fun `show loading, then error with no data`() {
        val someException = Exception()
        whenever(backend.getPosts()).thenReturn(Single.error(someException))

        postListPresenter.startPresenting()

        verify(view).show(Lce.Loading())
        verify(view).show(Lce.Error(someException, content = null))
    }

    @Test
    fun `after refresh, show new data`() {
        postListPresenter.startPresenting()
        verify(view, times(2)).show(any())
        whenever(backend.getPosts()).thenReturn(
            Single.just(listOf(Post(4, "Fresh data", "A new article", 1)))
        )

        postListPresenter.onRefresh()

        verify(view).show(Lce.Loading())
        verify(view).show(
            Lce.Content(
                PostListViewModel(
                    listOf(PostViewModel(4, "Fresh data", "A new article", 1))
                )
            )
        )
    }

    @Test
    fun `after refresh, show loading, then error with previous data`() {
        val data = PostListViewModel(
            listOf(
                PostViewModel(2, "Title 2", "Body 2", 1),
                PostViewModel(1, "Title 1", "Body 1", 1)
            )
        )
        postListPresenter.startPresenting()
        verify(view).show(Lce.Loading())
        verify(view).show(Lce.Content(data))

        val someException = Exception()
        whenever(backend.getPosts()).thenReturn(Single.error(someException))
        postListPresenter.onRefresh()

        verify(view).show(Lce.Loading())
        verify(view).show(Lce.Error(someException, content = data))
    }

    @Test
    fun `get latest known data to save in cache`() {
        postListPresenter.startPresenting()

        val expectedStateToSave = Lce.Content<PostListViewModel, Throwable>(
            PostListViewModel(
                listOf(
                    PostViewModel(2, "Title 2", "Body 2", 1),
                    PostViewModel(1, "Title 1", "Body 1", 1)
                )
            )
        )
        assertThat(postListPresenter.stateToSave()).isEqualTo(expectedStateToSave)
    }

    @Test
    fun `show cached data and do not hit the network`() {
        val cachedData = PostListViewModel(
            listOf(PostViewModel(2, "A title", "An article body", 1))
        )

        postListPresenter.startPresenting(Lce.Content(cachedData))

        verify(view).show(Lce.Content(cachedData))
        verify(backend, never()).getPosts()
    }

    @Test
    fun `go to post detail`() {
        postListPresenter.onPostSelected(PostViewModel(2, "Title 2", "Body 2", 1))

        verify(navigator).goToPostDetail(2, "Title 2", "Body 2", 1)
    }
}
