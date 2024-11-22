package com.app.stopwatchapp
//import androidx.databinding.DataBindingUtil
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var stopWatchService: StopWatchService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StopWatchService.LocalBinder
            stopWatchService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            stopWatchService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start the service
        Intent(this, StopWatchService::class.java).also { intent ->
            startService(intent)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // Button listeners
        binding.startButton.setOnClickListener {
            stopWatchService?.startStopwatch()
        }

        binding.pauseButton.setOnClickListener {
            stopWatchService?.pauseStopwatch()
        }

        binding.resetButton.setOnClickListener {
            stopWatchService?.resetStopwatch()
        }

        // Update the UI every second
        updateElapsedTime()
    }

    private fun updateElapsedTime() {
        binding.timeTextView.postDelayed({
            stopWatchService?.let {
                val elapsedTime = it.getElapsedTime()
                binding.timeTextView.text = formatTime(elapsedTime)
            }
            updateElapsedTime()
        }, 1000)
    }

    private fun formatTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}
