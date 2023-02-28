package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.util.Log
import com.fini.pro.sportsvet.utils.video.model.Media
import java.io.File

class Done(private val context: Context, private val mediaList: ArrayList<Media>, private val callBack: DoneCallBack): ProcedureCallBack{

    private val TAG = "worker"
    private lateinit var trimmer: Trimmer
    private lateinit var sizer: Sizer
    private lateinit var merger: Merger
    private var count = 0
    private var mergedPath: String? = null

    interface DoneCallBack{
        fun loading(isLoading: Boolean)
        fun onCompleted(path: String)
    }

    fun start(){

        Log.d(TAG, "size: ${mediaList.size}")
        count = 0
        trimmer = Trimmer(context, this)
        sizer = Sizer(context, this)
        merger = Merger(context, this)

        if(mediaList.any { it.startPosition != 0 || it.endPosition != it.duration } || mediaList.size > 1){
            trim(mediaList)

        }else{
            mediaList[0].path?.let {
                callBack.onCompleted(it)
            }
        }

    }

    private fun trim(list: ArrayList<Media>){
        trimmer.trimMediaList(list)
    }

    private fun isOneSize(): Boolean{
        var theSame = true
        val firstWidth = mediaList[0].width
        val firstHeight = mediaList[0].height
        for(media in mediaList){
            if(media.width != firstWidth || media.height != firstHeight) theSame = false
        }
        return  theSame
    }
    private fun resize(list: ArrayList<Media>){
        if(list.size == 1 || isOneSize()){
        //if(list.size == 3){
            merge(list)
            return
        }
        sizer.resizeMediaList(list)
    }

    private fun merge(list: ArrayList<Media>){
        merger.combineVideosProcess(list)
    }

    override fun onTrimmed(list: ArrayList<Media>) {
        resize(list)
    }

    override fun onResized(list: ArrayList<Media>) {
        merge(list)
    }

    override fun onMerged(file: File) {
        mergedPath = file.path
        Log.d(TAG, file.path)
        callBack.onCompleted(mergedPath!!)
    }

    override fun onFailed(message: String) {
        Log.d(TAG, message)
        callBack.loading(false)
    }

    override fun onLoading(isLoading: Boolean) {
        if(isLoading) callBack.loading(isLoading)
    }


}
