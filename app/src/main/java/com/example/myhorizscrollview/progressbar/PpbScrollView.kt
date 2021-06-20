package com.example.myhorizscrollview.progressbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PpbScrollView :
    HorizontalScrollView, UpdateProgressBarInterface,
    UpdateProgressBarInterface.ProgressBarListener {
    constructor(context: Context?) : super(context) {
        initData()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initData()
    }


    private var layoutHeight: Int = 0
    private var scaleLineViewItem: ScrollViewItem? = null
    private var l: Int = 0
    private var t: Int = Util.dip2px(context, 15f)
    private var r: Int = 0
    private var b: Int = Util.dip2px(context, 15f)
    private var isNext: MutableLiveData<Boolean> = MutableLiveData()

    private fun initData() {

    }

    private fun initView() {
        isHorizontalScrollBarEnabled = false
        l = layoutHeight / 4
        setPadding(l, t, r, b)

        scaleLineViewItem = ScrollViewItem(context)
        scaleLineViewItem?.height
        val lp = LayoutParams(
            GridLayout.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        scaleLineViewItem?.layoutParams = lp
        scaleLineViewItem?.gravity = Gravity.CENTER_VERTICAL
        scaleLineViewItem?.setScrollView(this)
        scaleLineViewItem?.nextPlay()?.observeForever {
            isNext.value = it
        }
        addView(scaleLineViewItem)
    }

    fun update(list: List<PpbBean>) {
        scaleLineViewItem?.update(list)
    }

    fun setParentLayoutHeight(parentLayoutHeight: Int) {
        layoutHeight = parentLayoutHeight
        initView()
    }


    override fun updateTime(time: Int) {
        scaleLineViewItem?.updateTime(time)

    }

    override fun updateNext() {
        scaleLineViewItem?.updateNext()
    }

    override fun nextPlay(): MutableLiveData<Boolean> {
        return isNext
    }


}