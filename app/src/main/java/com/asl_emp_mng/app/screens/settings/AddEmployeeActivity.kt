package com.asl_emp_mng.app.screens.settings

import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityAddEmployeeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEmployeeBinding
    private val calendar = Calendar.getInstance()
    private var mSteps = 1
    private lateinit var selectGender: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding?.apply {
            rpbBasicInfo.setProgress(100f)

            // Customize colors
            rpbBasicInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
            rpbBasicInfo.setFilledColor(resources.getColor(R.color.primaryColor))
        }


        onClickListener()

    }

    private fun onClickListener() {
        binding?.apply {
            binding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = group.findViewById<RadioButton>(R.id.male)
                val radioButton1 = group.findViewById<RadioButton>(R.id.female)
                when (checkedId) {
                    R.id.male -> {
                        selectGender = "male"
                        radioButton.setTextColor(resources.getColor(R.color.white))
                        val drawable = radioButton.compoundDrawables[0]
                        drawable.setColorFilter(
                            resources.getColor(R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton.setCompoundDrawables(drawable, null, null, null)

                        radioButton1.setTextColor(resources.getColor(R.color.black))
                        val drawable1 = radioButton1.compoundDrawables[0]
                        drawable1.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton1.setCompoundDrawables(drawable1, null, null, null)
                    }

                    R.id.female -> {
                        selectGender = "female"
                        radioButton1.setTextColor(resources.getColor(R.color.white))
                        val drawable = radioButton1.compoundDrawables[0]
                        drawable.setColorFilter(
                            resources.getColor(R.color.white),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton1.setCompoundDrawables(drawable, null, null, null)

                        radioButton.setTextColor(resources.getColor(R.color.black))
                        val drawable1 = radioButton.compoundDrawables[0]
                        drawable1.setColorFilter(
                            resources.getColor(R.color.black),
                            PorterDuff.Mode.SRC_IN
                        )
                        radioButton.setCompoundDrawables(drawable1, null, null, null)
                    }
                }


            }

            btnNext.setOnClickListener {
                if (mSteps == 1) {
                    if (validateBasicInfo()) {
                        mSteps++
                        btnSkip.visibility=View.VISIBLE
                        switchScreen(1)
                    }
                } else if (mSteps == 2) {

                    mSteps++
                    switchScreen(2)

                } else if (mSteps == 3) {
                    mSteps++
                    btnSkip.visibility=View.GONE
                    btnNext.text="Submit"
                    switchScreen(3)

                }
            }

            btnSkip.setOnClickListener {
                if (mSteps == 1) {
                    if (validateBasicInfo()) {
                        mSteps++
                        switchScreen(1)
                    }
                } else if (mSteps == 2) {

                    mSteps++
                    switchScreen(2)

                } else if (mSteps == 3) {
                    mSteps++
                    btnSkip.visibility=View.GONE
                    btnNext.text="Submit"
                    switchScreen(3)

                }
            }

            binding.tieDateJoining.setOnClickListener {
                showDatePicker()
            }


        }
    }


    private fun validateBasicInfo(): Boolean {
        binding?.apply {
            if (tieStaffName.text.isNullOrEmpty()) {
                tieStaffName.error = "Please enter staff name"
                tieStaffName.requestFocus()
                return false
            } /*else if (tieJobTitle.text.isNullOrEmpty()) {
                tieJobTitle.error = "Please enter job title"
                return false
            } else if (tieBranch.text.isNullOrEmpty()) {
                tieBranch.error = "Please enter branch"
                tieBranch.requestFocus()
                return false
            } else if (tieDepartment.text.isNullOrEmpty()) {
                tieDepartment.error = "Please enter department"
                tieDepartment.requestFocus()
                return false
            } */else if (tieMobileNo.text.isNullOrEmpty()) {
                tieMobileNo.error = "Please enter mobile number"
                tieMobileNo.requestFocus()
                return false
            } else if (tieEmailId.text.isNullOrEmpty()) {
                tieEmailId.error = "Please enter office email id"
                tieEmailId.requestFocus()
                return false
            } else if (tieEmailId.text.isNullOrEmpty()) {
                tieEmailId.error = "Please enter office email id"
                tieEmailId.requestFocus()
                return false
            } else if (tieDateJoining.text.isNullOrEmpty()) {
                tieDateJoining.error = "Please enter date of joining"
                tieDateJoining.requestFocus()
                return false
            } else if (tieAddress.text.isNullOrEmpty()) {
                tieAddress.error = "Please enter address"
                tieAddress.requestFocus()
                return false
            }
        }
        return true
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.tieDateJoining.setText("$formattedDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun switchScreen(flag: Int) {
        when (flag) {
            1 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.VISIBLE
                binding?.llDocumentInfo?.visibility = View.GONE
                binding?.llEmploymentDetails?.visibility = View.GONE

                binding.rpbPersonalInfo.setProgress(100f)
                binding.rpbPersonalInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbPersonalInfo.setFilledColor(resources.getColor(R.color.primaryColor))

            }

            2 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.GONE
                binding?.llDocumentInfo?.visibility = View.VISIBLE
                binding?.llEmploymentDetails?.visibility = View.GONE

                binding.rpbDocumentInfo.setProgress(100f)
                binding.rpbDocumentInfo.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbDocumentInfo.setFilledColor(resources.getColor(R.color.primaryColor))

            }

            3 -> {
                binding?.llBasicInfo?.visibility = View.GONE
                binding?.llPersonalInfo?.visibility = View.GONE
                binding?.llDocumentInfo?.visibility = View.GONE
                binding?.llEmploymentDetails?.visibility = View.VISIBLE

                binding.rpbEmploymentDetails.setProgress(100f)
                binding.rpbEmploymentDetails.setUnfilledColor(resources.getColor(R.color.tea_green))
                binding.rpbEmploymentDetails.setFilledColor(resources.getColor(R.color.primaryColor))

            }
        }
    }
}