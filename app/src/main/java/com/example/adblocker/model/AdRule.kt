package com.example.adblocker.model

/**
 * Represents a rule for detecting ad popups.
 * @param packageNamePattern A regex pattern to match against the package name of the window.
 * @param titleKeywords List of keywords that, if any are present in the window title/text, indicate an ad.
 * @param buttonTexts List of button texts that are typical for closing the ad (used for logging or future enhancement).
 */
data class AdRule(
    val packageNamePattern: String,
    val titleKeywords: List<String>,
    val buttonTexts: List<String>
) {
    /**
     * Checks if the given package name and text match this rule.
     * @param packageName The package name of the window.
     * @param text The text content of the window (concatenated from all text nodes).
     * @return true if the rule matches, false otherwise.
     */
    fun matches(packageName: String, text: String): Boolean {
        // Check package name pattern
        if (!packageName.matches(Regex(packageNamePattern), ignoreCase = true)) {
            return false
        }
        // Check if any title keyword is present in the text (case insensitive)
        val lowerText = text.lowercase()
        return titleKeywords.any { keyword -> lowerText.contains(keyword.lowercase()) }
    }
}