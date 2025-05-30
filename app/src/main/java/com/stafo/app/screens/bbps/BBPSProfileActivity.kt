package com.stafo.app.screens.bbps

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBbpsprofileBinding
import com.stafo.app.screens.bbps.adapters.PromoCodeAdapter
import com.stafo.app.utils.CustomLoader

class BBPSProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBbpsprofileBinding
    private val bbpsViewModel: BBPSViewModel by lazy { BBPSViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_bbpsprofile)
        binding = ActivityBbpsprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            backArrow.setOnClickListener { finish() }
            title.text = "Profile"
            promoRecyclerView.layoutManager =
                LinearLayoutManager(this@BBPSProfileActivity, LinearLayoutManager.HORIZONTAL, false)
            transactionRecyclerView.layoutManager =
                LinearLayoutManager(this@BBPSProfileActivity, LinearLayoutManager.VERTICAL, false)

        }

        bbpsViewModel.getPromoCodeList(this)

        observeViewModel()
    }

    private fun observeViewModel() {
        bbpsViewModel.getLoaderLiveData().observe(this) { status ->
            if (status == "load") customLoader.show() else customLoader.dismiss()
        }

        bbpsViewModel.mPromoCodeListResponse.observe(this) { response ->
            if (response != null) {
                if (!response.dataPromoList.isNullOrEmpty()) {
                    binding?.promoRecyclerView?.adapter =
                        PromoCodeAdapter(
                            this@BBPSProfileActivity,
                            response.dataPromoList ?: emptyList()
                        )
                }
            }
        }
    }
}