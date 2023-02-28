package com.fini.pro.sportsvet.utils.video.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler.Companion.itemWidth
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    var id: Int? = 55,
    var thumbList: ArrayList<Bitmap>? = arrayListOf(),
    var path: String? = "",
    var startPosition: Int? = 0,
    var endPosition: Int? = 0,
    var duration: Int? = 0,
    var minTrim: Int? = 0,
    var maxTrim: Int? = 0,
    var distance: Int? = 0,
    var speed: Float? = 0f,
    var width: Int? = 0,
    var height: Int? = 0,
    var mediaShape: Int? = 0,
    var hasAudio: Boolean? = true,
    var audioPath: String? = ""

): Parcelable {


    companion object{
        val SPLITTER = 0
        val FINAL_EXTRA = 1
        val VIDEO = 2
    }
    fun computeStartPixel(list : ArrayList<Media>): Int{
        var totalPixel = 0

        for(media in list){
            if(media.id!! < id!!){
                totalPixel += (media.thumbList?.size!! * itemWidth)
                if(media.thumbList?.size == 0){
                    totalPixel += itemWidth
                }

            }
        }

        return totalPixel
    }
}