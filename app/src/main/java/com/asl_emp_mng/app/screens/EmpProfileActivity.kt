package com.asl_emp_mng.app.screens

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.asl_emp_mng.app.base.model.ProfileType
import com.asl_emp_mng.app.databinding.ActivityEmpProfileBinding

class EmpProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEmpProfileBinding.inflate(layoutInflater)
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

            val profileType = ProfileType.valueOf(
                intent.getStringExtra("PROFILE_TYPE") ?: ProfileType.BASIC.name
            )

            if (profileType == ProfileType.BASIC) {
                binding.rlBasicDetails.visibility = View.VISIBLE

            } else if (profileType == ProfileType.EDUCATION) {
                binding.rlEducationDetails.visibility = View.VISIBLE

            } else if (profileType == ProfileType.DOCUMENT) {
                binding.rlDocumentDetails.visibility = View.VISIBLE
            } else {
                binding.nsvCompanyDetail.visibility = View.VISIBLE
            }



            imageBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        }
    }
}