package com.example.myhorizscrollview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.nio.channels.FileLock

class DrawLine : View {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private lateinit var mPaint: Paint
    private var lineSpace: Float = 10f
    private  var color: Int=0
    private fun initView() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 5f
        mPaint.color = Color.BLACK
    }

    companion object {
        const val DEFAULT_HEIGHT = 100f
    }

    fun setCirColor(color: Int) {
        this.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        lineSpace = height / 2
        setMeasuredDimension((lineSpace * 10).toInt(), height.toInt())
    }

    var marginTopAndBottom: Float = 0f
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        for (i in 0..10) {
            marginTopAndBottom = if (i % 5 == 0) {
                0f
            } else {
                30f
            }
            canvas.drawLine(
                i * lineSpace,
                marginTopAndBottom,
                i * lineSpace,
                height.toFloat() - marginTopAndBottom,
                mPaint
            )
        }
        canvas.restore()
        mPaint.color = color
        canvas.save()
        canvas.drawCircle(lineSpace, height / 2f, lineSpace, mPaint)

        canvas.restore()
    }
}