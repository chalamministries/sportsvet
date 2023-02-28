package com.fini.pro.sportsvet.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.fini.pro.sportsvet.MyApp
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.adapter.BrightnessAdapter
import com.fini.pro.sportsvet.adapter.SnapHelperOneByOne
import com.fini.pro.sportsvet.utils.video.filter.FilterType
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.FillMode
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.GPUMp4Composer
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilterGroup
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.OptiUtils


/**
 * A simple [Fragment] subclass.
 * Use the [BrightnessFragment] factory method to
 * create an instance of this fragment.
 */
class BrightnessFragment : Fragment() {

    private lateinit var tvFilter: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var tvMax: TextView
    private lateinit var tvMin: TextView

    private val filterTypeList = FilterType.customFilterList()
    private var currentFilter: FilterType = filterTypeList[0]
    private var maxValue: Double = 1.0
    private var minValue: Double = -1.0
    private var currentValue: Double = 0.0
    private var normalValue: Double = 0.0

    private var media: Media? = null
    private var fragmentListener: BrightnessFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_brightness, container, false)

        initLayout(view)

        updateFilterRange()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BrightnessFragmentListener) {
            fragmentListener = context
        }
    }

    // TODO: My Method

    private fun initLayout(view: View) {
        // Filter Type Label
        tvFilter = view.findViewById(R.id.tv_filter)
        tvFilter.text = FilterType.getLabelBy(currentFilter)

        seekBar = view.findViewById(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    currentValue = minValue + (maxValue - minValue) * progress.toDouble() / seekBar.max.toDouble()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    currentValue = minValue + (maxValue - minValue) * seekBar.progress.toDouble() / seekBar.max.toDouble()
                }
                applyFilter(currentFilter, value = currentValue)
            }
        })

        tvMax = view.findViewById(R.id.tv_max)
        tvMin = view.findViewById(R.id.tv_min)

        // Recycler View
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_filters)
        val layoutManager = object : LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false) {
            override fun getPaddingLeft(): Int {
                if (activity != null) {
                    if (MyApp.shared.isTablet())
                        return (displayMetrics().widthPixels - activity!!.resources.getDimensionPixelSize(
                            com.intuit.sdp.R.dimen._40sdp
                        )) / 2 - activity!!.resources.getDimensionPixelSize(
                            com.intuit.sdp.R.dimen._3sdp
                        )
                    return (displayMetrics().widthPixels - activity!!.resources.getDimensionPixelSize(
                        com.intuit.sdp.R.dimen._50sdp
                    )) / 2 - activity!!.resources.getDimensionPixelSize(
                        com.intuit.sdp.R.dimen._3sdp
                    )
                }
                return displayMetrics().widthPixels / 2
            }

            override fun getPaddingRight(): Int {
                if (activity != null) {
                    if (MyApp.shared.isTablet())
                        return (displayMetrics().widthPixels - activity!!.resources.getDimensionPixelSize(
                            com.intuit.sdp.R.dimen._40sdp
                        )) / 2 - activity!!.resources.getDimensionPixelSize(
                            com.intuit.sdp.R.dimen._3sdp
                        )
                    return (displayMetrics().widthPixels - activity!!.resources.getDimensionPixelSize(
                        com.intuit.sdp.R.dimen._50sdp
                    )) / 2 - activity!!.resources.getDimensionPixelSize(
                        com.intuit.sdp.R.dimen._3sdp
                    )
                }
                return displayMetrics().widthPixels / 2
            }
        }
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        recyclerView.layoutManager = layoutManager

        if (activity != null) {
            val adapter = BrightnessAdapter(requireActivity(), filterTypeList, onSelectFilter = { filterType, position ->
                currentFilter = filterType
                tvFilter.text = FilterType.getLabelBy(currentFilter)
//                layoutManager.smoothScrollToPosition(recyclerView, RecyclerView.State.STEP_ANIMATIONS, position)
//                recyclerView.smoothScrollToPosition(position)
                smoothScroller.targetPosition = position
                layoutManager.startSmoothScroll(smoothScroller)

                updateFilterRange()
                // Apply Filter
//                applyFilter(filterType)
            })
            recyclerView.adapter = adapter
        }
        val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
        linearSnapHelper.attachToRecyclerView(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val snapView = linearSnapHelper.findSnapView(layoutManager)
                if (snapView != null
                    && newState == SCROLL_STATE_IDLE) {
                    val currentPosition = recyclerView.getChildAdapterPosition(snapView)
                    Log.e("onScrollStateChanged", "$currentPosition")

                    Handler(Looper.getMainLooper()).post {
                        currentFilter = filterTypeList[currentPosition]
                        tvFilter.text = FilterType.getLabelBy(currentFilter)
                    }
                }
            }
        })
    }

    private fun displayMetrics() : DisplayMetrics {
//        val displayMetrics = DisplayMetrics()
//        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels
//        val width = displayMetrics.widthPixels
//        return displayMetrics
        return Resources.getSystem().displayMetrics
    }

    private fun updateFilterRange() {
        when (currentFilter) {
            FilterType.BRIGHTNESS -> { // Normal: 0.0
                maxValue = 1.0
                minValue = -1.0
                normalValue = 0.0
            }
            FilterType.CONTRAST -> { // Normal: 1.0
                maxValue = 4.0
                minValue = 0.0
                normalValue = 1.0
            }
            FilterType.GAMMA -> { // Normal: 1.2
                maxValue = 4.0
                minValue = 0.0
                normalValue = 0.0
            }
            FilterType.HAZE -> { // Normal: 0.0
                maxValue = 1.0
                minValue = -1.0
                normalValue = 0.0
            }
            FilterType.SHARP -> { // Normal: 1.0
                maxValue = 10.0
                minValue = 0.0
                normalValue = 1.0
            }
            FilterType.VIBRANCE -> { // Normal: 1.0
                maxValue = 10.0
                minValue = 0.0
                normalValue = 1.0
            }
            else -> {}
        }
        tvMax.text = "$maxValue"
        tvMin.text = "$minValue"
        val progress = seekBar.max.toDouble() * (normalValue - minValue) / (maxValue - minValue)
        seekBar.setProgress(progress.toInt(), true)
    }

    fun setMedia(sender: Media?) {
        media = sender?.copy()
    }

    // TODO: Apply Filter

    private var GPUMp4Composer: GPUMp4Composer? = null
    private var glFilter: GlFilter = GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter())

    private fun applyFilter(filterType: FilterType, value: Double) {
        if (media == null)
            return
        fragmentListener?.onBrightnessLoading(true)

        val output = OptiUtils.createVideoFile(requireContext())
        glFilter = FilterType.createGlFilter(filterType, requireContext(), value)
        GPUMp4Composer = null
        GPUMp4Composer =
            GPUMp4Composer(media!!.path, output.path) // .rotation(Rotation.ROTATION_270)
                //.size(720, 720)
                .fillMode(FillMode.PRESERVE_ASPECT_CROP)
                .filter(glFilter)
                .mute(false)
                .flipHorizontal(false)
                .flipVertical(false)
                .listener(object : GPUMp4Composer.Listener {

                    override fun onProgress(progress: Double) {
                        Log.d("decoder", "onProgress = $progress")
                    }

                    override fun onCompleted() {
                        Log.d("decoder", "onCompleted()")

                        val list : ArrayList<String> = arrayListOf()
                        list.add(output.path)
                        media!!.path = output.path

                        Handler(Looper.getMainLooper()).post {
                            fragmentListener?.onBrightnessLoading(false)
                            fragmentListener?.onApplyFilter(arrayListOf(media!!))
                        }

                    }

                    override fun onCanceled() {
                        Handler(Looper.getMainLooper()).post {
                            fragmentListener?.onBrightnessLoading(false)
                        }
                    }
                    override fun onFailed(exception: Exception?) {
                        Handler(Looper.getMainLooper()).post {
                            fragmentListener?.onBrightnessLoading(false)
                            Log.d("decoder", "onFailed()")
                        }
                    }
                })
                .start()
    }

    // TODO:  BrightnessFragmentListener

    interface BrightnessFragmentListener {

        fun onBrightnessLoading(isLoading: Boolean)
        fun onApplyFilter(list: ArrayList<Media>)

    }
}