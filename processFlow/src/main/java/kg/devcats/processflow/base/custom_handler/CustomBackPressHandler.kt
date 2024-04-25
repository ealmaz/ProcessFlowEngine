package kg.devcats.processflow.base.custom_handler

import kg.devcats.processflow.base.process.BackPressHandleState

interface CustomBackPressHandler {
    fun handleBackPress(): BackPressHandleState
}