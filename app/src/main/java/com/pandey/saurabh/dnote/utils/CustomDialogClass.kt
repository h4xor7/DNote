package com.pandey.saurabh.dnote.utils


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.pandey.saurabh.dnote.R


class CustomDialogClass     // TODO Auto-generated constructor stub
    (var c: Activity) : Dialog(c), View.OnClickListener {
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        yes = findViewById<Button>(R.id.btn_yes)
        no = findViewById<Button>(R.id.btn_no)
        yes?.setOnClickListener(this)
        no?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_yes -> c.finish()
            R.id.btn_no -> dismiss()
            else -> {
            }
        }
        dismiss()
    }
}