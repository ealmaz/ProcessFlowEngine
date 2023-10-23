package kg.devcats.processflow.item_creator

import android.content.Context
import kg.devcats.processflow.custom_view.InputFormGroupButtons
import kg.devcats.processflow.model.input_form.GroupButtonFormItem

object GroupButtonsCreator : ValidatableItem() {

    fun create(context: Context, groupInfo: GroupButtonFormItem, onSelectedChanged: (selected: List<String>, isValid: Boolean) -> Unit): InputFormGroupButtons {
        return InputFormGroupButtons(context).apply {
            tag = groupInfo.fieldId
            setSelectedItemChangedListener {
                val isValid = validateItem(groupInfo.validations, it)
                onSelectedChanged(it, isValid)
            }
            groupInfo.buttonType?.let { setButtonType(it) }
            groupInfo.chooseType?.let { setChooseType(it) }
            groupInfo.options?.let { setAllButtons(it) }
            renderButtons()
        }
    }
}