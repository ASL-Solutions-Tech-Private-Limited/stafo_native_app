package com.asl_emp_mng.app.screens.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.ActionsListAdapter
import com.asl_emp_mng.app.base.adapter.EmpListAdapter
import com.asl_emp_mng.app.base.adapter.RadioShiftAdapter
import com.asl_emp_mng.app.databinding.ActivityViewAllEmployeeBinding
import com.asl_emp_mng.app.screens.settings.dataClass.AssignShiftRequest
import com.asl_emp_mng.app.utils.CustomLoader
import com.asl_emp_mng.app.utils.CustomToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewAllEmployeeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAllEmployeeBinding

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: EmpListAdapter
    private lateinit var shiftID: String
    private var mFrom = "View All"



    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var shiftBottomSheetDialog: BottomSheetDialog
    private lateinit var rvRadioShift: RecyclerView
    private lateinit var rvRadioShiftAdapter: RadioShiftAdapter
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityViewAllEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mFrom = intent.getStringExtra("FROM").toString()

        setOnClickEvents()
        observeViewModel()
    }




    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mEmployeeListResponse.observe(this) {
            Log.d("res",it.message)
           if (it.status) {

               Log.d("res",it.data.toString())
               val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
               binding.rvViewEmpList.setLayoutManager(layoutManager)
               rvAdapter = EmpListAdapter(it.data, this, mFrom)
               binding.rvViewEmpList.adapter = rvAdapter
               rvAdapter.notifyDataSetChanged()

           } else {
              binding.txtMsg.visibility=View.VISIBLE
           }
       }


        settingsViewModel.mShiftListResponse.observe(this) {

            if (it.success) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                rvRadioShift.setLayoutManager(layoutManager)
                rvRadioShiftAdapter = RadioShiftAdapter(it.data, this,
                    object : RadioShiftAdapter.ActionClickListener {
                        override fun onActionClick(action: String) {
                            shiftID=action
                        }

                    })
                rvRadioShift.adapter = rvRadioShiftAdapter
                rvAdapter.notifyDataSetChanged()

            } else {
                CustomToast(this, it.message)
                shiftBottomSheetDialog.dismiss()
            }
        }




        settingsViewModel.mShiftAssignmentResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                shiftBottomSheetDialog.dismiss()

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

    private fun setOnClickEvents() {

        val curren = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        settingsViewModel.getEmpList(this@ViewAllEmployeeActivity, curren)


        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            settingsViewModel.getAllEmployeeList(this@ViewAllEmployeeActivity)

        }

        binding.imageBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }



    }

     @SuppressLint("MissingInflatedId")
     fun showCustomBottomSheet(id:String) {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.attendance_mode_bottom_sheet_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        val llFromOffice = view.findViewById<LinearLayout>(R.id.ll_from_office)
        val llFromAny = view.findViewById<LinearLayout>(R.id.ll_from_any)
        val imgOffice = view.findViewById<ImageView>(R.id.img_office)
        val imgAny = view.findViewById<ImageView>(R.id.img_any)

         llFromOffice.setOnClickListener {
             llFromOffice.setBackgroundResource(R.drawable.custom_switch_card_bg)
             llFromAny.setBackgroundResource(R.drawable.custom_switch_card_bg2)

             imgOffice.setImageResource(R.drawable.ic_lv_active_radio)
             imgAny.setImageResource(R.drawable.ic_lv_inactive_radio)

         }

         llFromAny.setOnClickListener {
             llFromOffice.setBackgroundResource(R.drawable.custom_switch_card_bg2)
             llFromAny.setBackgroundResource(R.drawable.custom_switch_card_bg)

             imgOffice.setImageResource(R.drawable.ic_lv_inactive_radio)
             imgAny.setImageResource(R.drawable.ic_lv_active_radio)
         }



        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()


    }

    @SuppressLint("MissingInflatedId")
    fun showShiftCustomBottomSheet(id:String) {
        shiftBottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.shift_time_bottom_sheet_layout, null)

        shiftBottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        shiftBottomSheetDialog.setCancelable(false)

        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)
        rvRadioShift = view.findViewById(R.id.rv_radio_shift)
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_submit)



        btnSubmit.setOnClickListener {


            if (::shiftID.isInitialized && shiftID.isNotEmpty()){
                val request=AssignShiftRequest(
                    employeeId = id,
                    shiftId = shiftID
                )
              Log.d("res","post: $request")
                settingsViewModel.assignShift(this,request)
            }else{
                CustomToast(this,"Please select shift!")
            }

        }



        settingsViewModel.getShiftList(this)




        btnCancel.setOnClickListener {
            shiftID=""
            shiftBottomSheetDialog.dismiss()
        }


        shiftBottomSheetDialog.setContentView(view)


        shiftBottomSheetDialog.show()


    }
}