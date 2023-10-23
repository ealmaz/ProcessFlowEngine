package kg.devcats.processflow.item_creator

import android.content.Context
import android.text.InputType
import android.widget.LinearLayout
import com.design2.chili2.view.input.MaskedInputView
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.component.InputFieldInputType

object InputFieldCreator : ValidatableItem() {

    fun create(
        context: Context,
        fieldInfo: FlowInputField,
        onFiledChanged: (result: List<String>, isValid: Boolean) -> Unit,
    ): MaskedInputView {
        return MaskedInputView(context).apply {
            tag = fieldInfo.fieldId
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp),
                    0,
                    resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_4dp)
                )
            }
            setSimpleTextChangedListener {
                val input = listOf(getInputText().trim())
                val isValid = validateItem(fieldInfo.validations, input) && isInputMaskFilled()
                this.clearFieldError()
                onFiledChanged(input, isValid)
            }
            setText(fieldInfo.value ?: "")
            fieldInfo.hint?.let { setMessage(it) }
            fieldInfo.label?.let {
                setHint(it)
                setMessage(it)
                setupMessageAsLabelBehavior(true)
            }
            fieldInfo.placeholder?.let { setHint(it) }
            fieldInfo.mask?.let { setupNewMask(it) }
            fieldInfo.maskSymbols?.let { setupNewMaskSymbols(it.map { it.first() }) }
            when (fieldInfo.inputType) {
                InputFieldInputType.NUMBER -> setInputType(InputType.TYPE_CLASS_NUMBER)
                else -> setInputType(InputType.TYPE_CLASS_TEXT)
            }
            fieldInfo?.errorMessage?.let { setupFieldAsError(it) }
            changeInputPositionToCenter()
            if (fieldInfo.disabled == true) disableEdition()
            else setupClearTextButton()
        }
    }
}