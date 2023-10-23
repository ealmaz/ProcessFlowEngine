package kg.devcats.processflow.model

import kg.devcats.processflow.model.common.ScreenState
import kg.devcats.processflow.model.component.FlowMessage

data class ProcessFlowScreenData(
    val screenKey: String? = null,
    val state: ScreenState? = null,
    val allowedAnswer: List<Any?>? = null,
    val message: List<FlowMessage?>? = null
)