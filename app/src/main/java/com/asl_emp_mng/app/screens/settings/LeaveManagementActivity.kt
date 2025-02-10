package com.asl_emp_mng.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.base.adapter.LeavesManagementAdapter
import com.asl_emp_mng.app.base.model.LeavesManagementModel
import com.asl_emp_mng.app.databinding.ActivityLeaveManagementBinding
import java.util.Collections
import java.util.Random

class LeaveManagementActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLeaveManagementBinding
    private lateinit var rvAdapter: LeavesManagementAdapter
    private lateinit var leaveRequestList : List<LeavesManagementModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityLeaveManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor= ContextCompat.getColor(this, R.color.primaryColorDark)

        onClickListener()
        loadLeaveRequestList()

    }

    private fun loadLeaveRequestList() {
        leaveRequestList = listOf(
            LeavesManagementModel("Hamid" , "","04/02/2025","Pending","01/02/2025"),
            LeavesManagementModel("Hamid2" , "","04/02/2025","Pending","01/02/2025"),
            LeavesManagementModel("Hamid3" , "","04/02/2025","Pending","01/02/2025"),
            LeavesManagementModel("Hamid4" , "","04/02/2025","Pending","01/02/2025"),
            LeavesManagementModel("Hamid5" , "","04/02/2025","Pending","01/02/2025")

        )
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvShowLeaveList.setLayoutManager(layoutManager)
        rvAdapter = LeavesManagementAdapter(leaveRequestList,this)
        binding.rvShowLeaveList.adapter = rvAdapter
        rvAdapter.notifyDataSetChanged()
    }

    private fun onClickListener() {
        binding?.apply {


            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                Collections.shuffle(leaveRequestList, Random(System.currentTimeMillis()))

                rvAdapter.notifyDataSetChanged()

            }

            imageSettings.setOnClickListener { view ->
                showPopupMenu(view)
            }





        }
    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val menu = popupMenu.menu
        val options = resources.getStringArray(R.array.leave_settings)
        options.forEachIndexed { index, option ->
            menu.add(0, index, index, option)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                0 -> {
                  startActivity(Intent(this, CreateLeavePolicyActivity::class.java))
                    true
                }
                1 -> {
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}