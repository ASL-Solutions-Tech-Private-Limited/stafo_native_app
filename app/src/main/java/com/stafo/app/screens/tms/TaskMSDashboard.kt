package com.stafo.app.screens.tms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTaskMsdashboardBinding

class TaskMSDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTaskMsdashboardBinding
    private lateinit var selectedTab: TextView
    private lateinit var tabList: List<TextView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_task_msdashboard)
        binding = ActivityTaskMsdashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            totalTask?.apply {
                summaryCount?.text = "6"
                summaryLabel?.text = "Total Tasks"
                iconSummary?.setImageResource(R.drawable.ic_task)
            }

            inProgressTask?.apply {
                summaryCount?.text = "2"
                summaryLabel?.text = "In Progress"
                iconSummary?.setImageResource(R.drawable.ic_pending)
            }

            completeTask?.apply {
                summaryCount?.text = "4"
                summaryLabel?.text = "Completed"
                iconSummary?.setImageResource(R.drawable.ic_complete)
            }

            overDuaTask?.apply {
                summaryCount?.text = "2"
                summaryLabel?.text = "Over Due"
                iconSummary?.setImageResource(R.drawable.ic_overdue)
            }

            btnAddTask.setOnClickListener {
                startActivity(Intent(this@TaskMSDashboard, CreateTaskActivity::class.java))
            }
        }
        setupTabSelection()
        setupRecyclerView()
    }


    private fun setupTabSelection() {
        // Initialize the tab list
        tabList = listOf(
            binding.tvAllTasks,
            binding.tvInProgress,
            binding.tvPending,
            binding.tvCompleted,
            binding.tvOverdue
        )

        // Set default selected tab
        selectedTab = binding.tvAllTasks
        selectTab(selectedTab)

        tabList.forEach { tab ->
            tab.setOnClickListener {
                if (tab != selectedTab) {
                    selectTab(tab)
                }
            }
        }
    }

    private fun selectTab(tab: TextView) {
        tabList.forEach {
            it.setBackgroundResource(R.drawable.bg_tab_unselected)
            it.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        tab.setBackgroundResource(R.drawable.bg_tab_selected)
        tab.setTextColor(ContextCompat.getColor(this, R.color.white))
        selectedTab = tab
        val selectedText = tab.text.toString()
        Log.d("SelectedTab", selectedText)
        // You can also call a function with selectedText
    }

    private fun setupRecyclerView() {
        val dummyTasks = listOf(
            TaskModel("Prepare quarterly report", "Aug 15", "High", "In Progress", "Hamid"),
            TaskModel("Team meeting follow-up", "Aug 17", "Medium", "Pending", "Sara"),
            TaskModel("Finalize UI Design", "Aug 10", "Low", "Completed", "Ali"),
            TaskModel("Client call review", "Aug 20", "High", "Overdue", "Ayesha"),
            TaskModel("Testing new build", "Aug 19", "High", "In Progress", "Omer"),
            TaskModel("Bug fixes - sprint 5", "Aug 22", "Medium", "Pending", "Zain"),
            TaskModel("Deployment setup", "Aug 18", "Low", "Completed", "Imran"),
            TaskModel("Write release notes", "Aug 23", "High", "Overdue", "Anam"),
            TaskModel("UX research", "Aug 16", "Medium", "In Progress", "Fahad"),
            TaskModel("New feature planning", "Aug 21", "High", "Pending", "Nida")
        )

        val taskAdapter = TaskAdapter(dummyTasks, {

        }, {}, {
            startActivity(Intent(this@TaskMSDashboard, TaskDescriptionActivity::class.java))
        })
        binding.recyclerTasks.adapter = taskAdapter
        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
    }


    data class TaskModel(
        val title: String,
        val date: String,
        val priority: String, // "High", "Medium", "Low"
        val status: String,   // "In Progress", "Pending", etc.
        val assignee: String
    )


}