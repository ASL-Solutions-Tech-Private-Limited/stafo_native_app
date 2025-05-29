package com.stafo.app.screens.bbps

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBbpsdashboardBinding
import com.stafo.app.screens.bbps.adapters.BBPSCategoriesAdapter
import com.stafo.app.screens.dashboard.BannerAdapterBBPS
import com.stafo.app.utils.gradientList


class BBPSDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityBbpsdashboardBinding
    private lateinit var servicesAdapter: BBPSCategoriesAdapter
    private lateinit var bannerAdapter: BannerAdapterBBPS
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
    private val bbpsViewModel: BBPSViewModel by lazy {
        BBPSViewModel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_bbpsdashboard)
        binding = ActivityBbpsdashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupServicesRecyclerView()
        setupBannerViewPager()
    }


    private fun setupServicesRecyclerView() {
        val services: List<ServiceItem> = createServicesData()
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.servicesRecyclerView.setLayoutManager(gridLayoutManager)
        servicesAdapter = BBPSCategoriesAdapter(context = this,
            isCategoryList = false,
            services = services,
            categories = null,
            onServiceClicked = { service ->
                when (service.name) {
                    "Mobile" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Mobile Prepaid")
                            putExtra("category_image", "uploads/images/rechargeService/mobile.png")
                        })
                    }

                    "Electricity" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Electricity")
                            putExtra(
                                "category_image",
                                "uploads/images/rechargeService/electricity.png"
                            )
                        })
                    }

                    "DTH" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "DTH")
                            putExtra("category_image", "uploads/images/rechargeService/dth.png")
                        })
                    }

                    "Water" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Water")
                            putExtra("category_image", "uploads/images/rechargeService/water.png")
                        })
                    }

                    "LPG Gas" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "LPG Gas")
                            putExtra("category_image", "uploads/images/rechargeService/lpg-gas.png")
                        })
                    }

                    "Fastag" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Fastag")
                            putExtra("category_image", "uploads/images/rechargeService/fastag.png")
                        })
                    }

                    "Loan Repayment" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Loan Repayment")
                            putExtra(
                                "category_image",
                                "uploads/images/rechargeService/loan-repayment.png"
                            )
                        })
                    }

                    "Insurance" -> {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", "Insurance")
                            putExtra("category_image", "")
                        })
                    }
                }
            },
            onCategoryClicked = { category ->
                // handle category click
            })
        binding.servicesRecyclerView.setAdapter(servicesAdapter)

        binding.tvViewAll.setOnClickListener {
            startActivity(Intent(this, BBPSBillerListActivity::class.java))
        }
        binding.ivBack.setOnClickListener { finish() }
        binding?.apply {
            tvZeroConvenienceFee.text = "Salary Calculation"
            tvZeroConvenienceFeeDesc.text = "Automated, error-free payroll linked to attendance."
            tvNewInsurancePremium.text = "NEW:Geo Location Tracking"
            tvNewInsurancePremiumDesc.text = "Live tracking for field staff with complete history."
        }
    }


    private fun createServicesData(): List<ServiceItem> {
        val services: MutableList<ServiceItem> = ArrayList<ServiceItem>()

        services.add(ServiceItem("Mobile", R.drawable.ic_mobiele_new, "#06B6D4"))
        services.add(ServiceItem("Electricity", R.drawable.ic_electricity, "#6366F1"))
        services.add(ServiceItem("DTH", R.drawable.ic_cabel, "#EF4444"))
        services.add(ServiceItem("Water", R.drawable.ic_water, "#3B82F6"))
        services.add(ServiceItem("LPG Gas", R.drawable.ic_gas, "#F59E0B"))
        services.add(ServiceItem("Fastag", R.drawable.ic_trafic, "#8B5CF6"))
        services.add(ServiceItem("Loan Repayment", R.drawable.ic_loan, "#EC4899"))
        services.add(ServiceItem("Insurance", R.drawable.svgviewer_output, "#10B981"))
        return services
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


    class ServiceItem(val name: String, val iconRes: Int, val color: String)
    data class BannerItem(
        val title: String,
        val subtitle: String,
        val gradientColors: IntArray, // e.g., intArrayOf(Color.RED, Color.BLUE)
        val iconRes: Int
    )


}