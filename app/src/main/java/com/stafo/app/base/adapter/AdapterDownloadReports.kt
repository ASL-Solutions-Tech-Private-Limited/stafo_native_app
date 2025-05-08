package com.stafo.app.base.adapter

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.databinding.RecyDownloadReportsItemLayoutBinding
import com.stafo.app.screens.payroll.dataClass.ReportFile
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.reportsFormatToMonthYear

class AdapterDownloadReports(
    private var list: List<ReportFile>,
    var context: Activity
) : RecyclerView.Adapter<AdapterDownloadReports.ViewHolder>() {
    inner class ViewHolder(val binding: RecyDownloadReportsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyDownloadReportsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                val displayName = this.file_name
                    .split("_")
                    .joinToString(" ") { word -> word.replaceFirstChar { it.uppercaseChar() } }

                binding.txtFileName.text = displayName

                binding.txtDate.text = reportsFormatToMonthYear(this.exported_date)

                when (this.file_type.uppercase()) {
                    "PDF" -> {
                        binding.sivFile.setImageResource(R.drawable.pdf)
                        binding.sivFile.imageTintList = null
                    }

                    "EXCEL" -> {
                        binding.sivFile.setImageResource(R.drawable.xls_file)
                        binding.sivFile.imageTintList =
                            ContextCompat.getColorStateList(context, R.color.blue)
                    }

                    "CSV" -> {
                        binding.sivFile.setImageResource(R.drawable.csv_file)
                        binding.sivFile.imageTintList =
                            ContextCompat.getColorStateList(context, R.color.blue)
                    }

                    else -> {
                        binding.sivFile.setImageResource(R.drawable.pdf)
                        binding.sivFile.imageTintList = null
                    }
                }


                binding.llcOpen.setOnClickListener {
                    val url = this.download_url
                    val fileType = this.file_type.uppercase()

                    if (!url.isNullOrEmpty()){
                        when (fileType) {
                            "PDF" -> {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                intent.setPackage("com.android.chrome")
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                            }

                            "EXCEL", "CSV" -> {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(
                                    Uri.parse(url), when (fileType) {
                                        "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                        "CSV" -> "text/csv"
                                        else -> "*/*"
                                    }
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {

                                    CustomToast(context, "No app found to open this file type")
                                }
                            }

                            else -> {
                                CustomToast(context, "Unsupported file type")
                            }
                        }
                    }


                }
                binding.llcShare.setOnClickListener {
                    val url = this.download_url

                    if (!url.isNullOrEmpty()) {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, url)
                            type = "text/plain"
                        }

                        val chooser = Intent.createChooser(shareIntent, "Share File Link via")
                        context.startActivity(chooser)
                    } else {
                        CustomToast(context,"Download URL is not available")
                    }

                }



            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}