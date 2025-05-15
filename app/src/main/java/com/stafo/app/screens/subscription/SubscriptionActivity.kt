package com.stafo.app.screens.subscription


import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Base64.NO_WRAP
import android.util.Log
import android.webkit.WebView
import android.widget.TextView
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
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.stafo.app.R
import com.stafo.app.databinding.ActivitySubscriptionBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.subscription.dataClass.HashGenerateRequest
import com.stafo.app.screens.subscription.dataClass.HashParam
import com.stafo.app.screens.subscription.dataClass.PackageData
import com.stafo.app.screens.subscription.dataClass.PaymentResponse
import com.stafo.app.screens.subscription.dataClass.PaymentUpdateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterPlanList

    private lateinit var planViews: Map<TextView, Int>


    private lateinit var list: List<PackageData>

    private var selectPlanId: String = "0"
    private var txnId: String = ""
    private var status: String = ""
    private var amount: String = ""
    private var paymentMode: String = ""
    private var productInfo: String = ""


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


                if (selectPlanId.isNotBlank()) {

                    getEmployeeComId()?.let { it1 ->
                        val request = HashGenerateRequest(
                            company_id = it1, package_id = selectPlanId
                        )

                        billPaymentsViewModel.getHashPayu(
                            this@SubscriptionActivity, request
                        )
                    }
                }

                // startPayment()

                /*  getEmployeeComId()?.let { it1 ->
                      val request=  HashGenerateRequest(
                          company_id = it1,
                          package_id = ""
                      )

                      billPaymentsViewModel.getHashPayu(
                          this@SubscriptionActivity,request
                      )
                  }*/


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


        selectPlanId = selectedPackage.id.toString()

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
                            selectPlanId = it.id.toString()
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

        billPaymentsViewModel.mHashGenerateResponse.observe(this) {

            if (it.status) {

                Log.d("PayU", "get generated hash & params ${it.hashParam}")

                it.hashParam?.let { it1 -> startPayment(it1, it.hash!!) }
            }
        }


        billPaymentsViewModel.mPaymentUpdateResponse.observe(this) {

            if (it.status) {
                paymentStatus(status, productInfo, txnId, amount)

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

    private fun startPayment(dataModel: HashParam, getHash: String) {


        txnId = dataModel.txnid.toString()
        status = ""
        amount = dataModel.amount.toString()
        paymentMode = ""
        productInfo =dataModel.productinfo.toString()


        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap["udf1"] = dataModel.user_id
        additionalParamsMap["udf2"] = dataModel.duration
        additionalParamsMap["udf3"] = dataModel.package_id


        val payUPaymentParams = PayUPaymentParams.Builder().setKey(dataModel.merchantKey)
            .setTransactionId(dataModel.txnid).setAmount(dataModel.amount)
            .setProductInfo(dataModel.productinfo).setFirstName(dataModel.firstname)
            .setEmail(dataModel.email).setPhone(dataModel.phone.toString())
            .setSurl("https://stafo.in/api/success").setFurl("https://stafo.in/api/failure")
            .setIsProduction(true).setUserCredential("${dataModel.merchantKey}:${dataModel.email}")
            .setAdditionalParams(additionalParamsMap).build()











        PayUCheckoutPro.open(
            this, payUPaymentParams, object : PayUCheckoutProListener {
                override fun generateHash(
                    map: HashMap<String, String?>,
                    hashGenerationListener: PayUHashGenerationListener
                ) {


                    val hashName = map["hashName"]
                    val hashData = map["hashString"]

                    Log.d("PayU", "hashName: $hashName")
                    Log.d("PayU", "hashString: $hashData")

                    if (!hashName.isNullOrEmpty() && !hashData.isNullOrEmpty()) {
                        val hashDataWithSalt = "$hashData${dataModel.salt}"
                        val hash = calculateHash(hashDataWithSalt.trim())
                        val hashMap = HashMap<String, String?>()
                        hashMap[hashName] = hash
                        Log.d("PayU", "Generated hash: $hash")
                        hashGenerationListener.onHashGenerated(hashMap)
                    }
                }

                override fun onPaymentSuccess(response: Any) {
                    response as HashMap<*, *>

                    val payUResponseStr =
                        response[PayUCheckoutProConstants.CP_PAYU_RESPONSE] as? String
                    val payUJson = JSONObject(payUResponseStr ?: "{}")


                    val resultJson =
                        if (payUJson.has("result") && payUJson.opt("result") is JSONObject) {
                            payUJson.optJSONObject("result") ?: JSONObject()
                        } else {
                            payUJson
                        }

                    txnId = resultJson.optString("txnid")
                    status = resultJson.optString("status")
                    amount = resultJson.optString("amount")
                    paymentMode = resultJson.optString("mode")
                    productInfo = resultJson.optString("productinfo")

                    getEmployeeComId()?.let { it1 ->
                        val request = PaymentUpdateRequest(
                            company_id = it1,
                            duration = dataModel.duration.toString(),
                            packageId = selectPlanId,
                            amount = amount,
                            txnid = txnId,
                            status = "success",
                            payment_Message = "success",
                            payment_info = payUJson.toMap()
                        )

                        billPaymentsViewModel.updateSubscriptionPayment(
                            this@SubscriptionActivity, request
                        )
                    }


                    Log.d("PayU", "TxnId: $txnId")
                    Log.d("PayU", "Status: $status")
                    Log.d("PayU", "Amount: $amount")
                    Log.d("PayU", "Mode: $paymentMode")
                    Log.d("PayU", "Product: $productInfo")


                }


                override fun onPaymentFailure(response: Any) {
                    response as HashMap<*, *>

                    val payUResponseStr =
                        response[PayUCheckoutProConstants.CP_PAYU_RESPONSE] as? String
                    val payUJson = JSONObject(payUResponseStr ?: "{}")






                    getEmployeeComId()?.let { it1 ->
                        val request = PaymentUpdateRequest(
                            company_id = it1,
                            duration = dataModel.duration.toString(),
                            packageId = selectPlanId,
                            amount = dataModel.amount.toString(),
                            txnid = dataModel.txnid.toString(),
                            status = "failed",
                            payment_Message = "failed",
                            payment_info = payUJson.toMap()
                        )

                        billPaymentsViewModel.updateSubscriptionPayment(
                            this@SubscriptionActivity, request
                        )
                    }


                }

                override fun onPaymentCancel(isTxnInitiated: Boolean) {

                    CustomToast(this@SubscriptionActivity, "Payment Cancelled")

                }

                override fun onError(errorResponse: ErrorResponse) {
                    Log.e("PayU", "Error: ${errorResponse.errorMessage} ${errorResponse.errorCode}")
                }

                override fun setWebViewProperties(webView: WebView?, bank: Any?) {
                    // Optional: Customize WebView if needed
                }
            })
    }

    fun calculateHash(data: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        messageDigest.update(data.toByteArray())
        val hashBytes = messageDigest.digest()
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun JSONObject.toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = this.get(key)
            map[key] = when (value) {
                is JSONObject -> value.toMap()
                else -> value
            }
        }
        return map
    }


    private fun paymentStatus(type: String, pName: String, txnId: String, amount: String) {

        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra("type", type)
            putExtra("pName", pName)
            putExtra("txnId", txnId)
            putExtra("amount", amount)
        }
        startActivity(intent)


    }


    private fun parseMerchantResponse(jsonString: String?): PaymentResponse? {
        return try {
            if (jsonString == null) return null
            val jsonObject = JSONObject(jsonString)
            PaymentResponse(
                status = jsonObject.optBoolean("status"),
                expire_date = jsonObject.optString("expire_date"),
                message = jsonObject.optString("message")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}