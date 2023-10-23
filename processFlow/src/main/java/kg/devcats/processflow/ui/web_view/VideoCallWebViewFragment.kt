package kg.devcats.processflow.ui.web_view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import kg.devcats.processflow.R
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.extension.negativeButton
import kg.devcats.processflow.extension.positiveButton
import kg.devcats.processflow.extension.showDialog

class VideoCallWebViewFragment : ProcessFlowWebViewFragment() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.any { !it.value }) onPermissionDeny()
        else getWebView().reload()
    }

    private val openSettingResult  = registerForActivityResult(object: ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
        }
        override fun parseResult(resultCode: Int, intent: Intent?) {}
    }) { requestAudioVideoPermission() }

    private fun onPermissionDeny() {
        showDialog {
            setMessage(R.string.process_flow_permission_denied)
            positiveButton(android.R.string.ok) {
                openSettingResult.launch(Unit)
            }
        }
    }

    override fun setupViews() {
        super.setupViews()
        vb.webView.apply {
            webChromeClient = object : WebChromeClient() {
                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                }
            }
            settings.mediaPlaybackRequiresUserGesture = false
            settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.5563.115 Safari/537.36"
        }
        requestAudioVideoPermission()
    }


    private fun requestAudioVideoPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))
    }

    override fun handleBackPress(): BackPressHandleState {
        showDialog {
            setMessage(R.string.process_flow_exit_video_call)
            positiveButton(R.string.process_flow_yes) { setStringResultAndClose(MANUAL_CLOSE_WEB_VIEW_STATUS) }
            negativeButton(R.string.process_flow_no)
            setCancelable(false)
        }
        return BackPressHandleState.HANDLED
    }
}