package com.stafo.app.base.model

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import com.stafo.app.R

class FullScreenDialog(context: Context, private val onAllowClicked: () -> Unit) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_fullscreen)
        window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnAllow = findViewById<Button>(R.id.btn_allow)
        val btnDeny = findViewById<Button>(R.id.btn_deny)

        btnAllow.setOnClickListener {
            onAllowClicked.invoke()
            dismiss()
        }

        btnDeny.setOnClickListener {
            dismiss()
        }
    }
}