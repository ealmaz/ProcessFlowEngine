package kg.devcats.processflow.model.request

import com.google.gson.JsonElement
import kg.devcats.processflow.model.component.FlowResponseType

data class FlowAllowedAnswer(
    val responseType: FlowResponseType,
    val responseItem: JsonElement
)

