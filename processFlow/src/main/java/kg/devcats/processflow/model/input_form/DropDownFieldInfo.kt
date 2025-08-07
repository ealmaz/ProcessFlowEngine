package kg.devcats.processflow.model.input_form

import java.io.Serializable

data class DropDownFieldInfo(
    val fieldId: String,
    val parentFieldId: String? = null,
    val chooseType: ChooseType? = ChooseType.SINGLE,
    val label: String? = null,
    val validations: List<Validation>? = null,
    val isNeedToFetchOptions: Boolean? = null,
    val options: List<Option>? = null,
    val isSearchEnabled: Boolean? = null,
    val value: String? = null,
    val errorMessage: String? = null
): Serializable