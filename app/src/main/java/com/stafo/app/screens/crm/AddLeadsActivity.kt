package com.stafo.app.screens.crm

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityAddLeadsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddLeadsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLeadsBinding

    private val leadSources = arrayListOf(
        SearchListItem(1, "Facebook"),
        SearchListItem(2, "Instagram"),
        SearchListItem(3, "LinkedIn"),
        SearchListItem(4, "Google Ads"),
        SearchListItem(5, "Referral")
    )

    private val leadStatuses = arrayListOf(
        SearchListItem(1, "New"),
        SearchListItem(2, "Contacted"),
        SearchListItem(3, "Qualified"),
        SearchListItem(4, "Lost"),
        SearchListItem(5, "Customer")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_add_leads)
        binding = ActivityAddLeadsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        // Set up searchable dialog for Lead Source
        binding.etfrom.setOnClickListener {

            val dialog = SearchableDialog(this, leadSources, "Lead Source")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.etfrom.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

        // Set up searchable dialog for Lead Status
        binding.etStatus.setOnClickListener {
            val dialog = SearchableDialog(this, leadStatuses, "Lead Status")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.etStatus.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

        binding.etnextfollowup.setOnClickListener {
            showCalendarAndSetDate()
        }

        // Save button click
        binding.btnSaveLead.setOnClickListener {
            if (validateInputs()) {
                // Handle data saving
                Toast.makeText(this, "Lead saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val company = binding.etCompany.text.toString().trim()
        val from = binding.etfrom.text.toString().trim()
        val status = binding.etStatus.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        return when {
            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                false
            }

            phone.isEmpty() -> {
                binding.etPhone.error = "Phone number is required"
                false
            }

            !android.util.Patterns.PHONE.matcher(phone).matches() -> {
                binding.etPhone.error = "Invalid phone number"
                false
            }

            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                false
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.etEmail.error = "Invalid email address"
                false
            }

            company.isEmpty() -> {
                binding.etCompany.error = "Company name is required"
                false
            }

            from.isEmpty() -> {
                binding.etfrom.error = "Select lead source"
                false
            }

            status.isEmpty() -> {
                binding.etStatus.error = "Select lead status"
                false
            }

            else -> true
        }
    }

    private fun showCalendarAndSetDate() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                val displayFormat = SimpleDateFormat("dd MMM yy", Locale.US)
                val formattedDate = displayFormat.format(calendar.time)

                binding.etnextfollowup.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

}