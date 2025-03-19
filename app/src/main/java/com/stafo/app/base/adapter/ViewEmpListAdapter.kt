package com.stafo.app.base.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.databinding.RecyViewEmployeeItemLayoutBinding
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.ui.AutoSearchPlaceActivity

class ViewEmpListAdapter (
    private var list: List<GetEmployee>,
    var context: Activity
) : RecyclerView.Adapter<ViewEmpListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RecyViewEmployeeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyViewEmployeeItemLayoutBinding.inflate(
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
               // binding.txtJobTitle.text = this.position
                binding.llcViewProfile.visibility = View.GONE
                binding.llcAddAttendance.visibility = View.GONE
                binding.llcShiftTime.visibility = View.GONE
                binding.llcReqLocation.visibility = View.GONE
                binding.ivEdit.visibility = View.GONE
                binding.llcViewMap.visibility = View.VISIBLE
                binding.llcViewMap.visibility = View.VISIBLE


                binding.llcViewMap.setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            AutoSearchPlaceActivity::class.java
                        ).apply {
                            putExtra("EMP_ID", list[position].id.toString())
                        })
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