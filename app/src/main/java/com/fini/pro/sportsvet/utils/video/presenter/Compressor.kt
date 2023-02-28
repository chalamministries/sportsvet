package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.widget.Toast
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.fini.pro.sportsvet.utils.video.videooperations.*
import org.apache.commons.lang3.StringUtils
import java.io.File

class Compressor(private val context: Context, private val callBack: CallBack): FFmpegCallBack, CompressionListener {

    interface CallBack{
        fun onCompressComplete(path: String)
        fun loading(isLoading: Boolean)
        fun onProgress(message: String)
    }

    private var durationSec: Long = 0
    private var width = 0
    private var height = 0
    private lateinit var output: File
    private var originalPath: String = ""
    private var tagName = "Compressor"

    fun compress(path: String){
        //35.582 sec for 1.2 min
        originalPath = path
        Log.d("countercc", "start")
        output = OptiUtils.createVideoFile(context)
        Common.getFrameRate(path)
        val ratio = 5
        val retriever = MediaMetadataRetriever()
        var targetBitrate: Long = 100
        try {
            retriever.setDataSource(path)
            val durationSec = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() / 1000
            val originBitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)!!.toLong()
            val originalSizeBit = File(path).length()

            val targetSizeBit = (originalSizeBit) / ratio

            val targetSizeKB = (targetSizeBit/ 1024) * 8

            targetBitrate = targetSizeKB / durationSec
            Log.d("information", "bitrate :1: $targetBitrate")
            Log.d("information", "bitrate :2: $originBitrate")
            Log.d("information", "bitrate :2: ${FFmpegQueryExtension.FRAME_RATE}")

        } catch (e: Exception) {
            Log.d("information", "error: $targetBitrate")
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        VideoCompressor.start(
            context = context, // => This is required if srcUri is provided. If not, it can be ignored or null.
            srcPath = path, // => This could be ignored or null if srcUri and context are provided.
            destPath = output.path,
            listener = this,

            configureWith = Configuration(
                quality = VideoQuality.LOW,
                //frameRate = FFmpegQueryExtension.FRAME_RATE - 5,
                isMinBitrateCheckEnabled = true,
                //videoBitrate = targetBitrate.toInt() /*Int, ignore, or null*/
            )
        )
    }

    override fun success() {
        super.success()
        Log.d("countercc", "complete")
        callBack.loading(false)
        callBack.onCompressComplete(output.path)
    }

    private var message = ""
    override fun process(logMessage: LogMessage) {
        super.process(logMessage)
        var timeProceed = StringUtils.substringBetween(logMessage.text, "time=", "bitrate=")
        if(timeProceed == null) timeProceed = "00:00:00:00"

        val units = timeProceed.replace(" ", "").split(":").toTypedArray() //will break the string up into an array
        val seconds = units[2].toFloat().toInt()
        val minutes = units[1].toInt()
        val hours = units[0].toInt()

        val proceedSec = 60 * 60 * hours +  60 * minutes + seconds //add up our values

        callBack.onProgress("" + (proceedSec * 100 / durationSec) + "%")
        Log.d(tagName, timeProceed)
    }

    override fun failed() {
        super.failed()
        Toast.makeText(context, tagName + ": " + message, Toast.LENGTH_LONG).show()
        callBack.loading(false)
    }

    override fun onProgress(percent: Float) {
        val process = "${percent.toLong()}%"
        callBack.onProgress(process)
    }

    override fun onStart() {
        // Compression start
        callBack.loading(true)
    }

    override fun onSuccess() {
        Log.d("countercc", "complete")
        // On Compression success
        callBack.loading(false)
        callBack.onCompressComplete(output.path)
    }

    override fun onFailure(failureMessage: String) {
        // On Failure
        //Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show()
        callBack.loading(false)
        callBack.onCompressComplete(originalPath)
    }

    override fun onCancelled() {
        // On Cancelled
    }
    // FFMPEG 3695 KB, 6.31 sec, 2900492 bitrate , 9.5 sec, 45sec >> 13.767sec
    // MEDIUM 5637 KB, 5.6 sec,
    // LOW 3850 KB, 5.6 sec, 23 sec, 45sec >> 24.323sec

}
