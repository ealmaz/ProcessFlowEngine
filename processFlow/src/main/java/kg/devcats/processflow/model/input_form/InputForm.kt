package kg.devcats.processflow.model.input_form

import kg.devcats.processflow.model.input_form.FormItem
import java.io.Serializable

data class InputForm(
    val formId: String,
    val title: String? = null,
    val formItems: List<FormItem>? = null
): Serializable
