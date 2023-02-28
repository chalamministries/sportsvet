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
import kotlin.collections.ArrayList

class Trimmer(private val context: Context, private val callBack: ProcedureCallBack):
    FFmpegCallBack {

    private lateinit var output: File
    private var tagName = "trimmer"
    private var originList: ArrayList<Media> = arrayListOf()
    private var resultList: ArrayList<Media> = arrayListOf()
    private var i = 0

    fun trimMediaList(mediaList: ArrayList<Media>){
        originList = mediaList
        trimNext()
    }

    private fun trimNext(){
        if(i >= originList.size) {
            callBack.onTrimmed(resultList)
            callBack.onLoading(false)
            return
        }
        val media = originList[i]
        if(media.minTrim ==0 && media.maxTrim == media.duration){
            output = File(media.path!!)
            success()
            return
        }

        output = OptiUtils.createVideoFile(context)
        val query = cutVideoCommand(media.path!!,
            stringTime(media.minTrim!!/1000),
            stringTime(media.maxTrim!!/1000),
            output.path)

        CallBackOfQuery().callQuery(query, this)
        callBack.onLoading(true)
    }

    private fun cutVideoCommand(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> {
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(inputVideoPath)
        command.add("-ss")
        command.add(startTime!!)
        command.add("-t")
        command.add(endTime!!)
        command.add("-c")
        command.add("copy")
        command.add("-preset")
        command.add("ultrafast")
        command.add(output)
//ffmpeg -ss 00:00:00 -t 00:50:00 -i largefile.mp4 -acodec copy \
//-vcodec copy smallfile.mp4
        return command.toTypedArray()
    }
    private fun stringTime(sec: Int): String{
        val totalSecs = sec.toLong()
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
        resultList.add(media)
        i++
        trimNext()
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
            Toast.makeText(context, "$tagName: $failMessage", Toast.LENGTH_LONG).show()
            callBack.onLoading(false)
            callBack.onFailed(failMessage)
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

}
