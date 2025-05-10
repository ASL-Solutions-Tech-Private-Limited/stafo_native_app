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
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_NAME
import com.payu.checkoutpro.utils.PayUCheckoutProConstants.CP_HASH_STRING
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener


import com.stafo.app.R
import com.stafo.app.databinding.ActivitySubscriptionBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.subscription.dataClass.PackageData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest


class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterPlanList

    private lateinit var planViews: Map<TextView, Int>


    private lateinit var list: List<PackageData>


    private val merchantKey = "1AJhSD"
    private val merchantSalt = "tBjCq35cgf3f12ya0usuhEtH9IJ7pSyq"

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
                startPayment()
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
            binding.tvDuration.text = "${selectedPackage.days} days"
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

                    list = it.data


                    val packageItem = list.firstOrNull()

                    packageItem?.let {

                        if (!it.discount_price.isNullOrBlank()) {
                            binding.tvPrice.text = "₹" + it.discount_price.removeSuffix(".00")
                            binding.tvDuration.text = "${list[0].days} days"
                        }


                    }

                    /*binding.tvPrice.text = "₹" + list[0].discount_price.removeSuffix(".00")
                    binding.tvDuration.text = "${it.data[0].days} days"*/


                    val features = list[0].features?.filter { it.pivot.feature_value != "No" }

                    if (!features.isNullOrEmpty()) {
                        rvAdapter = AdapterPlanList(features, this, 0)
                        val layoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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

    private fun startPayment() {

        val key = "1AJhSD"
        val salt = "tBjCq35cgf3f12ya0usuhEtH9IJ7pSyq"
        val txnId = System.currentTimeMillis().toString()

        val additionalParams: HashMap<String, Any?> = hashMapOf(
            "udf1" to "1",
            "udf2" to "30",
            "udf3" to "1"
        )

        val payUPaymentParams = PayUPaymentParams.Builder()
            .setKey(key)
            .setTransactionId(txnId)
            .setAmount("10.0")
            .setProductInfo("Macbook Pro")
            .setFirstName("John")
            .setEmail("john@yopmail.com")
            .setPhone("9999999999")
            .setSurl("https://stafo.in/success")
            .setFurl("https://stafo.in/failure")
            .setIsProduction(true)
            .setUserCredential("$key:john@yopmail.com")
            .setAdditionalParams(additionalParams)
            .build()

        PayUCheckoutPro.open(
            this,
            payUPaymentParams,
            object : PayUCheckoutProListener {
                override fun generateHash(
                    map: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {
                    val hashName = map["hashName"]
                    val hashData = map["hashString"]

                    Log.d("PayU_HASH", "hashName: $hashName")
                    Log.d("PayU_HASH", "hashString: $hashData")

                    if (!hashName.isNullOrEmpty() && !hashData.isNullOrEmpty()) {
                        val hash = sha512("$hashData|$salt")
                        val hashMap = HashMap<String, String?>()
                        hashMap[hashName] = hash
                        Log.d("PayU_HASH", "Generated hash: $hash")
                        hashGenerationListener.onHashGenerated(hashMap)
                    }
                }

                override fun onPaymentSuccess(response: Any) {
                    Log.d("PayU", "Payment Success: $response")
                }

                override fun onPaymentFailure(response: Any) {
                    Log.d("PayU", "Payment Failure: $response")
                }

                override fun onPaymentCancel(isTxnInitiated: Boolean) {
                    Log.d("PayU", "Payment Cancelled, txn initiated: $isTxnInitiated")
                }

                override fun onError(errorResponse: ErrorResponse) {
                    Log.e("PayU", "Error: ${errorResponse.errorMessage} ${errorResponse.errorCode}")

                    Log.e("PayU", "Response Error: ${errorResponse}")
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    // Optional: Customize WebView if needed
                }
            }
        )
    }

    private fun sha512(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-512")
            val hashBytes = md.digest(input.toByteArray(StandardCharsets.UTF_8))
            val sb = StringBuilder()
            for (b in hashBytes) {
                sb.append(String.format("%02x", b))
            }
            sb.toString().lowercase()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


}