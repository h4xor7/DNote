package com.pandey.saurabh.dnote.service.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class AlertReceiver : BroadcastReceiver() {

    private val mNotificationManager: NotificationManager? = null


    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "test alarm", Toast.LENGTH_SHORT).show()


    }


}
