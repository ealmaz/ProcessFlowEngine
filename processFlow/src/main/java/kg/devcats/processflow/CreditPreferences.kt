package kg.devcats.processflow

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class CreditPreferences(context: Context) {

    private val pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(SETTINGS_STORAGE_NAME, Context.MODE_PRIVATE)
    }

    var processId: String?
        get() {
            return pref.getString(CREDIT_FLOW_PROCESS_ID, "")
        }
        set(value) {
            pref.edit {
                putString(CREDIT_FLOW_PROCESS_ID, value)
            }
        }

    var flowStatus: String?
        get() {
            return pref.getString(CREDIT_FLOW_STATUS, "")
        }
        set(value) {
            pref.edit {
                putString(CREDIT_FLOW_STATUS, value)
            }
        }

    fun reset() {
        processId = ""
        flowStatus = ""
    }

    companion object {
        const val SETTINGS_STORAGE_NAME = "kg.o.nurtelecom.credit_prefs"

        private const val CREDIT_FLOW_PROCESS_ID = "CREDIT_FLOW_PROCESS_ID"
        private const val CREDIT_FLOW_STATUS = "CREDIT_FLOW_STATUS"
        private const val LOCATION = "LOCATION"
    }
}
