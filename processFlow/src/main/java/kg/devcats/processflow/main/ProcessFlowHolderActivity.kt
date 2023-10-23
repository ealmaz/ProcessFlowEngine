package kg.devcats.processflow.main

import com.design2.chili2.R
import kg.devcats.processflow.model.ProcessFlowCommit

interface ProcessFlowHolderActivity {

    fun setToolbarNavIcon(navIconRes: Int = R.drawable.chili_ic_close)
    fun setToolbarTitle(title: String = "")
    fun setupToolbarEndIcon(iconRes: Int?, onClick: (() -> Unit)?)

    fun commit(commit: ProcessFlowCommit)

}