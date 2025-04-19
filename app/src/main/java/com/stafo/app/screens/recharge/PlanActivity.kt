package com.stafo.app.screens.recharge

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPlanBinding
import com.stafo.app.screens.recharge.adapter.ViewPagerAdapter
import com.stafo.app.screens.recharge.dataclass.RechargeInfo

class PlanActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPlanBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        val tabData = listOf(
            listOf(
                RechargeInfo("199", "1GB/day", "28 Days", "Calls : Unlimited local, STD & Roaming | Data : 1.5GB/Day | SMS : 100SMS/day | Details: You can also recharge with above plan instead of Rs 299 plan pack"),
                RechargeInfo("399", "2GB/day", "56 Days", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/Day | Details: Revised price for Rs 239 pack"),
                RechargeInfo("399", "2GB/day", "56 Days", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/Day | Details: Revised price for Rs 239 pack"),
                RechargeInfo("399", "2GB/day", "56 Days", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/Day | Details: Revised price for Rs 239 pack"),
                RechargeInfo("399", "2GB/day", "56 Days", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/Day | Details: Revised price for Rs 239 pack"),
                RechargeInfo("399", "2GB/day", "56 Days", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/Day | Details: Revised price for Rs 239 pack")
            ),
            listOf(
                RechargeInfo("249", "1GB", "NA", "Calls : Unlimited local, STD & Roaming | Data : 1GB/day | SMS : 100 SMS/day | Details: Revised price for Rs 209 pack"),
                RechargeInfo("349", "3GB", "NA", "Calls : Unlimited local, STD & Roaming | Data : 3GB | SMS : 300 | Talktime : Rs 5 | Details: Revised price for Rs 199 pack"),
                RechargeInfo("349", "3GB", "NA", "Calls : Unlimited local, STD & Roaming | Data : 3GB | SMS : 300 | Talktime : Rs 5 | Details: Revised price for Rs 199 pack")
            ),
            listOf(
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack"),
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack"),
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack"),
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack")
            ),
            listOf(
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack"),
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack")
            ),
            listOf(
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack"),
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack")
            ),
            listOf(
                RechargeInfo("599", "1.5GB/day, Unlimited Calls", "84 Days", "Calls : Unlimited local, STD & Roaming | Data : 25GB | SMS : 100 SMS/day | Details: Revised price for Rs 296 pack")
            )
        )

        val tabTitles = listOf("Unlimited", "Data", "Talktime","IR","ISD","Inflight Roaming packs")

        val adapter = ViewPagerAdapter(this, tabData)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}