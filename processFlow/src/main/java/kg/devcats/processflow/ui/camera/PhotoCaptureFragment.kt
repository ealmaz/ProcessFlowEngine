package kg.devcats.processflow.ui.camera

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.system.ErrnoException
import android.system.OsConstants
import android.view.MotionEvent
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.camera_overlays.PassportCardOverlay
import com.design2.chili2.view.camera_overlays.RectangleOverlay
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseFragment
import kg.devcats.processflow.databinding.ProcessFlowFragmentPhotoCaptureBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.showWarningDialog
import kg.devcats.processflow.util.PictureUtil
import java.io.File
import java.io.Serializable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PhotoCaptureFragment : BaseFragment<ProcessFlowFragmentPhotoCaptureBinding>() {

    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imageCapture: ImageCapture
    private var camera: Camera? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var contextExecutor: Executor

    private val cameraSetting: CameraSettings by lazy {
        (arguments?.getSerializable(ARG_CAMERA_SETTING) as? CameraSettings) ?: CameraSettings()
    }

    private var passportOverlayView: PassportCardOverlay? = null

    override fun inflateViewBinging() = ProcessFlowFragmentPhotoCaptureBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCamera()
    }

    override fun onStart() {
        super.onStart()
        getProcessFlowHolder().setIsToolbarVisible(false)
    }

    override fun onStop() {
        super.onStop()
        getProcessFlowHolder().setIsToolbarVisible(true)
    }

    private fun setupCamera() {
        vb.flPreview.addView(
            getCameraOverlay(),
            android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        vb.surfacePreview.post { setUpCameraX() }

        vb.btnCapture.setOnSingleClickListener {
            showLoading()
            capturePhoto()
        }
        vb.btnClose.setOnSingleClickListener { requireActivity().onBackPressed() }
    }

    private fun getCameraOverlay(): View {
        return when (cameraSetting.cameraOverlayType) {
            CameraOverlayType.PASSPORT_FRAME -> PassportCardOverlay(requireContext()).apply {
                setHeaderText(cameraSetting.headerText)
                setTitle(cameraSetting.title)
                setDescription(cameraSetting.description)
                passportOverlayView = this
            }
            CameraOverlayType.RECTANGLE_FRAME -> RectangleOverlay(requireContext()).apply {
                cameraSetting.description.takeIf { it.isNotBlank() }?.let { setDescription(it) }
            }
        }
    }

    private fun setUpCameraX() {
        try {
            contextExecutor = ContextCompat.getMainExecutor(requireContext())
            cameraExecutor = Executors.newSingleThreadExecutor()
            val aspectRatio = AspectRatio.RATIO_16_9

            val rotation = vb.surfacePreview.display.rotation

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraSetting.lensFacing)
                .build()

            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener(Runnable {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                preview = Preview.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .setTargetRotation(rotation)
                    .build()


                preview.setSurfaceProvider(vb.surfacePreview.surfaceProvider)

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .setTargetAspectRatio(aspectRatio)
                    .setTargetRotation(rotation)
                    .build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetAspectRatio(aspectRatio)
                    .setTargetRotation(rotation)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer {

                        })
                    }
                cameraProvider.unbindAll()

                try {
                    camera = cameraProvider.bindToLifecycle(
                        this as LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )
                    camera?.cameraControl?.let { setUpTapToFocus(it) }
                } catch (exc: Exception) {
                }

            }, contextExecutor)
        } catch (e: Exception) {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTapToFocus(cameraControl: CameraControl) {
        vb.surfacePreview.setOnTouchListener { _, event ->
            if (event.action != MotionEvent.ACTION_UP) {
                return@setOnTouchListener true
            }

            return@setOnTouchListener try {
                val action = SurfaceOrientedMeteringPointFactory(
                    vb.surfacePreview.width.toFloat(),
                    vb.surfacePreview.height.toFloat()
                )
                    .createPoint(event.x, event.y)
                    .let { FocusMeteringAction.Builder(it, FocusMeteringAction.FLAG_AF).build() }
                cameraControl.startFocusAndMetering(action)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun capturePhoto() {
        var photoFile = PictureUtil.createTemporaryFiles(context, "PHOTO_REGISTRAR", ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            contextExecutor,
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    when (val croppedImage = cropImage(photoFile)) {
                        null -> {
                            hideLoading()
                            context?.showWarningDialog(getString(R.string.process_flow_no_available_free_space))
                        }
                        else -> {
                            photoFile = croppedImage
                            (parentFragment as PhotoFlowFragment).onPhotoCaptured(photoFile.absolutePath)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    hideLoading()
                    val exceptionMessage = when (val cause = exception.cause?.cause) {
                        is ErrnoException -> {
                            if (cause.errno == OsConstants.ENOSPC)
                                getString(R.string.process_flow_no_available_free_space)
                            else
                                exception.message
                        }
                        else -> exception.message
                    }
                    exceptionMessage?.let { context?.showWarningDialog(it) }
                }
            })
    }

    private fun cropImage(original: File): File? {
        return try {
            PictureUtil.compressImage(original.absolutePath, 80, getImageCropRect(), resources.displayMetrics.heightPixels)
        } catch (e: Exception) {
            original
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private fun getImageCropRect(): Rect? {
        return if (cameraSetting.cameraOverlayType == CameraOverlayType.PASSPORT_FRAME) {
            passportOverlayView?.getPassportMaskRectF()?.toRect()
        } else null
    }

    private fun showLoading() {}
    private fun hideLoading() {}

    override fun onDestroyView() {
        super.onDestroyView()
        if (::cameraExecutor.isInitialized) cameraExecutor.shutdown()
    }

    companion object {

        const val ARG_CAMERA_SETTING = "arg_camera_setting"

        fun create(cameraSetting: CameraSettings): PhotoCaptureFragment {
            return PhotoCaptureFragment().apply {
                arguments = bundleOf(
                    ARG_CAMERA_SETTING to cameraSetting
                )
            }
        }
    }
}

data class CameraSettings(
    val lensFacing: Int = LENS_FACING_BACK,
    val cameraOverlayType: CameraOverlayType = CameraOverlayType.PASSPORT_FRAME,
    val headerText: String = "",
    val title: String = "",
    val description: String = ""

): Serializable

enum class CameraOverlayType {
    PASSPORT_FRAME, RECTANGLE_FRAME
}