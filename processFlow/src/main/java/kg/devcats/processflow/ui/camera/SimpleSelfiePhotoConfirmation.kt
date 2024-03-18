package kg.devcats.processflow.ui.camera

import android.widget.ImageView
import androidx.core.os.bundleOf
import kg.devcats.processflow.extension.gone

class SimpleSelfiePhotoConfirmation : PhotoConfirmationFragment() {

    override fun setupViews() {
        super.setupViews()
        vb.tvConfirmTitle.gone()
    }

    companion object {
        fun create(filePath: String?, scaleType: ImageView.ScaleType? = null): PhotoConfirmationFragment {
            return SimpleSelfiePhotoConfirmation().apply {
                arguments = bundleOf(
                    ARG_FILE_PATH to filePath,
                    ARG_FILE_SCALE_TYPE to scaleType
                )
            }
        }
    }
}