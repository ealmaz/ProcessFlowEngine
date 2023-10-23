package kg.devcats.processflow.model.request

import kg.devcats.processflow.model.common.Content

data class FlowAnswer(
    val responseItemId: String,
    val additionalContents: List<Content>? = null,
)