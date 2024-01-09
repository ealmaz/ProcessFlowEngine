package kg.devcats.processflowengine.online

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.devcats.processflowengine.online.OnlineTestProcessFlow.Companion.EXTRA_BASE_URL
import kg.devcats.processflowengine.online.OnlineTestProcessFlow.Companion.EXTRA_PROCESS_TYPE
import kg.devcats.processflowengine.common.TestProcessPrefs
import kg.devcats.processflowengine.databinding.ActivityOnlineFlowConfiguratorBinding

class OnlineFlowConfiguratorActivity : AppCompatActivity() {

    lateinit var vb: ActivityOnlineFlowConfiguratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityOnlineFlowConfiguratorBinding.inflate(layoutInflater)
        setContentView(vb.root)

        val prefs = TestProcessPrefs(this)

        vb.baseUrl.setText(prefs.base_url.takeIf { it.isNotBlank() } ?: "https://devproxy.o.kg/api/flow-screen/")
        vb.baseUrl.setupClearTextButton()

        vb.etToken.setText(prefs.token)
        vb.etToken.setupClearTextButton()

        vb.etProcessFlowId.setText(prefs.process_type)
        vb.etProcessFlowId.setupClearTextButton()

        vb.btnStart.setOnSingleClickListener {
            val i = Intent(this, OnlineTestProcessFlow::class.java)

            vb.baseUrl.getInputText().let {
                prefs.base_url = it
                i.putExtra(EXTRA_BASE_URL, it)
            }

            vb.etToken.getInputText().let { prefs.token = it }

            vb.etProcessFlowId.getInputText().let {
                prefs.process_type = it
                i.putExtra(EXTRA_PROCESS_TYPE, it)
            }
            startActivity(i)
        }
    }
}