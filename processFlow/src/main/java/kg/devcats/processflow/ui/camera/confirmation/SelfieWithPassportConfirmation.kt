package kg.devcats.processflow.ui.camera.confirmation

import android.widget.ImageView
import androidx.core.os.bundleOf
import kg.devcats.processflow.R

class SelfieWithPassportConfirmation : PhotoConfirmationFragment() {

    override fun setupViews() {
        super.setupViews()
        vb.tvConfirmTitle.setText(R.string.process_flow_photo_confirmation_selfile_passport)
    }

    companion object {
        fun create(filePath: String?, scaleType: ImageView.ScaleType? = null): PhotoConfirmationFragment {
            return SelfieWithPassportConfirmation().apply {
                arguments = bundleOf(
                    ARG_FILE_PATH to filePath,
                    ARG_FILE_SCALE_TYPE to scaleType
                )
            }
        }
    }
}