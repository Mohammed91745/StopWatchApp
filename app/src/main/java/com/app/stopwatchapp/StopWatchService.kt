package com.app.stopwatchapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock

class StopWatchService : Service() {

    private val binder = LocalBinder()
    private val handler = Handler()
    private var isRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L

    inner class LocalBinder : Binder() {
        fun getService(): StopWatchService = this@StopWatchService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun startStopwatch() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime
            handler.post(updateRunnable)
            isRunning = true
        }
    }

    fun pauseStopwatch() {
        if (isRunning) {
            elapsedTime = SystemClock.elapsedRealtime() - startTime
            handler.removeCallbacks(updateRunnable)
            isRunning = false
        }
    }

    fun resetStopwatch() {
        startTime = 0L
        elapsedTime = 0L
        handler.removeCallbacks(updateRunnable)
        isRunning = false
    }

    fun getElapsedTime(): Long {
        return if (isRunning) {
            SystemClock.elapsedRealtime() - startTime
        } else {
            elapsedTime
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 100)
        }
    }
}
