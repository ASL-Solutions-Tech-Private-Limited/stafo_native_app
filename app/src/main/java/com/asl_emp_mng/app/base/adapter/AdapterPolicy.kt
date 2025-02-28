package com.asl_emp_mng.app.base.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.databinding.RecyPolicyItemLayoutBinding
import com.asl_emp_mng.app.screens.settings.dataClass.Policy
import com.asl_emp_mng.app.utils.CustomToast

class AdapterPolicy (
    private var list: List<Policy>,
    var context: Context,
    private val filePath: String
) : RecyclerView.Adapter<AdapterPolicy.ViewHolder>() {
    inner class ViewHolder(val binding: RecyPolicyItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyPolicyItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtPolicyName.text = this.title

                binding.llcViewPolicy.setOnClickListener {
                    val pdfUrl = "$filePath/${this.file}"

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