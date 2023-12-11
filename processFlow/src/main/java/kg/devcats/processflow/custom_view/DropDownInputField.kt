package kg.devcats.processflow.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.SearchSelectorBottomSheet
import kg.devcats.processflow.R
import kg.devcats.processflow.databinding.ProcessFlowViewFormItemDropDownBinding
import kg.devcats.processflow.extension.getThemeColor
import kg.devcats.processflow.item_creator.DropDownFieldCreator
import kg.devcats.processflow.model.input_form.ChooseType
import kg.devcats.processflow.model.input_form.DropDownFieldInfo

class DropDownInputField @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val views: ProcessFlowViewFormItemDropDownBinding by lazy {
        ProcessFlowViewFormItemDropDownBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var options: List<Option> = listOf()
        set(value) {
            field = value
            if (field.size == 1) field.first().isSelected = true
            onBottomSheetDismiss()
        }

    private var onSelectionChanged: ((values: List<String>, Boolean) -> Unit)? = null

    private var dropDownListInfo: DropDownFieldInfo? = null

    fun setupViews(dropDownFieldInfo: DropDownFieldInfo, onSelectionChanged: (values: List<String>, Boolean) -> Unit) {
        this.onSelectionChanged = onSelectionChanged
        this.dropDownListInfo = dropDownFieldInfo
        this.setOnClickListener {
            clearError()
            if (options.isEmpty()) return@setOnClickListener
            val bs = createSearchBottomSheet(context, dropDownFieldInfo.chooseType != ChooseType.MULTIPLE)
            bs.setOnDismissListener { onBottomSheetDismiss() }
            bs.show()
        }
        onBottomSheetDismiss()
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

    private fun createSearchBottomSheet(context: Context, isSingleSelection: Boolean): SearchSelectorBottomSheet {
        return SearchSelectorBottomSheet.Builder()
            .setIsHeaderVisible(true)
            .setIsSearchAvailable(true)
            .setIsSingleSelection(isSingleSelection)
            .build(context, options)
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
            if (it.isSelected) {
                selectedValues.add(it.value)
                selectedIds.add(it.id)
            }
        }
        setText(selectedValues.joinToString { it })
        val isValid = DropDownFieldCreator.validateItem(dropDownListInfo?.validations, selectedIds)
        onSelectionChanged?.invoke(selectedIds, isValid)
    }

    fun setupAsError() {
        views.root.setBackgroundColor(getColor(context, com.design2.chili2.R.color.red_3))
    }

    fun clearError() {
        val color = context.getThemeColor(com.design2.chili2.R.attr.ChiliInputViewBackgroundColor)
        views.root.setBackgroundColor(color)
    }
}