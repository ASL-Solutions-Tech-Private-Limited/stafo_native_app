package com.stafo.app.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterAllEmpList
import com.stafo.app.base.adapter.EmpListAdapter
import com.stafo.app.base.adapter.RadioShiftAdapter
import com.stafo.app.databinding.ActivityAssignBranchBinding
import com.stafo.app.screens.settings.dataClass.AssignBranchRequest
import com.stafo.app.screens.settings.dataClass.AssignDepartmentRequest
import com.stafo.app.screens.settings.dataClass.DataBranch
import com.stafo.app.screens.settings.dataClass.DataDepartment
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class AssignBranchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssignBranchBinding
    private var mAssignType = ""


    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var empList: List<GetEmployee> = listOf()
    private var filteredList: List<GetEmployee> = listOf()
    private lateinit var rvAdapter: AdapterAllEmpList


    private var selectBranch: Int = 0
    private var selectDepartment: Int = 0

    private lateinit var branchDialog: SearchableDialog
    private lateinit var departmentDialog: SearchableDialog

    private var mDepartmentList: ArrayList<DataDepartment>? = ArrayList()
    private var mBranchList: ArrayList<DataBranch>? = ArrayList()

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var tieBranch: TextInputEditText
    private lateinit var tieDepartment: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssignBranchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        mAssignType = intent.getStringExtra("Assign_Type").toString()


        setOnClickEvents()
        observeViewModel()
        setupSearchListener()

    }

    private fun setOnClickEvents() {


        settingsViewModel.getAllEmployeeList(this@AssignBranchActivity)


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            settingsViewModel.getAllEmployeeList(this@AssignBranchActivity)

        }

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }


    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getAllEmployeeList(this@AssignBranchActivity)
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mGetAllEmployeeResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    binding.etDirSearch.isFocusable = true
                    binding.etDirSearch.isFocusableInTouchMode = true

                    empList = it.data
                    filteredList = empList

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvViewEmpList.setLayoutManager(layoutManager)
                    rvAdapter = AdapterAllEmpList(empList, this, mAssignType)
                    binding.rvViewEmpList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()


                } else {
                    binding.etDirSearch.isFocusable = false
                    binding.etDirSearch.isFocusableInTouchMode = false
                    binding.txtMsg.visibility = View.VISIBLE
                }


            } else {
                binding.etDirSearch.isFocusable = false
                binding.etDirSearch.isFocusableInTouchMode = false
                binding.txtMsg.visibility = View.VISIBLE
            }
        }



        settingsViewModel.mBranchListResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                mBranchList = it.data
                setupSearchableDialog(
                    mBranchList,
                    "Branch",
                    tieBranch
                )
            }

        }




        settingsViewModel.mDepartmentListResponse.observe(this) {

            if (it.data.isNotEmpty()) {
                mDepartmentList = it.data
                setupSearchableDialog(
                    mDepartmentList,
                    "Department",
                    tieDepartment
                )
            }

        }


        settingsViewModel.mAssignBranchResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                bottomSheetDialog.dismiss()
            } else {
                CustomToast(this, it.message)
            }
        }

        settingsViewModel.mAssignDepartmentResponse.observe(this) {
            if (it.status) {
                CustomToast(this, it.message)
                bottomSheetDialog.dismiss()
            } else {
                CustomToast(this, it.message)
            }
        }


    }


    @SuppressLint("MissingInflatedId")
    fun showAssignBottomSheet(id: String) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_assign_branch_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        val textView = view.findViewById<TextView>(R.id.text_view)
        val tilBranch = view.findViewById<TextInputLayout>(R.id.til_branch)
        val tilDepartment = view.findViewById<TextInputLayout>(R.id.til_department)
        tieBranch = view.findViewById(R.id.tie_branch)
        tieDepartment = view.findViewById(R.id.tie_department)

        if (mAssignType == "department") {
            textView.text = "Assign Department"
            tilDepartment.visibility=View.VISIBLE
            getEmployeeComId()?.let {
                settingsViewModel.getDepartmentList(
                    this@AssignBranchActivity,
                    it
                )
            }
        } else {
            textView.text = "Assign Branch"

            tilBranch.visibility=View.VISIBLE
            getEmployeeComId()?.let { settingsViewModel.getBranchList(this, it) }
        }


        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)

        val btnSubmit = view.findViewById<Button>(R.id.btn_next)

        tieBranch.setOnClickListener {

            if (!mBranchList.isNullOrEmpty()) {
                branchDialog.show()
            } else {

                startActivity(Intent(this@AssignBranchActivity, AddBranchActivity::class.java))
                bottomSheetDialog.dismiss()
            }

        }
        tieDepartment.setOnClickListener {

            if (!mDepartmentList.isNullOrEmpty()) {
                departmentDialog.show()
            } else {
                startActivity(Intent(this@AssignBranchActivity, AddDepartmentActivity::class.java))
                bottomSheetDialog.dismiss()
            }

        }





        btnSubmit.setOnClickListener {


            if (mAssignType == "department"){

                if (selectDepartment==0){
                    CustomToast(this, "Please select department!")
                }else{
                    val request=AssignDepartmentRequest(
                        employee_id =id,
                        department_id = selectDepartment
                    )

                    settingsViewModel.assignDepartment(this, request)
                }

            }else{

                if (selectBranch==0){
                    CustomToast(this, "Please select branch!")
                }else{
                    val request= AssignBranchRequest(
                        employee_id=id,
                        branch_id = selectBranch
                    )
                    settingsViewModel.assignBranch(this,request)
                }


            }


        }






        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun setupSearchableDialog(
        dataList: List<Any>?,
        title: String,
        field: TextInputEditText
    ) {
        val items = dataList?.map {
            val name = when (it) {
                is DataBranch -> it.branch_name
                is DataDepartment -> it.name
                else -> "Unknown"
            }

            val id = when (it) {
                is DataBranch -> it.id
                is DataDepartment -> it.id
                else -> -1
            }

            SearchListItem(id, name)
        } ?: emptyList()

        val dialog = SearchableDialog(this, items as ArrayList<SearchListItem>, title)
        dialog.setOnItemSelected(object : OnSearchItemSelected {
            override fun onClick(position: Int, searchListItem: SearchListItem) {
                field.setText(searchListItem.title)
                if (title == "Branch") {
                    selectBranch = searchListItem.id
                } else if (title == "Department") {
                    selectDepartment = searchListItem.id
                }

                dialog.dismiss()


            }
        })
        when (title) {
            "Branch" -> branchDialog = dialog
            "Department" -> departmentDialog = dialog
        }
    }

    private fun setupSearchListener() {
        binding.etDirSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            empList
        } else {
            empList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }
        }

        rvAdapter.updateList(filteredList)
    }
}