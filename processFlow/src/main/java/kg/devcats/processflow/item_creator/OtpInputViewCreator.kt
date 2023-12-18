package kg.devcats.processflow.item_creator

import android.content.Context
import android.widget.LinearLayout
import com.design2.chili2.R
import com.design2.chili2.view.input.otp.OtpInputView
import com.design2.chili2.view.input.otp.OtpItemState
import kg.devcats.processflow.model.component.FlowInputField

object OtpInputViewCreator : ValidatableItem() {

    fun create(
        context: Context,
        fieldInfo: FlowInputField,
        onInputComplete: (result: List<String>, isValid: Boolean) -> Unit,
    ): OtpInputView {
        return OtpInputView(context).apply {
            tag = fieldInfo.fieldId
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(
                    resources.getDimensionPixelSize(R.dimen.padding_16dp),
                    resources.getDimensionPixelSize(R.dimen.padding_8dp),
                    resources.getDimensionPixelSize(R.dimen.padding_16dp),
                    resources.getDimensionPixelSize(R.dimen.padding_8dp)
                )
            }
            setOnOtpCompleteListener(object : com.design2.chili2.view.input.otp.OnOtpCompleteListener {
                override fun onInput(text: String?) {
                    setMessageText(null)
                    val input = listOf(text?.trim() ?: "")
                    onInputComplete(input, false)
                }

                override fun onOtpInputComplete(otp: String) {
                    val input = listOf(otp.trim())
                    val isValid = validateItem(fieldInfo.validations, input)
                    onInputComplete(input, isValid)
                }
            })
            setText(fieldInfo.value ?: "")
            fieldInfo.errorMessage?.let {
                setupState(OtpItemState.ERROR)
                setMessageText(it)
            }
        }
    }
}