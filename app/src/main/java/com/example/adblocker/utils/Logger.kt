package com.example.adblocker.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Simple logger that writes to a file in the app's internal storage and also to Logcat.
 */
object Logger {
    private const val TAG = "AdBlockerLogger"
    private const val LOG_FILE_NAME = "adblocker_log.txt"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    /**
     * Logs a message with the given level.
     * @param context The application context.
     * @param level The log level (e.g., Log.INFO, Log.DEBUG, Log.WARN, Log.ERROR).
     * @param tag The tag to identify the source of the log.
     * @param message The message to log.
     */
    fun log(context: Context, level: Int, tag: String, message: String) {
        // Also log to Logcat
        when (level) {
            Log.VERBOSE -> Log.v(tag, message)
            Log.DEBUG -> Log.d(tag, message)
            Log.INFO -> Log.i(tag, message)
            Log.WARN -> Log.w(tag, message)
            Log.ERROR -> Log.e(tag, message)
            else -> Log.i(tag, message)
        }

        // Write to file
        val timestamp = dateFormat.format(Date())
        val logLine = "[$timestamp] [$tag] $message\n"
        try {
            val logFile = File(context.filesDir, LOG_FILE_NAME)
            FileWriter(logFile, true).use { it.append(logLine) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }

    convenience fun info(context: Context, tag: String, message: String) {
        log(context, Log.INFO, tag, message)
    }

    convenience fun debug(context: Context, tag: String, message: String) {
        log(context, Log.DEBUG, tag, message)
    }

    convenience fun warn(context: Context, tag: String, message: String) {
        log(context, Log.WARN, tag, message)
    }

    convenience fun error(context: Context, tag: String, message: String) {
        log(context, Log.ERROR, tag, message)
    }
}