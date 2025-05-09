package com.stafo.app.screens.subscription

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.CustomSpinnerItemBinding
import com.stafo.app.databinding.RecyPlanViewChildLayoutBinding
import com.stafo.app.screens.subscription.dataClass.Feature

class AdapterPlanList (
    private var list: List<Feature>,
    var context: Activity,
    var getType:Int
) : RecyclerView.Adapter<AdapterPlanList.ViewHolder>() {
    inner class ViewHolder(val binding: RecyPlanViewChildLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyPlanViewChildLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                binding.tvItem.text=this.name

                when(getType){
                    0 -> binding.sivView.setBackgroundResource(R.color.plan_color1)
                    1 -> binding.sivView.setBackgroundResource(R.color.plan_color2)
                    2 -> binding.sivView.setBackgroundResource(R.color.plan_color3)
                    3 -> binding.sivView.setBackgroundResource(R.color.basic_deep)

                }







                /*when (this.package_name) {
                    "Monthly Plan" -> {
                        binding.rtlChangeBg.setBackgroundResource(R.drawable.card_rtl_bg_offer)
                        binding.view.setBackgroundResource(R.drawable.custom_plan_view_bg2)
                        binding.ivPlan.setImageResource(R.drawable.basic_plan)
                    }
                    "Quarterly Plan" -> {
                        binding.rtlChangeBg.setBackgroundResource(R.drawable.card_rtl_bg_offer2)
                        binding.view.setBackgroundResource(R.drawable.custom_plan_view_bg)
                        binding.ivPlan.setImageResource(R.drawable.car_plan)
                    }
                    "Yearly Plan" -> {
                        binding.rtlChangeBg.setBackgroundResource(R.drawable.card_rtl_bg_offer3)
                        binding.view.setBackgroundResource(R.drawable.custom_plan_view_bg3)
                        binding.ivPlan.setImageResource(R.drawable.yearly_plan)
                    }

                    else -> {
                        binding.rtlChangeBg.setBackgroundResource(R.drawable.card_rtl_bg_offer)
                        binding.view.setBackgroundResource(R.drawable.custom_plan_view_bg2)
                        binding.ivPlan.setImageResource(R.drawable.basic_plan)
                    }*/
                }


            }
        }
    }




