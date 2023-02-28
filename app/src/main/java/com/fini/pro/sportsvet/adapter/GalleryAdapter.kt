package com.fini.pro.sportsvet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.model.Video

class GalleryAdapter(
    private val context: Context,
    private var dateList: ArrayList<String>,
    private var items: HashMap<String, ArrayList<Video>>,
    var onSelectVideo: (Video) -> Unit,
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_group, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videos = items[dateList[position]]
        if (videos != null) {
            if (videos.size > 0) {
                val video = videos[0]
                val dateLabel: String = if (video.isToday())
                    "Today"
                else if (video.isYesterday())
                    "Yesterday"
                else
                    video.addedAt()
                holder.tvAdded.text = dateLabel
            }

            val adapter = VideoTileAdapter(context, videos, onItemClick = {
                onSelectVideo(it)
            })
            holder.recyclerView.adapter = adapter
            holder.recyclerView.layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        }
        else {
            holder.tvAdded.text = ""
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    // TODO: My Method

    fun setItems(
        dateList: ArrayList<String>,
        items: HashMap<String, ArrayList<Video>>
    ) {
        this.dateList = dateList
        this.items = items
        notifyDataSetChanged()
    }

    // TODO: ViewHolder

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAdded: TextView
        val recyclerView: RecyclerView

        init {
            tvAdded = view.findViewById(R.id.tv_added)
            recyclerView = view.findViewById(R.id.rv_videos)
        }
    }
}