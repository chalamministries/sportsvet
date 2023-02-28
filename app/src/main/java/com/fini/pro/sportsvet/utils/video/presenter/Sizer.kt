package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.videooperations.CallBackOfQuery
import com.fini.pro.sportsvet.utils.video.videooperations.FFmpegCallBack
import com.fini.pro.sportsvet.utils.video.videooperations.LogMessage
import com.fini.pro.sportsvet.utils.video.videooperations.Statistics
import java.io.File

class Sizer(private val context: Context, private val callBack: ProcedureCallBack): FFmpegCallBack {

    private lateinit var output: File
    private var tagName = "sizer"
    private var i = 0
    private var originList: ArrayList<Media> = arrayListOf()
    private var resultList: ArrayList<Media> = arrayListOf()
    var height = 0
    var width = 0

    fun resizeMediaList(mediaList: ArrayList<Media>){

        for(media in mediaList){
            if(media.height!! > height) height = media.height!!
            if(media.width!! > width) width = media.width!!
        }
        originList = mediaList
        //resultList.add(originList[0])
        resizeNext()
    }

    private fun resizeNext(){

        if(i >= originList.size) {
            callBack.onResized(resultList)
            return
        }
        val media = originList[i]
        output = OptiUtils.createVideoFile(context)
        val query = resizeQuery(media.path!!, output.path)
        CallBackOfQuery().callQuery(query, this)
        callBack.onLoading(true)
    }

    private fun resizeQuery(inputVideoPath: String, output: String): Array<String> {

        Log.d("process_count", "sizer")

        width = 720
        height = 1280
//        width = 920
//        height = 1920

        val scale = "" + width + "x" + height
        val scale1 = "" + width + ":" + height
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(inputVideoPath)
        command.add("-vf")
        //"[0:v]scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v0];[1:v] scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",

        //command.add("scale=$scale:force_original_aspect_ratio=decrease,pad=width=$width:height=$height:x=0:y=0:color=black")
        command.add("scale=iw*min($width/iw\\,$height/ih):ih*min($width/iw\\,$height/ih), pad=$scale1:($width-iw*min($width/iw\\,$height/ih))/2:($height-ih*min($width/iw\\,$height/ih))/2")
        command.add("-preset:v")
        command.add("ultrafast")
        command.add(output)

        return command.toTypedArray()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun statisticsProcess(statistics: Statistics) {
        super.statisticsProcess(statistics)

        Handler(Looper.getMainLooper()).post {
            callBack.onLoading(true)
        }

    }

    override fun success() {
        super.success()
        val media = originList[i]
        media.path = output.path
        media.width = originList[0].width
        media.height = originList[0].height
        resultList.add(media)
        i++
        resizeNext()
    }

    override fun cancel() {
        super.cancel()

        Handler(Looper.getMainLooper()).post {
            callBack.onLoading(false)
        }
    }


    var failMessage = ""
    override fun process(logMessage: LogMessage) {
        super.process(logMessage)
        failMessage = logMessage.text
        Log.d(tagName, logMessage.text)
    }

    override fun failed() {
        super.failed()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, tagName + ": " + failMessage, Toast.LENGTH_LONG).show()
            callBack.onFailed(failMessage)
            callBack.onLoading(false)
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

}
