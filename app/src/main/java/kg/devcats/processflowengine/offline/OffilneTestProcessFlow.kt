package kg.devcats.processflowengine.offline

import android.content.Context
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.model.common.Content
import kg.devcats.processflow.model.component.FlowButton
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.ui.main.ProcessFlowActivity
import kg.devcats.processflow.ui.main.ProcessFlowVM

class OfflineTestProcessFlow : ProcessFlowActivity<TestVM>()  {

    override fun setupViews() {
        super.setupViews()
    }

    override val vm: TestVM by lazy {
        TestVM(this)
    }
    override val processType: String
        get() = "TEST_TYPE"

    override fun getProcessFlowStartParams(): Map<String, String> {
        return mapOf("identificationNumber" to "123456678")
    }

    override fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        if (button?.buttonId == "EXIT_NAVIGATE_TO_WALLET_MAIN") finish()
        else super.resolveButtonClickCommit(button, additionalContent)
    }
}

object MyCommit : ProcessFlowCommit()

sealed class MyEvent : Event() {
    object MySubEvent : MyEvent()
}

class TestRepo(context: Context) : ProcessFlowRepository(ProcessFlowApiImpl) {

}


class TestVM(context: Context) : ProcessFlowVM<ProcessFlowRepository>(TestRepo(context)) {

}
