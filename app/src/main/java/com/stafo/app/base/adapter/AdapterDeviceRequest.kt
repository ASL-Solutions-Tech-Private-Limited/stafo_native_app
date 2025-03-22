package com.stafo.app.base.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RecyNewDeviceRequestEmpLayoutBinding
import com.stafo.app.screens.settings.ViewDeviceRequestEmpActivity
import com.stafo.app.screens.settings.dataClass.DeviceRequest
import com.stafo.app.utils.generateTextBitmap

class AdapterDeviceRequest (
    private var list: List<DeviceRequest>,
    var context: Context
) : RecyclerView.Adapter<AdapterDeviceRequest.ViewHolder>() {
    inner class ViewHolder(val binding: RecyNewDeviceRequestEmpLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyNewDeviceRequestEmpLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.name
                binding.txtMobile.text = this.phone
               // binding.txtJobTitle.text = this.position
                binding.txtEmail.text = this.email
                val placeholderBitmap = generateTextBitmap(this.name ?: "?")

                binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)

                if (this.device_status=="approved"){
                    binding.txtStatus.visibility= View.VISIBLE
                    binding.txtStatus.text="Approved"
                    binding.txtStatus.setTextColor(context.getColor(R.color.primaryColor))
                    binding.rtlApproveReject.visibility= View.GONE
                }else if (this.device_status=="rejected"){
                    binding.txtStatus.visibility= View.VISIBLE
                    binding.txtStatus.text="Rejected"
                    binding.txtStatus.setTextColor(context.getColor(R.color.reject))
                    binding.rtlApproveReject.visibility= View.GONE
                }else{
                    binding.rtlApproveReject.visibility= View.VISIBLE
                    binding.txtStatus.visibility= View.GONE
                }



                binding.btnApprove.setOnClickListener {
                    showAlert("approve",this.id,this.device_id)

                }
                binding.btnReject.setOnClickListener {
                    showAlert("reject",this.id,this.device_id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateList(newList: List<DeviceRequest>) {
        list = newList
        notifyDataSetChanged()
    }


    private fun showAlert(msg:String, id:Int,deviceId:String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Are you sure? Yuo want to $msg device change request!")

        builder.setPositiveButton("Yes") { dialog, which ->

            (context as ViewDeviceRequestEmpActivity).acceptDeviceRequest(id,msg,deviceId)
            dialog.dismiss()


        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }




}