package com.example.adblocker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adblocker.blocklist.RuleManager
import com.example.adblocker.model.AdRule
import java.util.ArrayList

class RuleManagementActivity : AppCompatActivity() {

    private lateinit var ruleManager: RuleManager
    private lateinit var rulesList: ArrayList<AdRule>
    private lateinit var adapter: RuleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addRuleButton: Button
    private lateinit var packageNameEdit: EditText
    private lateinit var keywordsEdit: EditText
    private lateinit var buttonTextsEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_management)

        ruleManager = RuleManager(this)
        rulesList = ArrayList(ruleManager.loadRules())

        recyclerView = findViewById(R.id.rulesRecyclerView)
        adapter = RuleAdapter(rulesList) { position ->
            // Delete rule
            rulesList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveRules()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addRuleButton = findViewById(R.id.addRuleButton)
        packageNameEdit = findViewById(R.id.packageNameEdit)
        keywordsEdit = findViewById(R.id.keywordsEdit)
        buttonTextsEdit = findViewById(R.id.buttonTextsEdit)

        addRuleButton.setOnClickListener {
            val packageName = packageNameEdit.text.toString().trim()
            val keywordsText = keywordsEdit.text.toString().trim()
            val buttonTextsText = buttonTextsEdit.text.toString().trim()

            if (packageName.isEmpty() || keywordsText.isEmpty()) {
                Toast.makeText(this, "请填写包名和关键词", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val keywords = keywordsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val buttonTexts = buttonTextsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            val newRule = AdRule(packageName, keywords, buttonTexts)
            rulesList.add(newRule)
            adapter.notifyItemInserted(rulesList.lastIndex)
            saveRules()

            // Clear input fields
            packageNameEdit.text.clear()
            keywordsEdit.text.clear()
            buttonTextsEdit.text.clear()
        }
    }

    private fun saveRules() {
        ruleManager.saveRules(rulesList)
        Toast.makeText(this, "规则已保存", Toast.LENGTH_SHORT).show()
    }

    // Adapter for displaying rules in RecyclerView
    inner class RuleAdapter(
        private val rules: List<AdRule>,
        private val onDeleteClick: (Int) -> Unit
    ) : RecyclerView.Adapter<RuleAdapter.RuleViewHolder>() {

        inner class RuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val packageNameText: TextView = itemView.findViewById(R.id.rulePackageName)
            val keywordsText: TextView = itemView.findViewById(R.id.ruleKeywords)
            val buttonTextsText: TextView = itemView.findViewById(R.id.ruleButtonTexts)
            val deleteButton: ImageButton = itemView.findViewById(R.id.deleteRuleButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rule, parent, false)
            return RuleViewHolder(view)
        }

        override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
            val rule = rules[position]
            holder.packageNameText.text = "包名: ${rule.packageNamePattern}"
            holder.keywordsText.text = "关键词: ${rule.titleKeywords.joinToString(", ")}"
            holder.buttonTextsText.text = "按钮: ${rule.buttonTexts.joinToString(", ")}"
            holder.deleteButton.setOnClickListener { onDeleteClick(holder.adapterPosition) }
        }

        override fun getItemCount(): Int = rules.size
    }
}