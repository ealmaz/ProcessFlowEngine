package kg.devcats.processflow.model.component

import com.google.gson.JsonElement
import java.io.Serializable

data class FlowFormInfoJson(
    val formId: String,
    val title: String,
    val formItems: List<FormItemJson>
): Serializable


data class FormItemJson(
    val formItemType: FormItemType?,
    val formItem: JsonElement?
): Serializable

enum class FormItemType {
    INPUT_FIELD, GROUP_BUTTON_FORM_ITEM, DROP_DOWN_FORM_ITEM, DATE_PICKER_FORM_ITEM, LABEL
}







