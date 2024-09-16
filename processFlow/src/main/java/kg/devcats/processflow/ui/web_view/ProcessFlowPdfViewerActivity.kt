package kg.devcats.processflow.ui.web_view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.design2.chili2.view.navigation_components.ChiliToolbar
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kg.devcats.processflow.R
import kg.devcats.processflow.databinding.ProcessFlowActivityPdfViewerBinding
import kg.devcats.processflow.extension.defaultSubscribe
import kg.devcats.processflow.extension.showWarningDialog
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

class ProcessFlowPdfViewerActivity : AppCompatActivity(), DownloadFile.Listener {

    private lateinit var vb: ProcessFlowActivityPdfViewerBinding
    private lateinit var remotePDFViewPager: RemotePDFViewPager
    private var adapter: PDFPagerAdapter? = null
    private val isAvailableReload: Boolean
        get() = failureReloadCounter < 3

    private var failureReloadCounter = 0L
    private var failureDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ProcessFlowActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(vb.root)
        setupToolbar()
        loadPdf()
    }

    private fun setupToolbar() {
        with(vb) {
            flContainer.removeAllViews()
            ctToolbar.initToolbar(
                ChiliToolbar.Configuration(
                    this@ProcessFlowPdfViewerActivity,
                    getTitleFromExtra(),
                    isNavigateUpButtonEnabled = true
                )
            )
        }
    }

    private fun loadPdf() {
        remotePDFViewPager = RemotePDFViewPager(this, getPdfUrlFromExtra(), this)
    }

    private fun getPdfUrlFromExtra() = intent.getStringExtra(EXTRA_URL) ?: ""

    private fun getTitleFromExtra() = intent.getStringExtra(EXTRA_TITLE)

    override fun onSuccess(url: String?, destinationPath: String?) {
        if (isFinishing) return
        vb.pbLoader.isVisible = false
        try {
            setupPdfViewer(destinationPath)
        } catch (e: Exception) {
            showFailureException()
        }
    }

    private fun setupPdfViewer(destinationPath: String?) {
        adapter = PDFPagerAdapter(this, destinationPath)
        remotePDFViewPager.adapter = adapter
        vb.flContainer.addView(remotePDFViewPager)
    }

    override fun onFailure(e: Exception?) {
        when {
            isAvailableReload && e is FileNotFoundException -> tryReloadPdf()
            else -> showFailureException()
        }
    }

    private fun tryReloadPdf() {
        failureDisposable = Observable.timer(++failureReloadCounter, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .defaultSubscribe(onSuccess = { loadPdf() })
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        vb.pbLoader.apply { isIndeterminate = false; max = total; this.progress = progress }
    }

    private fun showFailureException() {
        if (isFinishing) return
        try {
            vb.pbLoader.isVisible = false
            showWarningDialog(getString(R.string.process_flow_error_pdf_loading)) { finish() }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        failureDisposable?.takeIf { !it.isDisposed }?.dispose()
        adapter?.close()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_TITLE = "EXTRA_TITLE"

        fun start(
            context: Context,
            linkToPdf: String,
            title: String = "",
        ) {
            val intent = Intent(context, ProcessFlowPdfViewerActivity::class.java).apply {
                putExtra(EXTRA_URL, linkToPdf)
                putExtra(EXTRA_TITLE, title)
            }
            context.startActivity(intent)
        }
    }
}
