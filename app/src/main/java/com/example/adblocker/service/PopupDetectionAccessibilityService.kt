package com.example.adblocker.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import android.widget.Toast
import com.example.adblocker.blocklist.RuleManager
import com.example.adblocker.model.AdRule
import com.example.adblocker.utils.Logger

class PopupDetectionAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "PopupDetectionService"
    }

    private lateinit var ruleManager: RuleManager
    private lateinit var adRules: List<AdRule>

    override fun onCreate() {
        super.onCreate()
        ruleManager = RuleManager(this)
        adRules = ruleManager.loadRules()
        Logger.info(this, TAG, "Accessibility service created with ${adRules.size} rules")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // We are interested in window state changes (popups appearing)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val source = event.source
            if (source == null) return

            val packageName = event.packageName?.toString() ?: ""
            val className = event.className?.toString() ?: ""
            val text = event.text?.joinToString(" ") ?: ""

            Logger.debug(this, TAG, "Window changed: $packageName $className - $text")

            // Check against our rules
            for (rule in adRules) {
                if (rule.matches(packageName, text)) {
                    Logger.info(this, TAG, "Ad popup detected: package=$packageName, text='$text'")
                    // Attempt to close the popup by performing a global action (back)
                    performGlobalAction(GLOBAL_ACTION_BACK)
                    // Optional: show a toast (for debugging)
                    // Toast.makeText(this, "Ad popup closed: $packageName", Toast.LENGTH_SHORT).show()
                    break
                }
            }
            // Recycle the source
            source.recycle()
        }
    }

    override fun onInterrupt() {
        // Service interrupted
        Logger.warn(this, TAG, "Accessibility service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Logger.info(this, TAG, "Accessibility service connected")
        // Optional: show a toast when service starts
        Toast.makeText(this, "广告拦截器服务已启动", Toast.LENGTH_SHORT).show()
    }
}