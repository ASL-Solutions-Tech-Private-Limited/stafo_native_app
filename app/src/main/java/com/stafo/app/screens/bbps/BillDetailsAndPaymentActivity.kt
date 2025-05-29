package com.stafo.app.screens.bbps

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.payu.base.models.ErrorResponse
import com.payu.base.models.PayUPaymentParams
import com.payu.checkoutpro.PayUCheckoutPro
import com.payu.checkoutpro.utils.PayUCheckoutProConstants
import com.payu.ui.model.listeners.PayUCheckoutProListener
import com.payu.ui.model.listeners.PayUHashGenerationListener
import com.stafo.app.BuildConfig
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBillDetailsAndPaymentBinding
import com.stafo.app.screens.bbps.adapters.LabelValueAdapter
import com.stafo.app.screens.bbps.dataClasses.BillerBillFetchResponse.Data
import com.stafo.app.screens.bbps.dataClasses.DataBiller
import com.stafo.app.screens.bbps.dataClasses.DataInitiatePayment
import com.stafo.app.screens.subscription.dataClass.CheckPaymentStatusRequest
import com.stafo.app.screens.subscription.dataClass.Postdata
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getCompanyDetails
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails
import com.stafo.app.utils.getIsEMPLogin
import org.json.JSONObject
import java.security.MessageDigest

class BillDetailsAndPaymentActivity : AppCompatActivity() {
    private val TAG = "BillDetailsAndPaymentActivity"
    private lateinit var binding: ActivityBillDetailsAndPaymentBinding
    private var mBillerBillData: Data? = null
    private var mBillerData: DataBiller? = null
    private var mCategoryImage = ""
    private val bbpsViewModel: BBPSViewModel by lazy { BBPSViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private var txnId: String = ""
    private var status: String = ""
    private var amount: String = ""
    private var paymentMode: String = ""
    private var productInfo: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //   setContentView(R.layout.activity_bill_details_and_payment)
        binding = ActivityBillDetailsAndPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mBillerDetails = intent.getStringExtra("billerDetails")
        val mBillerBill = intent.getStringExtra("billerBill")
        mCategoryImage = intent.getStringExtra("category_image") ?: ""

        mBillerBillData = Gson().fromJson(mBillerBill, Data::class.java)
        mBillerData = Gson().fromJson(mBillerDetails, DataBiller::class.java)


        showBillDetails(mBillerBillData?.billData!!, mBillerData!!)

    }


    private fun showBillDetails(
        billData: Data.BillData, billerDetails: DataBiller
    ) {

        binding.apply {
            ivBack.setOnClickListener { finish() }
            tvCategory.text = billerDetails.category
            tvBillerName.text = billerDetails.name
            Glide.with(this@BillDetailsAndPaymentActivity)
                .load(BuildConfig.ENDPOINT + mCategoryImage).placeholder(R.drawable.ic_bbps_ic)
                .into(ivCategoryIcon)
            rvBillDetails.layoutManager = LinearLayoutManager(this@BillDetailsAndPaymentActivity)

            val details = extractLabelValuePairs(billData.billerResponse, billData.additionalInfo!!)

            val adapter = LabelValueAdapter(details)
            rvBillDetails.adapter = adapter
            val finalAmount = (billData.billerResponse.billAmount.toDouble() / 100)
            etAmount.setText(finalAmount.toString())
            if (isEditMode(billerDetails.category ?: "")) {
                etAmount.isEnabled = true
                etAmount.isFocusable = true
                etAmount.isFocusableInTouchMode = true
            } else {
                etAmount.isEnabled = false
                etAmount.isFocusable = false
                etAmount.isFocusableInTouchMode = false
            }
            btnPayBill.setOnClickListener {
                bbpsViewModel.initiateBillPayment(
                    this@BillDetailsAndPaymentActivity, getInitiateBillPaymentRequest()
                )
            }
        }


        observeViewModel()
    }

    private fun observeViewModel() {
        bbpsViewModel.getLoaderLiveData().observe(this) { status ->
            if (status == "load") customLoader.show() else customLoader.dismiss()
        }

        bbpsViewModel.mInitiateBillPaymentResponse.observe(this) { response ->
            if (response != null) {
                if (response.success == true) {
                    startPayment(response.dataInitiatePayment!!)
                }
            }
        }
    }

    fun extractLabelValuePairs(obj: Any, addinfo: Any?): List<LabelValuePair> {
        val pairs = mutableListOf<LabelValuePair>()
        obj::class.members.filterIsInstance<kotlin.reflect.KProperty1<Any, *>>()
            .filter { it.name != "amountOptions" } // exclude specific fields
            .forEach { prop ->
                val rawValue = prop.get(obj)?.toString()?.trim()
                if (!rawValue.isNullOrBlank()) {
                    val label = prop.name.split("(?=[A-Z])".toRegex())
                        .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

                    val value = when (prop.name) {
                        "billAmount" -> {
                            rawValue.toLongOrNull()?.let { "%.2f".format(it / 100.0) } ?: rawValue
                        }

                        else -> rawValue
                    }

                    pairs.add(LabelValuePair(label, value))
                }
            }

        if (addinfo != null) {
            addinfo::class.members.filterIsInstance<kotlin.reflect.KProperty1<Any, *>>()
                .forEach { prop ->
                    val rawValue = prop.get(addinfo)?.toString()?.trim()
                    if (!rawValue.isNullOrEmpty()) {
                        val label = prop.name.split("(?=[A-Z])".toRegex())
                            .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

                        if (prop.name.toLowerCase().contains("balance")) {
                            val label = "Balance"
                            val value =
                                rawValue.toLongOrNull()?.let { "%.2f".format(it) } ?: rawValue
                            pairs.add(LabelValuePair(label, value))
                        }
                    }
                }
        }
        return pairs
    }


    private fun getInitiateBillPaymentRequest(): HashMap<String, Any> {
        val id =
            if (getIsEMPLogin(this)) getEmployeeDetails()?.id ?: "" else getEmployeeComId() ?: ""

        val userType = if (getIsEMPLogin(this)) "employee" else "company"

        val request = HashMap<String, Any>()
        request["operator_code"] = mBillerData?.operatorCode ?: ""
        request["user_id"] = id
        request["user_type"] = userType
        request["postdata"] = HashMap<String, String>().apply {
            put("amount", binding.etAmount.text.toString().trim())
            put("requestId", mBillerBillData?.refid ?: "")
            put("mode", "online")
            put("billPaymentRequestxml", mBillerBillData?.xml ?: "")
        }
        request["category"] = mBillerData?.category ?: ""

        return request
    }

    private fun startPayment(dataModel: DataInitiatePayment) {

        txnId = dataModel.txnid.toString()
        status = ""
        amount = dataModel.amount.toString()
        paymentMode = ""
        productInfo = dataModel.productInfo ?: ""
        val salt = "tBjCq35cgf3f12ya0usuhEtH9IJ7pSyq"
        val name = if (getIsEMPLogin(this)) getEmployeeDetails()?.name
            ?: "" else getCompanyDetails()?.companyName ?: ""

        val email = if (getIsEMPLogin(this)) getEmployeeDetails()?.email
            ?: "" else getCompanyDetails()?.email ?: ""

        val phone = if (getIsEMPLogin(this)) getEmployeeDetails()?.phone.toString()
            ?: "" else getCompanyDetails()?.mobileNo.toString()

        val additionalParamsMap: HashMap<String, Any?> = HashMap()
        additionalParamsMap["udf1"] = dataModel.userId
        additionalParamsMap["udf2"] = dataModel.userType
        additionalParamsMap["udf3"] = dataModel.operatorCode

        val payUPaymentParams =
            PayUPaymentParams.Builder().setKey("1AJhSD").setTransactionId(dataModel.txnid)
                .setAmount(dataModel.amount).setProductInfo(dataModel.productInfo)
                .setFirstName(name).setEmail(email).setPhone(phone)
                .setSurl("https://stafo.in/api/success").setFurl("https://stafo.in/api/failure")
                .setIsProduction(true).setUserCredential("1AJhSD$email")
                .setAdditionalParams(additionalParamsMap).build()


        PayUCheckoutPro.open(this, payUPaymentParams, object : PayUCheckoutProListener {
            override fun generateHash(
                map: HashMap<String, String?>,
                hashGenerationListener: PayUHashGenerationListener
            ) {
                val hashName = map["hashName"]
                val hashData = map["hashString"]
                if (!hashName.isNullOrEmpty() && !hashData.isNullOrEmpty()) {
                    val hashDataWithSalt = "$hashData$salt"
                    val hash = calculateHash(hashDataWithSalt.trim())
                    val hashMap = HashMap<String, String?>()
                    hashMap[hashName] = hash
                    hashGenerationListener.onHashGenerated(hashMap)
                }
            }

            override fun onPaymentSuccess(response: Any) {
                response as HashMap<*, *>
                val payUResponseStr = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE] as? String
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


                val request = CheckPaymentStatusRequest(
                    category = mBillerData?.category ?: "",
                    operatorCode = mBillerData?.operatorCode ?: "",
                    postdata = Postdata(
                        amount = amount,
                        billPaymentRequestxml = mBillerBillData?.xml,
                        mode = paymentMode,
                        requestId = mBillerBillData?.refid
                    ),
                    transactionResponse = payUJson.toMap(),
                    txnId = txnId,
                    userId = getEmployeeDetails()?.id.toString(),
                    userType = if (getIsEMPLogin(this@BillDetailsAndPaymentActivity)) "employee" else "company"
                )
                bbpsViewModel.checkPaymentStatus(this@BillDetailsAndPaymentActivity, request)
            }

            override fun onPaymentFailure(response: Any) {
                response as HashMap<*, *>
                val payUResponseStr = response[PayUCheckoutProConstants.CP_PAYU_RESPONSE] as? String
                val payUJson = JSONObject(payUResponseStr ?: "{}")
                getEmployeeComId()?.let { it1 ->
                    val request = CheckPaymentStatusRequest(
                        category = mBillerData?.category ?: "",
                        operatorCode = mBillerData?.operatorCode ?: "",
                        postdata = Postdata(
                            amount = amount,
                            billPaymentRequestxml = mBillerBillData?.xml,
                            mode = paymentMode,
                            requestId = mBillerBillData?.refid
                        ),
                        transactionResponse = payUJson.toMap(),
                        txnId = txnId,
                        userId = getEmployeeDetails()?.id.toString(),
                        userType = if (getIsEMPLogin(this@BillDetailsAndPaymentActivity)) "employee" else "company"
                    )

                    bbpsViewModel.checkPaymentStatus(
                        this@BillDetailsAndPaymentActivity, request
                    )
                }
            }

            override fun onPaymentCancel(isTxnInitiated: Boolean) {
                CustomToast(this@BillDetailsAndPaymentActivity, "Payment Cancelled")
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


    data class LabelValuePair(
        val label: String, val value: String
    )

    private fun isEditMode(service: String): Boolean {
        val editableServices = listOf("DTH", "Mobile Prepaid", "Mobile Postpaid", "Fastag")
        return editableServices.contains(service)
    }


}