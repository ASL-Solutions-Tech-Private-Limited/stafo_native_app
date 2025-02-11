package com.asl_emp_mng.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.asl_emp_mng.app.R


/**
 * Created by Saikat on 21-11-2019.
 */
class CustomLoader(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //getWindow().getAttributes().windowAnimations = R.style.Theme_Progress_Dialog;
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //window!!.setDimAmount(0.0f)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
    }
}