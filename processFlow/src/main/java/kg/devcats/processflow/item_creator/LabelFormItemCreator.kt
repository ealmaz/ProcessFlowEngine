package kg.devcats.processflow.item_creator

import android.content.Context
import android.widget.TextView
import com.design2.chili2.R
import kg.devcats.processflow.model.input_form.LabelFormItem

object LabelFormItemCreator : ValidatableItem() {

    fun create(context: Context, labelFormItem: LabelFormItem): TextView {
        return TextView(context).apply {
            tag = labelFormItem.fieldId
            setTextAppearance(R.style.Chili_H7_Primary_Bold)
            setPadding(
                context.resources.getDimensionPixelSize(R.dimen.padding_16dp),
                context.resources.getDimensionPixelSize(R.dimen.padding_14dp),
                context.resources.getDimensionPixelSize(R.dimen.padding_16dp),
                context.resources.getDimensionPixelSize(R.dimen.padding_14dp)
            )
            text = labelFormItem.label
        }
    }
}