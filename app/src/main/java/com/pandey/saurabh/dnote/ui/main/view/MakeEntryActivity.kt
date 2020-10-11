package com.pandey.saurabh.dnote.ui.main.view

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.pandey.saurabh.dnote.R
import com.pandey.saurabh.dnote.service.receiver.AlertReceiver
import kotlinx.android.synthetic.main.activity_make_entry.*
import java.text.DateFormat
import java.util.*


class MakeEntryActivity : AppCompatActivity(),TimePickerDialog.OnTimeSetListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_entry)

        btnSave.setOnClickListener {

            val replyIntent =Intent()

            if (TextUtils.isEmpty(editTextTitle.text)|| TextUtils.isEmpty(editTextNote.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }
            else{
                val  title =editTextTitle.text.toString()
                val note = editTextNote.text.toString()

                replyIntent.putExtra(EXTRA_TITLE, title)
                replyIntent.putExtra(EXTRA_NOTE, note)
                setResult(Activity.RESULT_OK, replyIntent)

            }

            finish()
        }
        textTimePicker.setOnClickListener{
            val timePicker: DialogFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "time picker")
        }



    }


    companion object{
        const val EXTRA_TITLE= "title_extra_tag"
        const val EXTRA_NOTE= "note_extra_tag"
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c: Calendar = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        updateTimeText(c)
        startAlarm(c)

    }

    private fun updateTimeText(c: Calendar) {

        var timeText = "Alarm set for: "
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)

        textTimePicker.text = timeText
    }


    private fun startAlarm(c: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }




}