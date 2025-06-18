package com.stafo.app.screens.referral

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ActivityReferBinding
import com.stafo.app.screens.billpayment.Adapter.AdapterElectricityItemList
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.billpayment.OperatorDetailsActivity
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperatorRequest
import com.stafo.app.screens.referral.adapter.AdapterRefer
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class ReferActivity : AppCompatActivity() {

    private  lateinit var binding:ActivityReferBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()
    private lateinit var rvAdapter: AdapterRefer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityReferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnClickEvents()
        observeViewModel()


    }

    private fun setOnClickEvents() {

        binding.apply {
            billPaymentsViewModel.getReferList(this@ReferActivity)

            imageBack.setOnClickListener {
                onBackPressed()
            }

            tvSend.setOnClickListener {
                val code = binding.tvCode.text.toString()

                if (code.isNotEmpty()) {
                    val appLink = "https://play.google.com/store/apps/details?id=com.stafo.app"
                    val message = """
            Hey! 👋
            
            I recommend STAFO for boosting productivity and team tracking. Join using my referral code below.

            Use my referral code: ⭐ *$code* ⭐ to sign up.

            Download the app here:
            ⬇️ $appLink

            Cheers! 🚀
        """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        setPackage("com.whatsapp")
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        CustomToast(this@ReferActivity, "WhatsApp is not installed.")
                    }
                } else {
                    CustomToast(this@ReferActivity, "Referral code is not available!")
                }
            }


        }







    }

    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mReferralDetailsResponse.observe(this) {

            if (it.status) {

                val code = it.referral_code
                val count = it.referral_count

                if (code!= null) {
                    binding.tvCode.text = code
                }
                if (count != null){
                    binding.tvReferCount.text = "Your Referrer: $count"
                }

                if (!it.referral_list.isNullOrEmpty()) {
                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvReferList.setLayoutManager(layoutManager)
                    rvAdapter = AdapterRefer(it.referral_list, this)
                    binding.rvReferList.adapter = rvAdapter
                }

            } else CustomToast(this, it.message)


        }


    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }
}