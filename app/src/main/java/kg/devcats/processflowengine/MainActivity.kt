package kg.devcats.processflowengine

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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


        vb.swTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            setupDarkTheme(isChecked)
        }
    }

    private fun setupDarkTheme(isDark: Boolean) {
        when (isDark) {
            true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun openFlow(startPoint: String) {
        val i = Intent(this, TestProcessFlow::class.java)
        ProcessFlowApiImpl.FIRST_STEP_KEY = startPoint
        startActivity(i)
    }
}


