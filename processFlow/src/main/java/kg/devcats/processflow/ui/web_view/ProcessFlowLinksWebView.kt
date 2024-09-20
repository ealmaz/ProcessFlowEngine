package kg.devcats.processflow.ui.web_view

import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.extension.getProcessFlowHolder
import kg.devcats.processflow.extension.gone
import kg.devcats.processflow.extension.visible
import kg.devcats.processflow.model.common.ScreenState

class ProcessFlowLinksWebView : ProcessFlowWebViewFragment() {

    override fun onResume() {
        super.onResume()
        getProcessFlowHolder().setIsToolbarVisible(false)
        vb.popupToolbar.apply {
            initToolbar(ChiliToolbar.Configuration(
                hostActivity = requireActivity(),
                isNavigateUpButtonEnabled = true,
            ))
            visible()
        }
    }

    override fun renderScreenState(state: ScreenState?) {}

    override fun updateToolbarTitleFromPage(title: String) {
        super.updateToolbarTitleFromPage(title)
        vb.popupToolbar.setTitle(title)
    }

    override fun onPause() {
        super.onPause()
        vb.popupToolbar.gone()
        getProcessFlowHolder().apply {
            setIsToolbarVisible(true)
            setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
        }
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}