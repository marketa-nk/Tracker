package com.mint.minttracker

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable

open class BaseViewModel: ViewModel()  {

    private val compositeDisposable = CompositeDisposable()

    protected fun Disposable.addDisposable(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    protected fun Disposable.addDisposable(serialDisposable: SerialDisposable): Disposable {
        compositeDisposable.add(this)
        serialDisposable.set(this)
        return this
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()

    }
}