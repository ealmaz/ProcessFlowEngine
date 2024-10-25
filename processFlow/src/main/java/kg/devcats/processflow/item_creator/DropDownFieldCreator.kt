package kg.devcats.processflow.item_creator

import android.content.Context
import android.widget.LinearLayout
import kg.devcats.processflow.custom_view.drop_down_input_field.DropDownInputField
import kg.devcats.processflow.model.input_form.DropDownFieldInfo

object DropDownFieldCreator : ValidatableItem() {

    fun create(context: Context, dropDownFieldInfo: DropDownFieldInfo, onSelectionChanged: (selected: List<String>, isValid: Boolean) -> Unit, onRequestOptions: (String) -> Unit): DropDownInputField {
        return DropDownInputField(context).apply {
            tag = dropDownFieldInfo.fieldId
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp),
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_8dp),
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp),
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_8dp)
                )
            }
            setHint(dropDownFieldInfo.label ?: "")
            setupViews(dropDownFieldInfo, onSelectionChanged, onRequestOptions = onRequestOptions)
            dropDownFieldInfo.options?.let { options = it }
        }
    }

}