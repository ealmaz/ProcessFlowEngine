package kg.devcats.processflow.ui.web_view

import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import androidx.core.view.isVisible
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

open class ProcessFlowWebViewFragment : BaseProcessScreenFragment<ProcessFlowFragmentProcessFlowWebViewBinding>(),
    JsBridgeInterface {

    private var webViewId: String = ""

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llButtons

    override val unclickableMask: View?
        get() = null

    override fun inflateViewBinging() = ProcessFlowFragmentProcessFlowWebViewBinding.inflate(layoutInflater)

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        data?.allowedAnswer?.filterIsInstance<FlowWebView>()?.first()?.let {
            it.url?.let { getWebView().loadUrl(it) }
            webViewId = it.id
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

    open fun onSwipeRefresh() {
        vb.webView.reload()
    }

    private fun setupWebView() {
        getWebView().apply {
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

                override fun onPageFinished() {}

                override fun onProgressChanged(progress: Int) {
                    vb.progressBar.run {
                        setProgress(progress)
                        isVisible = progress < 100
                    }
                }
            }
            addJavascriptInterface(this@ProcessFlowWebViewFragment, webViewJsInterfaceName)
        }
    }

    open fun updateTitle(title: String) {
        getProcessFlowHolder().setToolbarTitle(title)
    }

    open fun updateBackIcon() {
        val iconRes = if (getWebView().canGoBack()) com.design2.chili2.R.drawable.chili_ic_back_arrow
        else com.design2.chili2.R.drawable.chili_ic_close
        getProcessFlowHolder().setToolbarNavIcon(iconRes)
    }

    open fun getWebView(): AppWebView = vb.webView

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
                webViewId,
                listOf(Content(result, ContentTypes.WEB_VIEW_RESULT))
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