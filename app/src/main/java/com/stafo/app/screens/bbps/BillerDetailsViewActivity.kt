package com.stafo.app.screens.bbps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBillerDetailsViewBinding
import com.stafo.app.screens.bbps.BBPSDashboard.BannerItem
import com.stafo.app.screens.bbps.adapters.DynamicInputAdapter
import com.stafo.app.screens.bbps.dataClasses.BillerDetailsResponse
import com.stafo.app.screens.bbps.dataClasses.DataBiller
import com.stafo.app.screens.dashboard.BannerAdapterBBPS
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.gradientList
import com.stafo.app.utils.jsonObjectToMap

class BillerDetailsViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillerDetailsViewBinding
    private val bbpsViewModel: BBPSViewModel by lazy { BBPSViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private var dataBiller: DataBiller? = null
    private lateinit var bannerAdapter: BannerAdapterBBPS
    private lateinit var mDynamicFieldAdapter: DynamicInputAdapter
    private var bannerRunnable: Runnable? = null
    private val bannerHandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBillerDetailsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // setContentView(R.layout.activity_biller_details_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mData = intent.getStringExtra("biller")
        dataBiller = Gson().fromJson(mData, DataBiller::class.java)
        setupViews()
    }

    private fun setupViews() {
        bbpsViewModel.getBillersDetails(this, dataBiller?.operatorCode ?: "")
        binding.apply {
            imgBack.setOnClickListener { finish() }
            tvPageTitle.text = dataBiller?.category + " By " + dataBiller?.name
        }
        binding.btnFetchBill.setOnClickListener {
            if (mDynamicFieldAdapter.validateInputs()) {
                val inputData = mDynamicFieldAdapter.getInputData()

                val request = HashMap<String, Any>()
                request["operator_code"] = dataBiller?.operatorCode ?: ""
                request["category"] = dataBiller?.category ?: ""
                request["postdata"] = jsonObjectToMap(inputData)

                Log.d("Inputs", Gson().toJson(request))
                bbpsViewModel.fetchBill(this, request)

            } else {

            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        bbpsViewModel.getLoaderLiveData().observe(this) { status ->
            if (status == "load") customLoader.show() else customLoader.dismiss()
        }

        bbpsViewModel.mBillerDetailsResponse.observe(this) {
            if (it.success) {
                if (it.data?.mdmRequestNew?.biller?.billerInputParams != null) {
                    setupDynamicInputs(
                        it.data.mdmRequestNew.biller.billerInputParams.paramInfo ?: emptyList()
                    )
                }
            }
        }

        bbpsViewModel.mBillerBillResponse.observe(this) {
            if (it.success) {
                startActivity(
                    Intent(
                        this@BillerDetailsViewActivity,
                        BillDetailsAndPaymentActivity::class.java
                    ).apply {
                        putExtra("billerBill", Gson().toJson(it.data))
                        putExtra("billerDetails", Gson().toJson(dataBiller))
                        putExtra("category_image", intent.getStringExtra("category_image"))
                    })
            }
        }

    }

    private fun setupDynamicInputs(paramList: List<BillerDetailsResponse.Data.MdmRequestNew.Biller.BillerInputParams.ParamInfo>) {
        binding.rvDyamicBillerFields.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mDynamicFieldAdapter = DynamicInputAdapter(this, paramList, binding.rvDyamicBillerFields)
        binding.rvDyamicBillerFields.adapter = mDynamicFieldAdapter
        binding.btnFetchBill.visibility = android.view.View.VISIBLE
        setupBannerViewPager()
    }

    private fun setupBannerViewPager() {
        val banners = listOf(
            BannerItem(
                "Employee Management",
                "Manage employee details, roles, and records seamlessly.",
                gradientList.random(),
                R.drawable.ic_employee
            ),
            BannerItem(
                "Attendance Tracking",
                "Real-time punch-in with accurate tracking and reports.",
                gradientList.random(),
                R.drawable.ic_location
            ),
            BannerItem(
                "Leave Management",
                "Easy leave requests, approvals, and policy control.",
                gradientList.random(),
                R.drawable.ic_leaves
            ),
            BannerItem(
                "CRM Integration",
                "Handle client interactions and follow-ups efficiently.",
                gradientList.random(),
                R.drawable.ic_crm
            ),
            BannerItem(
                "Task Management",
                "Create, assign, and monitor tasks with full visibility.",
                gradientList.random(),
                R.drawable.ic_employee_list
            )
        )


        bannerAdapter = BannerAdapterBBPS(banners)
        binding.bannerViewPager.adapter = bannerAdapter

        // Connect indicator
        binding.bannerIndicator.setViewPager(binding.bannerViewPager)

        // Auto scroll
        bannerRunnable = Runnable {
            val currentItem = binding.bannerViewPager.currentItem
            val nextItem = (currentItem + 1) % bannerAdapter.itemCount
            binding.bannerViewPager.setCurrentItem(nextItem, true)
            bannerHandler.postDelayed(bannerRunnable!!, 4000)
        }

        bannerHandler.postDelayed(bannerRunnable!!, 4000)

        // Optional: pause on touch
        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bannerHandler.removeCallbacks(bannerRunnable!!)
                bannerHandler.postDelayed(bannerRunnable!!, 4000)
            }
        })
    }
}