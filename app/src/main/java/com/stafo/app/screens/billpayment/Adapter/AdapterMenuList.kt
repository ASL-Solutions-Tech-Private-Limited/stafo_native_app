package com.stafo.app.screens.billpayment.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.DashboardMenuItemBillPaymentLayoutBinding
import com.stafo.app.screens.billpayment.dataClass.Category

class AdapterMenuList (
    private var list: List<Category>,
    var context: Context,
    private var itemClick:onItemClick
) : RecyclerView.Adapter<AdapterMenuList.ViewHolder>() {
    inner class ViewHolder(val binding: DashboardMenuItemBillPaymentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DashboardMenuItemBillPaymentLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtMenuName.text=this.name
                when (this.name) {
                "Broadband Postpaid" -> binding.ivMenuIcon.setImageResource(R.drawable.broadband)
                "Cable TV" -> binding.ivMenuIcon.setImageResource(R.drawable.cable_tv)
                "Clubs and Associations" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Credit Card" -> binding.ivMenuIcon.setImageResource(R.drawable.credit_card)
                "Donation" -> binding.ivMenuIcon.setImageResource(R.drawable.donate)
                "DTH" -> binding.ivMenuIcon.setImageResource(R.drawable.dth)
                "Education Fees" -> binding.ivMenuIcon.setImageResource(R.drawable.tuition)
                "Electricity" -> binding.ivMenuIcon.setImageResource(R.drawable.electricity)
                "Fastag" -> binding.ivMenuIcon.setImageResource(R.drawable.fastag)
                "Gas" -> binding.ivMenuIcon.setImageResource(R.drawable.ic_baseline_gas)
                "Hospital" -> binding.ivMenuIcon.setImageResource(R.drawable.hospital)
                "Hospital and Pathology" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Housing Society" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Insurance" -> binding.ivMenuIcon.setImageResource(R.drawable.insurance)
                "Landline Postpaid" -> binding.ivMenuIcon.setImageResource(R.drawable.landline)
                "Loan Repayment" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "LPG Gas" -> binding.ivMenuIcon.setImageResource(R.drawable.ic_baseline_gas)
                "Mobile Postpaid" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Municipal Taxes" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Subscription" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Recurring Deposit" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Rental" -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                "Water" -> binding.ivMenuIcon.setImageResource(R.drawable.ic_baseline_water_drop_24)
                else -> binding.ivMenuIcon.setImageResource(R.drawable.mobile_recharge)
                }


                holder.binding.ivMenuIcon.setOnClickListener {
                    itemClick.onItem(list[position].name)
                }



            }
        }
    }
    interface onItemClick {
        fun onItem(name: String)
    }
    override fun getItemCount(): Int {
        return list.size
    }



}