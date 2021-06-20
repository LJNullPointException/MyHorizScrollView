package com.example.myhorizscrollview.progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class PpbHeadLineView : View {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    lateinit var mPaint: Paint
    var lineHeight: Int = 0
    private fun initView() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.parseColor("#ff0000")
        mPaint.strokeWidth = Util.dip2px(context, 1f).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(measuredHeight)
        lineHeight = size / 2
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.drawLine(
            lineHeight / 2f,
            (measuredHeight / 2f - lineHeight / 2f - Util.dip2px(context, 10f)),
            lineHeight / 2f,
            lineHeight.toFloat() + lineHeight / 2 + Util.dip2px(context, 10f),
            mPaint
        )
        canvas.restore()
    }


}