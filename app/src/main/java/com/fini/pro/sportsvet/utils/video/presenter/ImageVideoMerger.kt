package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.fini.pro.sportsvet.utils.video.videooperations.*
import java.io.File
import kotlin.collections.ArrayList

class ImageVideoMerger(private val context: Context, private val callBack: CallBack):
    FFmpegCallBack {

    interface CallBack{
        fun onCreatingVideo(path: String)
        fun loading(isLoading: Boolean)
    }

    private lateinit var output: File
    private var tagName = "ImageToVideo"

    fun mergeImageWithVideo(videoPath: String, imagePath: String){
        output = OptiUtils.createVideoFile(context)
        val query = imageToVideo(videoPath, imagePath, output.path)

        CallBackOfQuery().callQuery(query, this)
        callBack.loading(true)
    }


    private fun imageToVideo(inputVideo: String, inputImage: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {

            add("-y")
            add("-loop")
            add("1")
            add("-framerate")
            add("24")
            add("-t")
            add("10")
            add("-i")
            add(inputImage)
            add("-i")
            add(inputVideo)
            add("-filter_complex")
            add("[0]scale=432:432,setsar=1[im];[1]scale=432:432,setsar=1[vid];[im][vid]concat=n=2:v=1:a=0")
            add("-preset")
            add("ultrafast")
            add(output)

            //ffmpeg -loop 1 -framerate 24 -t 10 -i image.jpg -i video.mp4
        // -filter_complex "[0]scale=432:432,setsar=1[im];[1]scale=432:432,setsar=1[vid];[im][vid]concat=n=2:v=1:a=0" out.mp4
            //ffmpeg -framerate 1/300 -i img%3d.jpg -c:v mpeg2video -b:v 2000k -r 5 video.mpg

//            [0:v][2:v]scale2ref[logo][video]; \
//            [logo]setsar=sar=1[logo];  \
//            [logo][1:a][video][2:a]concat=n=2:v=1:a=1
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()
        Log.d(tagName, output.path)
        callBack.onCreatingVideo(output.path)
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
