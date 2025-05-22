package com.stafo.app.screens.tms

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.bumptech.glide.Glide
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateTaskBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import java.util.Calendar

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var attachmentAdapter: AttachmentAdapter
    private val attachmentList = mutableListOf<String>() // Replace with your actual file model
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  setContentView(R.layout.activity_create_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        attachmentAdapter = AttachmentAdapter(attachmentList)
        binding.rvAttachFiles.apply {
            layoutManager =
                LinearLayoutManager(this@CreateTaskActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentAdapter
        }
    }

    private fun setupListeners() {
        settingsViewModel.getAllEmployeeList(this)
        updatePriorityUI()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }



        binding.btnAttach.setOnClickListener {
            addDummyAttachment() // Replace with actual file picker
        }

        binding.edtDeadline.setOnClickListener {
            showDatePicker()
        }

        binding.priorityGroup.setOnCheckedChangeListener { _, _ ->
            updatePriorityUI()
        }



        binding.btnCreateTask.setOnClickListener {
            val title = binding.edtTaskTitle.text.toString()
            val description = binding.edtTaskDescription.text.toString()
            val deadline = binding.edtDeadline.text.toString()
            val priority = getSelectedPriority()

            // Validate and handle task creation
            Toast.makeText(this, "Task Created with priority: $priority", Toast.LENGTH_SHORT).show()
        }

        obverseViewModel()
    }

    private fun obverseViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") {
                customLoader.show()
            } else {
                customLoader.dismiss()
            }
        }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) { employeeList ->
            if (!employeeList.data.isNullOrEmpty())
                setupEmpListDialog(employeeList.data)
            else CustomToast(this, "No Employee Found")
        }
    }

    private fun addDummyAttachment() {
        attachmentList.add("File ${attachmentList.size + 1}")
        binding.rvAttachFiles.visibility = View.VISIBLE
        attachmentAdapter.notifyItemInserted(attachmentList.size - 1)
    }

    private fun getSelectedPriority(): String {
        return when (binding.priorityGroup.checkedRadioButtonId) {
            R.id.priorityLow -> "Low"
            R.id.priorityMedium -> "Medium"
            R.id.priorityHigh -> "High"
            R.id.priorityUrgent -> "Urgent"
            else -> "Not Set"
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                binding.edtDeadline.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updatePriorityUI() {
        val low = binding.priorityLow
        val medium = binding.priorityMedium
        val high = binding.priorityHigh
        val urgent = binding.priorityUrgent

        val allButtons = listOf(low, medium, high, urgent)

        // Reset all to default color
        allButtons.forEach {
            it.setTextColor(ContextCompat.getColor(this, R.color.black))
            ViewCompat.setBackgroundTintList(
                it,
                ContextCompat.getColorStateList(this, R.color.grey_300)
            ) // optional
            it.buttonTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.grey_600
                )
            ) // default radio dot color
        }

        // Set color based on selection
        when (binding.priorityGroup.checkedRadioButtonId) {
            R.id.priorityLow -> {
                low.setTextColor(ContextCompat.getColor(this, R.color.priority_low))
                low.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.priority_low))
            }

            R.id.priorityMedium -> {
                medium.setTextColor(ContextCompat.getColor(this, R.color.priority_medium))
                medium.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.priority_medium))
            }

            R.id.priorityHigh -> {
                high.setTextColor(ContextCompat.getColor(this, R.color.priority_high))
                high.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.priority_high))
            }

            R.id.priorityUrgent -> {
                urgent.setTextColor(ContextCompat.getColor(this, R.color.priority_urgent))
                urgent.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.priority_urgent))
            }

        }
    }

    private fun setupEmpListDialog(data: List<GetEmployee>) {
        val empList = ArrayList<SearchListItem>().apply {
            data.forEach { employee ->
                add(
                    SearchListItem(
                        id = employee.id ?: 0,
                        title = employee.name ?: "No Name"
                    )
                )
            }
        }
        binding.btnAssign.setOnClickListener {
            val dialog = SearchableDialog(this@CreateTaskActivity, empList, "Employee List")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.btnAssign.visibility = View.GONE
                    binding.civAssign.visibility = View.VISIBLE
                    Glide.with(this@CreateTaskActivity)
                        .load(R.drawable.demo_avatar)
                        .into(binding.civAssign)
                    //   binding.etEmployee.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }

}