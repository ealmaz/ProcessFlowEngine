package kg.devcats.processflow.item_creator

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.buttons.LoaderButton
import kg.devcats.processflow.model.component.ButtonProperties
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.model.component.FlowButtonStyle

object FlowButtonCreator {

    fun create(
        context: Context,
        buttonInfo: FlowButton,
        onClick: (buttonInfo: FlowButton) -> Unit
    ): View {
        val view = if (buttonInfo.style == FlowButtonStyle.ACCENT) LoaderButton(context)
        else Button(context, null, 0, com.design2.chili2.R.style.Chili_ButtonStyle_Secondary)
        return view.apply {
            val margin = resources.getDimensionPixelSize(com.design2.chili2.R.dimen.padding_16dp)
            tag = buttonInfo.buttonId
            setOnSingleClickListener { onClick(buttonInfo) }
            (this as? LoaderButton)?.let { setupButton(it, buttonInfo) }
            (this as? Button)?.let { setupButton(it, buttonInfo) }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
        }
    }

    fun setupButton(button: LoaderButton, buttonInfo: FlowButton) {
        buttonInfo.apply {
            text?.let { button.setText(it) }
            disabled.let { button.isEnabled = !it  }
            properties?.get(ButtonProperties.ENABLED.propertyName)?.let { button.isEnabled = it.toBooleanStrictOrNull() ?: true }
        }
    }

    fun setupButton(button: Button, buttonInfo: FlowButton) {
        buttonInfo.apply {
            text?.let { button.text = it }
            disabled.let { button.isEnabled = !it  }
            properties?.get(ButtonProperties.ENABLED.propertyName)?.let { button.isEnabled = it.toBooleanStrictOrNull() ?: true }
        }
    }
}