package kg.devcats.processflow.ui.web_view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Base64
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
import kg.devcats.processflow.extension.visible
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.model.component.WebViewFileTypes
import java.io.File
import java.io.FileOutputStream

class ProcessFlowPdfWebViewFragment :
    BaseProcessScreenFragment<ProcessFlowFragmentPdfViewerBinding>(), DownloadFile.Listener {

    private lateinit var remotePDFViewPager: RemotePDFViewPager
    private var adapter: PDFPagerAdapter? = null

    private var isShareEnabled = false

    private var webViewId: String = ""

    override val unclickableMask: View
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout
        get() = vb.llButtons

    private val canBackPress: Boolean
        get() = arguments?.getBoolean(CAN_BACK_PRESS) ?: false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        deleteExternalStorageIfExist()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (canBackPress) getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_back_arrow)
    }

    override fun onPause() {
        super.onPause()
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
        if (canBackPress) getProcessFlowHolder().setToolbarTitle("")
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        data?.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()?.let { flow ->
            isShareEnabled = (flow.properties?.isShareEnabled) ?: false
            flow.url?.let { loadFileByType(flow.properties?.fileType, it) }
            webViewId = flow.id
        }
    }

    private fun loadFileByType(fileType: WebViewFileTypes?, fileSource: String) {
        when (fileType) {
            WebViewFileTypes.PDF -> loadPdfUrl(url = fileSource)
            WebViewFileTypes.BASE_64 -> parsePdfFromBase64(pdfBase64 = fileSource)
            else -> {}
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

    override fun onDestroyView() {
        deleteExternalStorageIfExist()
        super.onDestroyView()
    }

    private fun getExternalStorage(): File {
        return when (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            true -> File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FILE_DIRECTORY_NAME)
            else -> File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILE_DIRECTORY_NAME)
        }
    }

    private fun parsePdfFromBase64(pdfBase64: String) {
        loadPdfUrl(" ")
        vb.pbLoader.visible()
        val mediaStorageDir = getExternalStorage()
        mediaStorageDir.mkdir()
        val file = File(mediaStorageDir.path + File.separator + System.currentTimeMillis() + ".pdf")
        file.createNewFile()
        val pdfAsBytes: ByteArray = Base64.decode(pdfBase64, 0)
        val outputStream = FileOutputStream(file, false)
        try {
            outputStream.write(pdfAsBytes)
            outputStream.flush()
            outputStream.close()
            onSuccess(null, file.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            showFailureException()
        } finally {
            outputStream.close()
            vb.pbLoader.gone()
        }

    }

    private fun deleteExternalStorageIfExist() {
        val mediaStorageDir = getExternalStorage()
        if (!mediaStorageDir.exists()) return
        mediaStorageDir.deleteRecursively()
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
        return if (canBackPress) BackPressHandleState.CALL_SUPER
        else BackPressHandleState.NOT_HANDLE
    }

    companion object {

        const val CAN_BACK_PRESS = "canBackPress"
        const val FILE_DIRECTORY_NAME = "files"

        fun create(canBackPress: Boolean = false): ProcessFlowPdfWebViewFragment {
            return ProcessFlowPdfWebViewFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(CAN_BACK_PRESS, canBackPress)
                }
            }
        }
    }
}