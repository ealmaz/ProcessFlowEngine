package kg.devcats.processflow.ui.main

import androidx.lifecycle.MutableLiveData
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseVM
import kg.devcats.processflow.extension.defaultSubscribe
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.common.FlowStatus
import kg.devcats.processflow.model.request.FlowAnswer
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.network.parser.ProcessFlowResponseParser
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.util.PictureUtil
import kg.nurtelecom.text_recognizer.RecognizedMrz
import java.io.File

abstract class ProcessFlowVM<T: ProcessFlowRepository>(protected val _repository: T) : BaseVM() {

    protected var failGetStateCounts = 0

    val loaderState = MutableLiveData(false)

    protected var processFlowId: String? = null
    protected var processFlowStatus: FlowStatus? = null

    protected fun showLoading(){ loaderState.postValue(true) }
    protected fun hideLoading(){ loaderState.postValue(false) }

    protected val _flowResponseParser: ProcessFlowResponseParser by lazy {
        ProcessFlowResponseParser()
    }

    val processFlowScreenDataLive = MutableLiveData<ProcessFlowScreenData>()

    fun requireProcessFlowId(): String = processFlowId ?: throw Exception("Process flow id is null")

    fun updateProcessInfo(processFlow: FlowResponse): FlowResponse {
        processFlowId = processFlow.processId
        processFlowStatus = processFlow.flowStatus
        return processFlow
    }

    fun restoreActiveFlow(possibleProcessTypes: List<String>) = disposed {
        _repository
            .findActiveProcess(possibleProcessTypes)
            .map { updateProcessInfo(it) }
            .map {
                if (it.processType in possibleProcessTypes) it
                else throw Exception("Process flow not exist")
            }
            .flatMap { dispatchValuesToLiveData(it) }
            .subscribe(
                { Event.ProcessFlowIsExist(it != null) },
                { triggerEvent(Event.ProcessFlowIsExist(false)) }
            )
    }

    fun commit(responseId: String, additionalContents: List<Content>? = null) = disposed {
        _repository
            .commit(requireProcessFlowId(), FlowAnswer(responseId, additionalContents))
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun startProcessFlow(startFlowRequest: Map<String, Any>) = disposed {
        _repository
            .startProcessFlow(startFlowRequest)
            .doOnSubscribe { showLoading() }
            .doOnTerminate { hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(onError = ::handleError)
    }

    fun getState(showLoader: Boolean = true) = disposed {
        _repository
            .getProcessFlowState(requireProcessFlowId())
            .doOnSubscribe { if (showLoader) showLoading() }
            .doOnTerminate { if (showLoader) hideLoading() }
            .map { updateProcessInfo(it) }
            .flatMap { dispatchValuesToLiveData(it) }
            .defaultSubscribe(
                onSuccess = { failGetStateCounts = 0 },
                onError = ::handleOnGetStateFailure
            )
    }


    fun cancelProcessFlow() {
        if (processFlowId == null) {
            triggerEvent(Event.FlowCancelledCloseActivity)
            return
        }
        _repository
            .cancelProcessFlow(requireProcessFlowId())
            .defaultSubscribe(
                onSuccess = { triggerEvent(Event.FlowCancelledCloseActivity) },
                onError = { triggerEvent(Event.FlowCancelledCloseActivity) }
            )
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
            compressIfTooLarge(file)
                .flatMap { _repository.uploadAttachment(requireProcessFlowId(), file) }
                .observeOn(Schedulers.io())
                .flatMap {
                    val additionalData = mutableListOf<Content>()
                    additionalData.add(Content(it, type))
                    if (type == ContentTypes.PASSPORT_BACK_PHOTO) additionalData.add(Content(recognizedMrz ?: getDefaultMrz(), ContentTypes.RECOGNIZED_PASSPORT_DATA))
                    _repository.commit(
                        requireProcessFlowId(),
                        FlowAnswer(
                            responseId,
                            additionalData
                        )
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    onSuccess.invoke()
                    it
                }
                .map { updateProcessInfo(it) }
                .flatMap { dispatchValuesToLiveData(it) }
                .doOnSubscribe { showLoading() }
                .doOnTerminate { hideLoading() }
                .defaultSubscribe(onError = {
                    handleError(it)
                })
        }
    }

    private fun compressIfTooLarge(file: File): Single<File?> {
        return if ((file.length() / 1024) <= MAX_AVAILABLE_FILE_SIZE) Single.just(file)
        else compressFile(file, SECONDARY_COMPRESSION_QUALITY)
    }

    protected fun compressFile(file: File): Single<File?> {
        return Single.create<File?> {
            val compressedFile = PictureUtil.compressImage(file, COMPRESSION_QUALITY)
            if (compressedFile != null) {
                it.onSuccess(compressedFile)
            } else {
                it.onError(NullPointerException())
            }
        }
    }

    protected fun compressFile(file: File, quality: Int = COMPRESSION_QUALITY): Single<File?> {
        return Single.create<File?> {
            val compressedFile = PictureUtil.compressImage(file, quality)
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
        if (processFlowId != null) getState()
        else {
            val message = ex.message
            val event = if (message.isNullOrBlank()) Event.NotificationResId(R.string.process_flow_unexpected_error)
            else Event.Notification(message)
            triggerEvent(event)
        }
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
            .subscribe({
                triggerEvent(Event.AdditionalOptionsFetched(formId, it))
            }, {}))
    }

    fun isProcessTerminated() : Boolean {
        return processFlowStatus in listOf(FlowStatus.TERMINATED, FlowStatus.COMPLETED)
    }

    private fun getDefaultMrz(): RecognizedMrz {
        return RecognizedMrz(null, null,null,null,null,null,null,null,null,null,null,null)
    }

    companion object {
        const val COMPRESSION_QUALITY = 80
        const val SECONDARY_COMPRESSION_QUALITY = 50
        const val MAX_AVAILABLE_FILE_SIZE = 1024
    }
}