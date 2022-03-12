package com.mint.minttracker

import com.mint.minttracker.services.StopWatch
import org.junit.Assert.assertEquals
import org.junit.Test


class StopWatchTest {

    private val stopwatch: StopWatch = StopWatch()

    @Test
    fun testStart() {

        stopwatch.start(true, 0)

        Thread.sleep(1000)
        assertEquals(1, stopwatch.getTimeSeconds())

        Thread.sleep(1000)
        assertEquals(2, stopwatch.getTimeSeconds())

        Thread.sleep(3000)
        assertEquals(5, stopwatch.getTimeSeconds())

    }

    @Test
    fun testPause() {
        testStart(1000, 1)
        testStart(1000, 2)
        testPause(0, 2)
        testPause(2000, 2)
        testStart(3000, 5)
        testStart(3000, 8)
        testPause(2000, 8)
    }

    private fun testStart(sleep: Long, expected: Long) {
        stopwatch.start(true, 0)
        Thread.sleep(sleep)
        assertEquals(expected, stopwatch.getTimeSeconds())
    }

    private fun testPause(sleep: Long, expected: Long) {
        stopwatch.pause()
        Thread.sleep(sleep)
        assertEquals(expected, stopwatch.getTimeSeconds())
    }


}