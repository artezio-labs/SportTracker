package com.artezio.sporttracker.data.pedometer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.artezio.sporttracker.presentation.MainActivity

// сервис для шагомера
// но возможно здесь же буду делать всё остальное
// надо над этим подумать
class PedometerService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

}