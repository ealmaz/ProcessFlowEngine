package kg.devcats.processflow.model.component

import kg.devcats.processflow.model.input_form.Validation
import java.io.Serializable

data class FlowInputField(
    val fieldId: String,
    var value: String? = null,
    val hint: String? = null,
    val placeholder: String? = null,
    val label: String? = null,
    val inputType: InputFieldInputType? = null,
    val mask: String? = null,
    val maskSymbols: List<String>? = null,
    val validations: List<Validation>? = null,
    val enableActionAfterMills: Long? = null,
    val additionalActionResolutionCode: String? = null,
    val errorMessage: String? = null,
    val otpLength: Int? = null,
    val disabled: Boolean? = null,
): Serializable

enum class InputFieldInputType {
    TEXT, NUMBER
}

