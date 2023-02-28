package com.fini.pro.sportsvet.utils.video.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.model.EditType
import com.fini.pro.sportsvet.utils.video.Ranger
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler.Companion.itemHeight
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler.Companion.itemWidth

class MediaAdapter(
    val context: Context,
    val callBack: MediaCallBack,
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    var editType: EditType = EditType.SHUTTLE
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface MediaCallBack{
        fun onChanged(min: Int, max: Int, id: Int, startThumb: Boolean)
        fun onClick(inside: Boolean)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.rv_bitmaps)
        var outLayout: LinearLayout = itemView.findViewById(R.id.layout_out)
        var splitter: ImageView = itemView.findViewById(R.id.imv_splitter)
        var ranger: Ranger = itemView.findViewById(R.id.ranger)
        var audioBar: View = itemView.findViewById(R.id.bar_audio)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Media>() {
        override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
            return (oldItem.path == newItem.path && oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
            return (oldItem.path == newItem.path && oldItem.id == newItem.id)
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
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
        val itemPosition = position
        val media = differ.currentList[itemPosition]

        if (editType != EditType.TRIM)
            hideTools(holder = holder)

        holder.apply {

            ranger.visibility = View.GONE
            audioBar.visibility = View.INVISIBLE

            when(media.mediaShape) {

                Media.SPLITTER -> {
                    recyclerView.visibility = View.GONE
                    resetImage(splitter)
                }

                Media.FINAL_EXTRA -> {

                    splitter.visibility = View.GONE
                    val thumbAdapter = ThumbAdapter(
                        context = context,
                        object : ThumbAdapter.ClickCallBack{
                            override fun onClick() {

                            }
                        }
                    )
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.adapter = thumbAdapter
                    thumbAdapter.differ.submitList(media.thumbList)
                    setRanger(context, media, holder)
                }

                Media.VIDEO -> {
                    splitter.visibility = View.GONE
                    val thumbAdapter = ThumbAdapter(
                        context = context,
                        object : ThumbAdapter.ClickCallBack {
                            override fun onClick() {
                                if (ranger.visibility == View.GONE && (itemPosition < differ.currentList.size - 1)) {
                                    showTools(media, holder)
                                }
                            }
                        }
                    )
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.adapter = thumbAdapter
                    thumbAdapter.differ.submitList(media.thumbList)
                    setRanger(context, media, holder)

                    if (media.audioPath!!.isNotEmpty()) {
                        audioBar.visibility = View.VISIBLE
                    }
                }
            }

            outLayout.setOnClickListener {
//                callBack.onClick(false)
                holder.ranger.visibility = View.GONE
            }
        }
    }

    private fun resetImage(view: ImageView) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.layoutParams.width = (itemWidth)
                view.layoutParams.height = (itemHeight / 2)
                view.setPadding((itemWidth / 10), 0, (itemWidth / 10), 0)
                view.requestLayout()
                view.visibility = View.VISIBLE
            }
        })
    }

    private fun setRanger(context: Context, media: Media, holder: ViewHolder){
        holder.ranger.context = context
        holder.ranger.setProgressHeight(itemHeight.toFloat() + (holder.recyclerView.layoutParams as ViewGroup.MarginLayoutParams).topMargin)

        holder.ranger.minValue = 0f
        holder.ranger.maxValue = media.duration!!.toFloat()
        holder.ranger.selectedMinValue = media.minTrim!!.toFloat()
        holder.ranger.selectedMaxValue = media.maxTrim!!.toFloat()
        holder.ranger.setProgressColor(Color.TRANSPARENT)
        holder.ranger.setBackgroundColor(Color.TRANSPARENT)
        holder.ranger.setStartThumbImageResource(R.drawable.edge, true)
        holder.ranger.setEndThumbImageResource(R.drawable.edge_right, true)

        holder.ranger.setOnSeekBarRangedChangeListener(object : Ranger.OnSeekBarRangedChangeListener {
            override fun onChanged(view: Ranger?, minValue: Float, maxValue: Float) {
                Log.d("change_1", "$minValue, $maxValue")
            }

            override fun onChanging(view: Ranger?, minValue: Float, maxValue: Float) {
                Log.d("change_2", "$minValue, $maxValue")
                val startThumb = media.minTrim != minValue.toInt()
                media.minTrim = minValue.toInt()
                media.maxTrim = maxValue.toInt()

                callBack.onChanged(minValue.toInt(), maxValue.toInt(), media.id!!, startThumb)
            }
        })
    }

    private fun showTools(media: Media, holder: ViewHolder) {
//        val toolsFragment = ToolsFragment(media, object : ToolsFragment.LifecycleCallBack{
//            override fun onDismiss() {
//                holder.ranger.visibility = View.GONE
//            }
//
//            override fun onCreate() {
//                holder.ranger.visibility = View.VISIBLE
//            }
//        })
//        Log.d("mediaAdapter", "clicked: " + media.id!!)
//        callBack.onClick(toolsFragment, true)
        if (editType == EditType.TRIM) {
            holder.ranger.visibility = View.VISIBLE
        }
    }

    private fun hideTools(holder: ViewHolder) {
        holder.ranger.visibility = View.GONE
    }

}