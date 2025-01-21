package kg.devcats.processflow.base.process

import kg.devcats.processflow.model.ProcessFlowScreenData

interface ProcessFlowScreen {

    fun setScreenData(data: ProcessFlowScreenData? = null) {}

    fun handleBackPress(): BackPressHandleState = BackPressHandleState.NOT_HANDLE
    fun handleShowLoading(isLoading: Boolean): Boolean = false
    fun handleMultipleFileLoaderContentType(loadedType: String): String?
}

enum class BackPressHandleState {
    HANDLED, NOT_HANDLE, CALL_SUPER
}