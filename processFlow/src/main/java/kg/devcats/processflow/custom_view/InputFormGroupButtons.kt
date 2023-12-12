package kg.devcats.processflow.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Switch
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import kg.devcats.processflow.R
import kg.devcats.processflow.databinding.ProcessFlowViewFormItemGroupButtonsBinding
import kg.devcats.processflow.extension.handleUrlClicks
import kg.devcats.processflow.model.input_form.ButtonType
import kg.devcats.processflow.model.input_form.ChooseType
import kg.devcats.processflow.model.input_form.Option

class InputFormGroupButtons @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : LinearLayout(context, attributeSet),
    CompoundButton.OnCheckedChangeListener {

    private val buttonsMarginPx: Int by lazy {
        resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp)
    }

    private val buttonsLayoutParams: LayoutParams by lazy {
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(buttonsMarginPx, buttonsMarginPx, buttonsMarginPx, 0)
        }
    }

    private var buttonsContainer: LinearLayout? = null

    private var onSelectedItemChanged: ((selectedId: List<String>) -> Unit)? = null

    private var chooseType: ChooseType = ChooseType.MULTIPLE
    private var buttonType: ButtonType = ButtonType.CHECK_BOX

    private var lastCheckedId: String? = null

    val buttons = mutableListOf<Option>()

    private val vb: ProcessFlowViewFormItemGroupButtonsBinding by lazy {
        ProcessFlowViewFormItemGroupButtonsBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        obtainAttributes(context, attributeSet)
    }

    private fun obtainAttributes(context: Context, attributeSet: AttributeSet?) {
        context.obtainStyledAttributes(attributeSet, R.styleable.process_flow_InputFormGroupButtons).run {
            setButtonTypeInt(getInt(R.styleable.process_flow_InputFormGroupButtons_process_flow_btnType, ButtonType.CHECK_BOX.ordinal))
            setChooseTypeInt(getInt(R.styleable.process_flow_InputFormGroupButtons_process_flow_choosingType, ChooseType.MULTIPLE.ordinal))
            recycle()
        }
    }

    private fun setButtonTypeInt(intType: Int) {
        val buttonType = when (intType) {
            ButtonType.CHECK_BOX.ordinal -> ButtonType.CHECK_BOX
            ButtonType.TOGGLE.ordinal -> ButtonType.TOGGLE
            ButtonType.RADIO_BUTTON.ordinal -> ButtonType.RADIO_BUTTON
            else -> ButtonType.CHECK_BOX
        }
        setButtonType(buttonType)
    }

    fun setAllButtons(list: List<Option>) {
        this.buttons.clear()
        this.buttons.addAll(list)
    }

    fun setButtonType(buttonType: ButtonType) {
        this.buttonType = buttonType
    }

    private fun setChooseTypeInt(chooseTypeInt: Int) {
        val chooseType = when (chooseTypeInt) {
            ChooseType.MULTIPLE.ordinal -> ChooseType.MULTIPLE
            ChooseType.SINGLE.ordinal -> ChooseType.SINGLE
            else -> throw IllegalArgumentException()
        }
        setChooseType(chooseType)
    }

    fun setChooseType(chooseType: ChooseType) {
        this.chooseType = chooseType
    }


    @SuppressLint("ClickableViewAccessibility")
    fun renderButtons(onLinkClick: ((String) -> Unit)? = null) {
        buttonsContainer?.let { vb.flRoot.removeView(it) }
        prepareContainer().let { container ->
            buttons.forEach {
                val button = getButton().apply {
                    setOnCheckedChangeListener(this@InputFormGroupButtons)
                    tag = it.id
                    id = it.hashCode()
                    if (it.isHtmlText == true) {
                        text = it.label?.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
                        handleUrlClicks {
                            this.isChecked = this.isChecked.not()
                            onLinkClick?.invoke(it)
                        }
                    } else text = it.label
                }
                button.isChecked = it.isSelected ?: false
                container.addView(button)
            }
            val result = validateCheckedStatesAndGetResult()
            onSelectedItemChanged?.invoke(result)
            vb.flRoot.addView(container)
        }
    }

    private fun prepareContainer(): LinearLayout {
        return buttonsContainer ?: LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            buttonsContainer = this
        }
    }

    private fun getButton(): CompoundButton {

        return when (buttonType) {
            ButtonType.CHECK_BOX -> CheckBox(context).apply {
                layoutParams = buttonsLayoutParams
            }
            ButtonType.RADIO_BUTTON -> RadioButton(context).apply {
                layoutParams = buttonsLayoutParams
            }
            ButtonType.TOGGLE -> Switch(context).apply {
                layoutDirection = View.LAYOUT_DIRECTION_RTL
                layoutParams = buttonsLayoutParams
            }
        }
    }



    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        clearError()
        val optionId = buttonView?.tag?.toString()
        val option = buttons.find { it.id == optionId }
        if (option == null || option.isSelected == isChecked) return
        if (isChecked) uncheckPreviousCheckedButton()
        option.isSelected = isChecked
        val result = validateCheckedStatesAndGetResult()
        onSelectedItemChanged?.invoke(result)
    }

    private fun uncheckPreviousCheckedButton() {
        if (chooseType == ChooseType.MULTIPLE) return
        buttons.forEach {
            it.isSelected = false
        }
    }

    private fun validateCheckedStatesAndGetResult(): List<String> {
        val selectedIds = mutableListOf<String>()
        buttons.forEach {
            buttonsContainer?.findViewWithTag<CompoundButton>(it.id)?.apply {
                if (isChecked != it.isSelected) isChecked = it.isSelected ?: false
                if (isChecked) selectedIds.add(it.id)
            }
        }
        return selectedIds
    }

    fun isChecked(): Boolean {
        return lastCheckedId != null
    }

    fun setSelectedItemChangedListener(listener: (selectedId: List<String>) -> Unit) {
        this.onSelectedItemChanged = listener
    }

    fun setupAsError() {
        vb.flRoot.setBackgroundColor(context.getColor(com.design2.chili2.R.color.red_3))
    }

    fun clearError() {
        vb.flRoot.setBackgroundColor(context.getColor(android.R.color.transparent))
    }
}