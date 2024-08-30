package kg.devcats.processflow.ui.input_form

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.modals.picker.DatePickerDialog
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.custom_view.DatePickerInputField
import kg.devcats.processflow.custom_view.drop_down_input_field.DropDownInputField
import kg.devcats.processflow.custom_view.InputFormGroupButtons
import kg.devcats.processflow.databinding.ProcessFlowFragmentInputFormBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.hideKeyboard
import kg.devcats.processflow.item_creator.DatePickerFieldCreator
import kg.devcats.processflow.item_creator.DropDownFieldCreator
import kg.devcats.processflow.item_creator.GroupButtonsCreator
import kg.devcats.processflow.item_creator.InputFieldCreator
import kg.devcats.processflow.item_creator.LabelFormItemCreator
import kg.devcats.processflow.item_creator.PairFieldItemCreator
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.input_form.DatePickerFieldInfo
import kg.devcats.processflow.model.input_form.DropDownFieldInfo
import kg.devcats.processflow.model.input_form.EnteredValue
import kg.devcats.processflow.model.input_form.FormResponse
import kg.devcats.processflow.model.input_form.GroupButtonFormItem
import kg.devcats.processflow.model.input_form.InputForm
import kg.devcats.processflow.model.input_form.LabelFormItem
import kg.devcats.processflow.model.input_form.Option
import kg.devcats.processflow.model.input_form.PairFieldItem
import java.util.Calendar


class InputFormFragment : BaseProcessScreenFragment<ProcessFlowFragmentInputFormBinding>(), FragmentResultListener {

    private val optionsRelations = HashMap<String, MutableList<String>>()

    private var currentFormId: String = ""

    private val buttonTextRes = R.string.process_flow_next

    private val scrollOffset16px: Int by lazy { resources.getDimensionPixelSize(R.dimen.padding_75dp) }

    private var currentOpenedDatePickerId: String? = null

    override val unclickableMask: View
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llAdditionalButtons

    private val result = HashMap<String, List<String>?>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(DatePickerDialog.PICKER_DIALOG_RESULT, this, this)
    }

    override fun inflateViewBinging(): ProcessFlowFragmentInputFormBinding {
        return ProcessFlowFragmentInputFormBinding.inflate(layoutInflater)
    }

    override fun setupViews(): Unit = with(vb) {
        btnDone.apply {
            setOnClickListener { setFragmentResultAndClose() }
            setText(buttonTextRes)
        }
    }

    fun setAdditionalFetchedOptions(formId: String, options: List<Option>) {
        setOptionsForDropDownField(formId, options)
        vb.progressBar.isVisible = false
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        result.clear()
        data?.allowedAnswer?.filterIsInstance<InputForm>()?.firstOrNull()?.let {
            currentFormId = it.formId
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
                is PairFieldItem -> createPairFieldItem(it.formItem)
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

    private fun createInputField(inputField: FlowInputField): BaseInputView {
        result[inputField.fieldId] = null
        return InputFieldCreator.create(requireContext(), inputField, { values, isValid ->
            result[inputField.fieldId] = if (isValid) values else null
        }).apply {
            setupOnGetFocusAction { vb.scroll.smoothScrollTo(0, (this.bottom + scrollOffset16px)) }
        }
    }


    private fun createButtonGroup(groupInfo: GroupButtonFormItem): InputFormGroupButtons {
        result[groupInfo.fieldId] = null
        return GroupButtonsCreator.create(requireContext(), groupInfo, onLinkClick = ::onLinkClick, onSelectedChanged = { values, isValid ->
            result[groupInfo.fieldId] = if (isValid) values else null
        })

    }

    private fun createLabelFormItem(labelFormItem: LabelFormItem): View {
        return  LabelFormItemCreator.create(requireContext(), labelFormItem)
    }

    private fun createPairFieldItem(pairFieldItem: PairFieldItem): View {
        return  PairFieldItemCreator.create(requireContext(), pairFieldItem)
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
                this.clearError()
                currentOpenedDatePickerId = datePickerFieldInfo.fieldId
                DatePickerDialog.create(
                    getString(R.string.process_flow_next),
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
        getProcessFlowHolder().commit(ProcessFlowCommit.CommitContentFormResponseId(currentFormId, collectResult()))
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        vb.unclickableMask.isVisible = isLoading
        vb.btnDone.setIsLoading(isLoading)
        return true
    }

    private fun collectResult(): List<Content> {
        val resultValues = mutableListOf<EnteredValue>()
        result.forEach {
            resultValues.add(EnteredValue(it.key, it.value))
        }
        return listOf(Content(FormResponse(resultValues), ContentTypes.INPUT_FORM_DATA))
    }

    private fun validateInput(): Boolean {
        var isValid = true
        result.forEach {
            if (it.value == null) {
                isValid = false
                val view = vb.formContainer.findViewWithTag<View>(it.key)
                when (view) {
                    is BaseInputView -> view.setupFieldAsError(R.string.process_flow_invalid_input)
                    is DropDownInputField -> view.setupAsError()
                    is InputFormGroupButtons -> view.setupAsError()
                    is DatePickerInputField -> view.setupAsError()
                    else -> Toast.makeText(requireContext(), R.string.process_flow_invalid_input, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().hideKeyboard()
    }

    private fun onDropDownListItemSelectionChanged(dropDownId: String, selectedItemId: List<String>) {
        optionsRelations[dropDownId]?.forEach {
            if (selectedItemId.isEmpty()) {
                setOptionsForDropDownField(it, listOf())
            } else {
                fetchOptions(it, selectedItemId.first())
            }
        }

    }

    private fun needToFetchOptionsFor(formId: String, parentId: String?) {
        if (parentId == null) {
            fetchOptions(formId)
        } else {
            if (optionsRelations[parentId] == null) {
                optionsRelations[parentId] = mutableListOf(formId)
            } else {
                optionsRelations[parentId]?.add(formId)
            }
        }
    }

    private fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        vb.progressBar.isVisible = true
        getProcessFlowHolder().commit(ProcessFlowCommit.FetchAdditionalOptionsForDropDown(formId, parentSelectedOptionId))
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
}