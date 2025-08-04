package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class GroupButtonFormItem(
    val fieldId: String,
    val options: List<Option>? = null,
    val chooseType: ChooseType? = ChooseType.MULTIPLE,
    val buttonType: ButtonType? = ButtonType.CHECK_BOX,
    val validations: List<Validation>? = null,
    val label: String? = null
): Serializable

enum class ChooseType {
    MULTIPLE, SINGLE
}

enum class ButtonType {
    CHECK_BOX, TOGGLE, RADIO_BUTTON
}

data class Option(
    val id: String,
    val img: String? = null,
    val label: String? = null,
    val subtitle: String? = null,
    var isSelected: Boolean? = false,
    val isHtmlText: Boolean? = false,
    var isEdited: Boolean? = false,
) : Serializable