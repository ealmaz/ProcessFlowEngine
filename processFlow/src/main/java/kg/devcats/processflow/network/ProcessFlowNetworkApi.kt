package kg.devcats.processflow.network

import io.reactivex.Single
import kg.devcats.processflow.model.input_form.Option
import kg.devcats.processflow.model.request.FlowCancelRequest
import kg.devcats.processflow.model.request.FlowCommitRequest
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.model.request.FlowStatusResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ProcessFlowNetworkApi {

    @GET("v2/process/info/find-running-primary")
    fun findActiveProcess(): Single<FlowResponse?>

    @POST("v2/process/start")
    fun startFlow(@Body request: Map<String, String>): Single<FlowResponse>

    @GET("v2/process/state")
    fun getState(@Query("process_id") processId: String? = null): Single<FlowResponse>

    @POST("v2/process/commit")
    fun commit(@Body request: FlowCommitRequest): Single<FlowResponse>

    @Multipart
    @POST("v2/attachments/upload")
    fun uploadAttachment(
        @Part process_id: MultipartBody.Part? = null,
        @Part file: MultipartBody.Part? = null,
    ): Single<String>

    @GET("v2/dictionaries/form-item/options/{form_item_id}/{parent_selected_option_id}")
    fun fetchAdditionalOptions(
        @Path("form_item_id") formId: String,
        @Path("parent_selected_option_id") parentSelectedOptionId: String,
    ): Single<List<Option>>

    @POST("v2/process/cancel")
    fun cancelFlow(@Body request: FlowCancelRequest): Single<Boolean>

}