package com.stafo.app.screens.subscription

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.databinding.ActivityPackageDetailsBinding
import com.stafo.app.screens.billpayment.BillPaymentsViewModel
import com.stafo.app.screens.subscription.dataClass.HashGenerateRequest
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast
import com.stafo.app.utils.getEmployeeComId
import com.stafo.app.utils.getFormatDate

class PackageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPackageDetailsBinding
    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val billPaymentsViewModel: BillPaymentsViewModel by viewModels()

    private var downloadUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPackageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)

        onClickListener()
        observeViewModel()


    }

    private fun onClickListener() {


        binding.apply {

            billPaymentsViewModel.getSubscriptionInfo(
                this@PackageDetailsActivity
            )

            imageBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }


            btnInvoice.setOnClickListener {

                if (downloadUrl.isNotBlank()) downloadInvoice(this@PackageDetailsActivity,downloadUrl)

            }


            btnUpgrade.setOnClickListener {
                startActivity(Intent(this@PackageDetailsActivity, SubscriptionActivity::class.java))
            }


        }
    }

    private fun observeViewModel() {


        billPaymentsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }

        billPaymentsViewModel.mMySubscriptionResponse.observe(this) { response ->
            if (response.status && response.data != null) {

                downloadUrl = response.downloadUrl
                val data = response.data
                val packageInfo = data.`package`

                // Safely set data to views
                binding.tvPackageName.text = packageInfo?.package_name ?: "-"
                val packagePrice = data.package_price?.toInt() ?: 0
                binding.tvPrice.text = "₹ ${if (packagePrice == 0) "-" else packagePrice}"
                binding.tvSubscriptionStart.text = getFormatDate(data.subscription_start) ?: "-"
                binding.tvSubscriptionEnd.text = getFormatDate(data.subscription_end) ?: "-"

                val desc = if (packageInfo?.description != null) {
                    HtmlCompat.fromHtml(packageInfo.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } else {
                    "N/A"
                }
                binding.tvDescription.text = desc
            } else {
                // Optionally hide the layout or show "No subscription found"
                binding.rtlSubscriptionInfo.visibility = View.GONE
                startActivity(Intent(this@PackageDetailsActivity, SubscriptionActivity::class.java))
            }
        }


    }

    private fun handleLoader(status: String) {
        if (status.equals("load", ignoreCase = true)) {
            if (!customLoader.isShowing) customLoader.show()
        } else if (status.equals("stop", ignoreCase = true)) {
            if (customLoader.isShowing) customLoader.dismiss()
        }
    }


    private fun downloadInvoice(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)

            request.setTitle("Downloading Invoice")
            request.setDescription("Please wait...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, "invoice.pdf"
            )
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            CustomToast(this, "Invoice download started")
        } catch (e: Exception) {
            CustomToast(this, "Download failed: ${e.localizedMessage}")

        }
    }
}