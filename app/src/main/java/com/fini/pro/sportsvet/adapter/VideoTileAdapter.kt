package com.fini.pro.sportsvet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.model.Video

class VideoTileAdapter(
    private val context: Context,
    private var items: ArrayList<Video>,
    var onItemClick: (Video) -> Unit,
) : RecyclerView.Adapter<VideoTileAdapter.VideoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(LayoutInflater.from(context).inflate(R.layout.item_video_tile, parent, false))
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val video = items[position]
        holder.ivThumbnail.setImageBitmap(video.thumbnail(context))
        holder.tvDuration.text = video.durationLabel(context)

        holder.itemView.setOnClickListener {
            onItemClick(video)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // TODO: My Method

    fun setItems(items: ArrayList<Video>) {
        this.items = items
        notifyDataSetChanged()
    }

    // TODO: ViewHolder

    class VideoHolder(view: View): RecyclerView.ViewHolder(view) {
        val ivThumbnail: ImageView
        val tvDuration: TextView

        init {
            ivThumbnail = view.findViewById(R.id.iv_thumbnail)
            tvDuration = view.findViewById(R.id.tv_duration)
        }
    }
}