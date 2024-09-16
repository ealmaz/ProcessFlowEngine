package kg.devcats.processflow.ui.web_view

import android.Manifest
import android.os.Bundle
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import kg.devcats.processflow.R
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.custom_view.AppWebView
import kg.devcats.processflow.databinding.ProcessFlowFragmentProcessFlowWebViewBinding
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.model.ContentTypes
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.ProcessFlowScreenData
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.FlowWebView
import kg.devcats.processflow.model.component.WebViewFileTypes
import kg.devcats.processflow.model.component.WebViewProperties
import kg.devcats.processflow.ui.main.ProcessFlowActivity

open class ProcessFlowWebViewFragment :
    BaseProcessScreenFragment<ProcessFlowFragmentProcessFlowWebViewBinding>(), JsBridgeInterface {

    private var webViewId: String = ""
    override val buttonsLinearLayout: LinearLayout? get() = vb.llButtons
    override val unclickableMask: View? get() = null
    private var mGeoLocationRequestOrigin: String? = null
    private var mGeoLocationCallback: GeolocationPermissions.Callback? = null
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            mGeoLocationCallback?.invoke(mGeoLocationRequestOrigin, it, false)
        }

    override fun inflateViewBinging() =
        ProcessFlowFragmentProcessFlowWebViewBinding.inflate(layoutInflater)

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        Toast.makeText(requireContext(), "data: $data", Toast.LENGTH_SHORT).show()
        data?.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()?.let {
            Toast.makeText(requireContext(), "props: ${it.properties}", Toast.LENGTH_SHORT).show()
            if (it.properties?.fileType == WebViewFileTypes.PDF) (requireActivity() as ProcessFlowActivity<*>).navigateTo(
                ProcessFlowPdfWebViewFragment::class.java
            )

            it.url?.let { getWebView().loadUrl(it) }
            webViewId = it.id
            handleProperties(it.properties)
        }
    }

    override fun onResume() {
        super.onResume()
        updateBackIcon()
    }

    override fun setupViews() = with(vb) {
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            onSwipeRefresh()
        }
        swipeRefreshLayout.isEnabled = false
        setupWebView()
    }

    private fun openWebView(data: ProcessFlowScreenData?){

    }

    open fun onSwipeRefresh() {
        vb.webView.reload()
    }

    private fun setupWebView() {
        getWebView().apply {
            Toast.makeText(requireContext(), "setupWebView: $this", Toast.LENGTH_SHORT).show()
            setupAdditionalSettings {
                allowFileAccess = true
            }

            loadListener = object : AppWebView.PageLoadListener() {
                override fun onReceivedTitle(title: String) {
                    updateBackIcon()
                }

                override fun onPageStarted() {
                    updateBackIcon()
                }

                override fun onProgressChanged(progress: Int) {
                    vb.progressBar.run {
                        setProgress(progress)
                        isVisible = progress < 100
                    }
                }

                override fun onLocationPermissionRequest(
                    origin: String?, callback: GeolocationPermissions.Callback?
                ) {
                        mGeoLocationCallback = callback
                        mGeoLocationRequestOrigin = origin
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
            addJavascriptInterface(this@ProcessFlowWebViewFragment, webViewJsInterfaceName)
        }
    }

    open fun updateTitle(title: String) {
        getProcessFlowHolder().setToolbarTitle(title)
    }

    open fun updateBackIcon() {
        val iconRes =
            if (getWebView().canGoBack()) com.design2.chili2.R.drawable.chili_ic_back_arrow
            else com.design2.chili2.R.drawable.chili_ic_close
        getProcessFlowHolder().setToolbarNavIcon(iconRes)
    }

    open fun getWebView(): AppWebView = vb.webView

    open fun handleProperties(webViewProperties: WebViewProperties?) {
        Toast.makeText(requireContext(), "handleProperties: $webViewProperties", Toast.LENGTH_SHORT).show()
        webViewProperties?.faqUrl?.let {
            getProcessFlowHolder().setupToolbarEndIcon(R.drawable.process_flow_ic_faq) {
                getProcessFlowHolder().commit(ProcessFlowCommit.OnLinkClicked(it))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getWebView()?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            getWebView().restoreState(savedInstanceState)
        }
    }

    override fun onDestroy() {
        getWebView().clearWebView()
        super.onDestroy()
    }

    @JavascriptInterface
    override fun setStringResultAndClose(result: String) {
        getProcessFlowHolder().commit(
            ProcessFlowCommit.CommitContentFormResponseId(
                webViewId, listOf(Content(result, ContentTypes.WEB_VIEW_RESULT))
            )
        )
    }

    @JavascriptInterface
    override fun isThemeLight(): String = isAppThemeLight

    @JavascriptInterface
    override fun getLocale(): String = appLocale

    override fun handleBackPress(): BackPressHandleState {
        return getWebView().run {
            if (canGoBack()) {
                getWebView().goBack()
                BackPressHandleState.HANDLED
            } else BackPressHandleState.NOT_HANDLE
        }
    }


    companion object {
        const val webViewJsInterfaceName = "MoyOAndroid"
        const val MANUAL_CLOSE_WEB_VIEW_STATUS = "MANUAL_CLOSE_WEB_VIEW_STATUS"
    }

}