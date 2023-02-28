package com.fini.pro.sportsvet.utils.video.filter

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.FillMode
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer.GPUMp4Composer
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilterGroup
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlMonochromeFilter
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlVignetteFilter
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.OptiUtils

class FiltersFragment(val media: Media, val callBack: CallBack) : BottomSheetDialogFragment(), FilterAdapter.CallBack{

    private lateinit var rootView: View
    private lateinit var mainLayout: LinearLayout
    private lateinit var close: ImageView
    private var recyclerView: RecyclerView? = null
    private var filterAdapter: FilterAdapter? = null

    interface CallBack{
        fun loading(isLoading: Boolean)
        fun onMediaModified(originMedia: String, resultList: ArrayList<String>)
    }
    // dismiss if rotate
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        releaseAdapter()
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.editor_fargment_filters, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)

        resizeHeight()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onViewCreated(view, savedInstanceState)

        close = view.findViewById(R.id.imv_close)
        mainLayout = view.findViewById(R.id.mainLayout)
        recyclerView = view.findViewById(R.id.rv_filters)

        close.setOnClickListener {
            releaseAdapter()
            dismiss()
        }

        setupRecycler()
        filterAdapter!!.differ.submitList(FilterType.createFilterList().subList(0, 15))

    }

    private fun setupRecycler(){
        filterAdapter = FilterAdapter(requireContext(), media.path!!, this)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
        val gridLayout = GridLayoutManager(requireContext(), 3)
        recyclerView!!.apply {
            layoutManager = gridLayout
            adapter = filterAdapter
        }
    }

    override fun onClick(filterType: FilterType) {
        startCodec(filterType)
        callBack.loading(true)
        releaseAdapter()
        dismiss()
    }


    private var GPUMp4Composer: GPUMp4Composer? = null
    private var glFilter: GlFilter = GlFilterGroup(GlMonochromeFilter(), GlVignetteFilter())

    private fun startCodec(filter: FilterType) {
        val output = OptiUtils.createVideoFile(requireContext())
        glFilter = FilterType.createGlFilter(filter, requireContext())
        GPUMp4Composer = null
        GPUMp4Composer =
            GPUMp4Composer(media.path, output.path) // .rotation(Rotation.ROTATION_270)
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

                        Handler(Looper.getMainLooper()).post {
                            callBack.loading(false)
                            callBack.onMediaModified(media.path!!, list)
                        }

                    }

                    override fun onCanceled() {
                        Handler(Looper.getMainLooper()).post {
                            callBack.loading(false)
                        }
                    }
                    override fun onFailed(exception: Exception?) {
                        Handler(Looper.getMainLooper()).post {
                            callBack.loading(false)
                            Log.d("decoder", "onFailed()")
                        }
                    }
                })
                .start()
    }


    private fun releaseAdapter() {
        for(i in 0 until filterAdapter!!.differ.currentList.size){
            if(recyclerView!!.findViewHolderForAdapterPosition(i) != null){
                filterAdapter!!.releasePlayer(recyclerView!!.findViewHolderForAdapterPosition(i) as FilterAdapter.ViewHolder)
            }
        }
        filterAdapter = null
        recyclerView = null
    }


    private fun resizeHeight(){

        dialog!!.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { fullScreen ->
                val behaviour = BottomSheetBehavior.from(fullScreen)
                val layoutParams = fullScreen.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                fullScreen.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }
}
