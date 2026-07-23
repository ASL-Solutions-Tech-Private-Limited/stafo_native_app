package com.stafo.app.screens.performance

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreatePerformanceTypeBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.performance.adapter.AdapterPerformanceType
import com.stafo.app.screens.performance.dataClass.AddPerformanceRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class CreatePerformanceTypeActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCreatePerformanceTypeBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private var mID = ""
    private var mName = ""
    private var mDesc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCreatePerformanceTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mID = intent.getStringExtra("id") ?: ""
        mName = intent.getStringExtra("name") ?: ""
        mDesc = intent.getStringExtra("desc") ?: ""

        onClickListener()
        observeViewModel()

    }


    private fun onClickListener() {
        binding.apply {


            if (mID=="" && mName==""){
                tvTitle.text="Create Performance Type"
                tieDescription.setText("")
                tiePerformanceName.setText("")

            } else {
                tiePerformanceName.setText(mName)
                tieDescription.setText(mDesc)
                tvTitle.text="Update Performance Type"
            }


            getEmployeeComId()?.let {

                val request = ChatRequest(
                    company_id = it
                )

                billPaymentsViewModel.getPerformanceTypeList(
                    this@CreatePerformanceTypeActivity, request
                )
            }

            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnSubmit.setOnClickListener {

                  if (mID.isNotBlank() && mName.isNotBlank()){
                      Log.e("crm","update")
                      if (isValidate()){

                          if (!tieDescription.text.isNullOrEmpty()){
                              val request= AddPerformanceRequest(
                                  name = tiePerformanceName.text.toString(),
                                  description = tieDescription.text.toString()

                              )
                              billPaymentsViewModel.updatePerformanceType(this@CreatePerformanceTypeActivity,mID.toInt(), request)
                          }else{
                              val request= AddPerformanceRequest(
                                  name = tiePerformanceName.text.toString(),
                                  description = ""

                              )
                              billPaymentsViewModel.updatePerformanceType(this@CreatePerformanceTypeActivity,mID.toInt(), request)
                          }

                      }


                  }else{
                      if (isValidate()){

                          if (!tieDescription.text.isNullOrEmpty()){
                              val request= AddPerformanceRequest(
                                  name = tiePerformanceName.text.toString(),
                                  description = tieDescription.text.toString()

                              )
                              billPaymentsViewModel.createPerformance(this@CreatePerformanceTypeActivity, request)
                          }else{
                              val request= AddPerformanceRequest(
                                  name = tiePerformanceName.text.toString(),
                                  description = ""

                              )
                              billPaymentsViewModel.createPerformance(this@CreatePerformanceTypeActivity, request)
                          }

                      }
                  }



            }




        }
    }


    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mAddPerformanceResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()

            } else {
                CustomToast(this, it.message)
            }


        }

        billPaymentsViewModel.mUpdatePerformanceResponse.observe(this) {

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

    private fun isValidate(): Boolean {
        binding.apply {
            if (tiePerformanceName.text.isNullOrEmpty()) {
                CustomToast(this@CreatePerformanceTypeActivity,"Please enter performance type")
                return false
            }
        }
        return true
    }
}