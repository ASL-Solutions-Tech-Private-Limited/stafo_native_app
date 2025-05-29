package com.stafo.app.screens.bbps

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBillerByCategoryBinding
import com.stafo.app.screens.bbps.adapters.BBPSBillersAdapter
import com.stafo.app.utils.CustomLoader

class BillerByCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBillerByCategoryBinding
    private val bbpsViewModel: BBPSViewModel by lazy { BBPSViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private lateinit var mAdapter: BBPSBillersAdapter
    private var mBBPSCategory = ""
    private var mCategoryImage = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // setContentView(R.layout.activity_biller_by_category)
        binding = ActivityBillerByCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mBBPSCategory = intent.getStringExtra("category") ?: ""
        mCategoryImage = intent.getStringExtra("category_image") ?: ""
        setupViews()
    }

    private fun setupViews() {
        binding.apply {
            rvBillers.layoutManager = LinearLayoutManager(
                this@BillerByCategoryActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            mAdapter = BBPSBillersAdapter(this@BillerByCategoryActivity) { selectedBiller ->
                startActivity(
                    Intent(
                        this@BillerByCategoryActivity,
                        BillerDetailsViewActivity::class.java
                    ).apply {
                        putExtra("biller", Gson().toJson(selectedBiller))
                        putExtra("category_image", mCategoryImage)
                    })
            }
            rvBillers.adapter = mAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    /*if (!s.isNullOrEmpty()) {
                        if (!mAdapter.getAllData().isNullOrEmpty())
                            mAdapter.filter(s.toString())
                    }*/
                    mAdapter.filter(s?.toString() ?: "")
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })
            ivBack.setOnClickListener { finish() }
            tvTitle.text = mBBPSCategory
        }

        bbpsViewModel.getBillersByCategory(this, mBBPSCategory)

        observerData()
    }


    private fun observerData() {
        bbpsViewModel
            .getLoaderLiveData()
            .observe(this) { status ->
                if (status == "load") customLoader.show() else customLoader.dismiss()
            }

        bbpsViewModel
            .mBBPSBillersResponse
            .observe(this) { response ->
                if (response != null) {
                    if (!response.dataBiller.isNullOrEmpty()) {
                        mAdapter.setData(response.dataBiller ?: emptyList())
                    }
                }
            }
    }
}