package com.example.myhorizscrollview

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import androidx.lifecycle.Observer
import com.example.myhorizscrollview.progressbar.PodcastProgressBar
import com.example.myhorizscrollview.progressbar.PpbBean
import java.util.*

class MainActivity : AppCompatActivity() {
    var i = -1
    var isPlaying = false
    var preTimer = 0
    var time = 0
    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            time = msg.obj.toString().toInt()

            if (isPlaying && msg.arg1 == i && time - preTimer == 1000) {
                Log.e("aaa", "${msg.obj.toString().toInt()}")
                ppb.updateTime(msg.obj.toString().toInt())
                preTimer = time
            }

        }
    }
    var list: MutableList<PpbBean> = arrayListOf()
    private lateinit var ppb: PodcastProgressBar
    private var isNext = false
    var timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addAll(intiData())

        ppb = findViewById<PodcastProgressBar>(R.id.ppb)
        ppb.update(list)
        ppb.isNext.observe(this, Observer {
            if (it) {
               isPlaying = false
                preTimer = 0
                Log.e("aaa","xiayiqu")
            }

        })
        findViewById<Button>(R.id.add).setOnClickListener {
            list.addAll(intiData())
            ppb.update(list)
        }


        findViewById<Button>(R.id.play).setOnClickListener {
            isPlaying = true
            i++
            timer = Timer()
            timer.schedule(object : TimerTask() {
                var totalTime = 0
                override fun run() {
                    if (totalTime < list[i].time * 1000) {
                        totalTime += 1000
                        val message = Message.obtain()
                        message.obj = totalTime
                        message.arg1 = i
                        handler.sendMessage(message)
                    } else {
                        timer.cancel()
                    }
                }

            }, 0, 1000)
        }
        findViewById<Button>(R.id.next).setOnClickListener {
            ppb.updateNext()
            i++
            isPlaying = false
            preTimer = 0
        }
    }

    private fun intiData(): List<PpbBean> {
        return arrayListOf(
            PpbBean(30, BitmapFactory.decodeResource(resources, R.drawable.sg1)),
            PpbBean(30, BitmapFactory.decodeResource(resources, R.drawable.sg2)),
            PpbBean(52, BitmapFactory.decodeResource(resources, R.drawable.sg3)),
            PpbBean(120, BitmapFactory.decodeResource(resources, R.drawable.sg4)),
            PpbBean(30, BitmapFactory.decodeResource(resources, R.drawable.sg5)),
            PpbBean(90, BitmapFactory.decodeResource(resources, R.drawable.sg6)),
            PpbBean(100, BitmapFactory.decodeResource(resources, R.drawable.sg7)),
            PpbBean(50, BitmapFactory.decodeResource(resources, R.drawable.sg8)),
            PpbBean(60, BitmapFactory.decodeResource(resources, R.drawable.sg9)),
            PpbBean(30, BitmapFactory.decodeResource(resources, R.drawable.sg10)),
            PpbBean(15, BitmapFactory.decodeResource(resources, R.drawable.sg3)),
            PpbBean(10, BitmapFactory.decodeResource(resources, R.drawable.sg4)),
            PpbBean(70, BitmapFactory.decodeResource(resources, R.drawable.sg6)),
            PpbBean(89, BitmapFactory.decodeResource(resources, R.drawable.sg2)),
            PpbBean(90, BitmapFactory.decodeResource(resources, R.drawable.sg4)),
        )

    }


}