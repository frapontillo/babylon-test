package com.github.frapontillo.babylon.detail

import com.github.frapontillo.babylon.Lce
import com.github.frapontillo.babylon.SchedulerPair
import com.github.frapontillo.babylon.applySchedulerPair
import com.github.frapontillo.babylon.common.lastContentOrNull
import com.github.frapontillo.babylon.mapToLce
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class PostDetailPresenter(
    private val view: View,
    private val postDetailService: PostDetailService,
    private val schedulerPair: SchedulerPair
) {

    private val disposables = CompositeDisposable()

    private val refreshSubject = PublishSubject.create<Any>()
    private val dataSubject = BehaviorSubject.create<Lce<PostDetailViewModel, Throwable>>()

    fun startPresenting(latestViewModel: Lce<PostDetailViewModel, Throwable>) {
        dataSubject.onNext(latestViewModel)

        disposables += refreshSubject
            .map { dataSubject.value!!.content!!.postData }
            .flatMap { getAllPostDetails(it) }
            .subscribe(dataSubject::onNext)

        disposables += dataSubject
            .applySchedulerPair(schedulerPair)
            .subscribe { view.show(it) }

        if (latestViewModel.content!! is PostDetailViewModel.WithPostDataOnly) {
            onRefresh()
        }
    }

    private fun getAllPostDetails(postData: PostData): Observable<Lce<PostDetailViewModel, Throwable>> {
        return Singles
            .zip(
                postDetailService.getUsername(postData.authorId),
                postDetailService.getCommentsCount(postData.postId)
            ) { username: String, commentsCount: Int ->
                PostDetailViewModel.WithEverything(
                    postData,
                    UserData(username),
                    CommentsData(commentsCount)
                ) as PostDetailViewModel
            }
            .mapToLce(dataSubject.lastContentOrNull)
            .applySchedulerPair(schedulerPair)
    }

    fun onRefresh() {
        refreshSubject.onNext(Any())
    }

    fun stateToSave() = dataSubject.value

    fun stopPresenting() {
        disposables.clear()
    }

    interface View {

        fun show(postDetailViewModel: Lce<PostDetailViewModel, Throwable>)
    }
}
