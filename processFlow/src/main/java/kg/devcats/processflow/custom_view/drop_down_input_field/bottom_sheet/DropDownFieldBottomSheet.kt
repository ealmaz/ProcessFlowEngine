package kg.devcats.processflow.custom_view.drop_down_input_field.bottom_sheet

import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.extensions.setTopMargin
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kg.devcats.processflow.R
import kg.devcats.processflow.databinding.ProcessFlowViewFormItemDropDownBottomSheetFragmentBinding
import kg.devcats.processflow.extension.visible
import kg.devcats.processflow.model.input_form.Option

class DropDownFieldBottomSheet(
    mContext: Context,
    private val optionsList: List<Option>,
    private val title: String,
    private val isSingleSelectionType: Boolean,
    private val isSearchEnabled: Boolean
) : BottomSheetDialog(mContext, R.style.DropDownBottomSheetStyle), OnDropDownItemClick {

    private var vb: ProcessFlowViewFormItemDropDownBottomSheetFragmentBinding =
        ProcessFlowViewFormItemDropDownBottomSheetFragmentBinding.inflate(layoutInflater)

    private var filterText = ""

    private val itemsAdapter: DropDownFieldItemsAdapter by lazy {
        DropDownFieldItemsAdapter(this)
    }

    init {
        setContentView(vb.root)
        setupViews()
        setAdapterFilteredItems()
    }

    private fun setupViews() = with (vb) {
        setupTitle()
        ivClose.setOnSingleClickListener { dismiss() }
        rvItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
        if (isSearchEnabled) setupSearchInputView()
    }

    private fun setupTitle() = with(vb) {
        when (title.isNotEmpty()) {
            true -> tvTitle.run { visible(); text = title }
            else -> llSearch.setTopMargin(context.resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_14dp))
        }
    }

    private fun setupSearchInputView() = with(vb) {
        llSearch.visible()
        etSearch.run {
            doAfterTextChanged {
                expandBottomSheet()
                filterText = it?.toString() ?: ""
                setAdapterFilteredItems()
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) expandBottomSheet()
            }
        }
    }

    override fun onDropDownItemClick(option: Option?) {
        if (isSingleSelectionType) setSelectedForSingleType(option)
        else setSelectedForMultipleType(option)
    }

    private fun setSelectedForSingleType(option: Option?) {
        optionsList.forEach {
            it.isSelected = if (it.id == option?.id) it.isSelected?.not()
            else false
        }
        dismiss()
    }

    private fun setSelectedForMultipleType(option: Option?) {
        optionsList.find { it.id == option?.id }?.apply {
            isSelected = isSelected?.not()
        }
        setAdapterFilteredItems()
    }

    private fun setAdapterFilteredItems() {
        itemsAdapter.setItems(optionsList.filter { it.label?.contains(filterText.trim(), true) ?: false })
    }

    private fun expandBottomSheet() {
        if (isExpandBottomSheet()) return
        val bottomSheetInternal = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
        BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun isExpandBottomSheet(): Boolean {
        val bottomSheetInternal = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
        return BottomSheetBehavior.from(bottomSheetInternal).state == BottomSheetBehavior.STATE_EXPANDED
    }

    override fun show() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        super.show()
    }

}