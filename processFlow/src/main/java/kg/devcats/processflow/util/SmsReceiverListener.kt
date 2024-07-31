package kg.devcats.processflow.util

interface SmsReceiverListener {
    fun onSmsReceived(code: String)
}