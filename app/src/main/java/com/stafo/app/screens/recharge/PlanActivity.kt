package com.stafo.app.screens.recharge

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPlanBinding
import com.stafo.app.screens.recharge.adapter.ViewPagerAdapter
import com.stafo.app.screens.recharge.dataclass.ContactsAdapter
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

        onClickListener()

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
    private fun onClickListener() {
        binding.apply {



            binding.imgBackBtn.setOnClickListener {
                onBackPressed()
            }

            val minWidth = resources.getDimensionPixelSize(R.dimen.miniWidth)
            val maxWidth = resources.getDimensionPixelSize(R.dimen.maxWidth)

            binding.edtSearch.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val animator = ValueAnimator.ofInt(minWidth, maxWidth)
                    animator.addUpdateListener {
                        val value = it.animatedValue as Int
                        val layoutParams = binding.llcSearch.layoutParams
                        layoutParams.width = value
                        binding.llcSearch.layoutParams = layoutParams
                    }
                    animator.duration = 300
                    animator.start()

                    // Animate tabLayout out (slide + fade)
                    binding.tabLayout.animate()
                        .translationX(binding.tabLayout.width.toFloat())
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            binding.tabLayout.visibility = View.INVISIBLE
                            binding.tvCancelSearch.visibility = View.VISIBLE
                        }
                        .start()
                }
            }



            binding.tvCancelSearch.setOnClickListener {
                val animator = ValueAnimator.ofInt(maxWidth, minWidth)
                animator.addUpdateListener {
                    val value = it.animatedValue as Int
                    val layoutParams = binding.llcSearch.layoutParams
                    layoutParams.width = value
                    binding.llcSearch.layoutParams = layoutParams
                }
                animator.duration = 300
                animator.start()

                // Bring back tabLayout
                binding.tabLayout.visibility = View.VISIBLE
                binding.tabLayout.translationX = binding.tabLayout.width.toFloat()
                binding.tabLayout.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .start()

                binding.tvCancelSearch.visibility = View.GONE
                binding.edtSearch.clearFocus()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
            }




        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ContactsActivity::class.java))
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }
}