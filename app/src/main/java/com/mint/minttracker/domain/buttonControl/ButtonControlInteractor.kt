package com.mint.minttracker.domain.buttonControl

interface ButtonControlInteractor {

    fun controlButtonPressed(str: String): ButtonState

    fun start(str: String)
}