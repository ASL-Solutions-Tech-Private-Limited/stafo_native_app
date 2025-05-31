package com.stafo.app.screens.tms

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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
import com.stafo.app.screens.tms.adapter.AdapterAssignTaskEmp
import com.stafo.app.screens.tms.dataClass.AssignTaskEmp
import com.stafo.app.screens.tms.dataClass.CreateTaskRequest
import com.stafo.app.screens.tms.dataClass.TaskAttachment
import com.stafo.app.screens.tms.dataClass.TaskData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getFormattedDate2
import com.stafo.app.utils.reportsFormatToMonthYear
import com.stafo.app.utils.showFormatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTaskBinding
    private lateinit var attachmentAdapter: AttachmentAdapter
    private lateinit var rvAssignEmpList: AdapterAssignTaskEmp
    private val assignEmpList = mutableListOf<AssignTaskEmp>()


    private val settingsViewModel: SettingsViewModel by viewModels()
    private val tmsViewModel: TMSViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var selectedPriority: String = ""
    private var selectedEndDate: String = ""
    private var selectedTaskStatus: String = ""
    private var postTaskId: Int=0

    private val fileUrl:String ="https://stafo.in/uploads/task"

    private val FILE_PICKER_REQUEST_CODE = 101

    val attachmentList = mutableListOf<TaskAttachment>()
    val toDeleteIds = mutableListOf<Int>()



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
        attachmentAdapter = AttachmentAdapter(
            attachmentList,
            onRemoveClick = { position ->
                if (position in attachmentList.indices) {
                    val attachment = attachmentList[position]
                    if (!attachment.isLocal && attachment.id != null) {
                        toDeleteIds.add(attachment.id)
                    }

                    attachmentList.removeAt(position)
                    attachmentAdapter.notifyItemRemoved(position)

                    if (attachmentList.isEmpty()) {
                        binding.rvAttachFiles.visibility = View.GONE
                    }
                }
            }
        )
        binding.rvAttachFiles.apply {
            layoutManager =
                LinearLayoutManager(this@CreateTaskActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {


            binding.rvAssignEmp.layoutManager =
                LinearLayoutManager(this@CreateTaskActivity, LinearLayoutManager.HORIZONTAL, false)
            rvAssignEmpList = AdapterAssignTaskEmp(assignEmpList)
            binding.rvAssignEmp.adapter = rvAssignEmpList


            val options = resources.getStringArray(R.array.task_status)


            val adapterSpinner = object : ArrayAdapter<String>(
                this@CreateTaskActivity, R.layout.custom_spinner_item, options
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_item)
                    textView.setTextColor(
                        if (position == 0) Color.GRAY else Color.BLACK
                    )
                    return view
                }

                override fun getDropDownView(
                    position: Int, convertView: View?, parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view.findViewById<TextView>(R.id.tv_item)
                    textView.setTextColor(
                        if (position == 0) Color.GRAY else Color.BLACK
                    )
                    return view
                }
            }

            spinnerTaskStatus.adapter = adapterSpinner
            spinnerTaskStatus.setSelection(0)
            spinnerTaskStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    if (position != 0) {
                        selectedTaskStatus = parent.getItemAtPosition(position).toString()
                    } else {
                        selectedTaskStatus = ""
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }





            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val task = intent.getSerializableExtra("task_data") as? TaskData
            val taskType = intent.getStringExtra("task_type") ?: ""

            if (taskType == "Update") {
                pageTitle.text = "Update Task"
                btnCreateTask.text = "Update"

                task?.let {
                    postTaskId=task.id
                    edtTaskTitle.setText(it.title)
                    edtTaskDescription.setText(it.description)
                    selectedEndDate = it.endDate

                    edtDeadline.setText(showFormatDate(it.endDate))

                    val statusIndex = options.indexOfFirst { status ->
                        status.equals(it.status, ignoreCase = true)
                    }
                    if (statusIndex >= 0) {
                        spinnerTaskStatus.setSelection(statusIndex)
                    }

                    if (it.assignedEmployees.isNotEmpty()) {
                        assignEmpList.clear()
                        it.assignedEmployees.forEach { emp ->
                            val selectedEmp = AssignTaskEmp(id = emp.pivot.employeeId, name = emp.name)
                            assignEmpList.add(selectedEmp)
                            if (::rvAssignEmpList.isInitialized) {
                                rvAssignEmpList.notifyItemInserted(assignEmpList.size - 1)
                            }
                        }
                    }

                    // <-- FIXED HERE: use your TaskAttachment data class properly -->
                    if (it.taskFiles.isNotEmpty()) {
                        attachmentList.clear()
                        it.taskFiles.forEach { file ->
                            attachmentList.add(
                                TaskAttachment(
                                    id = file.taskId,
                                    uri = Uri.parse(file.filename),
                                    isLocal = false,
                                    fileUrl = fileUrl
                                )
                            )
                            attachmentAdapter.notifyItemInserted(attachmentList.size - 1)
                        }
                        binding.rvAttachFiles.visibility = View.VISIBLE
                    }

                    // Remaining UI setup unchanged
                    when (it.priority.lowercase()) {
                        "low" -> binding.priorityLow.isChecked = true
                        "medium" -> binding.priorityMedium.isChecked = true
                        "high" -> binding.priorityHigh.isChecked = true
                        "urgent" -> binding.priorityUrgent.isChecked = true
                    }

                    updatePriorityUI()
                }
            } else {
                pageTitle.text = "Create Task"
                btnCreateTask.text = "Create"
            }










            updatePriorityUI()




            settingsViewModel.getAllEmployeeList(this@CreateTaskActivity)




            binding.ivBack.setOnClickListener {
                onBackPressed()
            }



            binding.btnAttach.setOnClickListener {
                openFilePicker()
            }

            binding.edtDeadline.setOnClickListener {
                showDatePicker()
            }

            binding.priorityGroup.setOnCheckedChangeListener { _, _ ->
                updatePriorityUI()
            }



            binding.btnCreateTask.setOnClickListener {
                if (isValidation()) {
                    val fileUris = attachmentAdapter.getAttachFiles()

                    Log.e("attachedFiles", "post file : $fileUris")
                    val title = binding.edtTaskTitle.text.toString()
                    val description = binding.edtTaskDescription.text.toString()
                    selectedPriority = getSelectedPriority()
                    if (assignEmpList.isNotEmpty()) {

                        if (!selectedPriority.isNullOrBlank()) {


                            if (taskType == "Update") {
                                val newFileUris = attachmentList.filter { it.isLocal }.mapNotNull { it.uri }

                                getEmployeeComId()?.let { it1 ->
                                    val request =   CreateTaskRequest(
                                        company_id = it1.toInt(),
                                        title = title,
                                        description = description,
                                        start_date = currentDate,
                                        end_date = selectedEndDate,
                                        status = selectedTaskStatus,
                                        priority = selectedPriority,
                                        task_assign = assignEmpList.mapNotNull { it.id },
                                        files = emptyList()
                                    )
                                    tmsViewModel.updateTask(this@CreateTaskActivity, postTaskId, request, newFileUris)
                                }


                            } else {

                                if (!selectedTaskStatus.isNullOrBlank()) {

                                    getEmployeeComId()?.let { it1 ->
                                    val request = CreateTaskRequest(
                                        company_id = it1.toInt(),
                                        title = title,
                                        description = description,
                                        start_date = currentDate,
                                        end_date = selectedEndDate,
                                        status = selectedTaskStatus,
                                        priority = selectedPriority,
                                        task_assign = assignEmpList.mapNotNull { it.id },
                                        files = emptyList()
                                    )

                                    val fileUris = attachmentList.mapNotNull { it.uri }

                                    tmsViewModel.createTask(this@CreateTaskActivity, request, fileUris)
                                    }

                                } else CustomToast(
                                    this@CreateTaskActivity, "Please select task status"
                                )


                            }

                        } else CustomToast(this@CreateTaskActivity, "Please select priority")


                    } else CustomToast(this@CreateTaskActivity, "Please assign employee")


                }

            }





            obverseViewModel()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                attachmentList.add(TaskAttachment(uri = uri, isLocal = true))
                binding.rvAttachFiles.visibility = View.VISIBLE
                attachmentAdapter.notifyItemInserted(attachmentList.size - 1)
            }
        }
    }




    private fun isValidation(): Boolean {
        binding.apply {
            if (edtTaskTitle.text.isNullOrEmpty()) {
                CustomToast(this@CreateTaskActivity, "Please enter task title")
                return false
            } else if (edtTaskDescription.text.isNullOrEmpty()) {
                CustomToast(this@CreateTaskActivity, "Please enter task description")
                return false
            } else if (edtDeadline.text.isNullOrEmpty()) {
                CustomToast(this@CreateTaskActivity, "Please enter deadline")
                return false
            }
        }
        return true
    }


    private fun obverseViewModel() {
        tmsViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") {
                customLoader.show()
            } else {
                customLoader.dismiss()
            }
        }

        tmsViewModel.mCreateTaskResponse.observe(this) {
            if (it.success) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else CustomToast(this, it.message)

        }


        tmsViewModel.mUpdateTaskResponse.observe(this) {
            if (it.success) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else CustomToast(this, it.message)

        }


        settingsViewModel.getLoaderLiveData().observe(this) {
            if (it == "load") {
                customLoader.show()
            } else {
                customLoader.dismiss()
            }
        }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) { employeeList ->
            if (!employeeList.data.isNullOrEmpty()) setupEmpListDialog(employeeList.data)
            else CustomToast(this, "No Employee Found")
        }
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
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val postFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val displayDate = displayFormat.format(selectedCal.time)
                val postDate = postFormat.format(selectedCal.time)
                binding.edtDeadline.setText(displayDate)
                selectedEndDate = postDate
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
                it, ContextCompat.getColorStateList(this, R.color.grey_300)
            ) // optional
            it.buttonTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this, R.color.grey_600
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
                        id = employee.id ?: 0, title = employee.name ?: "No Name"
                    )
                )
            }
        }
        binding.btnAssign.setOnClickListener {
            val dialog = SearchableDialog(this@CreateTaskActivity, empList, "Employee List")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    val selectedEmp = AssignTaskEmp(
                        id = searchListItem.id, name = searchListItem.title
                    )
                    if (!assignEmpList.any { it.id == selectedEmp.id }) {
                        assignEmpList.add(selectedEmp)
                        rvAssignEmpList.notifyItemInserted(assignEmpList.size - 1)
                    }
                    dialog.dismiss()
                }
            })
            dialog.show()
        }


        /* binding.btnAssign.setOnClickListener {
             val dialog = SearchableDialog(this@CreateTaskActivity, empList, "Employee List")
             dialog.setOnItemSelected(object : OnSearchItemSelected {
                 override fun onClick(position: Int, searchListItem: SearchListItem) {
                *//*     binding.btnAssign.visibility = View.GONE
                    binding.civAssign.visibility = View.VISIBLE
                    Glide.with(this@CreateTaskActivity)
                        .load(R.drawable.demo_avatar)
                        .into(binding.civAssign)
                    //   binding.etEmployee.setText(searchListItem.title)*//*


                    dialog.dismiss()
                }
            })
            dialog.show()
        }*/
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(
                Intent.EXTRA_MIME_TYPES, arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            )
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select a file"), FILE_PICKER_REQUEST_CODE
        )
    }



}