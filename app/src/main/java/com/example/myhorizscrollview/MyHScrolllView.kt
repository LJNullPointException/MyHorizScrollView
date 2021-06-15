package com.example.myhorizscrollview

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.ref.WeakReference
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class MyHScrolllView :
    HorizontalScrollView {
    private lateinit var ll: LinearLayout

    private var playTotal = 5000;
    private var isDelect = false
    private var startPosition: MutableLiveData<Int> = MutableLiveData<Int>()
    private var colors =
        arrayOf(Color.RED, Color.GRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.rgb(4, 2, 9))
    private lateinit var anim: ValueAnimator

    private var musicTime = 877  //模拟一首歌曲的时间映射到进度条的位置


    @RequiresApi(Build.VERSION_CODES.N)
    constructor(context: Context) : super(context) {
        initView()
        startAnim()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        observ()
        startAnim()
    }


    //监听以及更新进度条
    private fun observ() {
        startPosition.observeForever {

            if (scrollX >= musicTime && !isDelect) {    //这里就是播放完成上一首，下一首开始播放的地方
                Log.e("aaa", "删除一个view")
                anim.pause()
                ll.removeViewAt(0)
                smoothScrollTo(0, 0) //第一个消失之后，后面一个移动到0的位置
                requestLayout()
                isDelect = true
                addScaleView(Color.MAGENTA, ll)
                addScaleView(Color.YELLOW, ll)

            } else {
                smoothScrollTo(it / 3, 0)
            }

        }
    }


    private fun initView() {
        startPosition.value = 1
        isHorizontalScrollBarEnabled = false
        ll = LinearLayout(context)
        val lp =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        ll.layoutParams = lp
        ll.gravity = Gravity.CENTER
        addChildView(ll)
        addView(ll)

    }

    private fun addChildView(ll: LinearLayout) {
        for (i in 0..5) {
            addScaleView(colors[i], ll)
        }
    }

    private fun addScaleView(color: Int, ll: LinearLayout) {
        val drawLine = DrawLine(context)
        drawLine.setCirColor(color)

        val lp = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        drawLine.layoutParams = lp
        ll.addView(drawLine)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Log.e("aaa", "l:$l--t:$t--oldl:$oldl---oldt:$oldt")

        //这里滑动的时候要写东西


    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> anim.pause()
            MotionEvent.ACTION_UP -> anim.resume()
        }


        return super.onTouchEvent(ev)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startAnim() {

        anim = ObjectAnimator.ofFloat(0f, 10000f)
        anim.duration = 5000
        anim.addUpdateListener {
            Log.e("aaaa", "${it.animatedFraction} ---:${it.currentPlayTime}")
            startPosition.value = it.currentPlayTime.toInt()

        }
        anim.start()

    }


}

