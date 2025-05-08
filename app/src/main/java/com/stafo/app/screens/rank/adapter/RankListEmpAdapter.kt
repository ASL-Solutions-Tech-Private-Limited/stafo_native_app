package com.stafo.app.screens.rank.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.ItemRankListLayoutBinding
import com.stafo.app.screens.rank.RankListActivity
import com.stafo.app.screens.rank.dataClass.RankItem
import com.stafo.app.utils.generateTextBitmap

class RankListEmpAdapter (
    private var list: List<RankItem>,
    var context: Activity
) : RecyclerView.Adapter<RankListEmpAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRankListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankListLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.employee.name
                binding.txtMobile.text = this.employee.phone
                binding.txtPoint.text = this.total_marks

                val placeholderBitmap = generateTextBitmap(this.employee.name ?: "?")

                binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)

                binding.llcViewDetails.setOnClickListener{
                    (context as RankListActivity).showPointsDetails()
                }








            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateList(newList: List<RankItem>) {
        list = newList
        notifyDataSetChanged()
    }

}