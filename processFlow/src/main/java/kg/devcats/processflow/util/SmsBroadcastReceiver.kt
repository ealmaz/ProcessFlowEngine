package kg.devcats.processflow.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsBroadcastReceiver(private val codeLength: Int = 6): BroadcastReceiver(){

    private var listener: Listener? = null

    fun setListener(listener: Listener?){
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent?.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    val code = extractCodeFromMessage(message)
                    listener?.onSmsReceived(code)
                }
                CommonStatusCodes.TIMEOUT -> {}
            }
        }
    }

    private fun extractCodeFromMessage(message: String): String {
        val pattern = Pattern.compile("\\d{${codeLength}}")
        val matcher = pattern.matcher(message)
        return when(matcher.find()){
            true -> matcher.group(0)
            else -> ""
        }
    }

    interface Listener {
        fun onSmsReceived(code: String)
    }
}