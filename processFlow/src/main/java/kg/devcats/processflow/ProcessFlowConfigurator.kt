package kg.devcats.processflow

import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflow.util.NullProcessFlowRepositoryException

object ProcessFlowConfigurator {

    private var processFlowRepositoryImpl: ProcessFlowRepository? = null

    var selfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/joy/light/selfi_full.png" }

    var recognizerTimeoutLimit = 20
    var recognizerTimeoutMills = 20000L

    internal fun getProcessFlowRepository(): ProcessFlowRepository = processFlowRepositoryImpl ?: throw NullProcessFlowRepositoryException

    fun setProcessFlowRepositoryImpl(impl: ProcessFlowRepository) {
        this.processFlowRepositoryImpl = impl
    }

    fun onDestroy() {
        processFlowRepositoryImpl = null
    }

}