package kg.devcats.processflow.model.common

enum class FlowStatus {
    NEW,
    RUNNING,
    COMPLETED,
    TERMINATED
}

object FlowStatusHelper {

    private val terminatedStatuses = listOf(FlowStatus.COMPLETED.toString(), FlowStatus.TERMINATED.toString())

    fun isProcessTerminated(status: String?): Boolean {
        return status?.let { it in terminatedStatuses } ?: true
    }
}