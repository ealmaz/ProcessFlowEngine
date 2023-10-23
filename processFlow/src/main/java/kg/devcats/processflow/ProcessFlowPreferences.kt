package kg.devcats.processflow

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ProcessFlowPreferences(context: Context) {

    private val pref: SharedPreferences

    init {
        pref = context.getSharedPreferences(SETTINGS_STORAGE_NAME, Context.MODE_PRIVATE)
    }

    var processId: String?
        get() {
            return pref.getString(FLOW_PROCESS_ID, "")
        }
        set(value) {
            pref.edit {
                putString(FLOW_PROCESS_ID, value)
            }
        }

    var flowStatus: String?
        get() {
            return pref.getString(FLOW_STATUS, "")
        }
        set(value) {
            pref.edit {
                putString(FLOW_STATUS, value)
            }
        }

    fun reset() {
        processId = ""
        flowStatus = ""
    }

    companion object {
        const val SETTINGS_STORAGE_NAME = "kg.o.nurtelecom.process_flow_prefs"

        private const val FLOW_PROCESS_ID = "PROCESS_FLOW_FLOW_PROCESS_ID"
        private const val FLOW_STATUS = "PROCESS_FLOW_FLOW_STATUS"
        private const val LOCATION = "PROCESS_FLOW_LOCATION"
    }
}
