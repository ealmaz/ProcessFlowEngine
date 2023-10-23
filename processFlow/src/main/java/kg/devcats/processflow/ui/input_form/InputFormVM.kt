package kg.devcats.processflow.ui.input_form

import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.base.BaseVM
import kg.devcats.processflow.model.Event

class InputFormVM : BaseVM() {

    val optionsRelations = HashMap<String, MutableList<String>>()

    fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        disposable.add(ProcessFlowConfigurator.getProcessFlowRepository().fetchOptions(formId, parentSelectedOptionId)
            .doOnSubscribe { triggerEvent(Event.AdditionalOptionsFetching) }
            .subscribe({
                val options = it.map { Option(it.id, it.label ?: "", it.isSelected ?: false) }
                triggerEvent(Event.AdditionalOptionsFetched(formId, options))
            }, {}))
    }
}
