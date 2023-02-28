package com.fini.pro.sportsvet.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.video.adapter.MomentsAdapter
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.ProcedureCallBack
import com.fini.pro.sportsvet.utils.video.presenter.Trimmer
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [TrimFragment] factory method to
 * create an instance of this fragment.
 */
class TrimFragment : Fragment() {

    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView

    private var media: Media? = null
    private var listener: TrimFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trim, container, false)
        initLayout(view = view)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TrimFragmentListener) {
            listener = context
        }
    }

    // TODO: My Method

    private fun initLayout(view: View) {
        val tvApply = view.findViewById<TextView>(R.id.tv_apply)
        tvApply.background = Utils.getRippleDrawable(requireContext().getColor(R.color.purple1), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp))
        tvApply.setOnClickListener {
            trimVideo()
        }

        tvStartTime = view.findViewById(R.id.tv_start_time)
        tvEndTime = view.findViewById(R.id.tv_end_time)
    }

    fun updateTimeFrame(media: Media, start: Double, end: Double) {
        Log.e("updateTimeFrame", "start: $start, end: $end")
        this.media = media.copy()
        tvStartTime.text = MomentsAdapter(requireContext()).getTimeDuration(start)
        tvEndTime.text = MomentsAdapter(requireContext()).getTimeDuration(end)
    }

    private fun trimVideo() {
        if (activity == null || media == null)
            return

        val trimmer = Trimmer(requireActivity(), procedureCallBack)
        trimmer.trimMediaList(arrayListOf(media!!))
    }

    // TODO: ProcedureCallBack

    private val procedureCallBack: ProcedureCallBack = object : ProcedureCallBack {
        override fun onTrimmed(list: ArrayList<Media>) {
            if (listener != null) {
                listener!!.onTrimmed(list = list)
            }
        }

        override fun onMerged(file: File) {

        }

        override fun onResized(list: ArrayList<Media>) {

        }

        override fun onFailed(message: String) {
            Log.e("Trim onFailed", message)
        }

        override fun onLoading(isLoading: Boolean) {
            if (listener != null) {
                listener!!.onTrimLoading(isLoading)
            }
        }

    }

    // TODO: TrimFragmentListener

    interface TrimFragmentListener {

        fun onTrimLoading(isLoading: Boolean)
        fun onTrimmed(list: ArrayList<Media>)

    }
}