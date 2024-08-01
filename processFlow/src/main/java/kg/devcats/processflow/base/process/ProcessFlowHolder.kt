package kg.devcats.processflow.base.process

import com.design2.chili2.R
import kg.devcats.processflow.model.ProcessFlowCommit

interface ProcessFlowHolder {

    fun setToolbarNavIcon(navIconRes: Int = R.drawable.chili_ic_close)
    fun setToolbarTitle(title: String = "")
    fun setupToolbarEndIcon(iconRes: Int?, onClick: (() -> Unit)?)
    fun setIsToolbarVisible(isVisible: Boolean)

    fun commit(commit: ProcessFlowCommit)

    fun setIsActivityLoading(isLoading: Boolean)

}