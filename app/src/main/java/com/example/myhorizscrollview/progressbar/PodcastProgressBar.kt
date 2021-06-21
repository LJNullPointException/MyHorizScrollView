package com.example.myhorizscrollview.progressbar

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData


class PodcastProgressBar : RelativeLayout, UpdateProgressBarInterface {

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        intiData(attrs)
        initView()
    }

    private lateinit var scrollView: PpbScrollView
    lateinit var headCircular: PpbHeadCircularView
    var parentLayoutHeight: Int = 0
    private var _isNext: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val isNext: LiveData<Boolean>
        get() = _isNext


    fun update(list: List<PpbBean>) {
        scrollView.update(list)
    }


    private fun initView() {
        addScrollView()
        addLineView()
        addCircular()
    }

    private fun intiData(attrs: AttributeSet) {
        val attrsArray = intArrayOf(
            android.R.attr.layout_height // 3
        )
        val ta: TypedArray = context.obtainStyledAttributes(attrs, attrsArray)
        parentLayoutHeight = ta.getDimensionPixelSize(0, ViewGroup.LayoutParams.MATCH_PARENT)
        ta.recycle()
    }

    private fun addLineView() {
        var line = PpbHeadLineView(context)

        val lp = LayoutParams(
            Util.dip2px(context, 1f),
            LayoutParams.MATCH_PARENT
        )
        line.layoutParams = lp
        addView(line)
    }

    private fun addCircular() {
        headCircular = PpbHeadCircularView(context)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        headCircular.layoutParams = lp
//        addView(headCircular)
    }

    private fun addScrollView() {
        scrollView = PpbScrollView(context)
        scrollView.setParentLayoutHeight(parentLayoutHeight)
        val lp = LayoutParams(
            LayoutParams.MATCH_PARENT, parentLayoutHeight
        )
        scrollView.layoutParams = lp
        _isNext = scrollView.nextPlay()
        addView(scrollView)
    }

    override fun updateTime(time: Int) {
        scrollView.updateTime(time)
    }

    override fun updateNext() {
        scrollView.updateNext()
    }
}