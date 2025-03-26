package com.stafo.app.screens.settings

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.stafo.app.R
import com.stafo.app.base.adapter.DynamicHolidayAdapter
import com.stafo.app.base.model.DynamicHolidayField
import com.stafo.app.databinding.ActivityAddHolidayBinding
import com.stafo.app.screens.settings.dataClass.CreateHolidayRequest
import com.stafo.app.screens.settings.dataClass.HolidayPostData
import com.stafo.app.utils.CustomLoader
import com.stafo.app.utils.CustomToast

class AddHolidayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHolidayBinding
    private lateinit var adapter: DynamicHolidayAdapter
    private val dynamicFields = mutableListOf<DynamicHolidayField>()

    private val customLoader: CustomLoader by lazy { CustomLoader(this) }
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddHolidayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorTextPrimary)


        setupRecyclerView()
        addDynamicField()
        onClickListener()
        observeViewModel()
    }






    private fun observeViewModel() {


        settingsViewModel.getLoaderLiveData().observe(this) { handleLoader(it) }



        settingsViewModel.mCreateHolidayResponse.observe(this) {

            Log.d("res",it.data.toString())

            if (it.status) {
               CustomToast(this,it.message)
                onBackPressedDispatcher.onBackPressed()
                finish()

            } else {
                CustomToast(this,it.message)
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

    private fun setupRecyclerView() {
        adapter = DynamicHolidayAdapter(dynamicFields)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun onClickListener() {
        binding?.apply {








            llcAddMore.setOnClickListener {
                addDynamicField()
            }



            ivBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }

            btnAddHoliday.setOnClickListener {


                if (dynamicFields.size>0){
                    if (adapter.isValid()) {
                        val allFields = adapter.getAllFields()

                        val holidaysList = allFields.map { field ->
                            HolidayPostData(
                                title = field.userInput,
                                description = "",
                                start_date = field.userInput2,
                                end_date = field.userInput3
                            )
                        }

                        val request = CreateHolidayRequest(holidaysList)

                        settingsViewModel.addHoliday(this@AddHolidayActivity,request)
                }else {
                        CustomToast(this@AddHolidayActivity, "Please fill blank field!")
                    }


                } else {
                    CustomToast(this@AddHolidayActivity, "Please add holiday!")
                }
            }


        }
    }

    private fun addDynamicField() {
        val newField =
            DynamicHolidayField("Enter Holiday Name", "Enter Start Date", "Enter End Date")

        if (::adapter.isInitialized) {
            adapter.addField(newField)
        } else {
            adapter = DynamicHolidayAdapter(dynamicFields)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }


}