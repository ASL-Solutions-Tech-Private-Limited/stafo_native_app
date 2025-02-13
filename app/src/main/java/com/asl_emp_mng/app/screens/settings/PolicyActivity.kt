package com.asl_emp_mng.app.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.R
import com.asl_emp_mng.app.databinding.ActivityPolicyBinding
import java.util.Collections
import java.util.Random

class PolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPolicyBinding


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

        onClickListener()

    }


    private fun onClickListener() {
        binding?.apply {


            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false

            }

            llcAddPolicy.setOnClickListener {
                startActivity(Intent(this@PolicyActivity, AddPolicyActivity::class.java))
            }

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


        }
    }


}