package kg.devcats.processflow.network.parser

import com.google.gson.Gson
import kg.devcats.processflow.model.component.FlowFormInfoJson
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.input_form.DatePickerFieldInfo
import kg.devcats.processflow.model.input_form.DropDownFieldInfo
import kg.devcats.processflow.model.input_form.FormItem
import kg.devcats.processflow.model.input_form.FormItemType
import kg.devcats.processflow.model.input_form.GroupButtonFormItem
import kg.devcats.processflow.model.input_form.InputForm
import kg.devcats.processflow.model.input_form.LabelFormItem
import kg.devcats.processflow.model.input_form.PairFieldItem

open class InputFormMapper {

    private val gson = Gson()

    open fun map(formJson: FlowFormInfoJson): InputForm {

        val formItem = formJson.formItems.mapNotNull {
            when (it.formItemType) {
                kg.devcats.processflow.model.component.FormItemType.INPUT_FIELD -> FormItem(
                    FormItemType.INPUT_FIELD,
                    gson.fromJson(it.formItem, FlowInputField::class.java)
                )

                kg.devcats.processflow.model.component.FormItemType.GROUP_BUTTON_FORM_ITEM -> FormItem(
                    FormItemType.GROUP_BUTTON_FORM_ITEM,
                    gson.fromJson(it.formItem, GroupButtonFormItem::class.java)
                )

                kg.devcats.processflow.model.component.FormItemType.DROP_DOWN_FORM_ITEM -> FormItem(
                    FormItemType.DROP_DOWN_FORM_ITEM,
                    gson.fromJson(it.formItem, DropDownFieldInfo::class.java)
                )

                kg.devcats.processflow.model.component.FormItemType.DATE_PICKER_FORM_ITEM -> FormItem(
                    FormItemType.DATE_PICKER_FORM_ITEM,
                    gson.fromJson(it.formItem, DatePickerFieldInfo::class.java)
                )

                kg.devcats.processflow.model.component.FormItemType.LABEL -> FormItem(
                    FormItemType.LABEL,
                    gson.fromJson(it.formItem, LabelFormItem::class.java)
                )

                kg.devcats.processflow.model.component.FormItemType.PAIR_FIELD -> FormItem(
                    FormItemType.PAIR_FIELD,
                    gson.fromJson(it.formItem, PairFieldItem::class.java)
                )
                else -> null
            }
        }
        return InputForm(formJson.formId, formJson.title, formItem)
    }
}