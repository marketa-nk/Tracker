package com.mint.minttracker.domain.buttonControl

import com.mint.minttracker.models.Status

interface ButtonControlInteractor {

    fun controlButtonPressed(status: Status): ButtonState

    fun startLocationService(status: Status)
}