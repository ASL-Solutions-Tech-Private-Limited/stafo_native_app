package com.stafo.app.screens.recharge

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.ActionsListAdapter
import com.stafo.app.databinding.ActivityContactsBinding
import com.stafo.app.screens.emp.EmpBranchDetailsActivity
import com.stafo.app.screens.emp.EmployeeAttendanceRecordActivity
import com.stafo.app.screens.emp.EmployeeLeaveHistoryActivity
import com.stafo.app.screens.recharge.dataclass.Contact
import com.stafo.app.screens.recharge.dataclass.ContactsAdapter
import com.stafo.app.screens.settings.PolicyActivity
import com.stafo.app.screens.settings.dataClass.GetEmployee
import com.stafo.app.utils.getEmployeeDetails

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityContactsBinding

    private var contactList: List<Contact> = listOf()
    private var filteredList: List<Contact> = listOf()

    private lateinit var rvAdapter:ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        onClickListener()
        setupSearchListener()
    }

    private fun onClickListener() {
        binding.apply {



            binding.imgBackBtn.setOnClickListener {
              onBackPressed()
            }

             contactList = loadContacts()
            filteredList=contactList

            if (contactList.isNotEmpty()){
                binding.edtSearch.isFocusable = true
                binding.edtSearch.isFocusableInTouchMode = true

                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(this@ContactsActivity, LinearLayoutManager.VERTICAL, false)
                binding.rvListContact.setLayoutManager(layoutManager)
                rvAdapter=ContactsAdapter(contactList,this@ContactsActivity,
                    object : ContactsAdapter.ContactsClickListener {
                        override fun onActionClick(action: String) {
                            startActivity(
                                Intent(
                                    this@ContactsActivity,
                                    PlanActivity::class.java
                                ).apply {
                                    putExtra("PHONE", "$action")
                                }
                            )
                            overridePendingTransition(
                                R.anim.slide_from_right,
                                R.anim.slide_to_left
                            )
                        }

                    })
                binding.rvListContact.adapter = rvAdapter
                rvAdapter.notifyDataSetChanged()

            }else{
                binding.edtSearch.isFocusable = false
                binding.edtSearch.isFocusableInTouchMode = false
            }






        }
    }

    private fun loadContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val number = it.getString(numberIndex)
                contactList.add(Contact(name, number))
            }
        }
        return contactList
    }

    private fun setupSearchListener() {
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        filteredList = if (query.isEmpty()) {
            contactList
        } else {
            val resultList = contactList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query, ignoreCase = true)
            }.toMutableList()

            // If query is a 10-digit number and not already in the list
            if (query.length == 10 && query.all { it.isDigit() } &&
                contactList.none { it.phone == query }) {

                resultList.add(Contact(name = "New Contact", phone = query))
            }

            resultList
        }

        rvAdapter.updateList(filteredList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, RechargeActivity::class.java))
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right)
        finish()
    }

}