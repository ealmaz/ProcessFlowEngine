package kg.devcats.processflow.ui.web_view

import android.widget.Toast
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflow.extension.getProcessFlowHolder

class ProcessFlowLinksWebView : ProcessFlowWebViewFragment() {

    override fun onResume() {
        super.onResume()
        Toast.makeText(requireContext(), "THATS IT", Toast.LENGTH_SHORT).show()
        getProcessFlowHolder().setupToolbarEndIcon(null, null)
    }

    override fun updateBackIcon() {
        getProcessFlowHolder().setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_back_arrow)
    }

    override fun onPause() {
        super.onPause()
        getProcessFlowHolder().apply {
            setToolbarNavIcon(com.design2.chili2.R.drawable.chili_ic_close)
            setToolbarTitle("")
        }
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}