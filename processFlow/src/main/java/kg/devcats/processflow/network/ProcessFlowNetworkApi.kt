package kg.devcats.processflow.network

import io.reactivex.Single
import kg.devcats.processflow.model.request.FlowCancelRequest
import kg.devcats.processflow.model.request.FlowCommitRequest
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.model.request.FlowStatusResponse
import okhttp3.MultipartBody

interface ProcessFlowNetworkApi {

    fun getFlowStatus(): Single<FlowStatusResponse>

    fun startFlow(request: Any): Single<FlowResponse>

    fun getState(processId: String? = null): Single<FlowResponse>

    fun commit(request: FlowCommitRequest): Single<FlowResponse>

    fun uploadAttachment(
        process_id: MultipartBody.Part? = null,
        file: MultipartBody.Part? = null,
    ): Single<String>

    fun fetchAdditionalOptions(
        formId: String,
        parentSelectedOptionId: String,
    ): Single<List<Any>> //todo: Change option

    fun cancelFlow(request: FlowCancelRequest): Single<Boolean>

}