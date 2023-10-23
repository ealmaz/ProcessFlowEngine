package kg.devcats.processflow.input_form

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.design2.chili2.view.input.MaskedInputView
import com.design2.chili2.view.modals.bottom_sheet.serach_bottom_sheet.Option
import com.design2.chili2.view.modals.picker.DatePickerDialog
import core.extension.showMessage
import extensions.hideKeyboard
import kg.o.nurtelecom.annotation.FragmentInjector
import o.credits.R
import o.credits.databinding.FragmentInputFormBinding
import o.credits.di.DaggerCreditsComponent
import o.credits.model.Event
import o.credits.model.process_flow.FlowInputField
import o.credits.new_ui.input_form.InputFormVM
import o.credits.new_ui.input_form.input_form_custom_views.DatePickerInputField
import o.credits.new_ui.input_form.input_form_custom_views.DropDownInputField
import o.credits.new_ui.input_form.input_form_custom_views.InputFormGroupButtons
import o.credits.new_ui.input_form.input_forms_model.DatePickerFieldInfo
import o.credits.new_ui.input_form.input_forms_model.DropDownFieldInfo
import o.credits.new_ui.input_form.input_forms_model.EnteredValue
import o.credits.new_ui.input_form.input_forms_model.FormResponse
import o.credits.new_ui.input_form.input_forms_model.GroupButtonFormItem
import o.credits.new_ui.input_form.input_forms_model.InputForm
import o.credits.new_ui.input_form.input_forms_model.LabelFormItem
import o.credits.new_ui.input_form.item_creators.DatePickerFieldCreator
import o.credits.new_ui.input_form.item_creators.DropDownFieldCreator
import o.credits.new_ui.input_form.item_creators.GroupButtonsCreator
import o.credits.new_ui.input_form.item_creators.InputFieldCreator
import o.credits.new_ui.input_form.item_creators.LabelFormItemCreator
import o.credits.new_ui.process_flow.BaseProcessFlowFragment
import o.credits.new_ui.process_flow.ProcessFlow
import o.credits.new_ui.process_flow.ProcessFlowCommit
import o.credits.new_ui.process_flow.ProcessFlowScreenData
import java.util.Calendar
import javax.inject.Inject

@FragmentInjector
class InputFormFragment : BaseProcessFlowFragment<FragmentInputFormBinding>(), FragmentResultListener {

    @Inject
    lateinit var vm: InputFormVM

    private val buttonTextRes = R.string.next

    private val scrollOffset16px: Int by lazy { resources.getDimensionPixelSize(R.dimen.padding_75dp) }

    private var currentOpenedDatePickerId: String? = null

    override val unclickableMask: View
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = null

    private val result = HashMap<String, List<String>?>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(DatePickerDialog.PICKER_DIALOG_RESULT, this, this)
        subscribeToLiveData()
    }

    override fun performAndroidInjection() {
        DaggerCreditsComponent.builder().inject(this)
    }

    override fun inflateViewBinging(): FragmentInputFormBinding {
        return FragmentInputFormBinding.inflate(layoutInflater)
    }

    override fun setupViews(): Unit = with(vb) {
        btnDone.apply {
            setOnClickListener { setFragmentResultAndClose() }
            setText(buttonTextRes)
        }
    }

    private fun subscribeToLiveData() {
        vm.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is Event.AdditionalOptionsFetched -> {
                    setOptionsForDropDownField(event.formId, event.options)
                    vb.progressBar.isVisible = false
                }
                is Event.AdditionalOptionsFetching -> {
                    vb.progressBar.isVisible = true
                }
            }
        }
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        data?.allowedAnswer?.filterIsInstance<InputForm>()?.firstOrNull()?.let {
            setupInputForm(it)
        }
    }

    private fun setupInputForm(inputForm: InputForm) {
        val container = LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }
        inputForm.formItems?.forEach {
            val view = when (it.formItem) {
                is FlowInputField -> createInputField(it.formItem).apply {
                    changeInputPositionToStart()
                }
                is GroupButtonFormItem -> createButtonGroup(it.formItem)
                is DropDownFieldInfo -> createDropDownField(it.formItem)
                is DatePickerFieldInfo -> createDatePickerField(it.formItem)
                is LabelFormItem -> createLabelFormItem(it.formItem)
                else -> null
            }
            view?.let { container.addView(it) }
        }
        vb.tvTitle.apply {
            text = inputForm.title
            isVisible = inputForm.title != null
        }
        vb.formContainer.removeAllViews()
        vb.formContainer.addView(container)
    }

    private fun createInputField(inputField: FlowInputField): MaskedInputView {
        result[inputField.fieldId] = null
        return InputFieldCreator.create(requireContext(), inputField, { values, isValid ->
            result[inputField.fieldId] = if (isValid) values else null
        }).apply {
            setupOnGetFocusAction { vb.scroll.smoothScrollTo(0, (this.bottom + scrollOffset16px)) }
        }
    }


    private fun createButtonGroup(groupInfo: GroupButtonFormItem): InputFormGroupButtons {
        result[groupInfo.fieldId] = null
        return GroupButtonsCreator.create(requireContext(), groupInfo) { values, isValid ->
            result[groupInfo.fieldId] = if (isValid) values else null
        }

    }

    private fun createLabelFormItem(labelFormItem: LabelFormItem): TextView {
        return  LabelFormItemCreator.create(requireContext(), labelFormItem)
    }

    private fun createDropDownField(dropDownList: DropDownFieldInfo): View {
        result[dropDownList.fieldId] = null
        if (dropDownList.isNeedToFetchOptions == true) {
            needToFetchOptionsFor(dropDownList.fieldId, dropDownList.parentFieldId)
        }
        return DropDownFieldCreator.create(requireContext(), dropDownList) { values, isValid ->
            result[dropDownList.fieldId] = if (isValid) values else null
            onDropDownListItemSelectionChanged(dropDownList.fieldId, values)
        }
    }

    private fun createDatePickerField(datePickerFieldInfo: DatePickerFieldInfo): View {
        result[datePickerFieldInfo.fieldId] = null
        return DatePickerFieldCreator.create(requireContext(), datePickerFieldInfo) { values, isValid ->
            result[datePickerFieldInfo.fieldId] = if (isValid) values else null
        }.apply {
            setOnClickListener {
                currentOpenedDatePickerId = datePickerFieldInfo.fieldId
                DatePickerDialog.create(
                    getString(R.string.next),
                    datePickerFieldInfo.label ?: "",
                    startLimitDate = datePickerFieldInfo.startDateLimit?.let { Calendar.getInstance().apply { timeInMillis = it } },
                    endLimitDate = datePickerFieldInfo.endDateLimit?.let { Calendar.getInstance().apply { timeInMillis = it } }
                ).show(childFragmentManager, null)
            }
        }
    }

    private fun setFragmentResultAndClose() {
        if (!validateInput()) return
        requireContext().hideKeyboard()
        (requireActivity() as ProcessFlow).commit(ProcessFlowCommit.OnInputFormFilled(collectResult()))
    }

    override fun setIsLoading(isLoading: Boolean): Boolean {
        vb.unclickableMask.isVisible = isLoading
        vb.btnDone.setIsLoading(isLoading)
        return true
    }

    private fun collectResult(): FormResponse {
        val resultValues = mutableListOf<EnteredValue>()
        result.forEach {
            resultValues.add(EnteredValue(it.key, it.value))
        }
        return FormResponse(resultValues)
    }

    private fun validateInput(): Boolean {
        result.forEach {
            if (it.value == null) {
                try {
                    vb.formContainer
                        .findViewWithTag<MaskedInputView>(it.key)
                        .setupFieldAsError(R.string.invalid_input)
                } catch (ex: Throwable) {
                    requireContext().showMessage(R.string.invalid_input)
                }
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().hideKeyboard()
    }

    private fun onDropDownListItemSelectionChanged(dropDownId: String, selectedItemId: List<String>) {
        vm.optionsRelations[dropDownId]?.forEach {
            if (selectedItemId.isEmpty()) {
                setOptionsForDropDownField(it, listOf())
            } else {
                vm.fetchOptions(it, selectedItemId.first())
            }
        }

    }

    private fun needToFetchOptionsFor(formId: String, parentId: String?) {
        if (parentId == null) {
            vm.fetchOptions(formId)
        } else {
            if (vm.optionsRelations[parentId] == null) {
                vm.optionsRelations[parentId] = mutableListOf(formId)
            } else {
                vm.optionsRelations[parentId]?.add(formId)
            }
        }

    }

    private fun setOptionsForDropDownField(fieldId: String, newOptions: List<Option>) {
        vb.root.findViewWithTag<DropDownInputField>(fieldId)?.options = newOptions
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            DatePickerDialog.PICKER_DIALOG_RESULT -> {
                val calendar = result.getSerializable(DatePickerDialog.ARG_SELECTED_DATE) as Calendar
                currentOpenedDatePickerId?.let {
                    vb.formContainer.findViewWithTag<DatePickerInputField>(it).setDate(calendar.timeInMillis)
                }
            }
        }
    }

    companion object {

        const val INPUT_FORM_RESULT = "input_form_result"
        const val INPUT_FORM_ARGUMENT = "inputForm"

        fun show(fragmentManager: FragmentManager, containerId: Int, inputForm: InputForm, lifecycleOwner: LifecycleOwner, fragmentResultListener: FragmentResultListener) {
            fragmentManager.setFragmentResultListener(INPUT_FORM_RESULT, lifecycleOwner, fragmentResultListener)
            val fragment = InputFormFragment().apply { arguments = bundleOf(
                INPUT_FORM_ARGUMENT to inputForm
            )
            }
            fragmentManager.commit {
                add(containerId, fragment)
                addToBackStack(null)
            }
        }
    }
}