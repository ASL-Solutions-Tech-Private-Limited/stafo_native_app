package com.stafo.app.screens.recharge

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.stafo.app.R
import com.stafo.app.databinding.ActivityRechargeBinding
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.screens.recharge.adapter.RecentRechargeAdapter
import com.stafo.app.screens.recharge.adapter.ViewPagerAdapter
import com.stafo.app.screens.recharge.dataclass.RechargeInfo
import com.stafo.app.utils.CustomToast

class RechargeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRechargeBinding
    private val CONTACT_PERMISSION_REQUEST = 1

    private var mCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityRechargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        mCategory = intent.getStringExtra("type").toString()

        onClickListener()



    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_PERMISSION_REQUEST)
        } else {
            startActivity(Intent(this, ContactsActivity::class.java))
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
        }
    }
    private fun onClickListener() {
        binding.apply {



            binding.imgBackBtn.setOnClickListener {
               onBackPressedDispatcher.onBackPressed()
                finish()
            }


            binding.tvPrepaid.setOnClickListener {
                binding.tvPrepaid.setTextColor(getColor(R.color.black))
                binding.preView.visibility = View.VISIBLE

                binding.tvPostpaid.setTextColor(getColor(R.color.gray_colour))
                binding.postView.visibility = View.GONE
            }


            binding.tvPostpaid.setOnClickListener {
                binding.tvPostpaid.setTextColor(getColor(R.color.black))
                binding.postView.visibility = View.VISIBLE

                binding.tvPrepaid.setTextColor(getColor(R.color.gray_colour))
                binding.preView.visibility = View.GONE
            }



            binding.tieContact.setOnClickListener {
                checkPermissions()
            }


            val layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(this@RechargeActivity, LinearLayoutManager.VERTICAL, false)
            binding.rvRecentList.setLayoutManager(layoutManager)
            rvRecentList.adapter=RecentRechargeAdapter(this@RechargeActivity)



        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACT_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, ContactsActivity::class.java))
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left)
            } else {
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                if (!showRationale) {
                    AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This permission is required to access your contacts for selecting numbers easily.")
                        .setCancelable(false)
                        .setPositiveButton("Go to Settings") { dialog, _ ->
                            dialog.dismiss()
                            openAppSettings()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    CustomToast(this, "Permission denied to read contacts")
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

}