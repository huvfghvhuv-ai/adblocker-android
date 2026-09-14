package com.example.adblocker.model

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for the AdRule class.
 */
class AdRuleTest {

    @Test
    fun `test matches when package name matches and keyword present`() {
        val rule = AdRule(
            packageNamePattern = ".*com\\.example\\.app.*",
            titleKeywords = listOf("广告", "ad"),
            buttonTexts = listOf("关闭")
        )

        // Should match
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "这是一个广告"))
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "This is an ad"))
        assertTrue(rule.matches("com.example.app.sub.AdActivity", "广告内容"))
    }

    @Test
    fun `test does not match when package name does not match`() {
        val rule = AdRule(
            packageNamePattern = ".*com\\.example\\.app.*",
            titleKeywords = listOf("广告"),
            buttonTexts = listOf("关闭")
        )

        // Should not match because package name doesn't match
        assertFalse(rule.matches("com.other.app.ui.AdActivity", "这是一个广告"))
    }

    @Test
    fun `test does not match when package name matches but no keyword`() {
        val rule = AdRule(
            packageNamePattern = ".*com\\.example\\.app.*",
            titleKeywords = listOf("广告", "ad"),
            buttonTexts = listOf("关闭")
        )

        // Should not match because no keyword
        assertFalse(rule.matches("com.example.app.ui.MainActivity", "这是正常内容"))
        assertFalse(rule.matches("com.example.app.ui.MainActivity", "This is normal content"))
    }

    @Test
    fun `test matches is case insensitive for keywords`() {
        val rule = AdRule(
            packageNamePattern = ".*com\\.example\\.app.*",
            titleKeywords = listOf("广告", "AD"), // Note: uppercase AD
            buttonTexts = listOf("关闭")
        )

        // Should match regardless of case
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "这是一个广告")) // Chinese
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "this is an ad")) // lowercase
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "this is an AD")) // uppercase
        assertTrue(rule.matches("com.example.app.ui.AdActivity", "this is an Ad")) // mixed
    }

    @Test
    fun `test empty keywords list never matches`() {
        val rule = AdRule(
            packageNamePattern = ".*",
            titleKeywords = emptyList(),
            buttonTexts = listOf("关闭")
        )

        // Even if package matches, no keywords means no match
        assertFalse(rule.matches("any.package", "任何文本"))
    }

    @Test
    fun `test package name pattern as regex works`() {
        val rule = AdRule(
            packageNamePattern = ".*\\.admob\\.*.*", // Matches any package containing ".admob."
            titleKeywords = listOf("广告"),
            buttonTexts = listOf("关闭")
        )

        assertTrue(rule.matches("com.google.android.gms.ads.AdActivity", "广告"))
        assertTrue(rule.matches("com.example.app.admob.banner", "广告"))
        assertFalse(rule.matches("com.example.app", "广告")) // No .admob.
    }
}