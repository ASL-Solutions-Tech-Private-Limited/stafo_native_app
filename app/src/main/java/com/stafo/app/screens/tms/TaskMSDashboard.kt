package com.stafo.app.screens.tms

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTaskMsdashboardBinding
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.tms.dataClass.TaskData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getIsCOMPANYLogin
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskMSDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityTaskMsdashboardBinding
    private lateinit var selectedTab: TextView
    private lateinit var tabList: List<TextView>


    private var filteredTaskList: MutableList<TaskData> = mutableListOf()
    private lateinit var taskAdapter: TaskAdapter
    private var fullTaskList: MutableList<TaskData> = mutableListOf()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val tmsViewModel: TMSViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskMsdashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        setupViews()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let {
            tmsViewModel.getTaskList(this@TaskMSDashboard, it)
        }
    }


    private fun setupViews() {
        binding.apply {

            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = false

                getEmployeeComId()?.let {
                    tmsViewModel.getTaskList(this@TaskMSDashboard, it)
                }

            }


            getEmployeeComId()?.let {
                tmsViewModel.getTaskList(this@TaskMSDashboard, it)
            }


            totalTask.apply {
                summaryCount.text = "6"
                summaryLabel.text = "Total Tasks"
                iconSummary.setImageResource(R.drawable.ic_task)
            }

            inProgressTask.apply {
                summaryCount.text = "2"
                summaryLabel.text = "In Progress"
                iconSummary.setImageResource(R.drawable.ic_pending)
            }

            completeTask.apply {
                summaryCount.text = "4"
                summaryLabel.text = "Completed"
                iconSummary.setImageResource(R.drawable.ic_complete)
            }

            overDuaTask.apply {
                summaryCount.text = "2"
                summaryLabel.text = "Over Due"
                iconSummary.setImageResource(R.drawable.ic_overdue)
            }


            if (getIsCOMPANYLogin(this@TaskMSDashboard)){
                btnAddTask.visibility = View.VISIBLE
            }else{
                btnAddTask.visibility = View.GONE
            }

            btnAddTask.setOnClickListener {
                startActivity(Intent(this@TaskMSDashboard, CreateTaskActivity::class.java))
            }
        }
        setupTabSelection()
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

        filterTasksByTab()
    }


    private fun observeViewModel() {


        tmsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }


        tmsViewModel.mTaskListResponse.observe(this) {
            if (it.success) {
                if (!it.data.isNullOrEmpty()) {

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val todayString = dateFormat.format(Date())
                    val today = dateFormat.parse(todayString)

                    val totalTasks = it.data.size

                    val inProgressTasks = it.data.count { task ->
                        val endDate = dateFormat.parse(task.endDate)
                        (task.status.equals(
                            "active",
                            ignoreCase = true
                        ) || task.status.equals("pending", ignoreCase = true) || task.status.equals(
                            "in progress",
                            ignoreCase = true
                        )) && endDate != null && (endDate.equals(today) || endDate.after(today))
                    }

                    val completedTasks = it.data.count { task ->
                        task.status.equals("completed", ignoreCase = true)
                    }


                    val overdueTasks = it.data.count { task ->
                        val endDate = dateFormat.parse(task.endDate)
                        (task.status.equals(
                            "pending",
                            ignoreCase = true
                        ) || task.status.equals(
                            "in progress",
                            ignoreCase = true
                        ) || task.status.equals(
                            "active",
                            ignoreCase = true
                        )) && endDate != null && endDate.before(today)
                    }


                    // Log for debug
                    Log.d("TaskCounts", "Total: $totalTasks")
                    Log.d("TaskCounts", "Completed: $completedTasks")
                    Log.d("TaskCounts", "In Progress: $inProgressTasks")
                    Log.d("TaskCounts", "Overdue: $overdueTasks")

                    // Set values to UI
                    binding.totalTask.summaryCount.text = totalTasks.toString()
                    binding.inProgressTask.summaryCount.text = inProgressTasks.toString()
                    binding.completeTask.summaryCount.text = completedTasks.toString()
                    binding.overDuaTask.summaryCount.text = overdueTasks.toString()

                    fullTaskList = it.data?.toMutableList() ?: mutableListOf()
                    setupRecyclerView()
                    filterTasksByTab()


                } else {

                    binding.totalTask.summaryCount.text = "0"
                    binding.inProgressTask.summaryCount.text = "0"
                    binding.completeTask.summaryCount.text = "0"
                    binding.overDuaTask.summaryCount.text = "0"
                }
            } else {
                CustomToast(this, it.message)
            }
        }



        tmsViewModel.mDeleteTaskResponse.observe(this) {

            if (it.status) {
                CustomToast(this, it.message)
                getEmployeeComId()?.let {
                    tmsViewModel.getTaskList(this@TaskMSDashboard, it)
                }

            } else {
                CustomToast(this, it.message)
            }
        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }

    private fun setupRecyclerView() {
        val isLogin= getIsCOMPANYLogin(this)

        taskAdapter = TaskAdapter(mutableListOf(), isLogin,{ task ->
            val intent = Intent(this@TaskMSDashboard, CreateTaskActivity::class.java)
            intent.putExtra("task_data", task)
            intent.putExtra("task_type", "Update")
            startActivity(intent)
        }, { task -> showCompanyDeleteDialog(task.id) }, { task ->
            val intent = Intent(this@TaskMSDashboard, TaskDescriptionActivity::class.java)
            intent.putExtra("task_data", task)
            startActivity(intent)
        })

        binding.recyclerTasks.adapter = taskAdapter
        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
    }


    private fun filterTasksByTab() {
        if (!::taskAdapter.isInitialized) return

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.parse(dateFormat.format(Calendar.getInstance().time))!!

        val filtered = when (selectedTab.text.toString().lowercase()) {
            "all tasks" -> fullTaskList

            "in progress" -> fullTaskList.filter { task ->
                task.status.equals("active", ignoreCase = true) || task.status.equals(
                    "in progress",
                    ignoreCase = true
                )
            }

            "pending" -> fullTaskList.filter {
                it.status.equals("pending", ignoreCase = true)
            }

            "completed" -> fullTaskList.filter {
                it.status.equals("completed", ignoreCase = true)
            }

            "overdue" -> fullTaskList.filter { task ->
                val endDate = dateFormat.parse(task.endDate)
                (task.status.equals(
                    "active",
                    ignoreCase = true
                ) || task.status.equals(
                    "in progress",
                    ignoreCase = true
                ) || task.status.equals(
                    "pending",
                    ignoreCase = true
                )) && endDate != null && endDate < today
            }

            else -> fullTaskList
        }

        filteredTaskList.clear()
        filteredTaskList.addAll(filtered)
        taskAdapter.updateData(filteredTaskList)
    }


    private fun showCompanyDeleteDialog(itemId: Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            tmsViewModel.deleteTask(this@TaskMSDashboard, itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}