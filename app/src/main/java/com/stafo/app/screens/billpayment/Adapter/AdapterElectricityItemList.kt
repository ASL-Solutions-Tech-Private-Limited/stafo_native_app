package com.stafo.app.screens.billpayment.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemOperatorLayoutBinding
import com.stafo.app.screens.billpayment.dataClass.ElectricityOperator

class AdapterElectricityItemList(
    private var list: List<ElectricityOperator>,
    var context: Context,
    private var operatorClick: onOperatorClick
) : RecyclerView.Adapter<AdapterElectricityItemList.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOperatorLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOperatorLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                binding.tvOperator.text = this.name

                when (this.name) {
                    "Airtel DTH" -> {
                        binding.sivTitle.background = null
                        binding.sivTitle.setImageResource(R.drawable.airtel_tv)
                        binding.tvOperator.text = "Airtel Digital TV"
                    }

                    "Dish TV" -> {
                        binding.sivTitle.background = null
                        binding.sivTitle.setImageResource(R.drawable.dish_tv)
                    }

                    "Sun Direct TV" -> {
                        binding.sivTitle.background = null
                        binding.sivTitle.setImageResource(R.drawable.sun_dth)
                    }

                    "TATA Play" -> {
                        binding.sivTitle.background = null
                        binding.sivTitle.setImageResource(R.drawable.tata_dth)
                    }

                    "DTH" -> {
                        binding.sivTitle.background = null
                        binding.sivTitle.setImageResource(R.drawable.d2h)
                        binding.tvOperator.text = "D2H"
                    }

                }

                itemView.setOnClickListener {
                    operatorClick.onElOperatorClick(this.operator_code)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onOperatorClick {
        fun onElOperatorClick(operatorCode: String)
    }

    fun updateList(newList: List<ElectricityOperator>) {
        list = newList
        notifyDataSetChanged()
    }

}