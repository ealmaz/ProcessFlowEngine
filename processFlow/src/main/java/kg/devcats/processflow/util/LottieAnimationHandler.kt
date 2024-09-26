package kg.devcats.processflow.util

import android.animation.Animator
import androidx.annotation.RawRes
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable.INFINITE
import java.util.LinkedList

class LottieAnimationHandler(private val animationView: LottieAnimationView) {

    private var isAnimating = false
    private val animationQueue = LinkedList<AnimationData>()
    private var lastAddedAnimation: AnimationData? = null

    init {
        animationView.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
                startNextAnimation()
            }
            override fun onAnimationRepeat(animation: Animator) { startNextAnimation(true) }
        })
    }

    fun addToAnimationQueue(animation: AnimationData) {
        if (animation != lastAddedAnimation) {
            animationQueue.add(animation)
            lastAddedAnimation = animation
            setAnimationSpeed()
        }
        if (!isAnimating) startNextAnimation()
    }

    private fun setAnimationSpeed(isRepeated: Boolean = false) {
        val animationSpeed = if (animationQueue.isEmpty()) {
            if(isAnimating && !isRepeated) INCREASED_ANIMATION_SPEED else DEFAULT_ANIMATION_SPEED
        }
        else animationQueue.size * INCREASED_ANIMATION_SPEED
        animationView.speed = animationSpeed
    }

    private fun startNextAnimation(isRepeated: Boolean = false) {
        val animData = animationQueue.pollFirst() ?: return
        setAnimationSpeed(isRepeated)
        isAnimating = true
        val repeatCount = if (animData.isInfiniteRepeat) INFINITE else 0

        when {
            !animData.animationUrl.isNullOrBlank() -> setLottieAnimationUrl(animData.animationUrl, repeatCount)
            !animData.animationJson.isNullOrBlank() -> setLottieAnimation(animData.animationJson, repeatCount)
            animData.animationRes != null -> setLottieAnimation(animData.animationRes, repeatCount)
        }
    }

    private fun setLottieAnimation(@RawRes lottieRes: Int, mRepeatCount: Int) {
        animationView.apply {
            LottieCompositionFactory.fromRawResSync(animationView.context, lottieRes)?.let { result ->
                result.value?.let { setComposition(it) }
                repeatCount = mRepeatCount
                playAnimation()
            }
        }
    }

    private fun setLottieAnimation(lottieString: String, mRepeatCount: Int) {
        animationView.apply {
            LottieCompositionFactory.fromJsonStringSync(lottieString, null)?.let { result ->
                result.value?.let { setComposition(it) }
                repeatCount = mRepeatCount
                playAnimation()
            }
        }
    }

    private fun setLottieAnimationUrl(jsonUrl: String, mRepeatCount: Int) {
        animationView.apply {
            LottieCompositionFactory
                .fromUrl(context, jsonUrl, null)
                .addListener { composition ->
                    setComposition(composition)
                    repeatCount = mRepeatCount
                    playAnimation()
                }
                .addFailureListener { }
        }
    }

    fun removeListeners() {
        animationView.removeAllAnimatorListeners()
    }

    companion object {
        const val DEFAULT_ANIMATION_SPEED = 1.0f
        const val INCREASED_ANIMATION_SPEED = 6.0f
    }
}

data class AnimationData(
    val animationRes: Int? = null,
    val animationJson: String? = null,
    val animationUrl: String? = null,
    val isInfiniteRepeat: Boolean = false
)