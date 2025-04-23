package com.stafo.app.screens.recharge

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPayRechargeBinding
import com.stafo.app.databinding.CouponBottomSheetLayoutBinding
import com.stafo.app.databinding.CustomBottomSheetAttendanceLayoutBinding
import com.stafo.app.screens.emp.EmpSelfieAttendanceActivity
import com.stafo.app.screens.emp.EmployeePunchInActivity
import com.stafo.app.screens.emp.QRCodeAttendanceEmpActivity
import com.stafo.app.screens.recharge.adapter.CouponAdapter
import com.stafo.app.screens.recharge.adapter.RecentRechargeAdapter
import com.stafo.app.screens.recharge.dataclass.ContactsAdapter
import com.stafo.app.utils.CustomToast

class PayRechargeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPayRechargeBinding


    //for bottom sheet
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var couponBottomSheetLayout: CouponBottomSheetLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPayRechargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        onClickListener()

    }

    private fun onClickListener() {
        binding.apply {



            imgBackBtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            tvViewPlans.setOnClickListener {
                startActivity(Intent(this@PayRechargeActivity,PlanActivity::class.java))
            }

            rtlCoupon.setOnClickListener {
                showCustomBottomSheet()
            }




        }
    }

    private fun showCustomBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(this)
        couponBottomSheetLayout = CouponBottomSheetLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent)
        }
        bottomSheetDialog.setCancelable(true)

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@PayRechargeActivity, LinearLayoutManager.VERTICAL, false)
        couponBottomSheetLayout.rvCouponList.setLayoutManager(layoutManager)
        couponBottomSheetLayout.rvCouponList.adapter = CouponAdapter(this@PayRechargeActivity)
        bottomSheetDialog.setContentView(couponBottomSheetLayout.root)
        bottomSheetDialog.show()
    }
}