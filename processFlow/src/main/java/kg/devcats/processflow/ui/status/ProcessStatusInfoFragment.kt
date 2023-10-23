package kg.devcats.processflow.ui.status

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentStatusInfoBinding
import kg.devcats.processflow.model.common.ScreenState
import kg.devcats.processflow.model.common.StateScreenStatus
import kg.devcats.processflow.util.AnimationData
import kg.devcats.processflow.util.LottieAnimationHandler

open class ProcessStatusInfoFragment : BaseProcessScreenFragment<ProcessFlowFragmentStatusInfoBinding>() {

    protected var lottieAnimationHandler: LottieAnimationHandler? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createLottieHandler()
        super.onViewCreated(view, savedInstanceState)
    }

    override val unclickableMask: View?
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llButtons

    override fun renderScreenState(state: ScreenState?) {
        super.renderScreenState(state)
        state?.run {
            vb.tvTitle.text = title ?: ""
            vb.tvSubtitle.text = description ?: ""
            status?.let { setupLottieAnimationByStatus(it) }
        }
    }

    override fun inflateViewBinging() = ProcessFlowFragmentStatusInfoBinding.inflate(layoutInflater)

    override fun onDestroyView() {
        lottieAnimationHandler = null
        super.onDestroyView()
    }

    private fun createLottieHandler() {
        lottieAnimationHandler = LottieAnimationHandler(vb.lavStatus)
    }

    private fun setupLottieAnimationByStatus(stateScreenStatus: StateScreenStatus) {
        val animationData = when (stateScreenStatus) {
            StateScreenStatus.IN_PROCESS -> AnimationData(
                R.raw.process_flow_lottie_anim_loop,
                isInfiniteRepeat = true
            )

            StateScreenStatus.COMPLETE -> AnimationData(R.raw.process_flow_lottie_anim_done)
            StateScreenStatus.REJECTED -> AnimationData(R.raw.process_flow_lottie_anim_reject)
        }
        lottieAnimationHandler?.addToAnimationQueue(animationData)
    }
}
