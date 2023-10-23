package kg.devcats.processflow.network.parser

import androidx.annotation.WorkerThread
import com.google.gson.Gson
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.model.component.FlowFormInfoJson
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.component.FlowResponseType
import kg.devcats.processflow.model.component.FlowRetryInfo
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.model.request.FlowAllowedAnswer

open class ProcessFlowResponseParser {

    protected open val inputFormMapper: InputFormMapper = InputFormMapper()

    private val gson = Gson()

    @WorkerThread
    open fun parseButtons(response: List<FlowAllowedAnswer>?): List<FlowButton>? {
        return response?.filter { it.responseType == FlowResponseType.BUTTON }?.mapNotNull {
            gson.fromJson(it.responseItem, FlowButton::class.java)
        }
    }

    @WorkerThread
    open fun parseInputForms(response: List<FlowAllowedAnswer>?): List<InputForm>? {
        return response?.filter { it.responseType == FlowResponseType.INPUT_FORM }?.mapNotNull {
            gson.fromJson(it.responseItem, FlowFormInfoJson::class.java)
        }?.map { inputFormMapper.map(it) }
    }

    @WorkerThread
    open fun parseRetry(response: List<FlowAllowedAnswer>?): FlowRetryInfo? {
        return response?.firstOrNull { it.responseType == FlowResponseType.RETRY }?.let {
            if (it.responseItem.isJsonNull) FlowRetryInfo("RETRY", null)
            else gson.fromJson(it.responseItem, FlowRetryInfo::class.java)
        }
    }

    @WorkerThread
    open fun parseInputField(response: List<FlowAllowedAnswer>?): FlowInputField? {
        return response?.firstOrNull { it.responseType == FlowResponseType.INPUT_FIELD }?.let {
            gson.fromJson(it.responseItem, FlowInputField::class.java)
        }
    }

    @WorkerThread
    open fun parseWebView(response: List<FlowAllowedAnswer>?): List<FlowWebView>? {
        return response?.filter { it.responseType == FlowResponseType.WEB_VIEW }?.mapNotNull {
            gson.fromJson(it.responseItem, FlowWebView::class.java)
        }
    }
}