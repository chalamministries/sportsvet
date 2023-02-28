package com.fini.pro.sportsvet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.video.filter.FilterType


class BrightnessAdapter(
    private val context: Context,
    private var items: List<FilterType>,
    private val onSelectFilter: ((FilterType, Int) -> Unit)
) : RecyclerView.Adapter<BrightnessAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_brightness, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterType = items[position]

        holder.llContent.background = Utils.getRippleDrawable(context.getColor(R.color.white), context.getColor(R.color.gray1), context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp))
        holder.llContent.setOnClickListener {
            onSelectFilter(filterType, position)
        }

        holder.tvFilter.text = filterType.name
        holder.ivFilter.setImageResource(FilterType.getIconBy(filterType))
    }

    // TODO: My Method

//    fun setItems(items: ArrayList<FilterType>) {
//        this.items = items
//    }

    // TODO: ViewHolder

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout
        val tvFilter: TextView
        val ivFilter: ImageView

        init {
            llContent = view.findViewById(R.id.ll_content)
            tvFilter = view.findViewById(R.id.tv_filter)
            ivFilter = view.findViewById(R.id.iv_filter)
        }
    }
}

// TODO: SnapHelperOneByOne

class SnapHelperOneByOne : LinearSnapHelper() {

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return RecyclerView.NO_POSITION
        }

        val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        val myLayoutManager = layoutManager as LinearLayoutManager

        val position1 = myLayoutManager.findFirstVisibleItemPosition()
        val position2 = myLayoutManager.findLastVisibleItemPosition()

        var currentPosition = layoutManager.getPosition(currentView)
        if (velocityX > 400) {
            currentPosition = position2
        }
        else if (velocityX < 400) {
            currentPosition = position1
        }

        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
//        Log.e("findTargetSnapPosition", "$currentPosition")
        return currentPosition

//        return super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
    }

}