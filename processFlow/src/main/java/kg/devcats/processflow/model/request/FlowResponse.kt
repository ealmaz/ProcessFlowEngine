package kg.devcats.processflow.model.request

import com.google.gson.annotations.SerializedName
import kg.devcats.processflow.model.component.FlowMessage
import kg.devcats.processflow.model.common.FlowStatus
import kg.devcats.processflow.model.common.ScreenState

data class FlowResponse(
    @SerializedName("process_id")
    val processId: String? = null,
    @SerializedName("process_status")
    val flowStatus: FlowStatus? = null,
    @SerializedName("screen_code")
    val screenKey: String,
    @SerializedName("screen_state")
    val screenState: ScreenState? = null,
    @SerializedName("messages")
    val messages: List<FlowMessage>? = null,
    @SerializedName("allowed_answers")
    val allowedAnswer: List<FlowAllowedAnswer>? = null,
)