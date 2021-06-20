package com.example.myhorizscrollview.progressbar

import android.content.Context
import android.graphics.*
import android.view.WindowManager
import kotlin.math.abs

object Util {
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.width
    }

    fun getScreenHeight(context: Context): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay.height
    }

    fun scaleBitmap(bitmap: Bitmap, target: Float): Bitmap {
        val scale = target / bitmap.width.coerceAtMost(bitmap.height).toFloat()
        val scaleWidth = scale * bitmap.width
        val scaleHeight = scale * bitmap.height
        var scaleBitmap =
            Bitmap.createScaledBitmap(bitmap, scaleWidth.toInt(), scaleHeight.toInt(), true)

        var x: Int
        var y: Int

        if (scaleHeight > scaleWidth) {
            x = 0
            y = abs(scaleHeight / 2 - target / 2f).toInt()
        } else {
            y = 0
            x = abs(scaleWidth / 2 - target / 2f).toInt()
        }
        val w = scaleHeight.coerceAtMost(scaleWidth).toInt()
        var result = Bitmap.createBitmap(scaleBitmap, x, y, w, w)
        scaleBitmap.recycle()

        return result
    }

    fun drawCircular(
        bitmap: Bitmap,
        mPaint: Paint,
        radius: Float,
        canvas: Canvas,
        drawCurrentPosition: Float
    ) {
        val shader = BitmapShader(
            bitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )
        mPaint.shader = shader
        canvas.drawCircle(
            0f,
            radius.toFloat(),
            radius.toFloat(),
            mPaint
        )
    }
}