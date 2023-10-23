package kg.devcats.processflow.item_creator

import android.content.Context
import android.widget.LinearLayout
import kg.devcats.processflow.custom_view.DatePickerInputField
import kg.devcats.processflow.model.input_form.DatePickerFieldInfo

object DatePickerFieldCreator : ValidatableItem() {

    fun create(
        context: Context,
        datePickerFieldInfo: DatePickerFieldInfo,
        onSetValue: (List<String> , Boolean) -> Unit
    ): DatePickerInputField {
        return DatePickerInputField(context).apply {
            tag = datePickerFieldInfo.fieldId
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp),
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp)
                )
            }
            setupViews(datePickerFieldInfo, onSetValue)
        }
    }
}