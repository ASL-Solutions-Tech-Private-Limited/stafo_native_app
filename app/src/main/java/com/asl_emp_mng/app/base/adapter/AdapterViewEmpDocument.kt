package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyEmpViewDocItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.Document
import com.asl_emp_mng.app.utils.CustomToast

class AdapterViewEmpDocument (
    private var list: List<Document>,
    var context: Context
) : RecyclerView.Adapter<AdapterViewEmpDocument.ViewHolder>() {
    inner class ViewHolder(val binding: RecyEmpViewDocItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyEmpViewDocItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtPolicyName.text = this.document_name

                binding.llcViewPolicy.setOnClickListener {
                    val pdfUrl = this.file_path

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(pdfUrl)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    try {
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {

                        CustomToast(context,"No browser found to open PDF")

                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}