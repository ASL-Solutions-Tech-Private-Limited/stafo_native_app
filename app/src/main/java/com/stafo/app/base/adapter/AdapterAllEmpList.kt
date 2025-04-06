package com.stafo.app.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.databinding.RecyAllEmpListItemLayoutBinding
import com.stafo.app.screens.settings.AssignBranchActivity
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.generateTextBitmap

class AdapterAllEmpList (
    private var list: List<GetEmployee>,
    var context: Context,
    var from: String
) : RecyclerView.Adapter<AdapterAllEmpList.ViewHolder>() {
    inner class ViewHolder(val binding: RecyAllEmpListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyAllEmpListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.name
                binding.txtMobile.text = this.phone
                binding.txtEmail.text = this.email

                val placeholderBitmap = generateTextBitmap(this.name ?: "?")
                if (!this.selfieImage.isNullOrEmpty()) {
                    val imageUrl = "${this.selfieImagePath}/${this.selfieImage}".replace("\\", "")

                    Glide.with(context)
                        .load(imageUrl)
                        .error(placeholderBitmap)
                        .into(binding.approveLvEmpImage)

                } else {
                    binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)
                }

                if (from == "department"){
                    binding.llcAssignDepartment.visibility=View.VISIBLE
                    binding.llcAssignDepartment.setOnClickListener {
                        (context as AssignBranchActivity).showAssignBottomSheet(this.id.toString())
                    }
                }else{
                    binding.llcAssignBranch.visibility=View.VISIBLE
                    binding.llcAssignBranch.setOnClickListener {
                        (context as AssignBranchActivity).showAssignBottomSheet(this.id.toString())
                    }
                }



            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateList(newList: List<GetEmployee>) {
        list = newList
        notifyDataSetChanged()
    }



}