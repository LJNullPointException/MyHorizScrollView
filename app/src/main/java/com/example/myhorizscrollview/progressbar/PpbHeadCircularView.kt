package com.example.myhorizscrollview.progressbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.myhorizscrollview.R

class PpbHeadCircularView : View {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private lateinit var mPaint: Paint
    private var radius: Int = 0
    private lateinit var mBitmap: Bitmap
    private fun initView() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        //TODO 更新头像，记忆缩放有问题
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.sg1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        radius = size / 4

//        mBitmap = Util.scaleBitmap(mBitmap, radius.toFloat())
        setMeasuredDimension(size, size)
    }

    fun setHeadBitmap(bitmap: Bitmap) {
        mBitmap = bitmap

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(radius.toFloat(), measuredHeight / 2f - radius)
        Util.drawCircular(mBitmap, mPaint, radius.toFloat(), canvas, 0f)
        canvas.restore()
    }


}