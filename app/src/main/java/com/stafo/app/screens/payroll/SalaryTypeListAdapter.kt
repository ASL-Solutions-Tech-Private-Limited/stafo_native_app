package com.stafo.app.screens.payroll

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RecyBranchItemLayoutBinding
import com.stafo.app.databinding.RecySalaryTypeListItemLayoutBinding
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.dataClass.SalaryType

class SalaryTypeListAdapter (
    private var list: List<SalaryType>,
    var context: Activity
) : RecyclerView.Adapter<SalaryTypeListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecySalaryTypeListItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecySalaryTypeListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtBranchName.text = this.salary_type
                binding.txtPaymentType.text = this.salary_type_description
                binding.txtAmount.text = if (this.amount_type == "Flat") {
                    "₹ ${this.amount}"
                } else {
                    "${this.amount}%"
                }

                binding.txtBranchAddress.text = this.salary_type_description

                binding.itemDelete.setOnClickListener {
                    showCompanyDeleteDialog(this.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateList(newList: List<SalaryType>) {
        list = newList
        notifyDataSetChanged()
    }


    private fun showCompanyDeleteDialog(itemId:Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            (context as SalaryTypeActivity).deleteSalaryType(itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}