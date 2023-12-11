package kg.devcats.processflow.ui.web_view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.databinding.ProcessFlowFragmentPdfViewerBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.gone
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.ui.web_view.ProcessFlowWebViewFragment.Companion.MANUAL_CLOSE_WEB_VIEW_STATUS
import java.io.File

class ProcessFlowPdfWebViewFragment : BaseProcessScreenFragment<ProcessFlowFragmentPdfViewerBinding>(), DownloadFile.Listener {

    private lateinit var remotePDFViewPager: RemotePDFViewPager
    private var adapter: PDFPagerAdapter? = null

    private var isShareEnabled = false

    private var webViewId: String = ""

    override val unclickableMask: View
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout
        get() = vb.llButtons

    override fun onResume() {
        super.onResume()
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_back_arrow)
    }

    override fun onPause() {
        super.onPause()
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        data?.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()?.let {
            it.url?.let { loadPdfUrl(it) }
            webViewId = it.id
            isShareEnabled = (it.properties?.isShareEnabled) ?: false
        }
    }

    override fun inflateViewBinging(): ProcessFlowFragmentPdfViewerBinding =
        ProcessFlowFragmentPdfViewerBinding.inflate(layoutInflater)

    private fun loadPdfUrl(url: String) {
        with(vb) {
            flContainer.removeAllViews()
        }
        try {
            remotePDFViewPager = RemotePDFViewPager(requireContext(), url, this)
        } catch (_: Exception) {}
    }

    override fun onSuccess(url: String?, destinationPath: String?) {
        vb.pbLoader.gone()
        try {
            setupPdfViewer(destinationPath)
            if (isShareEnabled) setupShare(destinationPath)
        } catch (e: Exception) {
            showFailureException()
        }
    }

    override fun onFailure(e: Exception?) {
        vb.pbLoader.gone()
        showFailureException()
    }

    override fun onProgressUpdate(progress: Int, total: Int) {}

    private fun setupPdfViewer(destinationPath: String?) {
        adapter = PDFPagerAdapter(requireContext(), destinationPath)
        remotePDFViewPager.adapter = adapter
        vb.flContainer.addView(remotePDFViewPager)
    }

    private fun showFailureException() {
        try {
            Toast.makeText(requireContext(), R.string.process_flow_error_pdf_loading, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        getProcessFlowHolder().setupToolbarEndIcon(null, null)
        adapter?.close()
        super.onDestroy()
    }

    private fun setupShare(pdfFilePath: String?) {
        if (pdfFilePath.isNullOrEmpty()) return
        getProcessFlowHolder().setupToolbarEndIcon(R.drawable.process_flow_ic_share_18) {
            sharePdfFile(pdfFilePath)
        }
    }

    private fun sharePdfFile(pdfFilePath: String) {
        ShareCompat.IntentBuilder(requireContext())
            .setType("application/pdf")
            .setStream(getPdfFileUri(pdfFilePath))
            .intent
            .apply { addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            .also {
                startActivity(Intent.createChooser(it, getString(R.string.process_flow_share_with)))
            }
    }

    private fun getPdfFileUri(pdfFilePath: String): Uri {
        var file = File(pdfFilePath)
        file = file.copyTo(File(requireActivity().cacheDir, "${file.name}.pdf"), true)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(requireContext(), "${requireActivity().packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}