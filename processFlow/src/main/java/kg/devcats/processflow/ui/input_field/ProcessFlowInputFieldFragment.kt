package kg.devcats.processflow.ui.input_field

import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.input.MaskedInputView
import com.google.android.gms.auth.api.phone.SmsRetriever
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentInputFieldBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.getThemeColor
import kg.devcats.processflow.extension.hideKeyboard
import kg.devcats.processflow.extension.toTimeFromMillis
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.common.ScreenState
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.model.component.FlowInputField
import kg.devcats.processflow.model.component.FlowRetryInfo
import kg.devcats.processflow.util.SmsBroadcastReceiver

class ProcessFlowInputFieldFragment :
    BaseProcessScreenFragment<ProcessFlowFragmentInputFieldBinding>(), SmsBroadcastReceiver.Listener {

    private var countDownTimer: CountDownTimer? = null

    private var receiver: SmsBroadcastReceiver? = null

    private var isConfirmClicked = false

    override val inputFieldContainer: FrameLayout?
        get() = vb.inputContainer

    override val unclickableMask: View?
        get() = vb.unclickableMask

    private var resultData: Pair<String, MutableList<Content>>? = null

    override fun inflateViewBinging() =
        ProcessFlowFragmentInputFieldBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.btnConfirm.setOnSingleClickListener {
            requireActivity().hideKeyboard()
            resultData?.first?.let {
                isConfirmClicked = true
                getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(it), resultData?.second))
                resultData = null
            }
        }
    }

    override fun renderScreenState(state: ScreenState?) {
        super.renderScreenState(state)
        state?.description?.let { vb.tvDescription.text = it }
    }

    override fun renderInputField(
        inputFieldContainer: FrameLayout,
        inputFieldInfo: FlowInputField
    ): BaseInputView {
        inputFieldInfo?.otpLength?.let { initSmsRetrieverApi(it) }
        resultData = inputFieldInfo.fieldId to mutableListOf()
        val inputView = super.renderInputField(inputFieldContainer, inputFieldInfo.copy(label = null))
        inputFieldInfo.enableActionAfterMills?.let {
            setTimer(it, inputView, inputFieldInfo.additionalActionResolutionCode ?: "")
        }
        return inputView
    }

    private fun initSmsRetrieverApi(otpLength: Int) {
        receiver = SmsBroadcastReceiver(otpLength)
        val client = SmsRetriever.getClient(requireContext())
        val retriever = client.startSmsRetriever()
        retriever.addOnSuccessListener {
            receiver?.setListener(this)
            context?.registerReceiver(receiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
    }

    private fun setTimer(timeOut: Long, inputField: BaseInputView, actionId: String) {
        countDownTimer = object : CountDownTimer(timeOut, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    inputField.setActionWithColor(
                        getString(R.string.process_flow_repeat_after, millisUntilFinished.toTimeFromMillis),
                        requireContext().getThemeColor(com.design2.chili2.R.attr.ChiliValueTextColor))
                } catch (_: Throwable) {}
            }

            override fun onFinish() {
                try {
                    inputField.setActionWithColor(
                        getString(R.string.process_flow_resend),
                        requireContext().getThemeColor(com.design2.chili2.R.attr.ChiliComponentButtonTextColorActive)
                    ) {
                        getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(actionId)))
                    }
                } catch (_: Throwable) {}
            }
        }.start()
    }

    override fun inputFieldChanged(result: List<String>, isValid: Boolean) {
        vb.btnConfirm.isEnabled = isValid
        if (isValid) {
            resultData?.second?.apply {
                clear()
                add(Content(result.firstOrNull() ?: "", ContentTypes.INPUT_FIELD_CONTENT))
            }
        }
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        if (!isConfirmClicked) return false
        vb.btnConfirm.setIsLoading(isLoading)
        unclickableMask?.isVisible = isLoading
        if (!isLoading) isConfirmClicked = false
        return true
    }

    override fun onHandleRetry(retry: FlowRetryInfo?) {
        super.onHandleRetry(retry)
        unclickableMask?.isVisible = retry != null
        vb.btnConfirm.setIsLoading(retry != null)
    }

    override fun onDestroyView() {
        try { context?.unregisterReceiver(receiver) }
        catch (_: Exception) {}
        super.onDestroyView()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onSmsReceived(code: String) {
        try {
            vb.inputContainer.findViewWithTag<MaskedInputView>(resultData!!.first)!!.setText(code)
        } catch (ex: Throwable) {}
    }
}