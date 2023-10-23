package kg.devcats.processflow.model.component

import java.io.Serializable
import java.util.Date

data class FlowRetryInfo(
    val id: String? = null,
    val properties: RetryInfoProperties? = null
): Serializable {

    fun getMillsOrNull(): Long? {
        return properties?.enableAt?.let {
            val mills = it - Date().time
            if (mills < 0) 0 else mills
        }
    }
}

data class RetryInfoProperties(
    val showLoader: Boolean? = null,
    val enableAt: Long? = null
): Serializable