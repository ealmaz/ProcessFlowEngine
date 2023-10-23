package kg.devcats.processflow.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.request.FlowAnswer
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.repository.CreditProcessFlowRepository
import kg.devcats.processflow.network.parser.ProcessFlowResponseParser
import java.io.File

class ProcessFlowVM(private val _repository: CreditProcessFlowRepository): ViewModel() {

    val disposable: CompositeDisposable by lazy {
        return@lazy CompositeDisposable()
    }

    var event: MutableLiveData<Event> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun triggerEvent(ev: Event?) {
        event.value = ev
    }


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun disposed(d: () -> Disposable) {
        disposable.add(d.invoke())
    }


    var resultData: Pair<String, MutableList<Content>>? = null
    var isProcessCreated = false

    private var failGetStateCounts = 0

    private fun showLoading(){}
    private fun hideLoading(){}

    private val _flowResponseParser: ProcessFlowResponseParser by lazy {
        ProcessFlowResponseParser()
    }

    val processFlowScreenDataLive = MutableLiveData<ProcessFlowScreenData>()

    fun commit(responseId: String, additionalContents: List<Content>? = null) = disposed {
        _repository
            .commit(FlowAnswer(responseId, additionalContents))
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun getFlowStatus() = disposed {
        _repository
            .getFlowStatus()
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .subscribe({
                isProcessCreated = it != null
//                triggerEvent(Event.ProcessFlowIsExist(it != null))
            }, {
                isProcessCreated = false
//                triggerEvent(Event.ProcessFlowIsExist(false))
            })
    }

    fun startProcessFlow(startFlowRequest: Any) = disposed {
        isProcessCreated = true
        _repository
            .startProcessFlow(startFlowRequest, "")
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun getState(showLoader: Boolean = true) = disposed {
        _repository
            .getProcessFlowState()
            .doOnSubscribe { if (showLoader) showLoading() }
            .doOnTerminate { if (showLoader) hideLoading() }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(
                onSuccess = { failGetStateCounts = 0 },
                onError = ::handleOnGetStateFailure
            )
    }


    fun cancelProcessFlow() {
        _repository
            .cancelProcessFlow()
            .defaultSubscribe()
    }


    fun upload(
        file: File,
        type: String,
        onSuccess: () -> Unit,
        onFail: (warningMessage: String, finishOnFail: Boolean) -> Unit
    ) {
        disposed {
            _repository.uploadAttachment(file)
                .observeOn(Schedulers.io())
                .flatMap {
                    _repository.commit(FlowAnswer(resultData?.first!!, mutableListOf(Content(it, type))))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    resultData = null
                    onSuccess.invoke()
                    it
                }
                .flatMap { dispatchValuesToLiveData(it) }
                .defaultSubscribe(onError = {
                    onFail.invoke("", true)
                })
        }
    }

    fun uploadRecognizedPhoto(
        file: File,
        recognizedMrz: Any?, //todo : recognized mrz
        onSuccess: () -> Unit,
        onFail: (warningMessage: String, finishOnFail: Boolean) -> Unit
    ) {
        disposed {
            compressFile(file)
                .subscribeOn(Schedulers.io())
                .flatMap { _repository.uploadAttachment(file) }
                .observeOn(Schedulers.io())
                .flatMap {
                    _repository.commit(
                        FlowAnswer(
                            resultData?.first!!,
                            mutableListOf(
                                Content(it, ContentTypes.PASSPORT_BACK_PHOTO),
                                Content(recognizedMrz ?: "", ContentTypes.RECOGNIZED_PASSPORT_DATA)
                            )
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    resultData = null
                    onSuccess.invoke()
                    it
                }
                .flatMap { dispatchValuesToLiveData(it) }
                .defaultSubscribe(onError = {
                    onFail.invoke("", true)
                })
        }
    }

    private fun compressFile(file: File): Single<File?> {
        return Single.create<File?> {
            //todo: compress
//            val compressedFile = PictureUtils.compressImage(file, COMPRESSION_QUALITY)
//            if (compressedFile != null) {
//                it.onSuccess(compressedFile)
//            } else {
//                it.onError(NullPointerException())
//            }
        }
    }

    private fun handleOnGetStateFailure(it: Throwable) {
//        if (failGetStateCounts > 5) {
//            failGetStateCounts = 0
//            val message = (it)?.message
//            val event = if (message.isNullOrBlank()) Event.NotificationResId(R.string.unexpected_error)
//            else Event.Notification(message)
//            triggerEvent(event)
//        } else {
//            failGetStateCounts++
//            handleError(it)
//        }
    }

    private fun handleError(ex: Throwable) {
        getState()
    }

    private fun dispatchValuesToLiveData(response: FlowResponse): Single<FlowResponse> {
        return Single.fromCallable {

            val allowedAnswers = mutableListOf<Any>()

            _flowResponseParser.parseButtons(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }
//            _flowResponseParser.parseInputField(response.allowedAnswer)?.let { allowedAnswers.add(it) }
//            _flowResponseParser.parseRetry(response.allowedAnswer)?.let { allowedAnswers.add(it) }
            _flowResponseParser.parseWebView(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }
            _flowResponseParser.parseInputForms(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }

            val screenData = ProcessFlowScreenData(
                screenKey = response.screenKey,
                state = response.screenState,
                allowedAnswer = allowedAnswers,
                message = response.messages
            )
            processFlowScreenDataLive.postValue(screenData)
            response
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun isProcessTerminated() : Boolean {
        return _repository.isProcessTerminated()
    }

    companion object {
        const val COMPRESSION_QUALITY = 80
    }


}

fun <R> Single<R>.defaultSubscribe(
    onSuccess: (R) -> Unit = {},
    onError: (Throwable) -> Unit = {}
): Disposable {
    return this.subscribe(onSuccess, onError)
}