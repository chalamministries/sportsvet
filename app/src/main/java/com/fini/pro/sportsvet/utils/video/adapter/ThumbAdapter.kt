package com.fini.pro.sportsvet.utils.video.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.roundedimageview.RoundedImageView

class ThumbAdapter(
    private val context: Context,
    private val clickCallBack: ClickCallBack,
) : RecyclerView.Adapter<ThumbAdapter.ViewHolder>() {

    interface ClickCallBack{
        fun onClick()
    }
    private lateinit var imageView: RoundedImageView

    companion object {
        var STEP_IN_MILL_SEC: Long = 1000000
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Bitmap>() {
        override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
            return false
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val viewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_thumb, parent, false))

        imageView = viewHolder.itemView.findViewById(R.id.imv_thumb)

        return viewHolder
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
        val thumb = differ.currentList[position]
        holder.itemView.apply {
            imageView.setImageBitmap(thumb)
            setOnClickListener {
                clickCallBack.onClick()
            }

            val radius = this@ThumbAdapter.context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
            if (position == 0) {
                imageView.setCornerRadius(radius.toFloat(), 0f, radius.toFloat(), 0f)
            }
            else if (position == differ.currentList.size - 1) {
                imageView.setCornerRadius(0f, radius.toFloat(), 0f, radius.toFloat())
            }
        }
    }

}