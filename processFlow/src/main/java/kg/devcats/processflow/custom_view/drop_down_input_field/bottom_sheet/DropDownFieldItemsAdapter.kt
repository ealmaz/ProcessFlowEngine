package kg.devcats.processflow.custom_view.drop_down_input_field.bottom_sheet

import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.cells.EndIconCellView
import kg.devcats.processflow.extension.gone
import kg.devcats.processflow.model.input_form.Option

class DropDownFieldItemsAdapter(private var listener: OnDropDownItemClick) : ListAdapter<Option, RecyclerView.ViewHolder>(DropDownFieldItemsDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DropDownItemViewHolder.create(parent) { listener.onDropDownItemClick(it) }
    }

    override fun getItemCount(): Int {
        return currentList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DropDownItemViewHolder -> holder.onBind(currentList[position], position == 0, position == currentList.lastIndex)
        }
    }

    fun clearItems() {
        submitList(emptyList())
    }

    fun setItems(items: List<Option?>) {
        submitList(items.mapNotNull { it })
    }
}

class DropDownItemViewHolder(val view: EndIconCellView) : RecyclerView.ViewHolder(view) {

    private var item: Option? = null

    fun onBind(item: Option, isFirst: Boolean, isLast: Boolean) {
        this.item = item
        view.apply {
            setupRoundedModeByPosition(isFirst, isLast)
            item.img?.let { setupStartIcon(it) } ?: getIconView().gone()
            setupTitles(item.label, item.subtitle, item.isHtmlText)
            setIsEndIconVisible(item.isSelected == true)
        }
    }

    private fun setupStartIcon(img: String) = with(view) {
        loadIcon(img)
        setIconSize(com.design2.chili2.R.dimen.view_48dp, com.design2.chili2.R.dimen.view_48dp)
        updateIconMargin(
            topMarginPx = resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_12dp),
            bottomMarginPx = resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_12dp)
        )
    }

    private fun setupTitles(label: String?, subtitle: String?, isHtmlText: Boolean?) = with(view) {
        subtitle?.let { setSubtitleTextAppearance(com.design2.chili2.R.style.Chili_H7_Primary) }
        if (isHtmlText == true) {
            setTitleOrHide(label?.parseAsHtml()?.toString())
            setSubtitle(subtitle?.parseAsHtml())
        } else {
            setTitleOrHide(label)
            setSubtitle(subtitle)
        }
    }

    companion object {
        fun create(parent: ViewGroup, onClick: (Option?) -> Unit): DropDownItemViewHolder {
            val cell = EndIconCellView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                setEndIcon(com.design2.chili2.R.drawable.chili_ic_choice)
                setEndIconSize(com.design2.chili2.R.dimen.view_32dp, com.design2.chili2.R.dimen.view_32dp)
            }
            return DropDownItemViewHolder(cell).apply {
                view.setOnSingleClickListener { onClick.invoke(item) }
            }
        }
    }
}

interface OnDropDownItemClick {
    fun onDropDownItemClick(option: Option?)
}

object DropDownFieldItemsDiffUtil : DiffUtil.ItemCallback<Option>() {
    override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem == newItem
    }

}