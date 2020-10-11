package com.pandey.saurabh.dnote.ui.main.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.format.DateFormat;
import java.util.*


class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour: Int = c.get(Calendar.HOUR_OF_DAY)
        val minute: Int = c.get(Calendar.MINUTE)
        return TimePickerDialog(
            activity, activity as OnTimeSetListener?, hour, minute, DateFormat.is24HourFormat(
                activity
            )
        )
    }
}