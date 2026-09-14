package com.example.adblocker

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.adblocker.utils.Logger
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class LogsActivity : AppCompatActivity() {

    private lateinit var logsTextView: TextView
    private val logFileName = "adblocker_log.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        logsTextView = findViewById(R.id.logsTextView)
        logsTextView.movementMethod = ScrollingMovementMethod()

        loadAndDisplayLogs()
    }

    private fun loadAndDisplayLogs() {
        val logFile = File(filesDir, logFileName)
        if (!logFile.exists()) {
            logsTextView.text = "暂无日志记录"
            return
        }

        val logs = StringBuilder()
        try {
            BufferedReader(FileReader(logFile)).use { reader ->
                var line: String?
                while ({ line = reader.readLine() }() && line != null) {
                    logs.append(line).append('\n')
                }
            }
        } catch (e: Exception) {
            logs.append("读取日志时出错: ").append(e.message)
        }

        logsTextView.text = logs.toString()
    }
}