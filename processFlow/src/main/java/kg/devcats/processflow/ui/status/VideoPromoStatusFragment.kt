package kg.devcats.processflow.ui.status

import kg.devcats.processflow.R
import kg.devcats.processflow.util.AnimationData

class VideoPromoStatusFragment : ProcessStatusInfoFragment() {

    override fun setupViews() {
        lottieAnimationHandler?.addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_video))
    }

}