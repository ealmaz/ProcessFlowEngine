package kg.devcats.processflow.ui.status

import android.view.View
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.databinding.ProcessFlowFragmentStatusInfoBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
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

    private var isScreenCloseDisabled: Boolean = false

    override val unclickableMask: View?
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llButtons

    override fun onStart() {
        super.onStart()
        getProcessFlowHolder().setIsNavigationUpEnabled(!isScreenCloseDisabled)
    }

    override fun onStop() {
        super.onStop()
        getProcessFlowHolder().setIsNavigationUpEnabled(true)
    }

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
            setupStatusIcon(status, statusImageUrl, animationUrl)
            setupTimer(state)
            setupScreenClosureAvailability(state.isScreenCloseDisabled ?: false)
        }
    }

    override fun inflateViewBinging() = ProcessFlowFragmentStatusInfoBinding.inflate(layoutInflater)

    override fun onDestroyView() {
        lottieAnimationHandler = null
        super.onDestroyView()
    }

    protected fun getOrCreateLottieAnimationHandler(): LottieAnimationHandler {
        return lottieAnimationHandler ?: LottieAnimationHandler(vb.lavStatus).also {
            lottieAnimationHandler = it
        }
    }

    protected open fun setupStatusIcon(
        stateScreenStatus: StateScreenStatus?,
        statusImageUrl: String?,
        animationUrl: String?
    ): Unit = with(vb) {

        when {
            animationUrl != null -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(
                    AnimationData(
                        animationUrl = animationUrl,
                        isInfiniteRepeat = true
                    )
                )
                lavStatus.visible()
                ivStatus.gone()
            }
            statusImageUrl != null -> ivStatus.apply {
                loadImage(statusImageUrl)
                visible()
                lavStatus.gone()
            }
            stateScreenStatus == StateScreenStatus.IN_PROCESS -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_loop,
                    animationUrl = animationUrl,
                    isInfiniteRepeat = true
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.COMPLETE -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_done,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.REJECTED -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_reject,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.WARNING -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_reject,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
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

    private fun setupScreenClosureAvailability(isScreenCloseDisabled: Boolean) {
        this.isScreenCloseDisabled = isScreenCloseDisabled
        getProcessFlowHolder().setIsNavigationUpEnabled(!isScreenCloseDisabled)
    }

    override fun handleBackPress(): BackPressHandleState {
        return if (isScreenCloseDisabled) BackPressHandleState.HANDLED
        else super.handleBackPress()
    }
}
