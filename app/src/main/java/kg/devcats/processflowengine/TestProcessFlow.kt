package kg.devcats.processflowengine

import kg.devcats.processflow.ui.main.ProcessFlowActivity

class TestProcessFlow : ProcessFlowActivity()  {

    override fun getProcessFlowStartParams(): Map<String, String> {
        return mapOf("identificationNumber" to "123456678")
    }
}