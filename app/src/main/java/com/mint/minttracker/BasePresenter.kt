package com.mint.minttracker

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T : MvpView> : MvpPresenter<T>() {

    private val compositeDisposable = CompositeDisposable()

    protected fun Disposable.addDisposable(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}