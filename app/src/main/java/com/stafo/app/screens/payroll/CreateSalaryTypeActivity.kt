package com.stafo.app.screens.payroll

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateSalaryTypeBinding
import com.stafo.app.screens.settings.SettingsViewModel
import com.stafo.app.screens.settings.dataClass.SalaryTypeRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.doLogout
import com.stafo.app.utils.getCompanyDetails
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails

class CreateSalaryTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateSalaryTypeBinding
    private var mPaymentType: String = "Earning"
    private var mAmountType: String = "Flat"

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateSalaryTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {


        binding.apply {
            initPaymentType()
            initAmountType()



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }



            btnSubmit.setOnClickListener {
                if (isValidated()) {

                    if (tieDescription.text.isNullOrEmpty()) {
                        getEmployeeComId()?.let { it1 ->
                            val request = SalaryTypeRequest(
                                company_id = it1.toInt(),
                                payment_type = mPaymentType,
                                salary_type = tieSalary.text.toString().trim(),
                                salary_type_description = "",
                                amount = tieAmount.text.toString().trim(),
                                amount_type = mAmountType

                            )
                            settingsViewModel.createSalaryType(
                                this@CreateSalaryTypeActivity,
                                request
                            )

                        }

                    } else {
                        getEmployeeComId()?.let { it1 ->
                            val request = SalaryTypeRequest(
                                company_id = it1.toInt(),
                                payment_type = mPaymentType,
                                salary_type = tieSalary.text.toString().trim(),
                                salary_type_description = tieDescription.text.toString(),
                                amount = tieAmount.text.toString().trim(),
                                amount_type = mAmountType

                            )
                            settingsViewModel.createSalaryType(
                                this@CreateSalaryTypeActivity,
                                request
                            )
                        }
                    }

                }
            }


        }
    }

    private fun observeViewModel() {

        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mSalaryTypeResponse.observe(this) {
            if (it.success) {

                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()


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

    private fun isValidated(): Boolean {
        binding.apply {
            if (tieSalary.text.isNullOrEmpty()) {
                CustomToast(this@CreateSalaryTypeActivity, "Please enter salary type")
                return false
            } else if (tieAmount.text.isNullOrEmpty()) {
                CustomToast(this@CreateSalaryTypeActivity, "Please enter amount")
                return false
            }
        }
        return true
    }

    private fun initPaymentType() {
        val marital = resources.getStringArray(R.array.payment_type)
        val adapterMarital = ArrayAdapter(this, R.layout.custom_spinner_item, marital)
        binding.spinnerPaymentType.setAdapter(adapterMarital)

        binding.spinnerPaymentType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    mPaymentType = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
    }

    private fun initAmountType() {
        val marital = resources.getStringArray(R.array.amount_type)
        val adapterMarital = ArrayAdapter(this, R.layout.custom_spinner_item, marital)
        binding.spinnerAmountType.setAdapter(adapterMarital)

        binding.spinnerAmountType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    mAmountType = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
    }
}