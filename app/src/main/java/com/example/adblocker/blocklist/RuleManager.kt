package com.example.adblocker.blocklist

import android.content.Context
import com.example.adblocker.model.AdRule
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.List

/**
 * Manages the collection of ad detection rules.
 * Handles loading rules from JSON file, saving rules, and providing default rules.
 */
class RuleManager(private val context: Context) {

    private val rulesFile: File = File(context.filesDir, "ad_rules.json")
    private val gson = Gson()
    private val ruleListType: Type = object : TypeToken<ArrayList<AdRule>>() {}.type

    /**
     * Loads rules from the JSON file. If the file doesn't exist or is invalid,
     * returns a set of default rules.
     */
    fun loadRules(): List<AdRule> {
        return try {
            if (!rulesFile.exists()) {
                // Create default rules if file doesn't exist
                val defaultRules = getDefaultRules()
                saveRules(defaultRules)
                defaultRules
            } else {
                BufferedReader(FileReader(rulesFile)).use { reader ->
                    gson.fromJson(reader, ruleListType) ?: getDefaultRules()
                }
            }
        } catch (e: Exception) {
            // In case of any error, return default rules
            e.printStackTrace()
            getDefaultRules()
        }
    }

    /**
     * Saves the given list of rules to the JSON file.
     */
    fun saveRules(rules: List<AdRule>) {
        try {
            BufferedWriter(FileWriter(rulesFile)).use { writer ->
                gson.toJson(rules, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Returns a list of default ad detection rules.
     * These rules target common ad patterns in popular apps.
     */
    private fun getDefaultRules(): List<AdRule> {
        return listOf(
            // Common ad dialog patterns
            AdRule(
                packageNamePattern = ".*com\\.android\\.vending.*", // Google Play Store
                titleKeywords = listOf("广告", "赞助", "推广", "Sponsored", "Ad"),
                buttonTexts = listOf("关闭", "跳过", "×", "Close", "Skip")
            ),
            AdRule(
                packageNamePattern = ".*com\\.baidu\\.searchbox.*", // Baidu search
                titleKeywords = listOf("推广", "广告", "Ads", "Sponsored"),
                buttonTexts = listOf("关闭", "不感兴趣", "Close", "Not Interested")
            ),
            AdRule(
                packageNamePattern = ".*com\\.qq\\.email.*", // QQ Mail ads
                titleKeywords = listOf("广告", "推荐", "Ad", "Recommend"),
                buttonTexts = listOf("关闭", "×", "Close")
            ),
            AdRule(
                packageNamePattern = ".*com\\.sina\\.weibo.*", // Weibo ads
                titleKeywords = listOf("广告", "推广", "Ad", "Promotion"),
                buttonTexts = listOf("关闭", "不看", "Close", "Hide")
            ),
            AdRule(
                packageNamePattern = ".*com\\.taobao\\.tao.*", // Taobao ads
                titleKeywords = listOf("广告", "推广", "Ad", "Sponsored"),
                buttonTexts = listOf("关闭", "×", "Close")
            ),
            // Generic catch-all for suspicious patterns (use with caution)
            AdRule(
                packageNamePattern = ".*", // Match any package
                titleKeywords = listOf("点击领取", "免费获得", "立即下载", "限时抢购", "仅剩", "秒杀"), // Common scam/ad phrases
                buttonTexts = listOf("关闭", "×", "Close")
            )
        )
    }
}