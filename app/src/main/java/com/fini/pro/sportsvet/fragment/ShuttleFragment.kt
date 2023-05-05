package com.fini.pro.sportsvet.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.dialView.DialView
import com.fini.pro.sportsvet.utils.dialView.DialView.OnDialValueChangeListener
import com.fini.pro.sportsvet.utils.video.filter.FilterType
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.FillMode
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.GPUMp4Composer
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilterGroup
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.OptiUtils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
class ShuttleFragment : Fragment() {

    private lateinit var dialView: DialView
    private lateinit var valueLabel: TextView
    private lateinit var applyButton: TextView

    private var media: Media? = null
    private var fragmentListener: ShuttleFragmentListener? = null
    private var shuttleValue: Float = 5000f

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shuttle, container, false)

        initLayout(view = view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShuttleFragmentListener) {
            fragmentListener = context
        }
    }

    // TODO: My Method

    private fun initLayout(view: View) {
        valueLabel = view.findViewById(R.id.tv_value)
        dialView = view.findViewById(R.id.dial_view)
        dialView.setOnDialValueChangeListener(object : OnDialValueChangeListener {
            override fun onDialValueChanged(value: String?, maxValue: Int) {
                if (value == null)
                    return
                val intValue = value.toInt()
                valueLabel.text = "$intValue"

                if (intValue <= 20) {
                    shuttleValue = 0f
                }
                else if (intValue in 21..39) {
                    shuttleValue = 0.25f
                }
                else if (intValue in 40..59) {
                    shuttleValue = 0.5f
                }
                else if (intValue in 60..80) {
                    shuttleValue = 0.75f
                }
                else {
                    shuttleValue = 1.0f
                }
                if (intValue < 0) {
                    shuttleValue = -shuttleValue
                }
//                Log.e("Shuttle Value", shuttleValue.toString())
                fragmentListener?.onPlaybackSpeed(speed = shuttleValue)
            }
        })

        // Apply Button
        applyButton = view.findViewById(R.id.tv_apply)
        applyButton.background = Utils.getRippleDrawable(requireContext().getColor(R.color.purple1), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp))
        applyButton.setOnClickListener {
            applyShuttle()
        }
    }

    fun setMedia(sender: Media?) {
        media = sender?.copy()
    }

    private var GPUMp4Composer: GPUMp4Composer? = null
    private var glFilter: GlFilter = GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter())

    private fun applyShuttle() {
        if (media == null)
            return

        applyButton.isEnabled = false
        fragmentListener?.onShuttleLoading(true)

        val output = OptiUtils.createVideoFile(requireContext())
        glFilter = FilterType.createGlFilter(FilterType.WHITE_BALANCE, requireContext(), shuttleValue.toDouble())
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
                            applyButton.isEnabled = true
                            fragmentListener?.onShuttleLoading(false)
                            fragmentListener?.onApplyShuttle(arrayListOf(media!!))
                        }

                    }

                    override fun onCanceled() {
                        Handler(Looper.getMainLooper()).post {
                            applyButton.isEnabled = true
                            fragmentListener?.onShuttleLoading(false)
                        }
                    }
                    override fun onFailed(exception: Exception?) {
                        Handler(Looper.getMainLooper()).post {
                            applyButton.isEnabled = true
                            fragmentListener?.onShuttleLoading(false)
                            Log.d("decoder", "onFailed()")
                        }
                    }
                })
                .start()
    }

    // TODO:  BrightnessFragmentListener

    interface ShuttleFragmentListener {

        fun onShuttleLoading(isLoading: Boolean)
        fun onApplyShuttle(list: ArrayList<Media>)
        fun onPlaybackSpeed(speed: Float)

    }
}