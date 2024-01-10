package kg.devcats.processflow.ui.camera

import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.util.GlideBitmapScaleTransformation
import kg.devcats.processflow.base.BaseFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentPassportCardConfirmationBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import java.io.File

class PhotoConfirmationFragment : BaseFragment<ProcessFlowFragmentPassportCardConfirmationBinding>() {

    private val filePath by lazy { arguments?.getString(ARG_FILE_PATH) ?: "" }
    private val scaleType: ImageView.ScaleType by lazy { (arguments?.getSerializable(ARG_FILE_SCALE_TYPE) as? ImageView.ScaleType) ?: ImageView.ScaleType.CENTER_CROP }

    override fun inflateViewBinging() = ProcessFlowFragmentPassportCardConfirmationBinding.inflate(layoutInflater)

    override fun setupViews() {
        super.setupViews()
        vb.btnRecapture.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow(true)
        }
        vb.btnConfirm.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).onPhotoConfirmed(filePath)
        }
        vb.ivCapturedPhoto.scaleType = scaleType
        loadImage(filePath)
    }

    fun setIsLoading(isLoading: Boolean) {
        vb.unclickableMask.isVisible = isLoading
        vb.btnConfirm.setIsLoading(isLoading)
    }

    private fun loadImage(filePath: String) {
        Glide.with(requireContext())
            .load(File(filePath))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transform(GlideBitmapScaleTransformation())
            .override(Target.SIZE_ORIGINAL)
            .into(vb.ivCapturedPhoto)
    }

    override fun onResume() {
        super.onResume()
        getProcessFlowHolder().setIsToolbarVisible(false)
    }

    override fun onPause() {
        super.onPause()
        getProcessFlowHolder().setIsToolbarVisible(true)
    }



    companion object {

        const val ARG_FILE_PATH = "ARG_FILE_PATH"
        const val ARG_FILE_SCALE_TYPE = "ARG_FILE_SCALE_TYPE"

        fun create(filePath: String?, scaleType: ScaleType? = null): PhotoConfirmationFragment {
            return PhotoConfirmationFragment().apply {
                arguments = bundleOf(
                    ARG_FILE_PATH to filePath,
                    ARG_FILE_SCALE_TYPE to scaleType
                )
            }
        }
    }
}