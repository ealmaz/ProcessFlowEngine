package kg.devcats.processflowengine.offline

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.devcats.processflowengine.databinding.ActivityOfflineFlowConfiguratorBinding
import kg.devcats.processflowengine.online.OnlineFlowConfiguratorActivity

class OfflineFlowConfig : AppCompatActivity() {

    private lateinit var vb: ActivityOfflineFlowConfiguratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityOfflineFlowConfiguratorBinding.inflate(layoutInflater)
        setContentView(vb.root)

        vb.toolbar.initToolbar(ChiliToolbar.Configuration(this, centeredTitle = true, isNavigateUpButtonEnabled = true))

        vb.startCode.setText(ProcessFlowApiImpl.FIRST_STEP_KEY)
        vb.delay.setText((ProcessFlowApiImpl.REQUESTS_DELAY).toString())
        vb.btnStart.setOnClickListener {
            ProcessFlowApiImpl.REQUESTS_DELAY = vb.delay.getInputText().toLongOrNull() ?: 300L
            openFlow(vb.startCode.getInputText().takeIf { it.isNotBlank() } ?: ProcessFlowApiImpl.FIRST_STEP_KEY)
        }
        vb.btnStartForm.setOnClickListener { openFlow("OTP_INPUT") }
        vb.btnStartAgreemrnt.setOnClickListener { openFlow("passport_form") }
        vb.btnStartOferta.setOnClickListener { openFlow("WEB_VIEW_OFERTA") }
        vb.btnStartCallWebView.setOnClickListener { openFlow("VIDEO_IDENT_BUTTON") }
        vb.btnStartOtp.setOnClickListener { openFlow("OTP") }

    }


    private fun openFlow(startPoint: String) {
        val i = Intent(this, OfflineTestProcessFlow::class.java)
        ProcessFlowApiImpl.FIRST_STEP_KEY = startPoint
        startActivity(i)
    }

}