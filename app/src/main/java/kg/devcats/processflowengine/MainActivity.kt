package kg.devcats.processflowengine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.design2.chili2.view.navigation_components.ChiliToolbar
import kg.devcats.processflowengine.databinding.ActivityMainBinding
import kg.devcats.processflowengine.offline.OfflineFlowConfig
import kg.devcats.processflowengine.offline.OfflineTestProcessFlow
import kg.devcats.processflowengine.offline.ProcessFlowApiImpl
import kg.devcats.processflowengine.online.OnlineFlowConfiguratorActivity

class MainActivity : AppCompatActivity() {

    lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.toolbar.initToolbar(ChiliToolbar.Configuration(this, centeredTitle = true, isNavigateUpButtonEnabled = false))

        vb.btnStartReal.setOnClickListener { startActivity(Intent(this, OnlineFlowConfiguratorActivity::class.java)) }
        vb.btnStart.setOnClickListener { startActivity(Intent(this, OfflineFlowConfig::class.java)) }

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


}


