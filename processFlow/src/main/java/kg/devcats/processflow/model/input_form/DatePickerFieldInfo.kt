package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class DatePickerFieldInfo(
    val fieldId: String,
    val value: Long? = null,
    val startDateLimit: Long? = null,
    val endDateLimit: Long? = null,
    val label: String? = null,
    val placeHolder: String? = null,
    val hint: String? = null,
    val validations: List<Validation>? = null
): Serializable