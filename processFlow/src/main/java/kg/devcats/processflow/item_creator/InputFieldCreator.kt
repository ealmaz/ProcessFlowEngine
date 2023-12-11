package kg.devcats.processflow.item_creator

import android.content.Context
import android.text.InputType
import android.widget.LinearLayout
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.input.MaskedInputView
import com.design2.chili2.view.input.MultilineInputView
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.component.InputFieldInputType

object InputFieldCreator : ValidatableItem() {

    fun create(
        context: Context,
        fieldInfo: FlowInputField,
        onFiledChanged: (result: List<String>, isValid: Boolean) -> Unit,
    ): BaseInputView {
        return createInputField(context, fieldInfo.numberOfLines).apply {
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
                val isValid = if (this is MaskedInputView) validateItem(fieldInfo.validations, input) && isInputMaskFilled()
                else validateItem(fieldInfo.validations, input)
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
            fieldInfo.mask?.let { (this as? MaskedInputView)?.setupNewMask(it) }
            fieldInfo.maskSymbols?.let { (this as? MaskedInputView)?.setupNewMaskSymbols(it.map { it.first() }) }
            when  {
                fieldInfo.inputType == InputFieldInputType.NUMBER -> setInputType(InputType.TYPE_CLASS_NUMBER)
                (fieldInfo.numberOfLines ?: 0) > 1 -> setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                else -> setInputType(InputType.TYPE_CLASS_TEXT)
            }
            fieldInfo?.errorMessage?.let { setupFieldAsError(it) }
            if (fieldInfo.disabled == true) disableEdition()
            else setupClearTextButton()
            fieldInfo.maxLength?.let { setMaxLength(it) }
        }
    }

    private fun createInputField(context: Context, linesNumber: Int?): BaseInputView {
        return when (linesNumber == null || linesNumber <= 1) {
            true -> BaseInputView(context).apply { changeInputPositionToCenter() }
            else -> MultilineInputView(context).apply {
                setMaxLines(linesNumber)
                setMinLines(linesNumber)
            }
        }
    }
}