package net.sarasarasa.lifeup.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jeff.settingitem.SettingView
import kotlinx.android.synthetic.main.activity_setting.*
import net.sarasarasa.lifeup.R


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setSupportActionBar(setting_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        var svReopenSplash = findViewById<SettingView>(R.id.setting_item_reopenSplash)
        svReopenSplash.setOnItemViewClick {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
