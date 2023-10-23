package kg.devcats.processflow.item_creator

import android.content.Context
import android.widget.LinearLayout
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import kg.devcats.processflow.custom_view.DropDownInputField
import kg.devcats.processflow.model.input_form.DropDownFieldInfo

object DropDownFieldCreator : ValidatableItem() {

    fun create(context: Context, dropDownFieldInfo: DropDownFieldInfo, onSelectionChanged: (selected: List<String>, isValid: Boolean) -> Unit): DropDownInputField {
        return DropDownInputField(context).apply {
            tag = dropDownFieldInfo.fieldId
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp),
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp)
                )
            }
            setHint(dropDownFieldInfo.label ?: "")
            setupViews(dropDownFieldInfo, onSelectionChanged)
            dropDownFieldInfo.options?.let { options = it.map { Option(it.id, it.label ?: "", it.isSelected ?: false) } }
        }
    }

}