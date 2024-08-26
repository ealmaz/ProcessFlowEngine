package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class PairFieldItem(
    val fieldId: String? = null,
    val label: String? = null,
    val validations: List<Validation>? = null,
    val startText: String? = null,
    val endText: String? = null,
    val isHtml: Boolean? = null
): Serializable