package kg.devcats.processflowengine.online

import kg.devcats.processflow.network.ProcessFlowNetworkApi
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.ui.main.ProcessFlowActivity
import kg.devcats.processflow.ui.main.ProcessFlowVM
import kg.devcats.processflowengine.common.TestProcessPrefs
import kg.devcats.processflowengine.online.util.RetrofitCreator

class OnlineTestProcessFlow : ProcessFlowActivity<OnlineTestVM>()  {

    private val prefs: TestProcessPrefs by lazy { TestProcessPrefs(this) }
    override val vm: OnlineTestVM by lazy { OnlineTestVM(RetrofitCreator.create(prefs.token, (intent.getStringExtra(EXTRA_BASE_URL) ?: "")).create(ProcessFlowNetworkApi::class.java)) }
    override val processType: String get() = intent.getStringExtra(EXTRA_PROCESS_TYPE) ?: ""

    override fun setupViews() {
        super.setupViews()
    }

    override fun getProcessFlowStartParams(): Map<String, Any> {
        val superMap = super.getProcessFlowStartParams()
        return superMap.toMutableMap().apply {
            put("identificationNumber", "123456678")
        }
    }



    companion object {
        const val EXTRA_PROCESS_TYPE = "EXTRA_PROCESS_TYPE"
        const val EXTRA_BASE_URL = "EXTRA_BASE_URL"
    }
}

class OnlineTestVM(api: ProcessFlowNetworkApi) : ProcessFlowVM<ProcessFlowRepository>(OnlineTestRepo(api))
class OnlineTestRepo(api: ProcessFlowNetworkApi) : ProcessFlowRepository(api)