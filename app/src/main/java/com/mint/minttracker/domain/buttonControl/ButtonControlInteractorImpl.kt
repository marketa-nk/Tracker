package com.mint.minttracker.domain.buttonControl

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_PAUSED
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_RESUMED
import com.mint.minttracker.mapFragment.MapPresenter.Companion.STATUS_STARTED
import com.mint.minttracker.services.LocationServiceForeground
import javax.inject.Inject

class ButtonControlInteractorImpl @Inject constructor(private val appContext: Context) : ButtonControlInteractor {

    override fun controlButtonPressed(str: String): ButtonState {
        return when (str) {
            STATUS_STARTED -> ButtonState(start = false, pause = true, resume = false, stop = true)
            STATUS_PAUSED -> ButtonState(start = false, pause = false, resume = true, stop = true)
            STATUS_RESUMED -> ButtonState(start = false, pause = true, resume = false, stop = true)
            else -> ButtonState(start = true, pause = false, resume = false, stop = false)
        }
    }

    override fun start(str: String) {
        when (str) {
            STATUS_STARTED, STATUS_RESUMED -> startLocationService(true, str)
            else -> startLocationService(false, str)
        }
    }

    private fun startLocationService(run: Boolean, status: String) {
        val serviceIntent = Intent(appContext, LocationServiceForeground::class.java).apply {
            putExtra(LocationServiceForeground.STATUS, status)
            action = if (run) {
                LocationServiceForeground.ACTION_START_FOREGROUND_SERVICE
            } else {
                LocationServiceForeground.ACTION_STOP_FOREGROUND_SERVICE
            }
        }
        ContextCompat.startForegroundService(appContext, serviceIntent)
    }
}