package com.stafo.app.screens.performance.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.ItemPerformanceTypeLayoutBinding
import com.stafo.app.screens.performance.CreatePerformanceTypeActivity
import com.stafo.app.screens.performance.PerformanceActivity
import com.stafo.app.screens.performance.dataClass.PerformanceTypeList
import com.stafo.app.screens.settings.BranchActivity
import com.stafo.app.screens.settings.UploadSelfieAttendanceActivity

class AdapterPerformanceType(
    private var list: List<PerformanceTypeList>, var context: Activity
) : RecyclerView.Adapter<AdapterPerformanceType.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPerformanceTypeLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPerformanceTypeLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                binding.tvName.text = this.name
                binding.tvDesc.text = this.description

                binding.itemEdit.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            CreatePerformanceTypeActivity::class.java
                        ).apply {
                            putExtra("id", list[position].id.toString())
                            putExtra("name", list[position].name)
                            putExtra("desc", list[position].description)
                        })



                }

                binding.itemDelete.setOnClickListener {
                    showCompanyDeleteDialog(this.id)
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun showCompanyDeleteDialog(itemId:Int) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Delete this.")

        builder.setPositiveButton("Yes") { dialog, _ ->
            (context as PerformanceActivity).deletePerformanceType(itemId)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}