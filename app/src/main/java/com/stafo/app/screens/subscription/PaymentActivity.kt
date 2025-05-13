package com.stafo.app.screens.subscription

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPaymentBinding
import com.stafo.app.screens.dashboard.EmployerDashboard
import com.stafo.app.screens.subscription.dataClass.HashGenerateRequest
import com.stafo.app.utils.getEmployeeComId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private var paymentType: String = "success"
    private var name: String = ""
    private var txnId: String = ""
    private var amt: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)
        paymentType = intent.getStringExtra("type") ?: ""
        name = intent.getStringExtra("pName") ?: ""
        txnId = intent.getStringExtra("txnId") ?: ""
        amt = intent.getStringExtra("amount") ?: ""
        onClickListener()
    }

    private fun onClickListener() {


        binding.apply {


            imageBack.setOnClickListener {
                when (paymentType) {
                    "success" -> {
                        startActivity(Intent(this@PaymentActivity, EmployerDashboard::class.java))
                        finish()
                    }

                    "failed" -> {
                        onBackPressedDispatcher.onBackPressed()
                        finish()
                    }

                    else -> {
                        onBackPressedDispatcher.onBackPressed()
                        finish()
                    }
                }
            }


            when (paymentType) {
                "success" -> {
                    animationSuccess.visibility = View.VISIBLE
                    animationFailed.visibility = View.GONE
                    animationSuccess.playAnimation()
                    tvPaymentStatus.text = "Payment Successful"
                    tvPaymentStatus.setTextColor(getColor(R.color.primaryColorDark))

                }

                "failed" -> {
                    animationSuccess.visibility = View.GONE
                    animationFailed.visibility = View.VISIBLE
                    animationFailed.playAnimation()
                    tvPaymentStatus.text = "Payment Failed"
                    tvPaymentStatus.setTextColor(getColor(R.color.pastel_red))

                }
            }


            btnDone.setOnClickListener {
                when (paymentType) {
                    "success" -> {
                        startActivity(Intent(this@PaymentActivity, EmployerDashboard::class.java))
                        finish()

                    }

                    "failed" -> {
                        onBackPressedDispatcher.onBackPressed()
                        finish()

                    }
                    else -> {
                        onBackPressedDispatcher.onBackPressed()
                        finish()
                    }
                }
            }



            tvTxnId.text = txnId
            tvPackageName.text = name
            tvAmount.text = "\u20B9 $amt"
            val currentDate = Date()
            val dateFormat = SimpleDateFormat("d MMM, yyyy", Locale.ENGLISH)
            val formattedDate = dateFormat.format(currentDate)

            tvDate.text = formattedDate
        }
    }
}