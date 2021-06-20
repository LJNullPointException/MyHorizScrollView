package com.example.myhorizscrollview.progressbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface UpdateProgressBarInterface {
    fun updateTime(time: Int)
    fun updateNext()

    interface ProgressBarListener {
        fun nextPlay(): MutableLiveData<Boolean>
    }
}