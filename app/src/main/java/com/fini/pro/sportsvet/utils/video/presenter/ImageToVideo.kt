package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.fini.pro.sportsvet.utils.video.videooperations.*
import kotlinx.coroutines.*
import java.io.*
import kotlin.collections.ArrayList


class ImageToVideo(private val context: Context, private val callBack: CallBack): FFmpegCallBack {

    interface CallBack{
        fun onCreatingVideo(path: String)
        fun loading(isLoading: Boolean)
    }

    var rotate = false
    private var imageWidth = 0
    private var imageHeight = 0
    private lateinit var output: File
    private var tagName = "ImageToVideo"

    fun createVideoByImages(path: String, second: Int, width: Int, height: Int){
        imageWidth = width
        imageHeight = height
        callBack.loading(true)

        val storageDirPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path
        val tempFolder = File(storageDirPath)
        if (!tempFolder.exists()) tempFolder.mkdirs()

        val filePath = tempFolder.path + File.separator + "img%03d.jpg"

        MainScope().launch(Dispatchers.IO) {
            OptiUtils.clearCachedImages(context)
            delay(100)

            for(i in 1..second + 1){
                val fileName = "img0" + String.format("%02d", i)
                val newPath = tempFolder.path + File.separator + fileName + ".jpg"
                OptiUtils.copyFile(File(path), File(newPath))
                Log.d("saveImagesSequence", newPath)
                delay(20)
            }

            withContext(Dispatchers.Main){
                output = OptiUtils.createVideoFile(context)
                val query = imageToVideo(filePath, output.path, second, width, height)
                CallBackOfQuery().callQuery(
                    query,
                    this@ImageToVideo
                )
            }
        }
    }

    private fun imageToVideo1(input: String, output: String, second: Int, width: Int?, height: Int?): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        val list = generateList(input, second)

        return arrayOf(
            "-y",
            "-f",
            "concat",
            "-safe",
            "0",
            "-i",
            list!!,
            "-t",
            "0.1",
            "-f",
            "lavfi",
            "-i",
            "anullsrc=channel_layout=stereo:sample_rate=48000",
            "-c",
            "copy",
            "-map",
            "0:v:0",
            "-map",
            "1:a:0",
            "-preset",
            "ultrafast",
            "-vsync",
            "vfr",
            "-vf",
            "scale=$width:$height:force_original_aspect_ratio=decrease,format=yuv420p",
            output
        )


    }

    private fun imageToVideo2(input: String, output: String, second: Int, width: Int?, height: Int?): Array<String> {
        val inputs: ArrayList<String> = ArrayList()

        inputs.apply {
            add("-y")
            add("-loop")
            add("1")
            add("-i")
            add(input)
            add("-c:v")
            add("libx264")
            add("-t")
            add("$second")
            add("-vf")
            add("fps=25")
            add("-pix_fmt")
            add("yuv420p")
            add(output)

            //" -r 1/1 -i " + inputImgPath + " -c:v libx264 -crf 23 -pix_fmt yuv420p -s 640x480 " + outputVideoPath;
        }
        //ffmpeg -framerate 1/5 -i img%03d.png -c:v libx264 -vf fps=25 -pix_fmt yuv420p out.mp4
        //-framerate 24 -i img%03d.png output.mp4
        //-framerate 1/5
        //ffmpeg -loop 1 -i img.jpg -c:v libx264 -t 30 -pix_fmt yuv420p out.mp4

        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    private fun imageToVideo(input: String, output: String, second: Int, width: Int?, height: Int?): Array<String> {
        val inputs: ArrayList<String> = ArrayList()

        inputs.apply {
            add("-y")
            add("-loop")
            add("1")
            add("-i")
            add(input)
            add("-c:v")
            add("libx264")
            add("-t")
            add("$second")
            add("-pix_fmt")
            add("yuv420p")
            add("-vf")
            add("scale=$width:$height")
            add("-preset")
            add("ultrafast")
            add(output)

        }
        //ffmpeg -loop 1 -i image.png -c:v libx264 -t 15 -pix_fmt yuv420p -vf scale=320:240 out.mp4
        //ffmpeg -framerate 1/5 -i img%03d.png -c:v libx264 -vf fps=25 -pix_fmt yuv420p out.mp4
        //-framerate 24 -i img%03d.png output.mp4
        //-framerate 1/5
        //ffmpeg -loop 1 -i img.jpg -c:v libx264 -t 30 -pix_fmt yuv420p out.mp4

        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

    private fun generateList(path: String, second: Int): String? {
        val list: File
        var writer: Writer? = null
        try {
            list = File.createTempFile("ffmpeg-list", ".txt")
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))
            for (i in 0..second) {
                writer.write("file '${path}'\n")
                writer.write("duration 1\n")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return "/"
        } finally {
            try {
                if (writer != null) writer.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return list.absolutePath
    }

    private fun rotation(input: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()

        inputs.apply {
            add("-y")
            add("-i")
            add(input)
            add("-vf")
            add("transpose=2")
            add(output)
        }
        return inputs.toArray(arrayOfNulls<String>(inputs.size))
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()
        Log.d(tagName, output.path)


        if(rotate){
            val input = output.path
            output = OptiUtils.createVideoFile(context)
            val query = rotation(input, output.path)

            CallBackOfQuery().callQuery(
                query,
                object : FFmpegCallBack {

                    override fun statisticsProcess(statistics: Statistics) {
                        super.statisticsProcess(statistics)
                    }

                    override fun success() {
                        super.success()
                        callBack.loading(false)
                        callBack.onCreatingVideo(output.path)
                    }

                    override fun process(logMessage: LogMessage) {
                        super.process(logMessage)
                        failMessage = logMessage.text
                        Log.d(tagName, logMessage.text)
                    }

                    override fun failed() {
                        super.failed()
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, tagName + ": " + failMessage, Toast.LENGTH_LONG)
                                .show()
                            callBack.loading(false)
                        }
                    }
                })

        }else{
            callBack.loading(false)
            callBack.onCreatingVideo(output.path)
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
            callBack.loading(false)
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun mergeAudioVideoCommand(inputVideo: String, inputAudio: String, output: String): Array<String> {
        val inputs: ArrayList<String> = ArrayList()
        inputs.apply {
            add("-y")
            add("-i")
            add(inputVideo)
            add("-t")
            add("0.1")
            add("-f")
            add("lavfi")
            add("-i")
            add("anullsrc=channel_layout=stereo:sample_rate=48000")
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
}