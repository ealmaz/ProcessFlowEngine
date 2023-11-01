package kg.devcats.processflowengine

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kg.devcats.processflow.ProcessFlowConfigurator
import kg.devcats.processflow.ProcessFlowPreferences
import kg.devcats.processflow.repository.ProcessFlowRepository
import kg.devcats.processflowengine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var vb: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)
        vb.btnStart.setOnClickListener {
            val i = Intent(this, TestProcessFlow::class.java)
            startActivity(i)
        }
    }
}


