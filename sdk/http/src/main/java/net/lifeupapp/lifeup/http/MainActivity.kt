package net.lifeupapp.lifeup.http


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.lifeupapp.lifeup.http.databinding.ActivityMainBinding
import net.lifeupapp.lifeup.http.service.KtorService
import net.lifeupapp.lifeup.http.service.LifeUpService
import net.lifeupapp.lifeup.http.utils.getIpAddressInLocalNetwork
import net.lifeupapp.lifeup.http.utils.getIpAddressListInLocalNetwork
import net.lifeupapp.lifeup.http.utils.setHtmlText

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val powerManager by lazy {
        getSystemService(POWER_SERVICE) as PowerManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // init the view logic
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            KtorService.isRunning.collect { running ->
                if (running == LifeUpService.RunningState.RUNNING || running == LifeUpService.RunningState.STARTING) {
                    binding.serverStatusText.text = getString(R.string.serverStartedMessage)
                    binding.switchStartService.isChecked = true
                } else {
                    binding.serverStatusText.text = getString(R.string.server_status)
                    binding.switchStartService.isChecked = false
                }
            }
        }

        binding.switchStartService.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                KtorService.start()
            } else {
                KtorService.stop()
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.includeBatteryConfig.btn.isGone = true
        }
        binding.includeBatteryConfig.apply {
            this.tvTitle.setText(R.string.ignore_battery_optimizations)
            this.tvDesc.setText(R.string.ignore_battery_optimizations_desc)

            this.btn.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !powerManager.isIgnoringBatteryOptimizations(
                        this@MainActivity.packageName
                    )
                ) {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    intent.resolveActivity(this@MainActivity.packageManager)?.let {
                        this@MainActivity.startActivity(intent)
                    }
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!Settings.canDrawOverlays(this)) {
                binding.includeOverlayConfig.btn.setOnClickListener {
                    val intent = Intent(ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = android.net.Uri.parse("package:" + packageName)
                    startActivity(intent)
                }
            }
        } else {
            binding.includeOverlayConfig.btn.isGone = true
        }


        val localIpAddress =
            getIpAddressListInLocalNetwork().filter { !it.startsWith("10.") }.joinToString {
                "${it}:13276"
            }
        if (localIpAddress.isNotBlank()) {
            binding.ipAddressText.text = getString(R.string.localIpAddressMessage, localIpAddress)
        }

        binding.tvAboutDesc.setHtmlText(R.string.about_text)
        binding.tvAboutDesc.movementMethod = android.text.method.LinkMovementMethod.getInstance()
        binding.tvAboutDesc.linksClickable = true
    }


}