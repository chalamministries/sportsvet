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

class Speeder(private val context: Context, private val callBack: SpeederCallBack): FFmpegCallBack {

    interface SpeederCallBack{
        fun onVideoSpeed(list: ArrayList<String>)
        fun loading(isLoading: Boolean)
    }

    private lateinit var output: File
    private var tagName = "speeder"

    private var resultList: ArrayList<String> = arrayListOf()

    fun speed(media: Media, playbackSpeed: String, tempo: String){
        Log.d(tagName, "cutFirstPart")
        output = OptiUtils.createVideoFile(context)
        val query = speedCommand(media,
            playbackSpeed,
            tempo,
            output.path)

        CallBackOfQuery().callQuery(query, this)
        callBack.loading(true)
    }


    private fun speedCommand(media: Media, playbackSpeed: String, tempo: String, output: String): Array<String> {
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(media.path!!)
        if(media.hasAudio!!){
            command.add("-filter_complex")
            command.add("[0:v]setpts=$playbackSpeed*PTS[v];[0:a]atempo=$tempo[a]")
            command.add("-map")
            command.add("[v]")
            command.add("-map")
            command.add("[a]")

        }else{
            command.add("-filter:v")
            command.add("setpts=$playbackSpeed*PTS")
        }
        command.add("-preset:v")
        command.add("ultrafast")
        command.add(output)

        return command.toTypedArray()
    }

    private fun speedCommandNoAudio(media: Media, playbackSpeed: String, tempo: String, output: String): Array<String> {
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(media.path!!)

        command.add("-filter:v")
        command.add("setpts=$playbackSpeed*PTS")

        command.add("-preset:v")
        command.add("ultrafast")
        command.add(output)

        return command.toTypedArray()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()

        resultList.add(output.path)
        callBack.onVideoSpeed(resultList)
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
