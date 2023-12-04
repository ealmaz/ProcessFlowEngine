package kg.devcats.processflowengine

import android.content.Context
import kg.devcats.processflow.model.Event
import kg.devcats.processflow.model.ProcessFlowCommit
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.ui.main.ProcessFlowActivity
import kg.devcats.processflow.ui.main.ProcessFlowVM

class TestProcessFlow : ProcessFlowActivity<TestVM>()  {

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
}

object MyCommit : ProcessFlowCommit()

sealed class MyEvent : Event() {
    object MySubEvent : MyEvent()
}

class TestRepo(context: Context) : ProcessFlowRepository(ProcessFlowApiImpl) {

}


class TestVM(context: Context) : ProcessFlowVM<ProcessFlowRepository>(TestRepo(context)) {

}
