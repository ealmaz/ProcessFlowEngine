package kg.devcats.processflow.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.databinding.ProcessFlowFragmentPhotoFlowBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.positiveButton
import kg.devcats.processflow.extension.showDialog
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.ui.camera.instruction.BasePhotoInstructionFragment
import kg.devcats.processflow.ui.camera.instruction.photo.PassportBackInstructionFragment
import kg.devcats.processflow.ui.camera.instruction.photo.PassportFrontInstructionFragment
import kg.devcats.processflow.ui.camera.instruction.photo.SelfiePhotoInstructionFragment
import kg.nurtelecom.text_recognizer.RecognizedMrz
import kg.nurtelecom.text_recognizer.photo_capture.OverlayType
import kg.nurtelecom.text_recognizer.photo_capture.PhotoRecognizerActivity
import kg.nurtelecom.text_recognizer.photo_capture.RecognizePhotoContract
import kg.nurtelecom.text_recognizer.photo_capture.ScreenLabels
import kg.nurtelecom.text_recognizer.photo_capture.TextRecognizerConfig

class PhotoFlowFragment : BaseProcessScreenFragment<ProcessFlowFragmentPhotoFlowBinding>() {

    private val cameraType by lazy { (arguments?.getSerializable(ARG_CAMERA_TYPE) as? CameraType) ?: CameraType.SELFIE }
    private val responseId by lazy { (arguments?.getSerializable(ARG_RESPONSE_ID) as? String) ?: "" }

    private var needRecognition = true
    private var recognizedMrz: RecognizedMrz? = null

    override val unclickableMask: View?
        get() = vb.unclickableMask

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.any { !it.value }) onPermissionDeny()
        else openPhotoCapture()
    }

    private val openSettingResult  = registerForActivityResult(object: ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
        }
        override fun parseResult(resultCode: Int, intent: Intent?) {}
    }) { checkPermission() }

    private fun checkPermission() {
        requestPermissionLauncher.launch(if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        })
    }

    private val textRecognizerContract = registerForActivityResult(RecognizePhotoContract()) {
        if (it == null) return@registerForActivityResult
        val filePath = it.getParcelableExtra<Uri>(PhotoRecognizerActivity.EXTRA_PHOTO_URI)?.path ?: ""
        if (needRecognition) recognizedMrz = it.getSerializableExtra(PhotoRecognizerActivity.EXTRA_MRZ_STRING) as? RecognizedMrz
        onPhotoCaptured(filePath)
    }

    private fun openRecognizer() {
        textRecognizerContract.launch(TextRecognizerConfig(
            false,
            ProcessFlowConfigurator.recognizerTimeoutLimit,
            ProcessFlowConfigurator.recognizerTimeoutMills,
            getString(R.string.process_flow_photo_recognizer_timeout_description),
            false,
            photoCaptureLabels = ScreenLabels(getString(R.string.process_flow_photo_capture_passport_back_title), description = getString(R.string.process_flow_photo_capture_passport_back_description)),
            recognitionLabels = ScreenLabels(getString(R.string.process_flow_photo_capture_passport_back_title), description = getString(R.string.process_flow_photo_capture_passport_back_description)),
            overlayType = OverlayType.PASSPORT_OVERLAY,
            hasCustomPhotoConfirmation = true,
            needRecognition = recognizedMrz == null)
        )
    }

    fun startPhotoFlow(isRetakingPhoto: Boolean = false) {
        needRecognition = (cameraType == CameraType.BACK_PASSPORT_WITH_RECOGNIZER && !isRetakingPhoto)
        checkPermission()
    }


    override fun inflateViewBinging() = ProcessFlowFragmentPhotoFlowBinding.inflate(layoutInflater)

    override fun setupViews() {
        super.setupViews()
        openPhotoInstruction()
    }

    private fun openPhotoInstruction() {
        val fragment = when (cameraType) {
            CameraType.FRONT_PASSPORT -> PassportFrontInstructionFragment()
            CameraType.BACK_PASSPORT_WITH_RECOGNIZER -> PassportBackInstructionFragment()
            CameraType.SELFIE -> SelfiePhotoInstructionFragment()
        }
        childFragmentManager.commit {
            replace(R.id.container, fragment)
        }
    }

    fun onPhotoCaptured(filePath: String?) {
        childFragmentManager.commit {
            replace(R.id.container, PhotoConfirmationFragment.create(filePath), PHOTO_CONFIRM_FRAGMENT_TAG)
            addToBackStack(null)
        }
    }

    private fun onPermissionDeny() {
        showDialog {
            setMessage(R.string.process_flow_permission_denied)
            positiveButton(android.R.string.ok) {
                openSettingResult.launch(Unit)
            }
        }
    }

    fun onPhotoConfirmed(filePath: String) {
        getProcessFlowHolder().commit(ProcessFlowCommit.OnFlowPhotoCaptured(responseId, filePath, getFileType(), recognizedMrz))
    }

    private fun getFileType(): String {
        return when (cameraType) {
            CameraType.FRONT_PASSPORT -> ContentTypes.PASSPORT_FRONT_PHOTO
            CameraType.BACK_PASSPORT_WITH_RECOGNIZER -> ContentTypes.PASSPORT_BACK_PHOTO
            CameraType.SELFIE -> ContentTypes.SELFIE_PHOTO
        }
    }

    private fun openPhotoCapture() {
        if (cameraType == CameraType.BACK_PASSPORT_WITH_RECOGNIZER) {
            openRecognizer()
            return
        }
        childFragmentManager.commit {
            replace(R.id.container, PhotoCaptureFragment.create(getCameraSettings()))
            addToBackStack(null)
        }
    }


    private fun getCameraSettings(): CameraSettings {
        return when (cameraType) {
            CameraType.SELFIE -> CameraSettings(lensFacing = LENS_FACING_FRONT, cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME)
            else -> CameraSettings(headerText = getString(R.string.process_flow_photo_capture_passport_front_description), description = getString(R.string.process_flow_photo_capture_passport_front_title))
        }
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        (childFragmentManager.findFragmentByTag(PHOTO_CONFIRM_FRAGMENT_TAG) as? PhotoConfirmationFragment)?.let {
            it.setIsLoading(isLoading)
            return true
        }
        return false
    }

    override fun handleBackPress(): BackPressHandleState {
        return when(childFragmentManager.findFragmentById(R.id.container)) {
            is BasePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is SelfiePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is PhotoConfirmationFragment -> BackPressHandleState.NOT_HANDLE
            else -> {
                childFragmentManager.popBackStack()
                BackPressHandleState.HANDLED
            }
        }
    }

    companion object {
        const val ARG_CAMERA_TYPE = "ARG_CAMERA_TYPE"
        const val ARG_RESPONSE_ID = "ARG_RESPONSE_ID"

        const val PHOTO_CONFIRM_FRAGMENT_TAG = "PHOTO_CONFIRM_FRAGMENT_TAG"

        fun create(cameraType: CameraType, responseId: String): PhotoFlowFragment {
            return PhotoFlowFragment().apply {
                arguments = bundleOf(
                    ARG_CAMERA_TYPE to cameraType,
                    ARG_RESPONSE_ID to responseId
                )
            }
        }
    }
}

enum class CameraType {
    FRONT_PASSPORT, BACK_PASSPORT_WITH_RECOGNIZER, SELFIE
}