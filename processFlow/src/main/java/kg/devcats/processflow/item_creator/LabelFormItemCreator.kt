package kg.devcats.processflow.item_creator

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.design2.chili2.extensions.color
import com.design2.chili2.view.cells.BaseCellView
import kg.devcats.processflow.R
import kg.devcats.processflow.model.input_form.LabelFormItem

object LabelFormItemCreator : ValidatableItem() {

    fun create(context: Context, labelFormItem: LabelFormItem): View {
        return if (labelFormItem.description.isNullOrEmpty()) createTextView(
            context,
            labelFormItem
        ) else createBaseCellView(context, labelFormItem)
    }

    private fun createTextView(context: Context, labelFormItem: LabelFormItem): TextView {
        val padding = context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp)
        return TextView(context).apply {
            tag = labelFormItem.fieldId
            setTextAppearance(com.design2.chili2.R.style.Chili_H7_Primary_Bold)
            setPadding(padding, padding, padding, padding)
            text = labelFormItem.label
        }
    }

    private fun createBaseCellView(context: Context, labelFormItem: LabelFormItem): BaseCellView {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp),
                context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_8dp),
                context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp),
                context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_8dp)
            )
        }

        return BaseCellView(context, null, 0, com.design2.chili2.R.style.Chili_InputViewStyle).apply {
            tag = labelFormItem.fieldId
            setTitleTextAppearance(com.design2.chili2.R.style.Chili_H7_Primary_Bold)
            setSubtitleTextAppearance(com.design2.chili2.R.style.Chili_H8_Primary)
            setIsChevronVisible(false)
            setDividerVisibility(false)
            setTitle(labelFormItem.label)
            setBackgroundResource(R.drawable.ic_label_cell_rounded_bg)
            this.layoutParams = layoutParams

            labelFormItem.description?.let {
                setSubtitle(it)
            }
            if (labelFormItem.hasBackground != true) setBackgroundColor(context.color(android.R.color.transparent))
        }
    }
}