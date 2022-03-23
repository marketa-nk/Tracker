package com.mint.minttracker.domain.buttonControl

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.mint.minttracker.models.Status
import com.mint.minttracker.services.LocationServiceForeground
import javax.inject.Inject

class ButtonControlInteractorImpl @Inject constructor(private val appContext: Context) : ButtonControlInteractor {

    override fun controlButtonPressed(status: Status): ButtonState {
        return when (status) {
            Status.STATUS_STARTED -> ButtonState(start = false, pause = true, resume = false, stop = false)
            Status.STATUS_PAUSED -> ButtonState(start = false, pause = false, resume = true, stop = true)
            Status.STATUS_RESUMED -> ButtonState(start = false, pause = true, resume = false, stop = false)
            Status.STATUS_FINISHED -> ButtonState(start = true, pause = false, resume = false, stop = false)
        }
    }

    override fun startLocationService(status: Status) {
        val serviceIntent = Intent(appContext, LocationServiceForeground::class.java).apply {
            putExtra(LocationServiceForeground.STATUS, status)
        }
        ContextCompat.startForegroundService(appContext, serviceIntent)
    }
}