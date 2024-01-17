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
    ENABLED("enabled")
}


enum class FlowButtonStyle {
    ACCENT, SECONDARY
}
