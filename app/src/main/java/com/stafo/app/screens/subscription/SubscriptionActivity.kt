package com.stafo.app.screens.subscription

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUBillingCycle
import com.payu.base.models.PayUPaymentParams
import com.payu.base.models.PayUSIParams
import com.payu.base.models.PayuBillingLimit
import com.payu.base.models.PayuBillingRule
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener

import com.stafo.app.R
import com.stafo.app.databinding.ActivitySubscriptionBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.subscription.dataClass.PackageData
import com.stafo.app.utils.CustomLoader
import java.security.MessageDigest


class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterPlanList

    private lateinit var planViews: Map<TextView, Int>


    private lateinit var list: List<PackageData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
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

            billPaymentsViewModel.getPackagePlanList(
                this@SubscriptionActivity
            )

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            planViews = mapOf(
                tvMonthly to R.drawable.card_rtl_bg_offer,
                tvQuarterly to R.drawable.card_rtl_bg_offer2,
                tvHlfYearly to R.drawable.card_rtl_bg_offer3,
                tvYearly to R.drawable.card_rtl_bg_offer4
            )


            tvMonthly.setOnClickListener { selectPlan(0, tvMonthly) }
            tvQuarterly.setOnClickListener { selectPlan(1, tvQuarterly) }
            tvHlfYearly.setOnClickListener { selectPlan(2, tvHlfYearly) }
            tvYearly.setOnClickListener { selectPlan(3, tvYearly) }


            btnBuyNow.setOnClickListener {
                callPayuSdk()
            }




        }
    }

    private fun selectPlan(index: Int, selectedView: TextView) {
        if (!::list.isInitialized || list.size <= index) return

        for ((view, bgRes) in planViews) {
            if (view == selectedView) {
                view.setBackgroundResource(bgRes)
                binding.btnBuyNow.setBackgroundResource(bgRes)
            } else {
                view.background = null
            }
        }

        val selectedPackage = list[index]

        val price = selectedPackage.discount_price
        if (!price.isNullOrBlank()) {
            binding.tvPrice.text = "₹" + price.removeSuffix(".00")
            binding.tvDuration.text="${selectedPackage.days} days"
        }


        val features = selectedPackage.features?.filter { it.pivot.feature_value != "No" }

        if (!features.isNullOrEmpty()) {
            rvAdapter = AdapterPlanList(features, this, index)
            binding.rvFeatureList.adapter = rvAdapter
            rvAdapter.notifyDataSetChanged()
            binding.rvFeatureList.scrollToPosition(0)
        }
    }









    private fun observeViewModel() {


        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mPackageResponse.observe(this) {

            if (it.status) {


                if (it.data.isNotEmpty()) {

                    list=it.data




                    val packageItem = list.firstOrNull()

                    packageItem?.let {

                        if (!it.discount_price.isNullOrBlank()) {
                            binding.tvPrice.text = "₹" + it.discount_price.removeSuffix(".00")
                            binding.tvDuration.text="${list[0].days} days"
                        }


                    }

                    /*binding.tvPrice.text = "₹" + list[0].discount_price.removeSuffix(".00")
                    binding.tvDuration.text = "${it.data[0].days} days"*/



                    val features = list[0].features?.filter { it.pivot.feature_value != "No" }

                    if (!features.isNullOrEmpty()) {
                        rvAdapter = AdapterPlanList(features, this, 0)
                        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rvFeatureList.layoutManager = layoutManager
                        binding.rvFeatureList.adapter = rvAdapter
                        rvAdapter.notifyDataSetChanged()
                    }


                }


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



   /* private fun callPayuSdk(){

        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("10.00")
            .setTransactionId("txn_${System.currentTimeMillis()}")
            .setPhone("9999999999")
            .setProductInfo("Test Product")
            .setFirstName("John")
            .setEmail("john@example.com")
            .setSurl("https://yourdomain.com/success")
            .setFurl("https://yourdomain.com/failure")
            .setKey("your_merchant_key")
            .setUserCredential("")
            .setIsProduction(false)
            .build()


        PayUCheckoutPro.open(
            this,
            payUPaymentParams,
            object :PayUCheckoutProListener {
                override fun generateHash(
                    map: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    TODO("Not yet implemented")
                }

                override fun onError(errorResponse: ErrorResponse) {
                    TODO("Not yet implemented")
                }

                override fun onPaymentCancel(isTxnInitiated: Boolean) {
                    TODO("Not yet implemented")
                }

                override fun onPaymentFailure(response: Any) {
                    TODO("Not yet implemented")
                }

                override fun onPaymentSuccess(response: Any) {
                    TODO("Not yet implemented")
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    TODO("Not yet implemented")
                }

            }
        )



    }*/

    private fun callPayuSdk() {
        val payUPaymentParams = PayUPaymentParams.Builder()
            .setAmount("10.00")
            .setTransactionId("txn_${System.currentTimeMillis()}")
            .setPhone("9999999999")
            .setProductInfo("id234")
            .setFirstName("Firstname")
            .setEmail("test@payu.in")
            .setSurl("https://stafo.in/success")
            .setFurl("https://stafo.in/failure")
            .setKey("1AJhSD")
            .setUserCredential("fdf ")
            .setIsProduction(false)
            .build()

        PayUCheckoutPro.open(
            this,
            payUPaymentParams,
            object : PayUCheckoutProListener {
                override fun generateHash(
                    map: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    Log.e("TAG", "generateHash: ${Gson().toJson(map)}", )
                    if (map.containsKey(CP_HASH_STRING) && map[CP_HASH_STRING] != null
                        && map.containsKey(CP_HASH_NAME) && map[CP_HASH_NAME] != null) {

                        val hashData = map[CP_HASH_STRING]
                        val hashName = map[CP_HASH_NAME]
                        val hash: String? = hashData
                        if (!TextUtils.isEmpty(hash)) {
                            val hashMap: HashMap<String, String?> = HashMap()
                            hashMap[hashName!!] = hash!!
                            hashGenerationListener.onHashGenerated(hashMap)
                        }
                    }
                }

                override fun onError(errorResponse: ErrorResponse) {
                    Log.e("PayU Error", Gson().toJson(errorResponse))
                    Toast.makeText(applicationContext, "Payment Error: ${errorResponse.errorCode}", Toast.LENGTH_SHORT).show()
                }

                override fun onPaymentCancel(isTxnInitiated: Boolean) {
                    Toast.makeText(applicationContext, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onPaymentFailure(response: Any) {
                    Log.e("PayU Failure", response.toString())
                    Toast.makeText(applicationContext, "Payment Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onPaymentSuccess(response: Any) {
                    Log.d("PayU Success", response.toString())
                    Toast.makeText(applicationContext, "Payment Successful", Toast.LENGTH_SHORT).show()
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    webView?.settings?.javaScriptEnabled = true
                    webView?.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        )
    }



}