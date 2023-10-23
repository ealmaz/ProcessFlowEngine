package kg.devcats.processflow.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kg.devcats.processflow.databinding.ViewFormItemDatePickerBinding
import kg.devcats.processflow.extension.getThemeColor
import kg.devcats.processflow.item_creator.DatePickerFieldCreator
import kg.devcats.processflow.model.input_form.DatePickerFieldInfo
import java.text.SimpleDateFormat
import java.util.Date

class DatePickerInputField @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    private var onNewValueListener: ((List<String>, Boolean) -> Unit)? = null

    private var datePickerFieldInfo: DatePickerFieldInfo? = null

    private val vb: ViewFormItemDatePickerBinding = ViewFormItemDatePickerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupViews(datePickerFieldInfo: DatePickerFieldInfo, onSetNewValue: (List<String>, Boolean) -> Unit) {
        setDate(datePickerFieldInfo.value)
        datePickerFieldInfo.hint?.let { setHelperText(it) }
        datePickerFieldInfo.placeHolder?.let { setHint(it) }
        datePickerFieldInfo.label?.let { setLabel(it) }
        this.onNewValueListener = onSetNewValue
    }

    fun setHint(hint: String) {
        vb.tvLabel.apply {
            text = hint
            setTextColor(ContextCompat.getColor(context, com.design2.chili2.R.color.gray_1_alpha_50))
        }
    }

    private fun setText(text: String) {
        if (text.isBlank()) return
        vb.tvLabel.apply {
            this.text = text
            setTextColor(context.getThemeColor(com.design2.chili2.R.attr.ChiliPrimaryTextColor))
        }
    }

    fun setDate(dateLong: Long?) {
        val values = dateLong?.toString()?.let { listOf(it) } ?: emptyList()
        val isValid = DatePickerFieldCreator.validateItem(datePickerFieldInfo?.validations, values)
        if (dateLong == null) {
            setText("")
        } else {
            setText(dateFormat.format(Date(dateLong)))
        }
        onNewValueListener?.invoke(values, isValid)
    }

    fun setLabel(label: String) {
        setHint(label)
    }

    fun setHelperText(helperText: String) {
        vb.tvHelper.apply {
            visibility = VISIBLE
            text = helperText
        }
    }
}