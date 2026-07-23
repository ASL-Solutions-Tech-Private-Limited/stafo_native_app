package com.stafo.app.screens.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stafo.app.R
import com.stafo.app.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = getColor(R.color.green)

        setOnclickListener()


    }
    private fun setOnclickListener(){
        binding.apply {

            lottieView.playAnimation()
            lottieViewSuccess.playAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                onBackPressedDispatcher.onBackPressed()
            }, 5000)


        }
    }

    override fun onBackPressed() {
        binding.lottieView.cancelAnimation()
        binding.lottieViewSuccess.cancelAnimation()
        overridePendingTransition(
            R.anim.slide_from_left, R.anim.slide_to_right
        )
        finish()
        super.onBackPressed()
    }
}