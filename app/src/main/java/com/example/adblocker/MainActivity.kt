package com.example.adblocker

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.provider.Settings
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var accessibilitySwitch: Switch
    private lateinit var statusTextView: TextView
    private lateinit var rulesButton: Button
    private lateinit var logsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessibilitySwitch = findViewById(R.id.accessibilitySwitch)
        statusTextView = findViewById(R.id.statusTextView)
        rulesButton = findViewById(R.id.rulesButton)
        logsButton = findViewById(R.id.logsButton)

        // Check if accessibility service is enabled
        updateAccessibilityState()

        // Set switch change listener
        accessibilitySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Direct user to accessibility settings to enable our service
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            } else {
                // Note: We cannot directly disable the service from here for security reasons.
                // We inform the user to go to settings to disable it.
                Toast.makeText(
                    this,
                    "请在无障碍服务设置中手动关闭 '广告拦截器' 服务",
                    Toast.LENGTH_LONG
                ).show()
                updateAccessibilityState()
            }
        }

        // Set button click listeners
        rulesButton.setOnClickListener {
            val intent = Intent(this, RuleManagementActivity::class.java)
            startActivity(intent)
        }

        logsButton.setOnClickListener {
            val intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityState()
    }

    private fun updateAccessibilityState() {
        val enabled = isAccessibilityServiceEnabled(PopupDetectionAccessibilityService::class.java.name)
        accessibilitySwitch.isChecked = enabled
        statusTextView.text = if (enabled) {
            "无障碍服务已启用 - 广告拦截器正在运行"
        } else {
            "无障碍服务未启用 - 请打开无障碍服务以启用广告拦截"
        }
    }

    private fun isAccessibilityServiceEnabled(serviceName: String): Boolean {
        val accessibilityManager =
            getSystemService(ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        for (service in enabledServices) {
            if (service.id.equals(serviceName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}