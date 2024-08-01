package kg.devcats.processflow.ui.status

import kg.devcats.processflow.R
import kg.devcats.processflow.extension.visible
import kg.devcats.processflow.model.common.StateScreenStatus
import kg.devcats.processflow.util.AnimationData

class VideoPromoStatusFragment : ProcessStatusInfoFragment() {

    override fun setupStatusIcon(stateScreenStatus: StateScreenStatus?, statusImageUrl: String?) {
        getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_video))
        vb.lavStatus.visible()
    }
}