package com.fini.pro.sportsvet.utils.video.presenter

import com.fini.pro.sportsvet.utils.video.model.Media
import java.io.File

interface ProcedureCallBack {

    fun onTrimmed(list: ArrayList<Media>)

    fun onMerged(file: File)

    fun onResized(list: ArrayList<Media>)

    fun onFailed(message: String)

    fun onLoading(isLoading: Boolean)


}