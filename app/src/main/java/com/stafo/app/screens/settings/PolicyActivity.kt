package com.stafo.app.screens.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stafo.app.R
import com.stafo.app.base.adapter.AdapterPolicy
import com.stafo.app.databinding.ActivityPolicyBinding
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getIsCOMPANYLogin

class PolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPolicyBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var rvAdapter: AdapterPolicy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPolicyBinding.inflate(layoutInflater)
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        finish()
    }


    private fun onClickListener() {
        binding.apply {


            if (getIsCOMPANYLogin(this@PolicyActivity) == true) {
                llcAddPolicy.visibility=View.VISIBLE
            } else {
                llcAddPolicy.visibility=View.GONE
            }






            getEmployeeComId()?.let { settingsViewModel.fetchPolicy(this@PolicyActivity, it.toInt()) }

            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
                getEmployeeComId()?.let { settingsViewModel.fetchPolicy(this@PolicyActivity, it.toInt()) }
            }

            llcAddPolicy.setOnClickListener {
                startActivity(Intent(this@PolicyActivity, AddPolicyActivity::class.java))
            }

            imageBack.setOnClickListener {
                onBackPressed()
            }


        }
    }


    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        settingsViewModel.mPolicyFetchResponse.observe(this) {


            if (it.success){
                Log.d("PolicyResponse", "Data received: ${it.data.size}")
                val filePath=it.file_path

                if (it.data.isNotEmpty()){
                    binding.txtMsg.visibility = View.GONE

                    val layoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.rvPolicyList.setLayoutManager(layoutManager)
                    rvAdapter = AdapterPolicy(it.data, this,filePath)
                    binding.rvPolicyList.adapter = rvAdapter
                    rvAdapter.notifyDataSetChanged()
                } else {
                    binding.txtMsg.visibility = View.VISIBLE
                }
            }else{
                CustomToast(this,it.message)
            }





        }


    }

    override fun onResume() {
        super.onResume()
        getEmployeeComId()?.let { settingsViewModel.fetchPolicy(this@PolicyActivity, it.toInt()) }
    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }




}