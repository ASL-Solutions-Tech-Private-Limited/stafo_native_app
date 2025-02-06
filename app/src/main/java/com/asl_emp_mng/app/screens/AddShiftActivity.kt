package com.asl_emp_mng.app.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityAddShiftBinding
import com.google.android.material.bottomsheet.BottomSheetDialog


class AddShiftActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddShiftBinding

    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var edtShiftName: AppCompatEditText
    private lateinit var edtShiftStartTime: AppCompatEditText
    private lateinit var edtShiftEndTime: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddShiftBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor= ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()

    }

    private fun onClickListener() {
        binding?.apply {


            llcAddShift.setOnClickListener {
                showCustomBottomSheet()
            }
            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }







        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showCustomBottomSheet(){
        bottomSheetDialog=BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.custom_bottom_sheet_add_shift_layout, null)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }

        bottomSheetDialog.setCancelable(false)

         edtShiftName = view.findViewById(R.id.edt_shift_name)
         edtShiftStartTime = view.findViewById(R.id.edt_shift_start_time)
         edtShiftEndTime = view.findViewById(R.id.edt_shift_end_time)
        val btnCancel = view.findViewById<AppCompatImageView>(R.id.bottom_sheet_cancel)

        btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val btnSubmit = view.findViewById<AppCompatButton>(R.id.btn_add_shift)

        btnSubmit.setOnClickListener {
            if (isValidate()){

            }
        }
        bottomSheetDialog.setContentView(view)


        bottomSheetDialog.show()



    }


    private fun isValidate(): Boolean {
        binding?.apply {
            if (edtShiftName.text.isNullOrEmpty()) {
                edtShiftName.error = "Please enter shift name"
                edtShiftName.requestFocus()
                return false
            } else if (edtShiftStartTime.text.isNullOrEmpty()){
                edtShiftStartTime.error = "Please enter shift start time"
                edtShiftStartTime.requestFocus()
                return false
            }else if (edtShiftEndTime.text.isNullOrEmpty()){
                edtShiftEndTime.error = "Please enter shift end time"
                edtShiftEndTime.requestFocus()
                return false
            }
        }
        return true
    }
}