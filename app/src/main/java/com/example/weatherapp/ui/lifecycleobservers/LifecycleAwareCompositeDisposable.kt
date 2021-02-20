package com.example.weatherapp.ui.lifecycleobservers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LifecycleAwareCompositeDisposable(
    private val lifecycle: Lifecycle
) : LifecycleObserver {
    private val subscriptions = CompositeDisposable()

    fun subscribe(disposable: Disposable) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            subscriptions.add(disposable)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearSubscriptions() {
        subscriptions.clear()
    }
}
