package com.stafo.app.screens.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.databinding.RecyPayoutEmployeeItemBinding
import com.stafo.app.screens.settings.SalaryDisbursementActivity
import com.stafo.app.screens.settings.ViewAllEmployeeActivity
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.currencyFormatter
import com.stafo.app.utils.generateTextBitmap

class PayoutEmpListAdapter (
    private var list: List<GetEmployee>,
    var context: Context,
    var onEmGeoClick: onGeoClick
) : RecyclerView.Adapter<PayoutEmpListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyPayoutEmployeeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyPayoutEmployeeItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.name
                binding.txtMobile.text = this.phone
                binding.txtEmail.text = this.email
                binding.txtJobTitle.text = this.position
                binding.txtSalaryTitle.text = currencyFormatter(this.salary.toString())

                val placeholderBitmap = generateTextBitmap(this.name ?: "?")
                if (!this.selfieImage.isNullOrEmpty()) {
                    val imageUrl = "${this.selfieImagePath}/${this.selfieImage}".replace("\\", "")

                    Glide.with(context).load(imageUrl).error(placeholderBitmap)
                        .into(binding.approveLvEmpImage)

                } else {
                    binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)
                }


                binding.tvPayout.setOnClickListener {
                    (context as SalaryDisbursementActivity).showCustomBottomSheet(
                        this.id, this.attendanceType
                    )
                }

                binding.tvViewSalarySlip.setOnClickListener {
                    (context as SalaryDisbursementActivity).showCustomBottomSheet(
                        this.id, this.attendanceType
                    )
                }

                itemView.setOnClickListener {
                    onEmGeoClick.onEMPClick(
                        list[position].id.toString(), "",position)

                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onGeoClick {
        fun onEMPClick(empID: String, type: String,position: Int)
    }

    fun updateList(newList: List<GetEmployee>) {
        list = newList
        notifyDataSetChanged()
    }




}