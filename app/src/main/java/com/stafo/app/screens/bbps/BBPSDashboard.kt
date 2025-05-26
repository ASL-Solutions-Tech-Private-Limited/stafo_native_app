package com.stafo.app.screens.bbps

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
import com.stafo.app.screens.dashboard.BannerAdapterBBPS


class BBPSDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityBbpsdashboardBinding
    private lateinit var servicesAdapter: BBPSCategoriesAdapter
    private lateinit var bannerAdapter: BannerAdapterBBPS
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
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
        // Create services data
        val services: List<ServiceItem> = createServicesData()


        // Setup GridLayoutManager with 4 columns
        val gridLayoutManager = GridLayoutManager(this, 4)
        binding.servicesRecyclerView.setLayoutManager(gridLayoutManager)


        // Setup adapter
        servicesAdapter = BBPSCategoriesAdapter(services, {

        })
        binding.servicesRecyclerView.setAdapter(servicesAdapter)

    }


    private fun createServicesData(): List<ServiceItem> {
        val services: MutableList<ServiceItem> = ArrayList<ServiceItem>()

        services.add(ServiceItem("Electricity", R.drawable.ic_holidays, "#6366F1"))
        services.add(ServiceItem("Mobile", R.drawable.ic_mobile, "#06B6D4"))
        services.add(ServiceItem("DTH", R.drawable.ic_employee, "#EF4444"))
        services.add(ServiceItem("Water", R.drawable.ic_mobile, "#3B82F6"))
        services.add(ServiceItem("Gas", R.drawable.ic_mobile, "#F59E0B"))
        services.add(ServiceItem("Broadband", R.drawable.ic_mobile, "#8B5CF6"))
        services.add(ServiceItem("Landline", R.drawable.ic_mobile, "#EC4899"))
        services.add(ServiceItem("Insurance", R.drawable.ic_mobile, "#10B981"))
        return services
    }

    private fun setupBannerViewPager() {
        val banners = listOf(
            BannerItem(
                "Pay Your Bills Instantly!",
                "Fast & secure bill payment across India.",
                R.drawable.gradient_purple_blue,
                R.drawable.qr_code_1
            ),
            BannerItem(
                "Recharge Anytime!",
                "Mobile & DTH recharges made easy.",
                R.drawable.gradient_orange_pink,
                R.drawable.ic_mobile
            ),
            BannerItem(
                "No Extra Charges",
                "Zero Convenience Fee!",
                R.drawable.gradient_light_blue,
                R.drawable.ic_clock
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
        val backgroundRes: Int,
        val iconRes: Int
    )


}