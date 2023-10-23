package o.credits.new_ui.input_form

import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import core.base.BaseVM
import o.credits.model.Event
import o.credits.repo.CreditProcessFlowRepository
import javax.inject.Inject

class InputFormVM @Inject constructor(
    private val _chatRepository: CreditProcessFlowRepository
) : BaseVM<Event>() {

    val optionsRelations = HashMap<String, MutableList<String>>()

    fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        disposable.add(_chatRepository.fetchOptions(formId, parentSelectedOptionId)
            .doOnSubscribe { triggerEvent(Event.AdditionalOptionsFetching) }
            .subscribe({
                val options = it.map { Option(it.id, it.label ?: "", it.isSelected ?: false) }
                triggerEvent(Event.AdditionalOptionsFetched(formId, options))
            }, {}))
    }

}
