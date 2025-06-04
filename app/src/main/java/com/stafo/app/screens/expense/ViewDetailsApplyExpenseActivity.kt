package com.stafo.app.screens.expense

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityViewDetailsApplyExpenseBinding

class ViewDetailsApplyExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDetailsApplyExpenseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityViewDetailsApplyExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        setOnClickListener()




    }

    private fun setOnClickListener(){
        binding.apply {
           imageBack.setOnClickListener {
               onBackPressedDispatcher.onBackPressed()
               finish()
           }
        }
    }
}