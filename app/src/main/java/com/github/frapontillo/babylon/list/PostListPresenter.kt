package com.github.frapontillo.babylon.list

import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.Navigator
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.applySchedulerPair
import com.github.frapontillo.babylon.common.lastContentOrNull
import com.github.frapontillo.babylon.mapToLce
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PostListPresenter(
    private val view: View,
    private val postListService: PostListService,
    private val navigator: Navigator,
    private val schedulerPair: SchedulerPair
) {

    private val disposables = CompositeDisposable()

    private val refreshSubject = PublishSubject.create<Any>()
    private val dataSubject = BehaviorSubject.create<Lce<PostListViewModel, Throwable>>()

    fun startPresenting(latestViewModel: Lce<PostListViewModel, Throwable>? = null) {
        disposables += refreshSubject
            .flatMap {
                postListService
                    .getPosts()
                    .mapToLce(dataSubject.lastContentOrNull)
                    .applySchedulerPair(schedulerPair)
            }
            .subscribe(dataSubject::onNext)

        disposables += dataSubject
            .distinctUntilChanged()
            .subscribe { view.show(it) }

        if (latestViewModel == null) {
            onRefresh()
        } else {
            dataSubject.onNext(latestViewModel)
        }
    }

    fun onRefresh() = refreshSubject.onNext(Any())

    fun onPostSelected(postViewModel: PostViewModel) = navigator.goToPostDetail(
        postViewModel.postId,
        postViewModel.title,
        postViewModel.body,
        postViewModel.authorId
    )

    fun stateToSave(): Lce<PostListViewModel, Throwable>? = dataSubject.value

    fun stopPresenting() = disposables.clear()

    interface View {

        fun show(postListViewModel: Lce<PostListViewModel, Throwable>)
    }
}

typealias OnPostSelected = (PostViewModel) -> Unit
