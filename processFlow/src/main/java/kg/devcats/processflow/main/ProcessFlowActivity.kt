package kg.devcats.processflow.main

import androidx.appcompat.app.AppCompatActivity
import kg.devcats.processflow.model.ProcessFlowCommit

open class ProcessFlowActivity : AppCompatActivity(), ProcessFlowHolderActivity {

    override fun setToolbarNavIcon(navIconRes: Int) {}
    override fun setToolbarTitle(title: String) {}
    override fun setupToolbarEndIcon(iconRes: Int?, onClick: (() -> Unit)?) {}

    override fun commit(commit: ProcessFlowCommit) {
        handleCommit(commit)
    }

    open fun handleCommit(commit: ProcessFlowCommit, isHandled: Boolean = false) {
        if (isHandled) return
    }
}
