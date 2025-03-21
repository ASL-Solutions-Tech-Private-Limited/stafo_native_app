package com.stafo.app.screens.recharge.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.stafo.app.screens.recharge.PlanFragment
import com.stafo.app.screens.recharge.dataclass.RechargeInfo

class ViewPagerAdapter (
    fragmentActivity: FragmentActivity,
    private val tabData: List<List<RechargeInfo>>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = tabData.size

    override fun createFragment(position: Int): Fragment {
        return PlanFragment.newInstance(tabData[position])
    }
}