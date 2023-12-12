package kg.devcats.processflowengine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kg.devcats.processflowengine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.btnStart.setOnClickListener { openFlow("start") }
        vb.btnStartForm.setOnClickListener { openFlow("OTP_INPUT") }
        vb.btnStartAgreemrnt.setOnClickListener { openFlow("passport_form") }
        vb.btnStartOferta.setOnClickListener { openFlow("WEB_VIEW_OFERTA") }
        vb.btnStartCallWebView.setOnClickListener { openFlow("VIDEO_IDENT_BUTTON") }
    }

    private fun openFlow(startPoint: String) {
        val i = Intent(this, TestProcessFlow::class.java)
        ProcessFlowApiImpl.FIRST_STEP_KEY = startPoint
        startActivity(i)
    }
}


