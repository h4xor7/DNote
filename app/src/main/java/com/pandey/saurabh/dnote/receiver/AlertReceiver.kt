package com.pandey.saurabh.dnote.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.pandey.saurabh.dnote.utils.NotifyMe


class AlertReceiver : BroadcastReceiver() {

    private val mNotificationManager: NotificationManager? = null


    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "test alarm", Toast.LENGTH_SHORT).show()
        NotifyMe.displayNotification(context,"Work Notification","You have some Important Work to do")
    }


}
