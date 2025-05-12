package com.stafo.app.screens.crm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ajithvgiri.searchdialog.OnSearchItemSelected
import com.ajithvgiri.searchdialog.SearchListItem
import com.ajithvgiri.searchdialog.SearchableDialog
import com.stafo.app.R
import com.stafo.app.databinding.ActivityTakeFollowUpBinding
import com.stafo.app.screens.crm.adapters.FollowUpAdapter

class TakeFollowUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTakeFollowUpBinding
    private lateinit var followUpAdapter: FollowUpAdapter


    private val followUpTypes = arrayListOf(
        SearchListItem(1, "By Visiting"),
        SearchListItem(2, "By Call")
    )

    private val leadStatuses = arrayListOf(
        SearchListItem(1, "New"),
        SearchListItem(2, "Contacted"),
        SearchListItem(3, "Qualified"),
        SearchListItem(4, "Lost"),
        SearchListItem(5, "Onboarding"),
        SearchListItem(6, "Customer")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //  setContentView(R.layout.activity_take_follow_up)
        binding = ActivityTakeFollowUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupLeadDetails()
        setupListener()
    }

    private fun setupListener() {
        binding.edtType.setOnClickListener {

            val dialog = SearchableDialog(this, followUpTypes, "Follow-up Type")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.edtType.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

        // Set up searchable dialog for Lead Status
        binding.edtStatus.setOnClickListener {
            val dialog = SearchableDialog(this, leadStatuses, "Follow-up Status")
            dialog.setOnItemSelected(object : OnSearchItemSelected {
                override fun onClick(position: Int, searchListItem: SearchListItem) {
                    binding.edtStatus.setText(searchListItem.title)
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }


    private fun setupLeadDetails() {
        // Set lead basic data (This would usually come via Intent or ViewModel)
        binding.tvLeadName.text = "John Doe"
        binding.tvLeadCompany.text = "Company: XYZ Ltd."
        binding.tvLeadPhone.text = "Phone: +91 9876543210"
    }
}