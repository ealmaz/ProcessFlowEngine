package kg.devcats.processflow.model.component

data class FlowMessage (
    val id: String,
    val content: String = "",
    val contentType: FlowMessageContentType,
    val messageType: FlowMessageType,
)

enum class FlowMessageContentType {
    TEXT, TEXT_HTML, IMAGE_URL
}

enum class FlowMessageType {
    USER, SYSTEM
}

