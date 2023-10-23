package kg.devcats.processflow.model

sealed class Event {

    class Notification(val message: String) : Event()
    class NotificationResId(val messageResId: Int) : Event()
    class DialogNotification(val message: String) : Event()

}