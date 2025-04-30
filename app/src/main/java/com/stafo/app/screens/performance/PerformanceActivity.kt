package com.stafo.app.screens.performance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPerformanceBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.performance.adapter.AdapterPerformanceType
import com.stafo.app.screens.performance.dataClass.DeletePerformanceRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId

class PerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerformanceBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()
    private lateinit var rvAdapter: AdapterPerformanceType



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPerformanceBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let {

            val request = ChatRequest(
                company_id = it
            )

            billPaymentsViewModel.getPerformanceTypeList(
                this@PerformanceActivity, request
            )
        }
    }

    private fun onClickListener() {
        binding.apply {


            getEmployeeComId()?.let {

                val request = ChatRequest(
                    company_id = it
                )

                billPaymentsViewModel.getPerformanceTypeList(
                    this@PerformanceActivity, request
                )
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            llcAdd.setOnClickListener {
              startActivity(Intent(this@PerformanceActivity,CreatePerformanceTypeActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            }


        }
    }




    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mPerformanceTypeResponse.observe(this) {

            if (it.success) {
                if (!it.data.isNullOrEmpty()) {

                    binding.txtMsg.visibility=View.GONE
                    binding.rvPerformanceList.visibility=View.VISIBLE

                    rvAdapter = AdapterPerformanceType(it.data, this)
                    binding.rvPerformanceList.layoutManager = LinearLayoutManager(this)
                    binding.rvPerformanceList.adapter = rvAdapter

                }else{

                    binding.txtMsg.visibility=View.VISIBLE
                    binding.rvPerformanceList.visibility=View.GONE
                }

            } else {

                binding.txtMsg.visibility=View.GONE
                binding.rvPerformanceList.visibility=View.VISIBLE
                CustomToast(this, it.message)
            }


        }
        billPaymentsViewModel.mDeletePerformanceResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                getEmployeeComId()?.let {

                    val request = ChatRequest(
                        company_id = it
                    )

                    billPaymentsViewModel.getPerformanceTypeList(
                        this@PerformanceActivity, request
                    )
                }

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

     fun deletePerformanceType(id:Int){

        billPaymentsViewModel.deletePerformanceType(
            this@PerformanceActivity, id
        )



    }
}