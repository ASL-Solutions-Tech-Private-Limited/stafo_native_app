package com.stafo.app.screens.referral.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemReferralLayoutBinding
import com.stafo.app.screens.referral.dataClass.ReferralItem

class AdapterRefer(
    private var list: List<ReferralItem>, var context: Activity
) : RecyclerView.Adapter<AdapterRefer.ViewHolder>() {
    inner class ViewHolder(val binding: ItemReferralLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReferralLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtBranchName.text = this.company_name
                binding.txtBranchAddress.text = this.company_code
                binding.txtMobile.text = this.mobile_no
                binding.txtEmail.text = this.email


            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}