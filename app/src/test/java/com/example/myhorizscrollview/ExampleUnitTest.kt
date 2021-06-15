package com.example.myhorizscrollview

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        repeat(50) {
            println(it)
        }
    }

    @Test
    fun testTime() {
        Timer().schedule(object:TimerTask(){
            override fun run() {
                println("Hello World!")
            }
        }, Date(), 1000)
    }

}