package kg.devcats.processflow.ui.camera.confirmation

import android.widget.ImageView
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.design2.chili2.util.GlideBitmapMirrorTransformation
import com.design2.chili2.util.GlideBitmapScaleTransformation
import kg.devcats.processflow.R
import java.io.File

class SelfiePhotoConfirmation : PhotoConfirmationFragment() {

    override fun setupViews() {
        super.setupViews()
        vb.tvConfirmTitle.setText(R.string.process_flow_photo_confirmation_simple_selfie)
    }

    override fun loadImage(filePath: String) {
        Glide.with(requireContext())
            .load(File(filePath))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transform(GlideBitmapScaleTransformation(), GlideBitmapMirrorTransformation())
            .override(Target.SIZE_ORIGINAL)
            .into(vb.ivCapturedPhoto)
    }

    companion object {
        fun create(filePath: String?, scaleType: ImageView.ScaleType? = null): PhotoConfirmationFragment {
            return SelfiePhotoConfirmation().apply {
                arguments = bundleOf(
                    ARG_FILE_PATH to filePath,
                    ARG_FILE_SCALE_TYPE to scaleType
                )
            }
        }
    }
}