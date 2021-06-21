package com.example.myhorizscrollview.progressbar

import android.animation.ObjectAnimator
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
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


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

    val playingAnimal = ValueAnimator.ofFloat(0f, 1000f)
    fun initAnimal(time: Int) {
        var t = time - 1000

        playingAnimal.duration = 1000
        playingAnimal.addUpdateListener {
            if (!isTouchDown) {
                currentPlayPosition =
                    headPosition + ((t + it.currentPlayTime) * datas[datasCurrentIndex].mFraction);
                scrollView.smoothScrollTo(currentPlayPosition.toInt(), 0)
                Log.e(
                    "aaa",
                    "$currentPlayPosition ===${headPosition + datas[datasCurrentIndex].gaps * gap}"
                )
            }

            if (currentPlayPosition >= headPosition + datas[datasCurrentIndex].gaps * gap) {
                //当前播放完毕，下一首
                Log.e("aaa", "xiayishou $headPosition")
                scroll2Next(
                    headPosition.toInt() + (datas[datasCurrentIndex].gaps * gap).toInt(), 0,
                    0
                )
            }
        }
        playingAnimal.start()
    }

    override fun updateNext() {
        scroll2Next(
            currentPlayPosition.toInt(), ((datas[datasCurrentIndex].gaps * gap)).toInt(), 0
        )

    }


    private fun scroll2Next(startX: Int, distanceX: Int, i: Int) {
        val scrollNextAnimal = ObjectAnimator.ofFloat(0f, 1000f)
        scrollNextAnimal.duration = 500
        scrollNextAnimal.interpolator = DecelerateInterpolator()
        scrollNextAnimal.addUpdateListener {
            scrollView.smoothScrollTo(
                (startX + it.animatedFraction * distanceX).toInt(),
                i
            );
        }
        scrollNextAnimal.addListener(onEnd = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                playingAnimal.pause()
            }
            headPosition += (datas[datasCurrentIndex].gaps * gap)
            _isNext.value = true
            datasCurrentIndex++
            currentPlayPosition = 0f
            scrollNextAnimal.cancel()
        })
        scrollNextAnimal.start()
    }

    private val scrollBackAnimal = ObjectAnimator.ofFloat(0f, 1000f)
    private fun scroll2Back(startX: Int, distanceX: Int, i: Int) {
        scrollBackAnimal.duration = 100
        scrollBackAnimal.interpolator = DecelerateInterpolator()
        scrollBackAnimal.addUpdateListener {
            scrollView.smoothScrollTo((startX - it.animatedFraction * distanceX).toInt(), i)
        }
        scrollBackAnimal.start()

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
                MotionEvent.ACTION_UP -> {
                    isScrollToNext(startX, event.x)

                }
            }
            false
        }

        scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            Log.e("scrollView", "$scrollX--$scrollY--$oldScrollX---$oldScrollY")
            prohibitScrollLeft()
        }
    }

    private fun prohibitScrollLeft() {
        if (scrollView.scrollX - 10 < headPosition) {
            scrollView.smoothScrollTo(headPosition.toInt(), 0)
            true
        }
    }

    private fun isScrollToNext(startX: Float, x: Float): Boolean {
        val surplusLength = headPosition + datas[datasCurrentIndex].gaps * gap - scrollView.scrollX
        if ((startX - x) > surplusLength / 2) {
            post {
                scroll2Next(
                    scrollView.scrollX,
                    (headPosition+datas[datasCurrentIndex].gaps * gap - scrollView.scrollX).toInt(),
                    0
                )
            }
        } else {
            post {
                scroll2Back(
                    scrollView.scrollX,
                    (scrollView.scrollX - currentPlayPosition).toInt(),
                    0
                )
            }

        }
        isTouchDown = false
        return false
    }


}