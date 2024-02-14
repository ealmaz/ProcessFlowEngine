package kg.devcats.processflow.ui.status

import android.view.View
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentStatusInfoBinding
import kg.devcats.processflow.extension.gone
import kg.devcats.processflow.extension.handleUrlClicks
import kg.devcats.processflow.extension.loadImage
import kg.devcats.processflow.extension.toTimeFromMillis
import kg.devcats.processflow.extension.visible
import kg.devcats.processflow.model.common.ScreenState
import kg.devcats.processflow.model.common.StateScreenStatus
import kg.devcats.processflow.util.AnimationData
import kg.devcats.processflow.util.LottieAnimationHandler

open class ProcessStatusInfoFragment : BaseProcessScreenFragment<ProcessFlowFragmentStatusInfoBinding>() {

    protected var lottieAnimationHandler: LottieAnimationHandler? = null

    override val unclickableMask: View?
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llButtons

    override fun renderScreenState(state: ScreenState?) {
        super.renderScreenState(state)
        state?.run {
            vb.tvTitle.text = title ?: ""
            vb.tvTitle.isVisible = title != null
            if (isDescriptionHtml == true) {
                vb.tvSubtitle.text = description?.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
                vb.tvSubtitle.handleUrlClicks {
                    vb.tvSubtitle.invalidate()
                    onLinkClick(it)
                }
            } else vb.tvSubtitle.text = description ?: ""
            vb.tvSubtitle.isVisible = description != null
            setupStatusIcon(status, statusImageUrl)
            setupTimer(state)
        }
    }

    override fun inflateViewBinging() = ProcessFlowFragmentStatusInfoBinding.inflate(layoutInflater)

    override fun onDestroyView() {
        lottieAnimationHandler = null
        super.onDestroyView()
    }

    private fun getOrCreateLottieAnimationHandler(): LottieAnimationHandler {
        return lottieAnimationHandler ?: LottieAnimationHandler(vb.lavStatus).also {
            lottieAnimationHandler = it
        }
    }

    private fun setupStatusIcon(stateScreenStatus: StateScreenStatus?, statusImageUrl: String?) {
        vb.lavStatus.gone()
        vb.ivStatus.gone()
        when {
            statusImageUrl != null -> vb.ivStatus.apply {
                loadImage(statusImageUrl)
                visible()
            }
            stateScreenStatus == StateScreenStatus.IN_PROCESS -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    R.raw.process_flow_lottie_anim_loop,
                    isInfiniteRepeat = true
                ))
                vb.lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.COMPLETE -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_anim_done))
                vb.lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.REJECTED -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_anim_reject))
                vb.lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.WARNING -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_anim_reject))
                vb.lavStatus.visible()
            }
        }
    }

    private fun setupTimer(state: ScreenState?) {
        state?.timer?.let {
            val timerText = state?.timerText ?: ""
            vb.tvTimer.visible()
            setupTimerFor(
                it,
                { vb.tvTimer.gone() },
                { vb.tvTimer.text = "$timerText ${it.toTimeFromMillis}" })
        }
    }
}
