package com.fini.pro.sportsvet.utils.video.filter

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.google.android.exoplayer2.ExoPlayer
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.player.GPUPlayerView


class FilterAdapter(
    val context: Context,
    private val videoPath: String,
    private val callBack: CallBack
) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    companion object{
        var itemWidth = 0
    }

    interface CallBack{
        fun onClick(filterType: FilterType)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var handler: Handler = Handler(Looper.getMainLooper())
        var filterTitle: TextView = itemView.findViewById(R.id.txv_filter)
        var wrapperView: MovieWrapperView = itemView.findViewById(R.id.movie_wrapper)

        var gpuPlayerView: GPUPlayerView? = null
        var player: ExoPlayer? = null
        var filter: GlFilter? = null

        init {
            resetSize(wrapperView)
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<FilterType>() {
        override fun areItemsTheSame(oldItem: FilterType, newItem: FilterType): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FilterType, newItem: FilterType): Boolean {
            return oldItem.name == newItem.name
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder  = ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_filter,
                parent,
                false
            )
        )

        holder.handler.post {
            holder.wrapperView.removeAllViews()
            setUpSimpleExoPlayer(holder)
            setUoGlPlayerView(holder)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterType = differ.currentList[position]
        holder.handler.post{
            holder.filterTitle.text = filterType.name
            holder.filter = FilterType.createGlFilter(filterType, context)
            holder.gpuPlayerView!!.setGlFilter(holder.filter)
        }

        holder.itemView.setOnClickListener {
            callBack.onClick(filterType)
        }

    }

    fun releasePlayer(holder: ViewHolder) {
        holder.handler.post {
            holder.gpuPlayerView!!.onPause()
            holder.wrapperView.removeAllViews()
            holder.gpuPlayerView = null
            holder.player!!.stop()
            holder.player!!.release()
            holder.player = null
        }
    }

    private fun setUpSimpleExoPlayer(holder: ViewHolder) {
//        val trackSelector: TrackSelector = DefaultTrackSelector()

        // SimpleExoPlayer
        holder.player = ExoPlayer.Builder(context)
            .build()//, trackSelector)
        // Prepare the holder.player with the source.
        VideoUtils.buildMediaSource(context, Uri.parse(videoPath), "LOCAL")
            ?.let { holder.player!!.addMediaSource(it) }
        holder.player!!.prepare()//VideoUtils.buildMediaSource(Uri.parse(videoPath), "LOCAL"))
        holder.player!!.playWhenReady = false
        holder.player!!.seekTo(holder.player!!.duration / 2)
    }

    private fun setUoGlPlayerView(holder: ViewHolder) {
        holder.gpuPlayerView = GPUPlayerView(context)
        holder.gpuPlayerView!!.setSimpleExoPlayer(holder.player)
        holder.gpuPlayerView!!.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        holder.wrapperView.addView(holder.gpuPlayerView)
        holder.gpuPlayerView!!.onResume()
    }

    private fun resetSize(view: MovieWrapperView) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.layoutParams.width = itemWidth
                view.layoutParams.height = itemWidth
                view.requestLayout()
            }
        })



    }

}