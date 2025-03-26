package com.stafo.app.screens.reports

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterDownloadReports
import com.stafo.app.databinding.ActivityAttendanceReportBinding

class AttendanceReportActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAttendanceReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAttendanceReportBinding.inflate(layoutInflater)
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


            imageBack.setOnClickListener {
               onBackPressed()
            }

            rgFormat.setOnCheckedChangeListener { group, checkedId ->
                val radioButtonCSV = findViewById<RadioButton>(R.id.csv)
                val radioButtonXLS = findViewById<RadioButton>(R.id.xsl)
                val radioButtonPDF = findViewById<RadioButton>(R.id.pdf)

                val allRadioButtons = listOf(radioButtonCSV, radioButtonXLS, radioButtonPDF)

                allRadioButtons.forEach { radioButton ->
                    radioButton.setTextColor(resources.getColor(R.color.black))
                    radioButton.compoundDrawables[0]?.setColorFilter(
                        resources.getColor(R.color.black),
                        PorterDuff.Mode.SRC_IN
                    )
                }


                val selectedRadioButton = findViewById<RadioButton>(checkedId)

                selectedRadioButton.setTextColor(resources.getColor(R.color.white))
                selectedRadioButton.compoundDrawables[0]?.setColorFilter(
                    resources.getColor(R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
            }



        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }
}