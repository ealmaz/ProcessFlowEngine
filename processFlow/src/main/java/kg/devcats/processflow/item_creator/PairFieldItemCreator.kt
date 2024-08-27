package kg.devcats.processflow.item_creator

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import com.design2.chili2.extensions.setIsSurfaceClickable
import com.design2.chili2.view.cells.AdditionalTextCellView
import kg.devcats.processflow.R
import com.design2.chili2.R as Chilli_R
import kg.devcats.processflow.model.input_form.PairFieldItem

object PairFieldItemCreator {

    fun create(context: Context, pairFieldItem: PairFieldItem): View {
        val padding0 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_0dp)
        val padding4 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_4dp)
        val padding8 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_8dp)
        val padding12 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_12dp)
        val padding16 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_16dp)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(padding16, padding8, padding16, padding8) }

        return AdditionalTextCellView(context, null, 0, Chilli_R.style.Chili_InputViewStyle).apply {
            tag = pairFieldItem.fieldId
            setAdditionalTextTextAppearance(Chilli_R.style.Chili_H7_Primary)
            setPadding(padding0, padding0, padding0, padding4)
            updateTitleMargin(padding16, padding16, padding16, padding0)
            setTitleMaxLines(Int.MAX_VALUE)
            pairFieldItem.startText?.let {
                if (pairFieldItem.isHtml == true) setTitle(it.parseAsHtml())
                else setTitle(it)
            }

            setTitleTextAppearance(Chilli_R.style.Chili_H7_Primary)
            updateAdditionalTextMargin(padding0, padding16, padding4, padding12)
            setAdditionalTextMaxLines(Int.MAX_VALUE)
            pairFieldItem.endText?.let {
                if (pairFieldItem.isHtml == true) setAdditionalText(it.parseAsHtml())
                else setAdditionalText(it)
            }

            setBackgroundResource(R.drawable.ic_label_cell_rounded_bg)
            setIsSurfaceClickable(false)
            this.layoutParams = layoutParams
            setDividerVisibility(false)
            setIsChevronVisible(false)
        }
    }
}