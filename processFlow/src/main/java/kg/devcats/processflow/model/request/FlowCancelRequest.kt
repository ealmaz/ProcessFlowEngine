package kg.devcats.processflow.model.request

import com.google.gson.annotations.SerializedName

data class FlowCancelRequest(
    @SerializedName("process_id")
    val processId: String
)
