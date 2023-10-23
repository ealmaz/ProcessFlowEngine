package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class LabelFormItem(
    val fieldId: String? = null,
    val label: String? = null,
): Serializable