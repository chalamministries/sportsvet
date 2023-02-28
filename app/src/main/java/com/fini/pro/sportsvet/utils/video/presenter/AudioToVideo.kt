package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.videooperations.*
import java.io.File
import kotlin.collections.ArrayList

class AudioToVideo(private val context: Context, val media: Media, private val callBack: CallBack):
    FFmpegCallBack {

    interface CallBack{
        fun onCreatingVideo(path: String)
        fun loading(isLoading: Boolean)
    }

    var inputVideoPath = ""
    private lateinit var outputAudio: File
    private lateinit var outputVideo: File
    private var tagName = "AudioToVideo"

    fun mergeAudioVideo(inputVideo: String, inputAudio: String){
        inputVideoPath = inputVideo
        val audioFormat = OptiUtils.audioFormat(inputAudio)
        if(audioFormat == null) {
            Toast.makeText(context, "Unsupported audio format", Toast.LENGTH_SHORT).show()
            return
        }
        outputAudio = OptiUtils.createFile(context, audioFormat)
        val query = cutAudioCommand(inputAudio, stringTime(0), stringTime(media.duration!! / 1000), outputAudio.path)
        CallBackOfQuery().callQuery(query, this)
        callBack.loading(true)
    }

    private fun mergeAudioVideoCommand(inputVideo: String, inputAudio: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-i")
            add(inputAudio)
            add("-c")
            add("copy")
            add("-map")
            add("0:v:0")
            add("-map")
            add("1:a:0")
            add(output)

        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))

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
        command.add(output)

        return command.toTypedArray()
    }

    private fun cutAudioCommand(inputPath: String, startTime: String?, endTime: String?, output: String): Array<String> {

        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(inputPath)
        command.add("-ss")
        command.add(startTime!!)
        command.add("-to")
        command.add(endTime!!)
        command.add("-c")
        command.add("copy")
        command.add("-preset")
        command.add("ultrafast")
        command.add(output)

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
        Log.d(tagName, outputAudio.path)
        outputVideo = OptiUtils.createVideoFile(context)
        val query = mergeAudioVideoCommand(inputVideoPath, outputAudio.path, outputVideo.path)
        CallBackOfQuery().callQuery(query, object : FFmpegCallBack{
            override fun success() {
                super.success()
                callBack.onCreatingVideo(outputVideo.path)
                callBack.loading(false)
            }

            override fun failed() {
                super.failed()
                callBack.loading(false)
            }
        })

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
