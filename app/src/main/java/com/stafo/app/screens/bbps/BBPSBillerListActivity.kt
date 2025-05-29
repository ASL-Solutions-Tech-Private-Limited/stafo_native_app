package com.stafo.app.screens.bbps

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityBbpsbillerListBinding
import com.stafo.app.screens.bbps.adapters.BBPSCategoriesAdapter
import com.stafo.app.utils.CustomLoader

class BBPSBillerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBbpsbillerListBinding
    private val bbpsViewModel: BBPSViewModel by lazy { BBPSViewModel() }
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBbpsbillerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()

        bbpsViewModel.getBBPSCategory(this)
        observeViewModel()
    }

    private fun setupViews() = with(binding) {
        ivBack.setOnClickListener { finish() }

        // Setup RecyclerView with GridLayout and Animation
        servicesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@BBPSBillerListActivity, 3)
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                context, R.anim.layout_animation_fall_down
            )
        }
    }

    private fun observeViewModel() {
        bbpsViewModel.getLoaderLiveData().observe(this) { status ->
            if (status == "load") customLoader.show() else customLoader.dismiss()
        }

        bbpsViewModel.mBBPSCategoryResponse.observe(this) { response ->
            response?.dataCategory?.let { categoryList ->
                val adapter = BBPSCategoriesAdapter(context = this,
                    isCategoryList = true,
                    services = null,
                    categories = categoryList,
                    onServiceClicked = {},
                    onCategoryClicked = {
                        startActivity(Intent(this, BillerByCategoryActivity::class.java).apply {
                            putExtra("category", it.name)
                            putExtra("category_image", it.categoryIcon)
                        })
                    })
                binding.servicesRecyclerView.adapter = adapter
                binding.servicesRecyclerView.scheduleLayoutAnimation()
            }
        }
    }
}

