package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class LabelFormItem(
    val fieldId: String? = null,
    val label: String? = null,
    val properties: Map<String, String>? = null
): Serializable

enum class LabelProperties(val propertyName: String) {
    DESCRIPTION("description"), HAS_ENABLED("hasBackground")
}