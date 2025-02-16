package com.asl_emp_mng.app.screens.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.asl_emp_mng.app.base.adapter.AdapterWishListFrom
import com.asl_emp_mng.app.base.model.DashboardWish
import com.asl_emp_mng.app.databinding.ActivityWishListBinding
import com.asl_emp_mng.app.screens.settings.SettingsViewModel
import com.asl_emp_mng.app.utils.CustomLoader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class WishListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWishListBinding
    private val calendar = Calendar.getInstance()
    private var mWishList = ArrayList<DashboardWish>()
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("WishList")) {
            mWishList = Gson().fromJson(
                intent.getStringExtra("WishList"),
                object : TypeToken<ArrayList<DashboardWish>>() {}.type
            )
        }

        initView()

    }

    private fun initView() {
        binding?.apply {
            ivBack.setOnClickListener { finish() }
            rvWishList.layoutManager =
                LinearLayoutManager(this@WishListActivity, LinearLayoutManager.VERTICAL, false)

            if (mWishList.isNullOrEmpty()) {
                rvWishList.visibility = View.GONE
                llLeaves.visibility = View.VISIBLE

            } else {
                rvWishList.adapter = AdapterWishListFrom(mWishList, this@WishListActivity)
                rvWishList.visibility = View.VISIBLE
            }

        }


    }
}