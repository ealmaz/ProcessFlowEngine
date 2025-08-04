package kg.devcats.processflow.custom_view.drop_down_input_field

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.devcats.processflow.custom_view.drop_down_input_field.bottom_sheet.DropDownFieldBottomSheet
import kg.devcats.processflow.databinding.ProcessFlowViewFormItemDropDownBinding
import kg.devcats.processflow.extension.getThemeColor
import kg.devcats.processflow.item_creator.DropDownFieldCreator
import kg.devcats.processflow.model.input_form.ChooseType
import kg.devcats.processflow.model.input_form.DropDownFieldInfo
import kg.devcats.processflow.model.input_form.Option

class DropDownInputField @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val views: ProcessFlowViewFormItemDropDownBinding by lazy {
        ProcessFlowViewFormItemDropDownBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var options: List<Option> = listOf()
        set(value) {
            field = value
            when {
                field.isEmpty() -> setHint(dropDownListInfo?.label ?: "")
                field.size == 1 -> field.first().isSelected = true
            }
            onBottomSheetDismiss()
        }

    private var onSelectionChanged: ((values: List<String>, Boolean) -> Unit)? = null

    private var dropDownListInfo: DropDownFieldInfo? = null

    fun setSelectedIds(ids: List<String>) {
        if (options.isNotEmpty()){
            options = options.map { opt -> opt.copy(isSelected = ids.contains(opt.id)) }
            onBottomSheetDismiss()
        }
    }

    fun setupViews(dropDownFieldInfo: DropDownFieldInfo, onSelectionChanged: (values: List<String>, Boolean) -> Unit, onRequestOptions: (String) -> Unit) {
        this.onSelectionChanged = onSelectionChanged
        this.dropDownListInfo = dropDownFieldInfo
        dropDownFieldInfo.options?.let { options = it }
        this.setOnSingleClickListener {
            clearError()
            if (options.isEmpty()) onRequestOptions(dropDownFieldInfo.fieldId)
            else showOptionsBS()
        }
    }

    fun showOptionsBS() {
        val bs = DropDownFieldBottomSheet(
            mContext = context,
            optionsList = options,
            title = dropDownListInfo?.label ?: "",
            isSingleSelectionType = dropDownListInfo?.chooseType != ChooseType.MULTIPLE,
            isSearchEnabled = dropDownListInfo?.isSearchEnabled ?: true
        )
        bs.setOnDismissListener { onBottomSheetDismiss() }
        bs.show()
    }

    fun setHint(hint: String) {
        views.tvLabel.apply {
            text = hint
            setTextColor(getColor(context, com.design2.chili2.R.color.gray_1_alpha_50))
        }
    }

    fun setText(text: String) {
        if (text.isBlank()) return
        views.tvLabel.apply {
            this.text = text
            setTextColor(context.getThemeColor(com.design2.chili2.R.attr.ChiliPrimaryTextColor))
        }
    }

    fun clearSelected() {
        options.forEach { it.isSelected = false }
        onBottomSheetDismiss()
    }

    private fun onBottomSheetDismiss() {
        setHint(dropDownListInfo?.label ?: "")
        val selectedValues = mutableListOf<String>()
        val selectedIds = mutableListOf<String>()
        options.forEach {
            if (it.isSelected == true) {
                selectedValues.add(it.label ?: "")
                selectedIds.add(it.id)
            }
        }
        setText(selectedValues.joinToString { it })
        val isValid = DropDownFieldCreator.validateItem(dropDownListInfo?.validations, selectedIds)
        onSelectionChanged?.invoke(selectedIds, isValid)
    }

    fun setupAsError() {
        views.root.setBackgroundResource(com.design2.chili2.R.drawable.chili_bg_input_view_error_rounded)
    }

    fun clearError() {
        views.root.setBackgroundResource(com.design2.chili2.R.drawable.chili_bg_input_view_rounded)
    }
}