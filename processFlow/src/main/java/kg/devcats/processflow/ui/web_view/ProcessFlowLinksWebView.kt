package kg.devcats.processflow.ui.web_view

import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.extension.getProcessFlowHolder

class ProcessFlowLinksWebView : ProcessFlowWebViewFragment() {

    override fun onResume() {
        super.onResume()
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_back_arrow)
    }

    override fun onPause() {
        super.onPause()
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}