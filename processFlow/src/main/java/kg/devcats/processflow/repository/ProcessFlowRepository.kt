package kg.devcats.processflow.repository

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kg.devcats.processflow.ProcessFlowPreferences
import kg.devcats.processflow.model.common.FlowStatusHelper
import kg.devcats.processflow.model.input_form.Option
import kg.devcats.processflow.model.request.FlowAnswer
import kg.devcats.processflow.model.request.FlowCancelRequest
import kg.devcats.processflow.model.request.FlowCommitRequest
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.network.ProcessFlowNetworkApi
import kg.devcats.processflow.util.PictureUtil
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

abstract class ProcessFlowRepository constructor(
    private val _api: ProcessFlowNetworkApi,
    private val _prefs: ProcessFlowPreferences,
) {

    fun getFlowStatus(processType: String): Single<String?> =
        _api
            .getFlowStatus(processType)
            .map {
                _prefs.processId = it.processId
                _prefs.flowStatus = it.flowStatus?.toString()
                it.processId
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun startProcessFlow(request: Map<String, String>): Single<FlowResponse> =
        _api
            .startFlow(request)
            .map { flow ->
                flow.processId?.let { _prefs.processId = it }
                _prefs.flowStatus = flow.flowStatus?.toString()
                flow
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getProcessFlowState(): Single<FlowResponse> =
        _api
            .getState(_prefs.processId)
            .map { flow ->
                flow.processId?.let { _prefs.processId = it }
                _prefs.flowStatus = flow.flowStatus?.toString()
                flow
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun commit(answer: FlowAnswer): Single<FlowResponse> =
        _api
            .commit(FlowCommitRequest(_prefs.processId, answer))
            .map { flow ->
                flow.processId?.let { _prefs.processId = it }
                _prefs.flowStatus = flow.flowStatus?.toString()
                flow
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun cancelProcessFlow(): Single<Boolean> {
        return _api
            .cancelFlow(FlowCancelRequest(_prefs.processId ?: ""))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadAttachment(file: File): Single<String> {
        val requestBody = file.asRequestBody(MultipartBody.FORM)
        val body = MultipartBody.Part.createFormData("file", file.absolutePath, requestBody)
        val applicationId = MultipartBody.Part.createFormData("process_id", _prefs.processId ?: "")
        return _api.uploadAttachment(applicationId, body)
            .flatMap { deleteFile(file, it) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchOptions(formId: String, parentSelectedId: String = ""): Single<List<Option>> {
        return _api.fetchAdditionalOptions(formId, parentSelectedId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun deleteFile(file: File, resultString: String): Single<String> {
        return Single.create {
            PictureUtil.deleteFile(file)
            it.onSuccess(resultString)
        }
    }

    fun isProcessTerminated(): Boolean = FlowStatusHelper.isProcessTerminated(_prefs.flowStatus)
}