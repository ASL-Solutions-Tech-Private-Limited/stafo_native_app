package com.stafo.app.base.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RecyBranchItemLayoutBinding
import com.stafo.app.screens.settings.AddBranchActivity
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.dataClass.BranchItem

class BranchAdapter (
    private var list: List<BranchItem>,
    var context: Activity
) : RecyclerView.Adapter<BranchAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyBranchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyBranchItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtBranchName.text = this.branch_name
                binding.txtBranchAddress.text = this.branch_address

                binding.itemDelete.setOnClickListener {
                    showCompanyDeleteDialog(this.id)
                }


                binding.itemEdit.setOnClickListener {
                    val intent = Intent(context, AddBranchActivity::class.java)
                    intent.putExtra("branch_type", "Edit")
                    intent.putExtra("branch_data", this)
                    context.startActivity(intent)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateList(newList: List<BranchItem>) {
        list = newList
        notifyDataSetChanged()
    }


    private fun showCompanyDeleteDialog(itemId:Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            (context as BranchActivity).deleteBranch(itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}