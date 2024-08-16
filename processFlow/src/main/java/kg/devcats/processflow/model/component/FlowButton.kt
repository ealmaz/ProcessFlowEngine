package kg.devcats.processflow.model.component

import java.io.Serializable

data class FlowButton(
    val buttonId: String,
    val text: String? = null,
    val style: FlowButtonStyle? = null,
    val disabled: Boolean = false,
    val properties: Map<String, String>? = null,
) : Serializable

enum class ButtonProperties(val propertyName: String) {
    ENABLED("enabled"), ENABLE_AT("enableAt"), DATA("data"),
    SUB_PROCESS_FLOW_TYPE("subProcessFlowType"), PARENT_PROCESS_ID("parent_instance_key"),
}


enum class FlowButtonStyle {
    ACCENT, SECONDARY
}
