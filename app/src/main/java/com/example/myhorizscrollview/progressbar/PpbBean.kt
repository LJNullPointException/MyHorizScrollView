package com.example.myhorizscrollview.progressbar

import android.graphics.Bitmap

data class PpbBean(
    var time: Long,
    var bitmap: Bitmap,
    var gaps: Float = 0f,
    var startPosition: Long = 0,
    var endPosition: Long = 0,
    var mFraction: Float = 0f
)

