package kg.devcats.processflowengine.common

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TestProcessPrefs(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences("TestProcessPrefs", Context.MODE_PRIVATE)


    var base_url: String
        get() {
            return pref.getString("base_url", "") ?: ""
        }
        set(value) {
            pref.edit {
                putString("base_url", value)
            }
        }


    var process_type: String
        get() {
            return pref.getString("process_type", "") ?: ""
        }
        set(value) {
            pref.edit {
                putString("process_type", value)
            }
        }

    var token: String
        get() {
            return pref.getString("token", "") ?: ""
        }
        set(value) {
            pref.edit {
                putString("token", value)
            }
        }

    var possibleProcessIds: String
        get() {
            return pref.getString("possibleProcessIds", "") ?: ""
        }
        set(value) {
            pref.edit {
                putString("possibleProcessIds", value)
            }
        }
}