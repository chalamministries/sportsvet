package com.fini.pro.sportsvet.utils.video.videooperations

import androidx.annotation.MainThread

interface FFmpegCallBack {
    @MainThread
    fun process(logMessage: LogMessage){}

    @MainThread
    fun statisticsProcess(statistics: Statistics) {}

    @MainThread
    fun success(){}

    @MainThread
    fun cancel(){}

    @MainThread
    fun failed(){}
}