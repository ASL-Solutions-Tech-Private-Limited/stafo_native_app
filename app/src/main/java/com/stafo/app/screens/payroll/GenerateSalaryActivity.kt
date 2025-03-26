package com.stafo.app.screens.payroll

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityGenerateSalaryBinding
import com.stafo.app.screens.settings.dataClass.AddEmpRequestBody
import com.stafo.app.utils.CustomToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GenerateSalaryActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGenerateSalaryBinding

    private var mMonthOfSalary: String = ""
    private val calendar = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityGenerateSalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()

    }

    private fun onClickListener() {


        binding.apply {




            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            tieMonth.setOnClickListener {
                showDatePicker()
            }

            btnSubmit.setOnClickListener {
                if (isValidated()){

                }
            }


        }
    }

    private fun isValidated(): Boolean {
        binding.apply {
            if (tieEmployee.text.isNullOrEmpty()) {
                CustomToast(this@GenerateSalaryActivity,"Please select employee")
                return false
            }  else if (tieMonth.text.isNullOrEmpty()) {
                CustomToast(this@GenerateSalaryActivity,"Please select month")
                return false
            }
        }
        return true
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                mMonthOfSalary=formattedDate

                val displayFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val formattedDisplayDate = displayFormat.format(selectedDate.time)

                binding.tieMonth.setText("$formattedDisplayDate")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}