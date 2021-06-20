package com.example.myhorizscrollview.progressbar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

import kotlin.math.abs
import kotlin.jvm.JvmName as JvmName1


class ScrollViewItem : androidx.appcompat.widget.AppCompatTextView, UpdateProgressBarInterface,
    UpdateProgressBarInterface.ProgressBarListener {
    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    companion object {
        //默认间隔时间2s
        const val DEFAULT_TIME_GAP = 2f

        //最小长度
        const val DEFAULT_MIN_GAP_NUMBER = 15
        const val DEFAULT_MAX_GAP_NUMBER = 30

    }

    private lateinit var mPaintLine: Paint
    private lateinit var mPaintCircular: Paint

    //间隔大小
    private var gap: Int = 0

    //每一个间隔代表多少时间
    private var timeGap = DEFAULT_TIME_GAP


    private var minGaps = DEFAULT_MIN_GAP_NUMBER

    private var maxGaps = DEFAULT_MAX_GAP_NUMBER

    private var totalLines = 0

    private var scrollTotalLength = 0

    private var lineMarginTopAndBottom = 0

    private var shortMargin2LongLine = 0


    private var radius = 0

    private lateinit var scrollView: PpbScrollView

    private var _isNext: MutableLiveData<Boolean> = MutableLiveData()

    private var datas: MutableList<PpbBean> = arrayListOf()

    private lateinit var scheduler: ScheduledExecutorService

    private var currentPlayPosition = 0f

    private var isTouchDown = false

    private var headPosition = 0f

    private var datasCurrentIndex = 0

    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            scrollView.smoothScrollTo(msg.obj.toString().toFloat().toInt(), 0)
        }
    }

    private fun initView() {
        initData()
        mPaintLine = Paint()
        mPaintLine.isAntiAlias = true
        mPaintLine.strokeWidth = Util.dip2px(context, 1f).toFloat()
        mPaintLine.color = Color.BLACK

        mPaintCircular = Paint()
        mPaintCircular.isAntiAlias = true
        mPaintCircular.color = Color.GRAY

    }

    private fun initData() {
        scheduler = Executors.newScheduledThreadPool(1);

    }


    fun update(list: List<PpbBean>) {
        calculationLineNumber(list)
    }

    var lastS = 0f
    private fun calculationLineNumber(list: List<PpbBean>) {
        for (bean in list) {
            var currentGaps = 0
            val quotient = bean.time / timeGap
            val reminder = bean.time % timeGap
            if (reminder == 0f)
                currentGaps = quotient.toInt() - 1
            else
                currentGaps = quotient.toInt()

            if (currentGaps < minGaps) {
                currentGaps = minGaps
            } else if (currentGaps > maxGaps) {
                currentGaps = maxGaps
            }
            if (reminder != 0f) {
                bean.gaps = currentGaps + (reminder / timeGap)  //将时间映射到刻度上
                lastS = timeGap - reminder
            } else {
                bean.gaps = currentGaps.toFloat()
            }
            totalLines += currentGaps + 1
        }
        datas.addAll(list)
        requestLayout()
    }

    private fun calculationCircularPosition() {
        for ((index, element) in datas.withIndex()) {
            if (index == 0) {
                element.startPosition = 0
            } else {
                element.startPosition = (element.gaps * gap).toLong()
            }
            element.mFraction = (element.gaps * gap) / element.time / 1000
            element.bitmap = Util.scaleBitmap(element.bitmap, radius * 2f)
        }

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        gap = height / 4
        maxGaps = MeasureSpec.getSize(widthMeasureSpec) / gap
        shortMargin2LongLine = gap
        radius = gap
        val width = totalLines * gap
        calculationCircularPosition()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLine(canvas)
        drawCircular(canvas)
    }

    private fun drawCircular(canvas: Canvas) {
        canvas.save()
        var nextPosition: Float = 0f

        for ((index, element) in datas.withIndex()) {
            canvas.translate(nextPosition * gap, radius.toFloat())
            if (index != 0) {
//                Util.drawCircular(
//                    element.bitmap,
//                    mPaintCircular,
//                    radius.toFloat(),
//                    canvas,
//                    nextPosition * gap
//                )
                canvas.drawBitmap(element.bitmap, 0f, 0f, mPaintCircular)
            }
            canvas.translate(-nextPosition * gap, -radius.toFloat())
            nextPosition += element.gaps
        }
        canvas.restore()
    }


    private fun drawLine(canvas: Canvas) {
        canvas.save()
        for (i in 0..totalLines) {
            if (i % 5 == 0) {
                lineMarginTopAndBottom = 0
            } else {
                lineMarginTopAndBottom = shortMargin2LongLine
            }
            canvas.drawLine(
                i * gap.toFloat(),
                lineMarginTopAndBottom.toFloat(),
                i * gap.toFloat(),
                measuredHeight.toFloat() - lineMarginTopAndBottom,
                mPaintLine
            )
        }
        canvas.restore()
    }

    override fun updateTime(time: Int) {
//        scrollView.smoothScrollTo((time* datas[0].mFraction).toInt(), 0)
        initAnimal(time)
    }

    fun initAnimal(time: Int) {
        var t = time - 1000
        val animal = ValueAnimator.ofFloat(0f, 1000f)
        animal.duration = 1000
        animal.addUpdateListener {
//            Log.e("qunima", "$t ===${it.currentPlayTime}----+${datas[0].mFraction}")

            currentPlayPosition =
                headPosition + ((t + it.currentPlayTime) * datas[datasCurrentIndex].mFraction);
            if (!isTouchDown) {
                scrollView.smoothScrollTo(currentPlayPosition.toInt(), 0)
            }
            Log.e(
                "qunima",
                "$currentPlayPosition ===${headPosition + datas[datasCurrentIndex].gaps * gap}"
            )
            if (currentPlayPosition >= headPosition + datas[datasCurrentIndex].gaps * gap) {
                //当前播放完毕，下一首

                headPosition = currentPlayPosition
                Log.e("aaa", "xiayishou $headPosition")
                datasCurrentIndex++
            }
        }
        animal.start()
    }

    override fun updateNext() {
        scrollView.smoothScrollTo(
            (headPosition + (datas[datasCurrentIndex].gaps * gap)).toInt(),
            0
        );
        headPosition += (datas[datasCurrentIndex].gaps * gap)
        _isNext.value = true
        datasCurrentIndex++
    }


    override fun nextPlay(): MutableLiveData<Boolean> {
        return _isNext
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    fun setScrollView(ppbScrollView: PpbScrollView) {
        scrollView = ppbScrollView
        var startX = 0f
        scrollView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    isTouchDown = true
                }
                MotionEvent.ACTION_MOVE -> {
//                    prohibitScrollLeft()
                }
                MotionEvent.ACTION_UP -> {
                    isScrollToNext(startX, event.x)

                }
            }

            false
        }

        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            Log.e("scrolll", "$scrollX -- $scrollY --- $oldScrollX ---$oldScrollY")
            prohibitScrollLeft()
        }
    }

    private fun prohibitScrollLeft() {
        if (scrollView.scrollX < headPosition) {
            scrollView.smoothScrollTo(headPosition.toInt(), 0)
            true
        }
    }

    private fun isScrollToNext(startX: Float, x: Float): Boolean {

        val surplusLength = headPosition + datas[datasCurrentIndex].gaps * gap - scrollView.scrollX
        if ((startX - x) > surplusLength / 2) {
            post {
                headPosition += datas[datasCurrentIndex].gaps * gap
                datasCurrentIndex++
                scrollView.smoothScrollTo(
                    headPosition.toInt(),
                    0
                )
            }
        } else {
            post {
                scrollView.smoothScrollTo(headPosition.toInt(), 0)
                isTouchDown = false
            }

        }
        return false
    }

//    private fun initNextData() {
//        datas.removeAt(0)
//        nextPlay()
//        isTouchDown = false
//        currentPlayFirstPositon = (datas[0].gaps * gap).toInt()
//        currentPlayPosition = 0
//    }

}