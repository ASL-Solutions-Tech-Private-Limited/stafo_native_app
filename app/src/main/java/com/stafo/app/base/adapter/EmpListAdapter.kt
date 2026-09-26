package com.stafo.app.base.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stafo.app.R
import com.stafo.app.databinding.RecyViewEmployeeItemLayoutBinding
import com.stafo.app.screens.emp.EmpSelfieAttendanceActivity
import com.stafo.app.screens.emp.EmployeeProfileDetails
import com.stafo.app.screens.settings.UploadSelfieAttendanceActivity
import com.stafo.app.screens.settings.ViewAllEmployeeActivity
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.screens.settings.dataClass.Shift
import com.stafo.app.screens.ui.AutoSearchPlaceActivity
import com.stafo.app.utils.checkExactAlarmPermission
import com.stafo.app.utils.currencyFormatter
import com.stafo.app.utils.generateTextBitmap
import com.stafo.app.utils.requestIgnoreBatteryOptimization
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EmpListAdapter(
    private var list: List<GetEmployee>,
    var context: Context,
    var from: String,
    var onEmGeoClick: onGeoClick
) : RecyclerView.Adapter<EmpListAdapter.ViewHolder>() {
    private var mAssignShifts: List<Shift> = emptyList()


    inner class ViewHolder(val binding: RecyViewEmployeeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyViewEmployeeItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.txtEmpName.text = this.name
                binding.txtMobile.text = this.phone
                binding.txtEmail.text = this.email
                binding.txtJobTitle.text = this.position
                binding.txtSalaryTitle.text = currencyFormatter(this.salary.toString())

                val placeholderBitmap = generateTextBitmap(this.name ?: "?")
                if (!this.selfieImage.isNullOrEmpty()) {
                    val imageUrl = "${this.selfieImagePath}/${this.selfieImage}".replace("\\", "")

                    Glide.with(context).load(imageUrl).error(placeholderBitmap)
                        .into(binding.approveLvEmpImage)

                } else {
                    binding.approveLvEmpImage.setImageBitmap(placeholderBitmap)
                }
                if (from == "View All") {

                    binding.llcViewProfile.visibility = View.GONE
                    binding.ivEdit.visibility = View.VISIBLE
                    binding.ivPunch.visibility = View.VISIBLE
                    binding.llcAddAttendance.visibility = View.GONE
                    binding.llcShiftTime.visibility = View.GONE

                    if (this.status == "1") {
                        binding.llcActiveEmp.visibility = View.GONE
                        binding.llcInActiveEmp.visibility = View.VISIBLE
                    } else {
                        binding.llcActiveEmp.visibility = View.VISIBLE
                        binding.llcInActiveEmp.visibility = View.GONE
                    }


                    if (this.selfieImage.isNullOrEmpty()) {
                        binding.llcUploadSelfie.visibility = View.VISIBLE
                        binding.llcRemoveSelfie.visibility = View.GONE

                    } else {
                        binding.llcRemoveSelfie.visibility = View.VISIBLE
                        binding.llcUploadSelfie.visibility = View.GONE
                    }

                    binding.llcRemoveSelfie.setOnClickListener {
                        (context as ViewAllEmployeeActivity).showRemoveAlert(list[position].id.toString())
                    }


                    binding.llcUploadSelfie.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context, UploadSelfieAttendanceActivity::class.java
                            ).apply {
                                putExtra("EMP_ID", list[position].id.toString())
                            })
                    }


                    binding.llcActiveEmp.setOnClickListener {
                        (context as ViewAllEmployeeActivity).showActiveAlert(
                            list[position].id.toString(), "1"
                        )
                    }

                    binding.llcInActiveEmp.setOnClickListener {
                        (context as ViewAllEmployeeActivity).showActiveAlert(
                            list[position].id.toString(), "0"
                        )
                    }

                    binding.ivEdit.setOnClickListener {

                        Log.d("res", "id :${list[position].id}]")
                        context.startActivity(
                            Intent(
                                context, EmployeeProfileDetails::class.java
                            ).apply {
                                putExtra("EMP_ID", list[position].id.toString())
                                putExtra("EMP_TYPE", "Edit")

                            })
                    }

                    val todayDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val punchesToday = this.punches?.filter { punch ->
                        punch.punchIn?.startsWith(todayDate) == true
                    } ?: emptyList()



                    if (punchesToday.isNotEmpty()) {
                        val ongoingPunchToday = punchesToday.find { punch ->
                            !punch.punchIn.isNullOrBlank() &&
                            (punch.punchOut == null || punch.punchOut.isBlank()) &&
                            punch.punchOut?.startsWith("0000") != true
                        }

                        if (ongoingPunchToday != null) {
                            binding.ivPunch.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context, R.color.pastel_red
                                )
                            )
                        } else {
                            binding.ivPunch.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    context, R.color.green
                                )
                            )
                        }
                    } else {
                        binding.ivPunch.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context, R.color.green
                            )
                        )
                    }









                    binding.ivPunch.setOnClickListener {

                        Log.d("EMP_ID", "id :${list[position].id}]")
                        context.startActivity(
                            Intent(
                                context, EmpSelfieAttendanceActivity::class.java
                            ).apply {
                                putExtra("EMP_ID", list[position].id.toString())

                            })
                    }


                    if (this.geo_status == "2") {
                        // Case when geo_status is "2" (Tracking Active)
                        binding.llcReqLocation.visibility = View.GONE
                        binding.llcViewMap.visibility = View.VISIBLE
                    } else if (this.geo_status == "1") {
                        // Case when geo_status is "1" (Request Pending)
                        binding.llcReqLocation.visibility = View.GONE
                        binding.llcViewMap.visibility = View.GONE
                    } else {
                        // Case when geo_status is "0" or NULL/EMPTY
                        binding.llcViewMap.visibility = View.GONE
                        binding.llcReqLocation.visibility = View.VISIBLE
                    }



                    binding.llcViewMap.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context, AutoSearchPlaceActivity::class.java
                            ).apply {
                                putExtra("EMP_ID", list[position].id.toString())
                            })
                    }

                    binding.llcReqLocation.setOnClickListener {
                        onEmGeoClick.onEMPClick(
                            list[position].id.toString(), binding.txtReqLocation.text.toString()
                        )
                    }
                } else {

                    binding.llcActiveEmp.visibility = View.GONE
                    binding.llcInActiveEmp.visibility = View.GONE
                    binding.llcViewProfile.visibility = View.GONE
                    binding.llcReqLocation.visibility = View.GONE
                    binding.llcViewMap.visibility = View.GONE
                    binding.ivEdit.visibility = View.GONE
                    binding.ivPunch.visibility = View.GONE
                    binding.llcAddAttendance.visibility = View.VISIBLE
                    binding.llcShiftTime.visibility = View.VISIBLE

                }

                binding.llcAddAttendance.setOnClickListener {
                    (context as ViewAllEmployeeActivity).showCustomBottomSheet(
                        this.id, this.attendanceType
                    )
                }

                binding.llcShiftTime.setOnClickListener {
                    mAssignShifts = this.shifts
                    (context as ViewAllEmployeeActivity).showShiftCustomBottomSheet(
                        this.id.toString()
                    )
                }


            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onGeoClick {
        fun onEMPClick(empID: String, type: String)
    }

    fun updateList(newList: List<GetEmployee>) {
        list = newList
        notifyDataSetChanged()
    }


    fun getAssignShift(): List<Shift> {
        return mAssignShifts
    }

}