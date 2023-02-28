package com.fini.pro.sportsvet.model

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.os.Parcelable
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Size
import com.fini.pro.sportsvet.utils.FirebaseApp
import com.fini.pro.sportsvet.utils.Utils
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.*

@Parcelize
class Video(
    val uri: Uri,
    val path: String,
    val added: Long, // Timestamp - Seconds
) : Parcelable {
    companion object {
        const val AddedDateFormat = "MMM dd, yyyy"
    }

    fun addedAt() : String {
        return DateFormat.format(AddedDateFormat, added * 1000).toString()
    }

    fun isToday() : Boolean {
        return addedAt() == DateFormat.format(AddedDateFormat, Date()).toString()
    }

    fun isYesterday() : Boolean {
        return addedAt() == DateFormat.format(AddedDateFormat, Date().time - 86400 * 1000).toString()
    }

    fun thumbnail(context: Context) : Bitmap? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        if (cursor == null || cursor.count == 0)
            return null

        cursor.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        var bitmap: Bitmap? = null
        if (picturePath != null) {
            try {
                bitmap = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                    ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MINI_KIND)
                else
                    ThumbnailUtils.createVideoThumbnail(
                        File(picturePath),
                        Size(
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp),
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
                        ),
                        CancellationSignal()
                    )
            }
            catch (e: Exception) {
                e.printStackTrace()
                FirebaseApp.writeException("video.thumbnail - 1", e.localizedMessage ?: "createVideoThumbnail failed")
            }
            try {
                if (bitmap == null && Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    bitmap = context.contentResolver.loadThumbnail(
                        uri,
                        Size(
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp),
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
                        ),
                        CancellationSignal()
                    )
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                FirebaseApp.writeException("video.thumbnail - 2", e.localizedMessage ?: "createVideoThumbnail failed")
            }
            try {
                if (bitmap == null) {
                    bitmap = Utils.createThumbnail(context as Activity, picturePath)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                FirebaseApp.writeException("video.thumbnail - 3", e.localizedMessage ?: "createVideoThumbnail failed")
            }
        }
        else {
            val videoPath = uri.path ?: ""
            FirebaseApp.writeException("video.thumbnail", "video uri: $videoPath, column index: $columnIndex")
        }
        return bitmap
    }

    fun duration(context: Context) : Long {
        try {
            var durationTime: Long
            MediaPlayer.create(context, uri).apply {
                durationTime = (duration / 1000).toLong()
                reset()
                release()
            }
            return durationTime
        }
        catch (e: Exception) {
            e.printStackTrace()
            FirebaseApp.writeException("video.duration", e.localizedMessage ?: "MediaPlayer create failed")
        }
        return 0
    }

    fun durationLabel(context: Context) : String {
        val totalSeconds = duration(context)
        return Utils.timeLabel(totalSeconds = totalSeconds)
    }
}