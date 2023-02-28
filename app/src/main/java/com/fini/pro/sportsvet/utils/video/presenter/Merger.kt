package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.videooperations.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Merger(private val context: Context, private val callBack: ProcedureCallBack):
    FFmpegCallBack {

    private lateinit var output: File
    companion object{
        var width: Int? = 0
        var height: Int? = 0
    }

    private var tagName = "merger"

    fun combineVideosProcess(mediaList: ArrayList<Media>) {

        Log.d(tagName, mediaList.size.toString())
        if(mediaList.size == 1){
            callBack.onMerged(File(mediaList[0].path!!))
            return
        }

        output = OptiUtils.createVideoFile(context)

        Log.d("video_dimensions", "width:" + width + ", height:" + height)
        val query = combineVideos_1(mediaList, output.path)
        callBack.onLoading(true)
        CallBackOfQuery().callQuery(query, this)

    }

    private fun combineVideos(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val str8 = "480x640"
        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
//                "-i",
//                paths[2].filePath,
            "-filter_complex",
//            "[0:v]scale=640:480:force_original_aspect_ratio=decrease,pad=640:480:(ow-iw)/2:(oh-ih)/2[v0];[v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
            "[0:v]scale=$str8:force_original_aspect_ratio=decrease,setsar=1[v0];[1:v]scale=$str8:force_original_aspect_ratio=decrease,setsar=1[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1",
            "-ab",
            "48000",
            "-ac",
            "2",
            "-ar",
            "22050",
            "-s",
            "480x640",
            "-vcodec",
            "libx264",
            "-crf",
            "27",
            "-preset",
            "ultrafast",
            output
        )
    }

    private fun combineVideos_7(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
//        ffmpeg -i 480.mp4 -i 640.mp4 -filter_complex \
//        "[0:v]scale=640:480:force_original_aspect_ratio=decrease,pad=640:480:(ow-iw)/2:(oh-ih)/2[v0]; \
//        [v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]" \
//        -map "[v]" -map "[a]" -c:v libx264 -c:a aac -movflags +faststart output.mp4
        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
            "-filter_complex",
            "[0:v]scale=480:640:force_original_aspect_ratio=decrease,pad=640:640:(ow-iw)/2:(oh-ih)/2[v0];" +
                    "[v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",


//            "[0:v]scale=$str:force_original_aspect_ratio=decrease,pad=$str:(ow-iw)/2:(oh-ih)/2,setsar=1[v0];"+
//                    "[1:v]scale=$str:force_original_aspect_ratio=decrease,pad=$str:(ow-iw)/2:(oh-ih)/2,setsar=1[v1];[v0][v1]"+
//                    "concat=n=2:v=1:a=0" ,
            //"concat=n=2:v=1:a=1[v][a]" ,
            "-map",
            "[v]",
            "-map",
            "[a]",
            "-c:v",
            "libx264",
            "-c:a",
            "aac",
            "-movflags",
            "+faststart",
            output
        )
    }

    private fun combineVideos_6(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val str = "640:360"
        val str3 = "640:352"
        val str2 = "2800:1800"
        val str1 = "576:626"
        val str4 = "576:1024"
        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
            "-filter_complex",
            "[0:v]scale=$str:force_original_aspect_ratio=decrease,pad=$str:(ow-iw)/2:(oh-ih)/2[v0];" +
                    "[v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",


//            "[0:v]scale=$str:force_original_aspect_ratio=decrease,pad=$str:(ow-iw)/2:(oh-ih)/2,setsar=1[v0];"+
//                    "[1:v]scale=$str:force_original_aspect_ratio=decrease,pad=$str:(ow-iw)/2:(oh-ih)/2,setsar=1[v1];[v0][v1]"+
//                    "concat=n=2:v=1:a=0" ,
            //"concat=n=2:v=1:a=1[v][a]" ,
            "-map",
            "[v]",
            "-map",
            "[a]",
            "-c:v",
            "libx264",
            "-c:a",
            "aac",
            "-movflags",
            "ultrafast",
            output

//            "-ab",
//            "48000",
//            "-ac",
//            "2",
//            "-ar",
//            "22050",
//            "-s",
//            "480x640",
//            "-vcodec",
//            "libx264",
//            "-crf",
//            "27",
//            "-preset",
//            "ultrafast",
//            output
        )
    }

    private fun combineVideos_3(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val str0 = "640x360"
        val str1 = "1280x720"
        val str2 = "2800x1800"
        val str3 = "426x240"
        val str4 = "576x1024"
        val str5 = "854x480"
        val str6 = "1920x1080"
        val str7 = "2560x1440"
        val str8 = "480x640"
        val str9 = "640x480"


        //val  str = "" + width + "x" + height
        val  str = str8
        val h = height!! * 100
        val command: ArrayList<String> = arrayListOf()
        command.add("-y")
        command.add("-i")
        command.add(paths[0].filePath)
        command.add("-i")
        command.add(paths[1].filePath)
        command.add("-filter_complex")
        //command.add("[0:v]scale=640:480:force_original_aspect_ratio=decrease[v0],setsar=1:2[v0];[1:v]scale=640:480:force_original_aspect_ratio=decrease[v1],setsar=1:2[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1")
        command.add("[0:v]scale=$str,setsar=1[v0];[1:v]scale=$str,setsar=1[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1")
        command.add("-ab")
        command.add("48000")
        command.add("-ac")
        command.add("2")
        command.add("-ar")
        command.add("22050")
        command.add("-s")
        command.add(str)
        command.add("-vcodec")
        command.add("libx264")
        command.add("-crf")
        command.add("27")
        command.add("-preset")
        command.add("ultrafast")
        command.add(output)

        return command.toTypedArray()
        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
//                "-i",
//                paths[2].filePath,
            "-filter_complex",
//            "[0:v]scale=640:480:force_original_aspect_ratio=decrease,pad=640:480:(ow-iw)/2:(oh-ih)/2[v0];[v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
            "[0:v]scale=$str9,setsar=1[v0];[1:v]scale=$str9,setsar=1[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1",
            "-ab",
            "48000",
            "-ac",
            "2",
            "-ar",
            "22050",
            "-s",
            "480x640",
            "-vcodec",
            "libx264",
            "-crf",
            "27",
            "-preset",
            "ultrafast",
            output
        )


    }

    private fun combineVideos_4(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val str = "640x360"
        val str1 = "1280x720"
        val str2 = "2800x1800"
        val str3 = "426x240"
        val str4 = "576x1024"
        val str5 = "854x480"
        val str6 = "1920x1080"
        val str7 = "2560x1440"
        val str8 = "480x640"

        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
//                "-i",
//                paths[2].filePath,
            "-filter_complex",
            "[0:v]scale=$str8:force_original_aspect_ratio=decrease,pad=$str8:(ow-iw)/2:(oh-ih)/2[v0];[v0][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
//            "[0:v]scale=$str8,setsar=1[v0];[1:v]scale=$str8,setsar=1[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1",
            "-ab",
            "48000",
            "-ac",
            "2",
            "-ar",
            "22050",
            "-s",
            "480x640",
            "-vcodec",
            "libx264",
            "-crf",
            "27",
            "-preset",
            "ultrafast",
            output
        )
    }

    private fun combineVideos_5(paths: ArrayList<Paths>, width: Int?, height: Int?, output: String): Array<String> {
        val str = "640x360"
        val str1 = "1280x720"
        val str2 = "2800x1800"
        val str3 = "426x240"
        val str4 = "576x1024"
        val str5 = "854x480"
        val str6 = "1920x1080"
        val str7 = "2560x1440"
        val str8 = "480x640"

        //"ffmpeg -i video1 -i video2 -f lavfi -i color=s=854x480:r=30 -filter_complex "[0]scale=854x480:force_original_aspect_ratio=decrease[vid1];[1]scale=854x480:force_original_aspect_ratio=decrease[vid2];[2][vid1]overlay=x='(W-w)/2':y='(H-h)/2':shortest=1[vid1];[2][vid2]overlay=x='(W-w)/2':y='(H-h)/2':shortest=1[vid2];[vid1][vid2]concat=n=2:v=1:a=0,setsar=1" out.mp4"
        return arrayOf(
            "-y",
            "-i",
            paths[0].filePath,
            "-i",
            paths[1].filePath,
            "-f",
            "lavfi",
            "color=s=854x480:r=30",
            "-filter_complex",
            "[0:v]scale=854x480:force_original_aspect_ratio=decrease[v0];" +
                    "[1:v]scale=854x480:force_original_aspect_ratio=decrease[v1]" +
                    "[0][v0]overlay=x='(W-w)/2':y='(H-h)/2':shortest=1[v0]" +
                    "[1][v1]overlay=x='(W-w)/2':y='(H-h)/2':shortest=1[v1]" +
                    "[v0][v1]concat=n=2:v=1:a=0,setsar=1",
//            "[0:v]scale=$str8,setsar=1[v0];[1:v]scale=$str8,setsar=1[v1];[v0][0:a][v1][1:a]concat=n=2:v=1:a=1",
            "-ab",
            "48000",
            "-ac",
            "2",
            "-ar",
            "22050",
            "-s",
            "480x640",
            "-vcodec",
            "libx264",
            "-crf",
            "27",
            "-preset",
            "ultrafast",
            output
        )
    }

    private fun combineVideos_0(medias: ArrayList<Media>, output: String): Array<String> {
        Log.d("process_count", "merge")
        val list = generateList(medias)
        return arrayOf(
            "-y",
            "-f",
            "concat",
            "-safe",
            "0",
            "-i",
            list!!,
            "-c",
            "copy",
            output
        )

        //ffmpeg -f concat -i inputs.txt -vcodec copy -acodec copy Mux1.mp4

        //ffmpeg -f concat -safe 0 -i mylist.txt -c copy output.mp4
    }

    private fun combineVideos_1(medias: ArrayList<Media>, output: String): Array<String> {
        Log.d("process_count", "merge")
        val list = generateList(medias)
        return arrayOf(
            "-y",
            "-f",
            "concat",
            "-safe",
            "0",
            "-i",
            list!!,
            "-vcodec",
            "copy",
            "-acodec",
            "copy",
            "-preset",
            "ultrafast",
            output
        )

        //ffmpeg -f concat -i inputs.txt -vcodec copy -acodec copy Mux1.mp4
        //ffmpeg -f concat -safe 0 -i mylist.txt -c copy output.mp4
    }

    private fun generateList(inputs: ArrayList<Media>): String? {
        val list: File
        var writer: Writer? = null
        try {
            list = File.createTempFile("ffmpeg-list", ".txt")
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))
            for (input in inputs) {
                writer.write("file '${input.path}'\n")
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
////////////////////////////////////////////////////////////////////////////////////////////////////


    override fun statisticsProcess(statistics: Statistics) {
        super.statisticsProcess(statistics)

        Handler(Looper.getMainLooper()).post {
            callBack.onLoading(true)
        }

    }

    override fun success() {
        super.success()
        callBack.onMerged(output)

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
            callBack.onLoading(false)
            callBack.onFailed(failMessage)
        }
    }

    private fun createSaveVideoFile(): File {
        val timeStamp: String =
            SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName: String = OptiConstant.APP_NAME + timeStamp + "_"

        val path = Environment.getExternalStorageDirectory()
            .toString() + File.separator + OptiConstant.APP_NAME + File.separator + OptiConstant.MY_VIDEOS + File.separator
        val folder = File(path)
        if (!folder.exists())
            folder.mkdirs()

        return File.createTempFile(imageFileName, OptiConstant.VIDEO_FORMAT, folder)
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

}