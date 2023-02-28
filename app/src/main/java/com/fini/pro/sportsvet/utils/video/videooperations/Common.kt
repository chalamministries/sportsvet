package com.fini.pro.sportsvet.utils.video.videooperations

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

object Common {
    var bitmap: Bitmap? = null
    const val PERM = 111
    const val VIDEO_FILE_REQUEST_CODE = 112
    const val IMAGE_FILE_REQUEST_CODE = 113
    const val AUDIO_FILE_REQUEST_CODE = 114
    const val TIME_FORMAT = "HH:mm:ss"
    const val OUT_PUT_DIR: String = "Output"
    private val format: DecimalFormat = DecimalFormat("#.##")
    private const val MB = (1024 * 1024).toLong()
    private const val KB: Long = 1024

    //Output Files
    const val IMAGE: String = "IMAGE"
    const val VIDEO: String = "VIDEO"
    const val GIF: String = "GIF"
    const val MP3: String = "MP3"

   // Standard Ratio
    const val RATIO_1: String = "16:9"
    const val RATIO_2: String = "4:3"
    const val RATIO_3: String = "16:10"
    const val RATIO_4: String = "5:4"
    const val RATIO_5: String = "2:21:1"
    const val RATIO_6: String = "2:35:1"
    const val RATIO_7: String = "2:39:1"

    // Standard  Bitrate
    const val BITRATE_96: String = "96" //kbps
    const val BITRATE_112: String = "112" //kbps
    const val BITRATE_128: String = "128" //kbps
    const val BITRATE_160: String = "160" //kbps
    const val BITRATE_192: String = "192" //kbps
    const val BITRATE_256: String = "256" //kbps
    const val BITRATE_320: String = "320" //kbps

    const val DURATION_LONGEST: String = "longest"
    const val DURATION_SHORTEST: String = "shortest"
    const val DURATION_FIRST: String = "first"

    fun stringForTime(timeMs: Long?): String {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs?.div(1000)
        val seconds = (totalSeconds?.rem(60))?.toInt()
        val minutes = ((totalSeconds?.div(60))?.rem(60))?.toInt()
        val hours = (totalSeconds?.div(3600))?.toInt()
        mFormatBuilder.setLength(0)
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    fun getFrameRate(fileString: String) {
        val extractor = MediaExtractor()
        val file = File(fileString)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val fd = fis.fd
            extractor.setDataSource(fd)
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        FFmpegQueryExtension.FRAME_RATE = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            extractor.release()
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getBitRate(context: Context, file: File) {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(context, Uri.parse(file.path))
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() / 1000

            Log.d("information", "bitrate: $bitrate")
//            Log.d("information", "size: ${getFileSize(file)}")
//            Log.d("information", "duration: $duration s")

            val sizeKBit = (file.length()/ 1024).toString().toInt() * 8
            //Log.d("information", "size: $sizeKBit")
            val bitrate2 = sizeKBit / duration
            Log.d("information", "bitrate: $bitrate2")

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFileSize(file: File): String {
        require(file.isFile) { "Expected a file" }
        val length = file.length().toDouble()
        if (length > MB) {
            return format.format(length / MB).toString() + " MB"
        }
        return if (length > KB) {
            format.format(length / KB).toString() + " KB"
        } else {
            format.format(length).toString() + " GB"
        }
    }

    fun getFilePath(context: Context, fileExtension: String) : String {
        val dir = File(context.getExternalFilesDir(Common.OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var extension:String? = null
        when {
            TextUtils.equals(fileExtension, IMAGE) -> {
                extension = "%03d.jpg"
            }
            TextUtils.equals(fileExtension, VIDEO) -> {
                extension = ".mp4"
            }
            TextUtils.equals(fileExtension, GIF) -> {
                extension = ".gif"
            }
            TextUtils.equals(fileExtension, MP3) -> {
                extension = ".mp3"
            }
        }
        val dest = File(dir.path + File.separator + Common.OUT_PUT_DIR + "shit_trim" + extension)
        return dest.absolutePath
    }

    fun getFilePathMerge(context: Context, fileExtension: String) : String {
        val dir = File(context.getExternalFilesDir(Common.OUT_PUT_DIR).toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        var extension:String? = null
        when {
            TextUtils.equals(fileExtension, IMAGE) -> {
                extension = "%03d.jpg"
            }
            TextUtils.equals(fileExtension, VIDEO) -> {
                extension = ".mp4"
            }
            TextUtils.equals(fileExtension, GIF) -> {
                extension = ".gif"
            }
            TextUtils.equals(fileExtension, MP3) -> {
                extension = ".mp3"
            }
        }
        val dest = File(dir.path + File.separator + Common.OUT_PUT_DIR + "shit_merge" + extension)
        return dest.absolutePath
    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName).also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }
}