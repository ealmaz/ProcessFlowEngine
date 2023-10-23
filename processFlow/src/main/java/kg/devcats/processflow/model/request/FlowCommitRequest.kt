package kg.devcats.processflow.model.request

import com.google.gson.annotations.SerializedName

data class FlowCommitRequest(
    @SerializedName("process_id") val processId: String? = null,
    @SerializedName("answer") val answer: FlowAnswer,
)

