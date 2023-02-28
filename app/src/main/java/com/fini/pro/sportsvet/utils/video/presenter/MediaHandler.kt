package com.fini.pro.sportsvet.utils.video.presenter

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.fini.pro.sportsvet.MyApp
import com.fini.pro.sportsvet.utils.video.adapter.ThumbAdapter
import com.fini.pro.sportsvet.utils.video.model.Media
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MediaHandler(private val context: Context, private val extraItems: Int, private val callBack: MediaCallBack) {

    interface MediaCallBack {
        fun onListReady(medias: ArrayList<Media>)
    }

    //private var mediaList: ArrayList<Media> = arrayListOf()

    companion object {
        var allMediaList: ArrayList<String> = arrayListOf()
        var itemWidth = 0
        var itemHeight = 0
        var videoFrame: Long = 3000000
    }

    fun getMediaList(filePath: String) {
        MainScope().launch(Dispatchers.IO) {
            val mediaList: ArrayList<Media> = arrayListOf()
            val jobs: ArrayList<Deferred<ArrayList<Bitmap>>> = arrayListOf()
            val listTask = async { getVideoThumbs(filePath) }
            jobs.addAll(listOf(listTask))

            val media = Media()
            media.path = filePath
            media.mediaShape = Media.VIDEO
            media.thumbList?.addAll(jobs[0].await())
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(context, Uri.parse(media.path))
                media.duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
                media.width = retriever.frameAtTime!!.width
                media.height = retriever.frameAtTime!!.height
                val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                media.hasAudio = hasAudioStr == "yes"
                retriever.release()
                media.maxTrim = media.duration
                media.minTrim = 0
                mediaList.add(media)

            } catch (e: Exception) {
                Log.d("mediaHandler", "Error: path: " + media.path)
            }

            //add extra items after
            Log.d("mediaHandler", "Final one")
            val extraMedia = Media()
            for (j in 0 until extraItems) {
                val bitmap = Bitmap.createBitmap(itemWidth, itemHeight, Bitmap.Config.ARGB_8888)
                extraMedia.thumbList?.add(bitmap!!)
            }
            extraMedia.mediaShape = Media.FINAL_EXTRA
            mediaList.add(extraMedia)

            withContext(Dispatchers.Main) {
                callBack.onListReady(mediaList)
            }
        }
    }

    fun getMediaList2(pathList: ArrayList<String>) {
        MainScope().launch(Dispatchers.IO) {

            val mediaList: ArrayList<Media> = arrayListOf()


            val jobs: ArrayList<Deferred<ArrayList<Bitmap>>> = arrayListOf()


            for (i in 0 until pathList.size) {
                val listTask = async { getVideoThumbs(pathList[i]) }
                jobs.addAll(listOf(listTask))
            }


            for(i in 0 until pathList.size){

                val media = Media()
                media.path = pathList[i]
                media.mediaShape = Media.VIDEO
                media.thumbList?.addAll(jobs[i].await())
                val retriever = MediaMetadataRetriever()

                try {
                    retriever.setDataSource(context, Uri.parse(media.path))
                    media.duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
                    media.width = retriever.frameAtTime!!.width
                    media.height = retriever.frameAtTime!!.height
                    val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                    val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                    media.hasAudio = hasAudioStr == "yes"
                    retriever.release()
                    media.maxTrim = media.duration
                    media.minTrim = 0
                    mediaList.add(media)

                } catch (e: Exception) {
                    Log.d("mediaHandler", "Error: path: " + media.path)
                }


                if(i == pathList.size - 1){
                    //add extra items after
                    Log.d("mediaHandler", "Final one")
                    val extraMedia = Media()
                    for (j in 0 until extraItems) {
                        val bitmap = Bitmap.createBitmap(itemWidth, itemHeight, Bitmap.Config.ARGB_8888)
                        extraMedia.thumbList?.add(bitmap!!)
                    }
                    extraMedia.mediaShape = Media.FINAL_EXTRA
                    mediaList.add(extraMedia)

                }else{
                    // add splitter after
                    Log.d("mediaHandler", "splitter")
                    val splitter = Media()
                    splitter.thumbList = arrayListOf()
                    splitter.mediaShape = Media.SPLITTER
                    mediaList.add(splitter)

                }

            }

            withContext(Dispatchers.Main) {
                callBack.onListReady(mediaList)
            }

        }
    }


    fun getMediaList(pathList: ArrayList<String>, splitterPosition: Int) {
        MainScope().launch(Dispatchers.IO) {

            val mediaList: ArrayList<Media> = arrayListOf()

            val jobs: ArrayList<Deferred<ArrayList<Bitmap>>> = arrayListOf()

            for (i in 0 until pathList.size) {
                val listTask = async { getVideoThumbs(pathList[i]) }
                jobs.addAll(listOf(listTask))
            }

            for(i in 0 until pathList.size){

                // add splitter before
                val splitter = Media()
                if(splitterPosition == -1){
                    splitter.thumbList = arrayListOf()
                    mediaList.add(splitter)
                }

                val media = Media()
                media.path = pathList[i]
                media.thumbList?.addAll(jobs[i].await())
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, Uri.parse(media.path))
                    media.duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt()
                    media.width = retriever.frameAtTime!!.width
                    media.height = retriever.frameAtTime!!.height
                    val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                    media.hasAudio = hasAudioStr == "yes"
                    retriever.release()
                    media.maxTrim = media.duration
                    media.minTrim = 0
                    mediaList.add(media)

                } catch (e: Exception) {
                    mediaList.remove(splitter)
                    Log.d("mediaHandler", "Error: path: " + media.path)
                }
            }

            //add extra items after
            if(splitterPosition == 0){
                val media = Media()
                for (j in 0 until extraItems) {
                    val bitmap = Bitmap.createBitmap(
                        itemWidth,
                        itemHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    media.thumbList?.add(bitmap!!)
                }
                mediaList.add(media)
            }

            withContext(Dispatchers.Main) {
                callBack.onListReady(mediaList)
            }
        }
    }

    private fun getVideoThumbs(path: String): ArrayList<Bitmap> {
        val bitmaps = ArrayList<Bitmap>()

        val time = measureTimeMillis {
            ThumbAdapter.STEP_IN_MILL_SEC = videoFrame / 1000
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(MyApp.shared, Uri.parse(path))
            }
            catch (e: Exception) {
                Log.d("mediaHandler", "Error: path: $path")
                return bitmaps
            }

            Log.d("mediaHandler", "path: $path")
            val mp = MediaPlayer.create(context, Uri.parse(path))
            val millis = mp.duration
            var i : Long = 0

            // add first thumb
            var bitmap = retriever.getFrameAtTime((1).toLong())
            if (bitmap != null) {

                if (Merger.width!! == 0) Merger.width = bitmap.width
                if (Merger.height!! == 0) Merger.height = bitmap.height
                Log.d("mediaHandler", "" + bitmap.width + ", " + bitmap.height)

                bitmap = Bitmap.createScaledBitmap(bitmap, itemWidth, itemHeight, false)
                bitmaps.add(bitmap!!)

            }

            while (i < millis * 1000 || bitmaps.isEmpty()) {
                bitmap = retriever.getFrameAtTime(i)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, itemWidth, itemHeight, false)
                bitmaps.add(bitmap!!)
                i += videoFrame
            }

            // add last thumb
            bitmap = retriever.getFrameAtTime((millis * 1000).toLong())
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, itemWidth, itemHeight, false)
                bitmaps.add(bitmap)
            }

            retriever.release()
            mp.release()
        }
        Log.d("async_test", time.toString())
        return bitmaps

    }

}