package com.fini.pro.sportsvet.utils.video.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import kotlinx.coroutines.*
import java.io.File

class MomentsAdapter(private val context: Context) : RecyclerView.Adapter<MomentsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val thread = Thread()
        val IOscope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        val mainScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
        val mainHandler = Handler()
    }

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_media_grid,
                parent,
                false
            )
        )
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
        val media = differ.currentList[position]
        holder.itemView.apply {
            val thumbImageView = findViewById<ImageView>(R.id.imv_thumb)
            val imvType = findViewById<ImageView>(R.id.imv_media_type)
            val imvCheck= findViewById<ImageView>(R.id.imv_checked)
            val tvDuration = findViewById<TextView>(R.id.txv_duration)
            //imvType.setImageResource(R.drawable.ic_video_camera)
            resizeView(thumbImageView)

            holder.IOscope.launch {

                val retriever = MediaMetadataRetriever()

                try {
                    retriever.setDataSource(context, Uri.fromFile(File(media)))
                    val bitmap = retriever.frameAtTime
                    val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val duration = (time?.toDouble())?.div(1000)
                    retriever.release()

                    holder.mainScope.launch {
                        thumbImageView.setImageBitmap(bitmap)
                        tvDuration.text = getTimeDuration(duration!!)
                    }

                }catch (e: Exception){
                }


            }

            imvCheck.visibility = View.INVISIBLE

            setOnClickListener {
                onItemClickListener?.let { it(media) }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    // convert duration from seconds into time (hour: min: sec)
    fun getTimeDuration(sec: Double): String{
        val totalSecs = sec.toLong()
        val hours = totalSecs / 3600;
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
        else {
            String.format("%02d:%02d", minutes, seconds)
        }

    }

    private fun resizeView(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.layoutParams.height = view.layoutParams.width
                view.requestLayout()
            }
        })

    }
}