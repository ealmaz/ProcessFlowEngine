package kg.devcats.processflowengine.online

import android.view.View
import kg.devcats.processflow.base.BaseProcessScreenFragment
import kg.devcats.processflow.base.process.BackPressHandleState
import kg.devcats.processflowengine.databinding.FragmentDocumetsBinding

class DocumetsFragment : BaseProcessScreenFragment<FragmentDocumetsBinding>() {
    override val unclickableMask: View?
        get() = null

    override fun inflateViewBinging(): FragmentDocumetsBinding {
        return FragmentDocumetsBinding.inflate(layoutInflater)
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}