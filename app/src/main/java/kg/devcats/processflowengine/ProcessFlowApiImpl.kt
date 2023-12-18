package kg.devcats.processflowengine

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import kg.devcats.processflow.model.input_form.Option
import kg.devcats.processflow.model.request.FlowCancelRequest
import kg.devcats.processflow.model.request.FlowCommitRequest
import kg.devcats.processflow.model.request.FlowResponse
import kg.devcats.processflow.model.request.FlowStatusResponse
import kg.devcats.processflow.network.ProcessFlowNetworkApi
import java.util.concurrent.TimeUnit

object ProcessFlowApiImpl : ProcessFlowNetworkApi {

    var FIRST_STEP_KEY = "start"
    private const val REQUESTS_DELAY = 300L

    override fun startFlow(request: Map<String, Any>): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "startFlow: request: $request")
        return Single.just(
            Gson().fromJson(ProcessMocker.mock.get(FIRST_STEP_KEY), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun findActiveProcess(): Single<FlowResponse?> {
        Log.d("SAMPLE_TESTER", "getFlowStatus: Response null")
        return Single.error(java.lang.NullPointerException())
    }

    override fun getState(processId: String?): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "getState")
        return Single.just(
            Gson().fromJson(ProcessMocker.mock.get("retry1"), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun commit(request: FlowCommitRequest): Single<FlowResponse> {
        Log.d("SAMPLE_TESTER", "commit: $request")
        return Single.just(
            Gson().fromJson(ProcessMocker.mock.get(request.answer.responseItemId), FlowResponse::class.java)
        ).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun uploadAttachment(
        process_id: okhttp3.MultipartBody.Part?,
        file: okhttp3.MultipartBody.Part?
    ): io.reactivex.Single<String> {
        Log.d("SAMPLE_TESTER", "upload")
        return Single.just("attachment").delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun fetchAdditionalOptions(
        formId: String,
        parentSelectedOptionId: String
    ): io.reactivex.Single<List<Option>> {
        Log.d("SAMPLE_TESTER", "fetchAdditionalOptions")
        return Single.just(listOf(
            Option("1", "Бишкек"),
            Option("2", "Токмок"),
            Option("3", "Ош"),
            Option("4", "Талас"),
            Option("5", "Нарын"),
        )).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

    override fun cancelFlow(request: FlowCancelRequest): io.reactivex.Single<Boolean> {
        Log.d("SAMPLE_TESTER", "cancelFlow")
        return Single.just(true).delay(REQUESTS_DELAY, TimeUnit.MILLISECONDS)
    }

}