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
import java.io.File
import kotlin.collections.ArrayList

class Splitter(private val context: Context, private val cuttingLabel: Int, private val callBack: SplitterCallBack):
    FFmpegCallBack {

    interface SplitterCallBack{
        fun onVideoSplitted(list: ArrayList<String>)
        fun loading(isLoading: Boolean)
    }

    private lateinit var outPath1: String
    private lateinit var outPath2: String
    private lateinit var output: File
    private var tagName = "splitter"
    private var originMedia: Media? = null

    private var resultList: ArrayList<String> = arrayListOf()

    fun split(media: Media){
        Log.d(tagName, "cut")
        originMedia = media
        cutFirstPart()
        callBack.loading(true)
    }

    private fun cutFirstPart(){
        Log.d(tagName, "cutFirstPart")
        output = OptiUtils.createVideoFile(context)
        outPath1 = OptiUtils.createVideoFile(context).path
        outPath2 = OptiUtils.createVideoFile(context).path
        resultList.add(outPath1)
        resultList.add(outPath2)
        val query = cutVideoCommand(originMedia!!.path!!,
            stringTime(0),
            stringTime(cuttingLabel/1000),
            output.path)

        CallBackOfQuery().callQuery(query, this)
    }

    private fun cutSecondPart(){
        Log.d(tagName, "cutSecondPart")
        output = OptiUtils.createVideoFile(context)
        val query = cutVideoCommand(originMedia!!.path!!,
            stringTime(cuttingLabel/1000),
            stringTime(originMedia!!.duration!!/1000),
            output.path)

        CallBackOfQuery().callQuery(query, this)
    }

    private fun cutVideoCommand1(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> {
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
        command.add(output)

        return command.toTypedArray()
    }
    private fun cutVideoCommand(inputVideoPath: String, startTime: String?, endTime: String?, output: String): Array<String> {
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(inputVideoPath)
        command.add("-t")
        command.add(stringTime(cuttingLabel/1000))
        command.add("-c")
        command.add("copy")
        command.add(outPath1)

        command.add("-ss")
        command.add(stringTime(cuttingLabel/1000))
        command.add("-c")
        command.add("copy")
        command.add("-preset")
        command.add("ultrafast")
        command.add(outPath2)

        //ffmpeg -i largefile.mp4 -t 00:50:00 -c copy smallfile1.mp4 \ -ss 00:50:00 -c copy smallfile2.mp4
        return command.toTypedArray()
    }

    private fun stringTime(sec: Int): String{
        val totalSecs = sec.toLong()
        val hours = totalSecs / 3600;
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()
        callBack.onVideoSplitted(resultList)

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
            callBack.loading(false)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

}
