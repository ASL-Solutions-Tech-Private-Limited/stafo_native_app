package com.stafo.app.screens.expense

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding2.widget.text
import com.rajat.pdfviewer.util.FileUtils
import com.stafo.app.R
import com.stafo.app.databinding.ActivityEmployeeApplyExpenseBinding
import com.stafo.app.screens.billpayment.dataClass.Category
import com.stafo.app.screens.expense.adapter.AdapterExpenseCategory
import com.stafo.app.screens.expense.dataClass.ApplyExpenseDetailRequest
import com.stafo.app.screens.expense.dataClass.ExpenseApplyRequest
import com.stafo.app.screens.expense.dataClass.ExpenseCategory
import com.stafo.app.screens.expense.dataClass.ExpenseDetail
import com.stafo.app.screens.expense.dataClass.ExpenseFormTypeList
import com.stafo.app.screens.expense.dataClass.UpdateExpenseDetailRequest
import com.stafo.app.screens.expense.dataClass.UpdateExpenseEmployeeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.formatCreatedAtDate
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsCOMPANYLogin
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmployeeApplyExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeApplyExpenseBinding


    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    private var expenseTypeId: String = ""

    private var selectedFile: File? = null
    private var attachFile: Boolean = false

    private val REQUEST_CODE_PICK_FILE = 101

    private var expenseId: Int = 0

    private val inputFieldMap = mutableMapOf<String, EditText>()
    private var selectedCategory: ExpenseFormTypeList? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmployeeApplyExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        expenseId = intent.getIntExtra("expense_id", 0)


        setOnClickListener()
        observeViewModel()
    }

    private fun setOnClickListener() {
        binding.apply {

            if (expenseId != 0) {
                Log.e("res", " get expense type ")
                tvTitlePageName.text = "Edit Expense"
                expenseViewModel.viewExpenseDetails(this@EmployeeApplyExpenseActivity, expenseId)

            } else {
                Log.e("res", " get expense type list ")
                getEmployeeComId()?.let {
                    expenseViewModel.getAllExpenseFormList(
                        this@EmployeeApplyExpenseActivity,
                        companyId = it
                    )
                }
            }



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }



            tieAttachFile.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    REQUEST_CODE_PICK_FILE
                )
            }

            binding.btnSubmit.setOnClickListener {
                val inputs = inputFieldMap.mapValues { it.value.text.toString().trim() }
                val hasEmptyField = inputs.any { it.value.isEmpty() }
                if (hasEmptyField) {
                    CustomToast(
                        this@EmployeeApplyExpenseActivity,
                        "Please fill in all required fields"
                    )
                    return@setOnClickListener
                }
                val expenseDetails = selectedCategory?.expense_forms?.map { field ->
                    ApplyExpenseDetailRequest(
                        expenseform_id = field.id,
                        expense_value = inputFieldMap[field.field_name]?.text.toString().trim()
                    )
                } ?: emptyList()

                val companyId = getEmployeeComId().toString()
                val employeeId = getEmployeeDetails()?.id.toString()
                val expenseTypeId = expenseTypeId
                val amount = binding.tieExpenseAmount.text.toString().trim()
                val attachmentFile: File? = selectedFile

                when {
                    amount.isEmpty() -> {
                        CustomToast(
                            this@EmployeeApplyExpenseActivity,
                            "Please enter expense amount"
                        )
                    }

                    expenseDetails.isNullOrEmpty() -> {
                        CustomToast(
                            this@EmployeeApplyExpenseActivity,
                            "Please fill all required expense details"
                        )
                    }

                    attachFile && attachmentFile == null -> {
                        CustomToast(
                            this@EmployeeApplyExpenseActivity,
                            "Please attach required document"
                        )
                    }

                    else -> {

                        if (attachFile) {
                            expenseViewModel.empApplyExpense(
                                mContext = this@EmployeeApplyExpenseActivity,
                                companyId = companyId,
                                employeeId = employeeId,
                                expenseTypeId = expenseTypeId,
                                amount = amount,
                                expenseDetails = expenseDetails,
                                attachmentFile = attachmentFile,
                                isDocumentRequired = attachFile
                            )
                        } else {
                            val request = ExpenseApplyRequest(
                                company_id = companyId.toInt(),
                                employee_id = employeeId.toInt(),
                                amount = amount,
                                expensetype_id = expenseTypeId.toInt(),
                                expense_details = expenseDetails
                            )

                            expenseViewModel.empApplyExpenseWithoutAttach(
                                this@EmployeeApplyExpenseActivity,
                                request
                            )
                        }


                    }
                }
            }

            binding.btnEditExpense.setOnClickListener {

                val inputs = inputFieldMap.mapValues { it.value.text.toString().trim() }

                val hasEmptyField = inputs.any { it.value.isEmpty() }
                if (hasEmptyField) {
                    CustomToast(
                        this@EmployeeApplyExpenseActivity,
                        "Please fill in all required fields"
                    )
                    return@setOnClickListener
                }

                val expenseDetails = inputFieldMap.map {
                    UpdateExpenseDetailRequest(
                        expense_id = expenseId,
                        expenseform_id = it.key.toInt(),
                        expense_value = it.value.text.toString().trim()
                    )
                }

                val amount = binding.tieExpenseAmount.text.toString().trim()
                if (amount.isEmpty()) {
                    CustomToast(this@EmployeeApplyExpenseActivity, "Please enter expense amount")
                    return@setOnClickListener
                }

                val request = UpdateExpenseEmployeeRequest(
                    company_id = getEmployeeComId()?.toInt() ?: 0,
                    employee_id = getEmployeeDetails()?.id ?: 0,
                    amount = amount,
                    status = "Pending",
                    expense_details = expenseDetails
                )

                expenseViewModel.employeeUpdateExpense(
                    this@EmployeeApplyExpenseActivity,
                    expenseId,
                    request
                )
            }


        }

    }

    private fun observeViewModel() {
        expenseViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        expenseViewModel.mGetAllExpenseFormListResponse.observe(this) { it ->
            if (it.status) {

                Log.e("res", " get expense success")
                if (!it.data.isNullOrEmpty()) {

                    val expenseCategories = it.data

                    val expenseTypeList = ArrayList<SearchListItem>().apply {
                        expenseCategories.forEach { category ->
                            add(
                                SearchListItem(
                                    id = category.id,
                                    title = category.name
                                )
                            )
                        }
                    }

                    Log.e("res", " get expense type list ${expenseTypeList}")

                    binding.tieExpenseType.setOnClickListener {
                        val dialog = SearchableDialog(
                            this@EmployeeApplyExpenseActivity, expenseTypeList, "Expense Type"
                        )
                        dialog.setOnItemSelected(object : OnSearchItemSelected {
                            override fun onClick(position: Int, searchListItem: SearchListItem) {
                                dialog.dismiss()
                                expenseTypeId = searchListItem.id.toString()
                                selectedCategory =
                                    expenseCategories.find { it.id == searchListItem.id }
                                if (selectedCategory != null && !isFinishing && !isDestroyed) {
                                    renderFields(selectedCategory!!)
                                    binding.tieExpenseType.setText(searchListItem.title)
                                    binding.btnSubmit.visibility = View.VISIBLE
                                } else {
                                    binding.btnSubmit.visibility = View.GONE
                                }
                            }
                        })

                        if (!isFinishing && !isDestroyed) {
                            dialog.show()
                        }
                    }
                }
            } else Log.e("res", " get expense else")
        }




        expenseViewModel.mViewExpenseDetailsResponse.observe(this) { it ->
            if (it.success) {


                val expense = it.data



                binding.tieExpenseAmount.setText(expense.amount)
                binding.tieExpenseType.setText(expense.expense_type.name)
                expenseTypeId = expense.expensetype_id.toString()

                if (expense.attachments.isNotEmpty()) {
                    val filename = expense.attachments.first().filename
                    binding.tieAttachFile.setText(filename)
                }

                renderFieldsFromExpenseDetails(
                    details = expense.expense_details,
                    hasAttachment = expense.attachments.isNotEmpty()
                )
            } else {
                CustomToast(this, it.message)
            }
        }




        expenseViewModel.mUpdateExpenseResponse.observe(this) { it ->
            if (it.success) {
                CustomToast(this, it.message)
                startActivity(Intent(this,ExpenseDashboardActivity::class.java))
                finish()
            } else CustomToast(this, it.message)
        }





        expenseViewModel.mEmpApplyExpenseResponse.observe(this) { it ->
            if (it.success) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()
            } else CustomToast(this, it.message)
        }


    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    @SuppressLint("MissingInflatedId")
    private fun renderFieldsFromExpenseDetails(
        details: List<ExpenseDetail>,
        hasAttachment: Boolean
    ) {


        binding.dynamicFieldContainer.removeAllViews()
        inputFieldMap.clear()
        binding.btnEditExpense.visibility = View.VISIBLE

        for (detail in details) {
            val field = detail.expense_form ?: continue
            val value = detail.expense_value

            val fieldView = layoutInflater.inflate(
                R.layout.item_expense_dynamic_input, binding.dynamicFieldContainer, false
            )

            val editText = fieldView.findViewById<TextInputEditText>(R.id.tie_expense_dynamic_field)
            val hintEdit = fieldView.findViewById<TextInputLayout>(R.id.til_expense_dynamic_field)

            if (editText == null) {
                Log.e("exp", "EditText not found in layout!")
                continue
            }

            hintEdit.hint = field.field_name
            editText.setText(value)

            // Map using field name (as in your original logic)
            inputFieldMap[field.id.toString()] = editText

            // Handle input type
            when (field.field_name.lowercase()) {
                "date" -> {
                    editText.inputType = InputType.TYPE_NULL
                    editText.isFocusable = false
                    editText.isFocusableInTouchMode = false
                    editText.setOnClickListener {
                        showDatePickerDialog(editText)
                    }
                }

                "text", "textarea" -> {
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                    if (field.field_name.equals("textarea", ignoreCase = true)) {
                        editText.maxLines = 4
                        editText.setLines(4)
                        editText.gravity = Gravity.TOP
                    }
                }

                "number" -> {
                    editText.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                }

                else -> {
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

            binding.dynamicFieldContainer.addView(fieldView)
        }


        // Show/hide attach file section
        binding.tilAttachFile.visibility = if (hasAttachment) View.VISIBLE else View.GONE
        attachFile = hasAttachment


    }


    @SuppressLint("MissingInflatedId")
    private fun renderFields(category: ExpenseFormTypeList) {

        binding.tieExpenseAmount.setText("")
        binding.tieAttachFile.setText("")
        binding.dynamicFieldContainer.removeAllViews()
        inputFieldMap.clear()

        for (field in category.expense_forms) {
            val fieldView = layoutInflater.inflate(
                R.layout.item_expense_dynamic_input, binding.dynamicFieldContainer, false
            )

            val editText = fieldView.findViewById<TextInputEditText>(R.id.tie_expense_dynamic_field)
            val hintEdit = fieldView.findViewById<TextInputLayout>(R.id.til_expense_dynamic_field)

            if (editText == null) {
                Log.e("exp", "EditText not found in layout!")
                continue
            }

            hintEdit.hint = field.field_name
            inputFieldMap[field.field_name] = editText

            when (field.field_type.lowercase()) {
                "date" -> {
                    editText.inputType = InputType.TYPE_NULL
                    editText.isFocusable = false
                    editText.isFocusableInTouchMode = false
                    editText.setOnClickListener {
                        showDatePickerDialog(editText)
                    }
                }

                "text", "textarea" -> {
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                    if (field.field_type.equals("textarea", ignoreCase = true)) {
                        editText.maxLines = 4
                        editText.setLines(4)
                        editText.gravity = Gravity.TOP
                    }
                }

                "number" -> {
                    editText.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                }

                else -> {
                    editText.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

            binding.dynamicFieldContainer.addView(fieldView)
        }

        binding.tilAttachFile.visibility =
            if (category.is_document_req.equals(
                    "yes",
                    ignoreCase = true
                )
            ) View.VISIBLE else View.GONE

        attachFile = category.is_document_req.equals("yes", ignoreCase = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_FILE) {
            val uri = data?.data
            uri?.let {
                selectedFile = com.stafo.app.screens.expense.FileUtils.getFileFromUri(this, it)
                if (selectedFile != null) {
                    binding.tieAttachFile.setText("${selectedFile!!.absolutePath}")
                    Log.d("FileSelect", "Selected: ${selectedFile!!.absolutePath}")
                } else {
                    CustomToast(this, "Failed to get file")
                }
            }
        }
    }


    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val selectedDate = dateFormat.format(calendar.time)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}