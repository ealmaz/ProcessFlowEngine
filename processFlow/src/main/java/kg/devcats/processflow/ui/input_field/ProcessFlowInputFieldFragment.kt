package kg.devcats.processflow.ui.input_field

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.input.MaskedInputView
import com.design2.chili2.view.input.otp.OtpInputView
import com.google.android.gms.auth.api.phone.SmsRetriever
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentInputFieldBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.getThemeColor
import kg.devcats.processflow.extension.handleUrlClicks
import kg.devcats.processflow.extension.hideKeyboard
import kg.devcats.processflow.extension.toTimeFromMillis
import kg.devcats.processflow.extension.visible
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
        state?.run {
            title?.let { vb.tvTitle.apply {
                text = it
                visible()
            }}
            description?.let { vb.tvDescription.text = it }
            bottomDescriptionHtml?.let { vb.tvBottomDescription.apply {
                text = it.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
                visible()
                handleUrlClicks {
                    vb.tvBottomDescription.invalidate()
                    onLinkClick(it)
                }
            } }
        }
    }

    override fun renderOtpInputView(
        inputFieldContainer: FrameLayout,
        inputFieldInfo: FlowInputField
    ): OtpInputView {
        inputFieldInfo?.otpLength?.let { initSmsRetrieverApi(it) }
        resultData = inputFieldInfo.fieldId to mutableListOf()
        val inputView = super.renderOtpInputView(inputFieldContainer, inputFieldInfo.copy(label = null))
        inputFieldInfo.enableActionAfterMills?.let {
            setTimerForOtp(it, inputView, inputFieldInfo.additionalActionResolutionCode ?: "")
        }
        return inputView
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
        val client = smsRetrieverClient ?: SmsRetriever.getClient(requireContext())
        val retriever = client.startSmsRetriever()
        retriever.addOnSuccessListener {
            receiver?.setListener(this)
            ContextCompat.registerReceiver(requireContext(), receiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), ContextCompat.RECEIVER_EXPORTED)
        }
    }

    private fun setTimer(timeOut: Long, inputField: BaseInputView, actionId: String) {
        setupMillsTimerFor(timeOut, {
            try {
                inputField.setActionWithColor(
                    getString(R.string.process_flow_resend),
                    requireContext().getThemeColor(com.design2.chili2.R.attr.ChiliComponentButtonTextColorActive)
                ) {
                    getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(actionId)))
                }
            } catch (_: Throwable) {}
        }, {
            try {
                inputField.setActionWithColor(
                    getString(R.string.process_flow_repeat_after, it.toTimeFromMillis),
                    requireContext().getThemeColor(com.design2.chili2.R.attr.ChiliValueTextColor))
            } catch (_: Throwable) {}
        })
    }

    private fun setTimerForOtp(timeOut: Long, inputField: OtpInputView, actionId: String) {
        setupMillsTimerFor(timeOut, {
            try {
                inputField.apply {
                    setActionTextEnabled(true)
                    setActionText(R.string.process_flow_resend)
                    setOnActionClickListener {
                        getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(actionId)))
                    }
                }
            } catch (_: Throwable) {}
        }, {
            try {
                inputField.apply {
                    setActionTextEnabled(false)
                    setActionText(getString(R.string.process_flow_repeat_after, it.toTimeFromMillis))
                }
            } catch (_: Throwable) {}
        })
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
    }

    override fun onSmsReceived(code: String) {
        try {
            vb.inputContainer.findViewWithTag<MaskedInputView>(resultData!!.first)!!.setText(code)
        } catch (_: Throwable) {}
    }
}