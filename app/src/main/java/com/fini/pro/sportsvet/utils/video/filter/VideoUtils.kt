package com.fini.pro.sportsvet.utils.video.filter

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import java.util.concurrent.TimeUnit

object VideoUtils {

    fun secToTime(totalSeconds: Long): String {
        return String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totalSeconds),
            TimeUnit.MILLISECONDS.toMinutes(totalSeconds) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalSeconds)), // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(totalSeconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalSeconds))
        )
    }

    fun buildMediaSource(context: Context, uri: Uri, fromWho: String, userAgent: String = ""): MediaSource? {

        when (fromWho) {

            VideoFrom.LOCAL -> {

                val dataSpec = DataSpec(uri)

                val fileDataSource = FileDataSource()
                try {
                    fileDataSource.open(dataSpec)
                } catch (e: FileDataSource.FileDataSourceException) {
                    e.printStackTrace()
                }
                val factory = DataSource.Factory { fileDataSource }

                fileDataSource.uri?.apply {
                    return DefaultMediaSourceFactory(context).setDataSourceFactory(factory).createMediaSource(
                        MediaItem.fromUri(this))
                }
                return null
            }

            VideoFrom.REMOTE -> {
//                return ExtractorMediaSource.Factory(
//                    DefaultHttpDataSourceFactory(userAgent)
//                ).createMediaSource(uri)
            }

            else -> {
                return null
            }

        }

        return null
    }
}


class VideoFrom {
    companion object {
        const val LOCAL = "LOCAL"
        const val REMOTE = "REMOTE"
    }
}