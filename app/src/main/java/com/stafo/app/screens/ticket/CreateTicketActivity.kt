package com.stafo.app.screens.ticket

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityCreateTicketBinding
import com.stafo.app.screens.settings.dataClass.AddEmpRequestBody
import com.stafo.app.utils.CustomToast

class CreateTicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTicketBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onClickListener()


    }

    private fun onClickListener() {


        binding.apply {


            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            btnNext.setOnClickListener {
                if (validateInfo()) {
                   CustomToast(this@CreateTicketActivity,"OK")
                }


            }


        }
    }

    private fun validateInfo(): Boolean {
        binding.apply {
            if (tieTitle.text.isNullOrEmpty()) {
                tieTitle.error = "Please enter title"
                tieTitle.requestFocus()
                return false
            } else if (tieMessage.text.isNullOrEmpty()) {
                tieMessage.error = "Please enter your message"
                tieMessage.requestFocus()
                return false
            }


        }
        return true
    }
}