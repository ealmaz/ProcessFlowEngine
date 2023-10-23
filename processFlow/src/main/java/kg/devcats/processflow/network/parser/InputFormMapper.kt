package kg.devcats.processflow.network.parser

import com.google.gson.Gson
import o.credits.model.process_flow.FlowFormInfoJson
import o.credits.model.process_flow.FlowInputField
import kg.devcats.processflow.model.input_form.DatePickerFieldInfo
import kg.devcats.processflow.model.input_form.DropDownFieldInfo
import kg.devcats.processflow.model.input_form.FormItem
import kg.devcats.processflow.model.input_form.FormItemType
import kg.devcats.processflow.model.input_form.GroupButtonFormItem
import kg.devcats.processflow.model.input_form.InputForm
import kg.devcats.processflow.model.input_form.LabelFormItem

open class InputFormMapper {

    private val gson = Gson()

    open fun map(formJson: FlowFormInfoJson): InputForm {

        val formItem = formJson.formItems.mapNotNull {
            when (it.formItemType) {
                o.credits.model.process_flow.FormItemType.INPUT_FIELD -> FormItem(
                    FormItemType.INPUT_FIELD,
                    gson.fromJson(it.formItem, FlowInputField::class.java)
                )

                o.credits.model.process_flow.FormItemType.GROUP_BUTTON_FORM_ITEM -> FormItem(
                    FormItemType.GROUP_BUTTON_FORM_ITEM,
                    gson.fromJson(it.formItem, GroupButtonFormItem::class.java)
                )

                o.credits.model.process_flow.FormItemType.DROP_DOWN_FORM_ITEM -> FormItem(
                    FormItemType.DROP_DOWN_FORM_ITEM,
                    gson.fromJson(it.formItem, DropDownFieldInfo::class.java)
                )

                o.credits.model.process_flow.FormItemType.DATE_PICKER_FORM_ITEM -> FormItem(
                    FormItemType.DATE_PICKER_FORM_ITEM,
                    gson.fromJson(it.formItem, DatePickerFieldInfo::class.java)
                )

                o.credits.model.process_flow.FormItemType.LABEL -> FormItem(
                    FormItemType.LABEL,
                    gson.fromJson(it.formItem, LabelFormItem::class.java)
                )
                else -> null
            }
        }
        return InputForm(formJson.formId, formJson.title, formItem)
    }
}