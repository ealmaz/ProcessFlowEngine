package kg.devcats.processflow.ui.camera

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

    override fun inflateViewBinging() = ProcessFlowFragmentPassportCardConfirmationBinding.inflate(layoutInflater)

    override fun setupViews() {
        super.setupViews()
        vb.btnRecapture.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow(true)
        }
        vb.btnConfirm.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).onPhotoConfirmed(filePath)
        }
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

        fun create(filePath: String?): PhotoConfirmationFragment {
            return PhotoConfirmationFragment().apply {
                arguments = bundleOf(ARG_FILE_PATH to filePath)
            }
        }
    }
}