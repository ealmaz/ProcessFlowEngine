package kg.devcats.processflow.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kg.devcats.processflow.model.Event

open class BaseVM : ViewModel() {

    val disposable: CompositeDisposable by lazy {
        return@lazy CompositeDisposable()
    }

    var event: MutableLiveData<Event> = MutableLiveData()

    fun triggerEvent(event: Event) {
        this.event.value = event
    }

    fun disposed(d: () -> Disposable) {
        disposable.add(d.invoke())
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}