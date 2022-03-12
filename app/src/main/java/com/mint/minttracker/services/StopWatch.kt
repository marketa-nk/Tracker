package com.mint.minttracker.services

import javax.inject.Inject

class StopWatch @Inject constructor() {

    private var state: State = State.PAUSE(0)

    fun getTimeSeconds(): Long {
        return (getTimeMilliseconds()) / 1000
    }

    private fun getTimeMilliseconds(): Long {
        return state.getTimeMilliseconds()
    }

    fun start(firstStart: Boolean, totalTimeInMillis: Long) {
        val state = state
        if (state is State.PAUSE) {
            this.state = if (firstStart) {
                State.START(System.currentTimeMillis(), totalTimeInMillis)
            } else {
                State.START(System.currentTimeMillis(), state.totalTimeInMillis)
            }
        }
    }

    fun pause() {
        val state = state
        if (state is State.START) {
            this.state = State.PAUSE(state.totalTimeInMillis + System.currentTimeMillis() - state.startTime)
        }
    }

    sealed class State {
        class START(
            val startTime: Long,
            val totalTimeInMillis: Long,
        ) : State() {
            override fun getTimeMilliseconds(): Long {
                return System.currentTimeMillis() - startTime + totalTimeInMillis
            }
        }

        class PAUSE(
            val totalTimeInMillis: Long,
        ) : State() {
            override fun getTimeMilliseconds(): Long {
                return totalTimeInMillis
            }
        }

        abstract fun getTimeMilliseconds(): Long
    }
}