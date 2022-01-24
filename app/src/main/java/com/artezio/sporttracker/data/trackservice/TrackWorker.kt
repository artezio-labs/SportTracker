package com.artezio.sporttracker.data.trackservice

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TrackWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}