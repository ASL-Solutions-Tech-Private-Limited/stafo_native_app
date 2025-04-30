package com.stafo.app.screens.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.model.ProfileType
import com.stafo.app.databinding.ActivityChatWithCompanyBinding
import com.stafo.app.screens.billpayment.Adapter.AdapterElectricityItemList
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.chat.adapter.CompanyChatAdapter
import com.stafo.app.screens.chat.dataClass.ChatRequest
import com.stafo.app.screens.chat.dataClass.ChatResponse
import com.stafo.app.screens.chat.dataClass.SendChatRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getEmployeeDetails

class ChatWithCompanyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatWithCompanyBinding
    private lateinit var chatAdapter: CompanyChatAdapter

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatWithCompanyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()


    }


    private fun onClickListener() {
        binding.apply {


            getEmployeeComId()?.let {

                val request = ChatRequest(
                    company_id = it
                )

                billPaymentsViewModel.getChatList(
                    this@ChatWithCompanyActivity, request
                )
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            sivChat.setOnClickListener {
                if (!edtChat.text.isNullOrEmpty()) {
                    getEmployeeComId()?.let {

                        val request = SendChatRequest(
                            company_id = it, message = edtChat.text.toString()
                        )

                        billPaymentsViewModel.sendChatRequest(
                            this@ChatWithCompanyActivity, request
                        )
                        edtChat.text?.clear()
                        edtChat.clearFocus()

                        // For Hide keyboard
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(edtChat.windowToken, 0)
                    }

                } else CustomToast(this@ChatWithCompanyActivity, "Please enter something!")
            }


        }
    }


    private fun observeViewModel() {
        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mChatResponse.observe(this) {

            if (it.success) {
                if (!it.data.isNullOrEmpty()) {
                    binding.recyclerViewChat.visibility = View.VISIBLE
                    chatAdapter = CompanyChatAdapter(it.data, this)
                    binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
                    binding.recyclerViewChat.adapter = chatAdapter

                } else binding.recyclerViewChat.visibility = View.GONE

            } else CustomToast(this, it.message)


        }

        billPaymentsViewModel.mSendChatResponse.observe(this) {

            if (it.success) {
                CustomToast(this, it.message)
                getEmployeeComId()?.let {

                    val request = ChatRequest(
                        company_id = it
                    )

                    billPaymentsViewModel.getChatList(
                        this@ChatWithCompanyActivity, request
                    )
                }

            } else CustomToast(this, it.message)


        }


    }


    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }
}