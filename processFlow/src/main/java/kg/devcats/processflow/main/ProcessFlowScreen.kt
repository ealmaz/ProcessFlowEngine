package kg.devcats.processflow.main

interface ProcessFlowScreen {
    fun handleBackPress(): Boolean = false
    fun handleShowLoading(isLoading: Boolean): Boolean = false
}