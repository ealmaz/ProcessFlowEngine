package kg.devcats.processflow.ui.main

import androidx.lifecycle.MutableLiveData
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseVM
import kg.devcats.processflow.extension.defaultSubscribe
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.request.FlowAnswer
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.network.parser.ProcessFlowResponseParser
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.util.PictureUtil
import kg.nurtelecom.text_recognizer.RecognizedMrz
import java.io.File

abstract class ProcessFlowVM<T: ProcessFlowRepository>(protected val _repository: T) : BaseVM() {

    private var failGetStateCounts = 0

    val loaderState = MutableLiveData(false)

    private fun showLoading(){ loaderState.postValue(true) }
    private fun hideLoading(){ loaderState.postValue(false) }

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

    fun getFlowStatus(processType: String) = disposed {
        _repository
            .getFlowStatus(processType)
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .subscribe({
                triggerEvent(Event.ProcessFlowIsExist(it != null))
            }, {
                triggerEvent(Event.ProcessFlowIsExist(false))
            })
    }

    fun startProcessFlow(startFlowRequest: Map<String, String>) = disposed {
        _repository
            .startProcessFlow(startFlowRequest)
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
        responseId: String,
        file: File,
        type: String,
        recognizedMrz: RecognizedMrz? = null,
        onSuccess: () -> Unit,
        onFail: (warningMessage: String, finishOnFail: Boolean) -> Unit
    ) {
        disposed {
            _repository.uploadAttachment(file)
                .observeOn(Schedulers.io())
                .flatMap {
                    _repository.commit(
                        FlowAnswer(
                            responseId,
                            mutableListOf(
                                Content(it, type),
                                Content(recognizedMrz ?: "", ContentTypes.RECOGNIZED_PASSPORT_DATA)
                            )
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    onSuccess.invoke()
                    it
                }
                .flatMap { dispatchValuesToLiveData(it) }
                .doOnSubscribe { showLoading() }
                .doOnTerminate { hideLoading() }
                .defaultSubscribe(onError = {
                    onFail.invoke("", true)
                })
        }
    }

    private fun compressFile(file: File): Single<File?> {
        return Single.create<File?> {
            val compressedFile = PictureUtil.compressImage(file, COMPRESSION_QUALITY)
            if (compressedFile != null) {
                it.onSuccess(compressedFile)
            } else {
                it.onError(NullPointerException())
            }
        }
    }

    protected open fun handleOnGetStateFailure(it: Throwable) {
        if (failGetStateCounts > 5) {
            failGetStateCounts = 0
            val message = (it)?.message
            val event = if (message.isNullOrBlank()) Event.NotificationResId(R.string.process_flow_unexpected_error)
            else Event.Notification(message)
            triggerEvent(event)
        } else {
            failGetStateCounts++
            handleError(it)
        }
    }

    protected open fun handleError(ex: Throwable) {
        getState()
    }

    protected open fun dispatchValuesToLiveData(response: FlowResponse): Single<FlowResponse> {
        return Single.fromCallable {

            val allowedAnswers = mutableListOf<Any>()

            _flowResponseParser.parseButtons(response.allowedAnswer)?.let { allowedAnswers.addAll(it) }
            _flowResponseParser.parseInputField(response.allowedAnswer)?.let { allowedAnswers.add(it) }
            _flowResponseParser.parseRetry(response.allowedAnswer)?.let { allowedAnswers.add(it) }
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

    fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        disposable.add(_repository.fetchOptions(formId, parentSelectedOptionId)
            .doOnSubscribe { triggerEvent(Event.AdditionalOptionsFetching) }
            .subscribe({
                val options = it.map { Option(it.id, it.label ?: "", it.isSelected ?: false) }
                triggerEvent(Event.AdditionalOptionsFetched(formId, options))
            }, {}))
    }

    fun isProcessTerminated() : Boolean {
        return _repository.isProcessTerminated()
    }

    companion object {
        const val COMPRESSION_QUALITY = 80
    }


}