package com.fini.pro.sportsvet.activity

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.dialog.ExportDialog
import com.fini.pro.sportsvet.fragment.AngleFragment
import com.fini.pro.sportsvet.fragment.BrightnessFragment
import com.fini.pro.sportsvet.fragment.ShuttleFragment
import com.fini.pro.sportsvet.fragment.TrimFragment
import com.fini.pro.sportsvet.model.EditType
import com.fini.pro.sportsvet.model.Video
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.video.adapter.MediaAdapter
import com.fini.pro.sportsvet.utils.video.adapter.MomentsAdapter
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler.Companion.itemHeight
import com.fini.pro.sportsvet.utils.video.presenter.MediaHandler.Companion.itemWidth
import com.fini.pro.sportsvet.utils.video.presenter.OptiConstant
import com.fini.pro.sportsvet.utils.video.presenter.Speeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class EditActivity : AppCompatActivity(),
    TrimFragment.TrimFragmentListener, BrightnessFragment.BrightnessFragmentListener, ShuttleFragment.ShuttleFragmentListener {

    companion object {
        private const val TAG_SHUTTLE = "shuttle"
        private const val TAG_ANGLE = "angle"
        private const val TAG_TRIM = "trim"
        private const val TAG_BRIGHTNESS = "brightness"
    }

    private var video: Video? = null

    private lateinit var mainLayout: LinearLayout
    private lateinit var llExport: LinearLayout
    private lateinit var llShuttle: LinearLayout
    private lateinit var llAngle: LinearLayout
    private lateinit var llTrim: LinearLayout
    private lateinit var llBrightness: LinearLayout
    private lateinit var tvShuttle: TextView
    private lateinit var tvAngle: TextView
    private lateinit var tvTrim: TextView
    private lateinit var tvBrightness: TextView
    private lateinit var videoView: VideoView
    private lateinit var ivPlay: ImageView
    private lateinit var progressVideo: ProgressBar
    private lateinit var recyclerBitmaps: RecyclerView
    private lateinit var ivSeekline: ImageView
    private lateinit var llTimeRange: LinearLayout
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var tvCurrentTime: TextView
    private lateinit var ivPrevious: ImageView
    private lateinit var ivNext: ImageView
    private var mediaPlayer: MediaPlayer? = null

    private var fragmentList: ArrayList<Fragment> = arrayListOf()
    private var currentFragmentTag = ""
    private lateinit var shuttleFragment: ShuttleFragment
    private lateinit var angleFragment: AngleFragment
    private lateinit var trimFragment: TrimFragment
    private lateinit var brightnessFragment: BrightnessFragment

    private lateinit var videoTrimmerAdapter: MediaAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var scrollListener: RecyclerView.OnScrollListener? = null
    private var touchListener: View.OnTouchListener? = null

    private var screenWidth = 0
    private var extraItems = 0
    private var currentMedia: Media? = null
        set(value) {
            field = value
            updateEditList(value)
        }
    private var mediaList: ArrayList<Media> = arrayListOf()
    private val videoHandler = Handler(Looper.getMainLooper())
    private val mainScrollHandler = Handler(Looper.getMainLooper())
    private val infoHandler = Handler(Looper.getMainLooper())
    private var isScrolling = false
    private var recyclerIsPreparing = true
    private var scrollingPosition = 0
    private var totalPixels = 0
    private var isPlaying = false
    private var isForward = false
    private var scrollOrdered = false
    private var pastSeekPosition = 0
    private var pastScrollingOffset = 0
    private var isLoading = false
//    private var mediaHasAudioPositions: ArrayList<String> = arrayListOf()

//    private var uploadReady = false
    /**
     * It includes all video changes
     */
    private var editList: ArrayList<Media> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        Utils.showStatusBar(this, lightStatusBar = true, false)

        if (intent.hasExtra("video"))
            video = intent.getParcelableExtra("video")

        initLayout()

        initTabBar()

        presentShuttleFragment()

        setupRecycler()

        Handler(Looper.getMainLooper()).postDelayed({
            onVideoSetup()
        }, 500)
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        if (fragmentList.size > 1) {
            fragmentList.removeAt(fragmentList.size - 1)
            val fragment = fragmentList[fragmentList.size - 1]
            var fragmentTag = ""

            initTabBar()
            when (fragment) {
                is ShuttleFragment -> {
                    fragmentTag = TAG_SHUTTLE
                    llShuttle.setBackgroundResource(R.drawable.tab_item_selected)
                    tvShuttle.setTextColor(getColor(R.color.purple2))
                }
                is AngleFragment -> {
                    fragmentTag = TAG_ANGLE
                    llAngle.setBackgroundResource(R.drawable.tab_item_selected)
                    tvAngle.setTextColor(getColor(R.color.purple2))
                }
                is TrimFragment -> {
                    fragmentTag = TAG_TRIM
                    llTrim.setBackgroundResource(R.drawable.tab_item_selected)
                    tvTrim.setTextColor(getColor(R.color.purple2))
                }
                is BrightnessFragment -> {
                    fragmentTag = TAG_BRIGHTNESS
                    llBrightness.setBackgroundResource(R.drawable.tab_item_selected)
                    tvBrightness.setTextColor(getColor(R.color.purple2))
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragmentTag)
                .commit()
        }
    }

    // TODO: My Method

    private fun initLayout() {
        mainLayout = findViewById(R.id.main_layout)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        llExport = findViewById(R.id.ll_export)
        llExport.background = Utils.getRippleDrawable(getColor(R.color.purple1), resources.getDimensionPixelSize(
            com.intuit.sdp.R.dimen._3sdp))
        llExport.setOnClickListener {
            presentExportDialog()
        }

        progressVideo = findViewById(R.id.progress_horizontal)
        progressVideo.visibility = View.INVISIBLE

        videoView = findViewById(R.id.video_view)

        ivPlay = findViewById(R.id.iv_play)

        llTimeRange = findViewById(R.id.ll_time_range)
        llTimeRange.visibility = View.GONE

        tvStartTime = findViewById(R.id.tv_start)
        tvEndTime = findViewById(R.id.tv_end)
        tvCurrentTime = findViewById(R.id.tv_position)

        // Recycler Bitmaps
        recyclerBitmaps = findViewById(R.id.recycler_bitmaps)

        ivSeekline = findViewById(R.id.iv_seek_line)

        // Bottom Navigation
        llShuttle = findViewById(R.id.ll_shuttle)
        llShuttle.setOnClickListener {
            llTimeRange.visibility = View.GONE
            videoTrimmerAdapter.editType = EditType.SHUTTLE
            presentShuttleFragment()

            shuttleFragment.setMedia(sender = currentMedia)
        }
        llAngle = findViewById(R.id.ll_angle)
        llAngle.setOnClickListener {
            llTimeRange.visibility = View.GONE
            videoTrimmerAdapter.editType = EditType.ANGLE
            presentAngleFragment()
        }
        llTrim = findViewById(R.id.ll_trim)
        llTrim.setOnClickListener {
            llTimeRange.visibility = View.VISIBLE
            videoTrimmerAdapter.editType = EditType.TRIM
            presentTrimFragment()
        }
        llBrightness = findViewById(R.id.ll_brightness)
        llBrightness.setOnClickListener {
            llTimeRange.visibility = View.GONE
            videoTrimmerAdapter.editType = EditType.FILTER
            presentBrightnessFragment()

            brightnessFragment.setMedia(sender = currentMedia)
        }

        tvShuttle = findViewById(R.id.tv_shuttle)
        tvAngle = findViewById(R.id.tv_angle)
        tvTrim = findViewById(R.id.tv_trim)
        tvBrightness = findViewById(R.id.tv_brightness)

        shuttleFragment = ShuttleFragment()
        angleFragment = AngleFragment()
        trimFragment = TrimFragment()
        brightnessFragment = BrightnessFragment()

        // --
        mainLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mainLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                screenWidth = mainLayout.width
//                val screenHeight = mainLayout.height

                val thumbCount = 10//((((12 * screenWidth) / screenHeight) + 1) / 2) * 2
                itemWidth = screenWidth / thumbCount
                itemHeight = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)//(itemWidth * 3) / 2

                extraItems = thumbCount / 2

                (ivSeekline.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = (screenWidth / 2 - ivSeekline.width / 2)
                ivSeekline.requestLayout()

                val rvParams = recyclerBitmaps.layoutParams as ViewGroup.MarginLayoutParams
                rvParams.leftMargin = 0//((screenWidth - (thumbCount * itemWidth)) / 2)
                rvParams.rightMargin = 0//((screenWidth - (thumbCount * itemWidth)) / 2)
                //recyclerView.setPadding(screenWidth/2, 0,0,0)
                recyclerBitmaps.requestLayout()
            }
        })

        // Redo & Undo
        ivPrevious = findViewById(R.id.iv_undo)
        ivPrevious.background = Utils.getRippleDrawable(getColor(R.color.white), 0)
        ivPrevious.setOnClickListener {
            onPrevOrNextMedia(prev = true)
        }
        ivNext = findViewById(R.id.iv_redo)
        ivNext.background = Utils.getRippleDrawable(getColor(R.color.white), 0)
        ivNext.setOnClickListener {
            onPrevOrNextMedia(prev = false)
        }

        // Speed
        val ivSpeed = findViewById<ImageView>(R.id.iv_speed)
        ivSpeed.setOnClickListener {
            val popupMenu = PopupMenu(this@EditActivity, ivSpeed)
            popupMenu.menu.add(OptiConstant.SPEED_0_25)
            popupMenu.menu.add(OptiConstant.SPEED_0_5)
            popupMenu.menu.add(OptiConstant.SPEED_0_75)
            popupMenu.menu.add(OptiConstant.SPEED_1_0)
            popupMenu.menu.add(OptiConstant.SPEED_1_25)
            popupMenu.menu.add(OptiConstant.SPEED_1_5)

            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                if (currentMedia == null)
                    return@setOnMenuItemClickListener false

                var playbackSpeed = 0.0F
                var tempo = 0.0F
                when (it.title) {

                    OptiConstant.SPEED_0_25 -> {
                        playbackSpeed = 1.75F
                        tempo = 0.50F
                    }

                    OptiConstant.SPEED_0_5 -> {
                        playbackSpeed = 1.50F
                        tempo = 0.50F
                    }

                    OptiConstant.SPEED_0_75 -> {
                        playbackSpeed = 1.25F
                        tempo = 0.75F
                    }

                    OptiConstant.SPEED_1_0 -> {
                        playbackSpeed = 1.0F
                        tempo = 1.0F
                    }

                    OptiConstant.SPEED_1_25 -> {
                        playbackSpeed = 0.75F
                        tempo = 1.25F
                    }

                    OptiConstant.SPEED_1_5 -> {
                        playbackSpeed = 0.50F
                        tempo = 2.0F
                    }

                }

                val speeder = Speeder(this@EditActivity, object : Speeder.SpeederCallBack {
                    override fun onVideoSpeed(list: ArrayList<String>) {
                        Log.e("onVideoSpeed", "${list.count()}")
                        if (list.isNotEmpty()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                onVideoSetup(list[0])
                            }, 500)
                        }
                    }

                    override fun loading(isLoading: Boolean) {
                        if (isLoading)
                            showLoading()
                        else
                            hideLoading()
                    }
                })

                speeder.speed(currentMedia!!, playbackSpeed.toString(), tempo.toString())
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun updateEditList(media: Media?) {
        if (media == null)
            return
        val existed = editList.indexOfFirst {
            it.path == media.path
        }

        if (existed > -1) {
            editList.removeAt(existed)
            editList.add(existed, media)

            ivPrevious.setImageResource(R.drawable.ic_undo)
            ivPrevious.isEnabled = true
            ivNext.setImageResource(R.drawable.ic_redo)
            ivNext.isEnabled = true

            if (existed == 0) {
                ivPrevious.setImageResource(R.drawable.ic_undo_disable)
                ivPrevious.isEnabled = false
            }
            if (existed == editList.size - 1) {
                ivNext.setImageResource(R.drawable.ic_redo_disable)
                ivNext.isEnabled = false
            }
            return
        }

        editList.add(media)

        if (editList.size > 1) {
            ivPrevious.setImageResource(R.drawable.ic_undo)
            ivPrevious.isEnabled = true
            ivNext.setImageResource(R.drawable.ic_redo_disable)
            ivNext.isEnabled = false
        }
    }

    private fun onPrevOrNextMedia(prev: Boolean) {
        if (currentMedia == null)
            return

        val existed = editList.indexOfFirst {
            it.path == currentMedia!!.path
        }
        if (existed < 0 || (prev && existed == 0) || (!prev && existed == editList.size - 1))
            return
        var newIndex = existed + 1
        if (prev)
            newIndex = existed - 1
        val media = editList[newIndex]
        if (media.path == null)
            return

        onVideoSetup(media.path!!)
    }

    // TODO: Recycler Initialize

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecycler() {
        videoTrimmerAdapter = MediaAdapter(this, object : MediaAdapter.MediaCallBack {
            override fun onChanged(min: Int, max: Int, id: Int, startThumb: Boolean) {
                if (id == currentMedia?.id!!) {
                    tvStartTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                        currentMedia!!.minTrim!!.toDouble().div(1000)
                    )
                    tvEndTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                        currentMedia!!.maxTrim!!.toDouble().div(1000)
                    )

                    videoHandler.post {
                        if (startThumb)
                            videoView.seekTo(min)
                        else
                            videoView.seekTo(max)
                        Log.d("ranger", "currentPosition" + videoView.currentPosition)
                    }

                    if (currentFragmentTag == TAG_TRIM) {
                        trimFragment.updateTimeFrame(
                            media = currentMedia!!,
                            start = currentMedia!!.minTrim!!.toDouble().div(1000),
                            end = currentMedia!!.maxTrim!!.toDouble().div(1000)
                        )
                    }
                }
            }

            override fun onClick(inside: Boolean) {
//                Log.d("mediaAdapter", "clicked: $inside")
//                if (inside && !isLoading) {
//                    toolsFragment = fragment
//                    supportFragmentManager.beginTransaction().replace(
//                        R.id.container,
//                        toolsFragment!!
//                    ).commit()
//                    toolsFragment!!.setToolCallBack(this@EditorActivity)
//                    toolsFragment!!.setCuttingLabel(videoView.currentPosition)
//                    toolsFragment!!.setMediaList(mediaList)
//
//                } else {
//                    if (toolsFragment != null) supportFragmentManager.beginTransaction().remove(
//                        toolsFragment!!
//                    ).commit()
//                }
            }

        })

        linearLayoutManager = object : LinearLayoutManager(this, HORIZONTAL, false) {
            override fun getPaddingLeft(): Int {
                return screenWidth / 2
            }
            override fun getPaddingEnd(): Int {
                return screenWidth / 2
            }
        }

        recyclerBitmaps.apply {
            adapter = videoTrimmerAdapter
            layoutManager = linearLayoutManager
        }

        scrollListener?.let { recyclerBitmaps.removeOnScrollListener(it) }

        touchListener = View.OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d("recyclerView_event", "up")
                //isScrolling = false
            }
            if (event.action == MotionEvent.ACTION_MOVE) {
                Log.d("recyclerView_event", "move")
                isScrolling = true
                recyclerIsPreparing = false
                if (videoView.isPlaying) {
                    ivPlay.callOnClick()
                }
            }
            else if (event.action == MotionEvent.ACTION_BUTTON_PRESS) {
//                supportFragmentManager.beginTransaction().remove(toolsFragment!!).commit()
            }
            v.performClick()
            return@OnTouchListener false
        }

        scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(
                        "scrolling_stop",
                        "" + scrollingPosition + " offset: " + recyclerView.computeHorizontalScrollOffset()
                    )
                    if (scrollingPosition == 0 && !recyclerView.canScrollHorizontally(-1)) {
                        Log.d("scrolling_stop", "start")
                        recyclerIsPreparing = false
                        initValues()
                    }

                    if ((scrollingPosition == mediaList.size - 1) && recyclerIsPreparing) {
                        Log.d("scrolling_stop", "end")
                        mainScrollHandler.post {
                            recyclerView.smoothScrollToPosition(0)
                        }
                    }

                    // val forward = recyclerView.computeHorizontalScrollOffset() >= ((currentMedia?.endPosition!! - extraItems) * itemWidth)
//                    val forward = (scrollingPosition > currentMedia?.id!!)
                    if (mediaList[scrollingPosition].mediaShape == Media.SPLITTER) {
                        mainScrollHandler.post {
                            if (!isPlaying) {
                                if (isForward) {
                                    currentMedia = mediaList[currentMedia?.id!! + 2]
                                    Log.d("scrolling__stop", "change" + currentMedia?.id!!)
                                    initVideoView()
                                }
                                //recyclerView.scrollToPosition(scrollingPosition + 1)
                                linearLayoutManager.scrollToPositionWithOffset(scrollingPosition + 1, 0)
                                totalPixels = currentMedia!!.computeStartPixel(mediaList)
//                                    recyclerView.scrollToPosition(currentMedia?.startPosition!! + extraItems - 1)
                            }
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mediaList.size == 0) return

                totalPixels += dx
                scrollingPosition = linearLayoutManager.findFirstVisibleItemPosition()
                tvCurrentTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                    videoView.currentPosition.toDouble().div(
                        1000
                    )
                )

                if (recyclerIsPreparing) {
                    if (dx < 0) {
                        mediaList[scrollingPosition].endPosition =
                            recyclerView.computeHorizontalScrollOffset()

                        if (scrollingPosition == 0) {
                            mediaList[scrollingPosition].startPosition = 0
                        }

                        if (scrollingPosition < mediaList.size - 1) {
                            mediaList[scrollingPosition + 1].startPosition =
                                recyclerView.computeHorizontalScrollOffset()
                        }

                        Log.d(
                            "endPosition_1_",
                            "media: " + mediaList[scrollingPosition].id + ":" + mediaList[scrollingPosition].endPosition
                        )
                    }
                    return
                }

                isForward = scrollingPosition > currentMedia?.id!!
                val isBackward = scrollingPosition < currentMedia?.id!!

                if (isBackward) {

                    MainScope().launch(Dispatchers.IO) {
                        //recyclerView.scrollTo(itemWidth * -1, 0)
                        if (!isPlaying && currentMedia?.id!! > 1) {
                            currentMedia = mediaList[currentMedia?.id!! - 2]
                            Log.d("scrolling__move", "change" + currentMedia?.id!!)
                            initVideoView()
                        }
                    }

                }
                else if (scrollingPosition != currentMedia?.id!!) {
                    MainScope().launch(Dispatchers.IO) {
                        //recyclerView.scrollTo(itemWidth, 0)
                        if (!isPlaying && currentMedia?.id!! < mediaList.size - 2 && mediaList[scrollingPosition].thumbList!!.isNotEmpty()) {
                            currentMedia = mediaList[scrollingPosition]
                            Log.d("scrolling__move", "change" + currentMedia?.id!!)
                            initVideoView()
                        }
                    }

                }

                if (!videoView.isPlaying && !isPlaying && isScrolling) {
                    videoHandler.post {

                        currentMedia?.distance = currentMedia?.thumbList!!.size * itemWidth

                        val msec = (((totalPixels.toFloat() -
                                (currentMedia?.computeStartPixel(mediaList)!!).toFloat())) *
                                (currentMedia?.duration!!.toFloat() / currentMedia?.distance!!.toFloat())).toInt()

                        videoView.seekTo(msec)
                    }
                }
            }
        }

        recyclerBitmaps.setOnTouchListener(touchListener)
        recyclerBitmaps.addOnScrollListener(scrollListener!!)
    }

    private fun initValues() {
        scrollingPosition = 0
        totalPixels = 0

        isPlaying = false
        scrollOrdered = false
        if (mediaList.size > 0) {
            currentMedia = mediaList[0]
            videoView.isEnabled = true
            recyclerBitmaps.smoothScrollBy(pastScrollingOffset, 0)
            initVideoView()
            videoView.pause()
            videoHandler.postDelayed({
                videoView.seekTo(pastSeekPosition)
                pastSeekPosition = 0
                pastScrollingOffset = 0
                Log.d("initValues", "current position : " + videoView.currentPosition)
            }, 1000)



            Log.d("initValues", "reset: a: " + mediaList.size)

        }
        else {
            Log.d("initValues", "reset: b: " + mediaList.size)
            currentMedia = null
        }
        ivPlay.setImageResource(R.drawable.ic_play)
        updateDoneButton()
        Log.d("initValues", "reset: c: " + mediaList.size)
        Log.d("initValues", "................................................")
    }

    private fun initVideoView() {
        videoHandler.post {
            tvStartTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                currentMedia!!.minTrim!!.toDouble().div(
                    1000
                )
            )
            tvCurrentTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                currentMedia!!.minTrim!!.toDouble().div(
                    1000
                )
            )
            tvEndTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
                currentMedia!!.maxTrim!!.toDouble().div(
                    1000
                )
            )

            if (currentFragmentTag == TAG_TRIM && currentMedia != null) {
                trimFragment.updateTimeFrame(
                    media = currentMedia!!,
                    start = currentMedia!!.minTrim!!.toDouble().div(1000),
                    end = currentMedia!!.maxTrim!!.toDouble().div(1000)
                )
            }

            videoView.setVideoURI(Uri.parse(currentMedia?.path))
            videoView.setOnPreparedListener { player ->

                if(currentMedia == null){
                    player.release()
                    return@setOnPreparedListener
                }

                mediaPlayer = player

                player.start()
                ivPlay.setImageResource(R.drawable.ic_pause)

                if (!isPlaying) {
                    player.pause()
                    ivPlay.setImageResource(R.drawable.ic_play)
                }

                player.setOnCompletionListener {
                    //recyclerView.scrollTo(itemWidth, 0)
                    scrollOrdered = false
                    if (!isPlaying) {
                        ivPlay.setImageResource(R.drawable.ic_play)
                    }

                    if (currentMedia?.id!! < (mediaList.size - 2)) {
                        currentMedia = mediaList[currentMedia?.id!! + 2]
                        linearLayoutManager.scrollToPositionWithOffset(scrollingPosition + 1, 0)
                        totalPixels = currentMedia?.computeStartPixel(mediaList)!!
                        initVideoView()
                    }
                    else {
                        isPlaying = false
                    }
                }

                ivPlay.setOnClickListener { _ ->
                    Log.d("seeker_position_1", "" + player.currentPosition)
                    if (player.isPlaying) {
                        pausePlayer()
                    }
                    else {
                        resumePlayer()
                    }
                    Log.d("playButton", "currentPosition :" + videoView.currentPosition)
                }

                // Fast Forward
                findViewById<ImageView>(R.id.iv_forward).setOnClickListener {
                    val duration = player.duration
                    var forwardPosition = videoView.currentPosition + 10 * 1000
                    if (forwardPosition > duration)
                        forwardPosition = duration
                    videoView.seekTo(forwardPosition)

//                    if (!player.isPlaying) {
//                        resumePlayer()
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            pausePlayer()
//                        }, 500)
//                    }

                    val offset = currentMedia?.thumbList!!.size * itemWidth * 10 * 1000 / duration.toFloat()
                    Log.e("fast-forward", "////////////// Fast-Forward: $offset, ${currentMedia?.distance!!}, $forwardPosition, $duration")
                    recyclerBitmaps.smoothScrollBy(offset.toInt(), 0)
                }

                // Fast Rewind
                findViewById<ImageView>(R.id.iv_rewind).setOnClickListener {
                    var rewindPosition = videoView.currentPosition - 10 * 1000
                    if (rewindPosition < 0)
                        rewindPosition = 0
                    videoView.seekTo(rewindPosition)

//                    if (!player.isPlaying) {
//                        resumePlayer()
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            pausePlayer()
//                        }, 500)
//                    }

                    var offset = (currentMedia?.thumbList!!.size * itemWidth * 10 * 1000 / player.duration.toFloat()).toInt()
                    Log.e("fast-forward", "////////////// Fast-Rewind: $offset, ${currentMedia?.distance!!}, $rewindPosition, ${player.duration}")
                    val currentOffset = recyclerBitmaps.computeHorizontalScrollOffset()
                    if (currentOffset < offset)
                        offset = currentOffset
                    recyclerBitmaps.smoothScrollBy(-offset, 0)
                }
            }
        }
    }

    private fun pausePlayer() {
        if (mediaPlayer == null)
            return

        isPlaying = false
        ivPlay.setImageResource(R.drawable.ic_play)
        if (mediaPlayer != null && mediaPlayer!!.isPlaying)
            mediaPlayer!!.pause()
        recyclerBitmaps.stopScroll()
    }

    private fun resumePlayer() {
        if (mediaPlayer == null)
            return

        isScrolling = false
        isPlaying = true
        ivPlay.setImageResource(R.drawable.ic_pause)
        mediaPlayer!!.start()
        playingListener()
    }

    private fun playReverse(player: MediaPlayer, interval: Int) {
        val currentPosition = player.currentPosition
        if (currentPosition > 0) {
            player.pause()
            videoView.seekTo(currentPosition - interval)
            player.start()
        }
    }

    private fun updateDoneButton() {
        when(mediaList.size) {
            0 -> {
//                tvDone.visibility = View.GONE
            }
            2 -> {
//                tvDone.text = "Upload"
            }
            else -> {
//                tvDone.text = "Create film"
            }
        }
    }

    private fun playingListener() {
        infoHandler.postDelayed({
            playerInfo()
        }, 50)
    }

    private fun playerInfo() {
        tvCurrentTime.text = MomentsAdapter(this@EditActivity).getTimeDuration(
            videoView.currentPosition.toDouble().div(
                1000
            )
        )
        if (isPlaying) {
            if (!scrollOrdered) {
                scrollOrdered = true
                currentMedia?.distance = currentMedia?.thumbList!!.size * itemWidth

                // Remain Pixels
                val distance = currentMedia?.distance!! + currentMedia?.computeStartPixel(mediaList)!! - totalPixels
                // Remain Time (Milliseconds)
                val remainTime = currentMedia?.duration!!.toFloat() - videoView.currentPosition.toFloat()
                currentMedia?.speed = remainTime / distance.toFloat()

                Log.e("remainTime", "///////// ${currentMedia?.distance!!}, ${currentMedia?.computeStartPixel(mediaList)!!}, ${totalPixels}")

                if (playbackSpeed < 0) {
                    scrollTo(currentMedia?.speed!!, distance)
                }
                else {
                    scrollTo(currentMedia?.speed!!, -distance)
                }
            }

            playingListener()
        }
        else {
            scrollOrdered = false
            ivPlay.setImageResource(R.drawable.ic_play)
//            if (!isPlaying)
        }
    }

    private fun scrollTo(speed: Float, distance: Int) {
        Log.e("scrollTo", "========= $speed = $distance")
        val linearSmoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(this) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return speed
            }
            override fun calculateTimeForScrolling(dx: Int): Int {
                return (speed * dx).toInt()
            }
            override fun calculateTimeForDeceleration(dx: Int): Int {
                return (speed * dx).toInt()
            }
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int
            ): Int {
                return distance
            }
        }
        mainScrollHandler.post {
            linearSmoothScroller.targetPosition = 1 // To the second item
            linearLayoutManager.startSmoothScroll(linearSmoothScroller)
        }
    }

    private fun onVideoSetup() {
        if (video == null)
            return

        showLoading()
        val handler = MediaHandler(this, extraItems, object : MediaHandler.MediaCallBack {
            override fun onListReady(medias: ArrayList<Media>) {
                Log.e("process_in", medias.size.toString())
                Log.e("process_in", "***********************")
                hideLoading()

                mediaList.clear()
                mediaList.addAll(medias)

                for (i in 0 until mediaList.size) {
                    mediaList[i].id = i
                }

                recyclerIsPreparing = true
                videoTrimmerAdapter.differ.submitList(arrayListOf())
                setupRecycler()
                videoTrimmerAdapter.differ.submitList(mediaList)

                if (mediaList.isNotEmpty()) {
                    currentMedia = mediaList[0]
                    mainScrollHandler.post {
                        recyclerBitmaps.smoothScrollToPosition(mediaList.size - 1)
                    }
                    // First Tab
                    shuttleFragment.setMedia(sender = currentMedia)
                }
                else {
                    currentMedia = null
                }
            }
        })
        handler.getMediaList(video!!.path)
    }

    private fun onVideoSetup(videoPath: String) {
        if (videoPath.isEmpty())
            return

        showLoading()
        val handler = MediaHandler(this, extraItems, object : MediaHandler.MediaCallBack {
            override fun onListReady(medias: ArrayList<Media>) {
                Log.e("process_in", medias.size.toString())
                Log.e("process_in", "***********************")
                hideLoading()

                mediaList.clear()
                mediaList.addAll(medias)

                for (i in 0 until mediaList.size) {
                    mediaList[i].id = i
                }

                recyclerIsPreparing = true
                videoTrimmerAdapter.differ.submitList(arrayListOf())
                setupRecycler()
                videoTrimmerAdapter.differ.submitList(mediaList)

                if (mediaList.isNotEmpty()) {
                    currentMedia = mediaList[0]
                    mainScrollHandler.post {
                        recyclerBitmaps.smoothScrollToPosition(mediaList.size - 1)
                    }
                }
                else {
                    currentMedia = null
                }
            }
        })
        handler.getMediaList(videoPath)
    }

    private fun showLoading() {
        isLoading = true
        llExport.isEnabled = false
        progressVideo.visibility = View.VISIBLE
        ivPlay.isEnabled = false
        recyclerBitmaps.isEnabled = false
        recyclerBitmaps.isNestedScrollingEnabled = false
    }

    private fun hideLoading() {
        isLoading = false
        llExport.isEnabled = true
        progressVideo.visibility = View.INVISIBLE
        ivPlay.isEnabled = true
        recyclerBitmaps.isEnabled = true
        recyclerBitmaps.isNestedScrollingEnabled = true
    }

    private fun presentExportDialog() {
        val dialog = ExportDialog(this@EditActivity)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        if (dialog.window != null)
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
    }

    // TODO: Fragments

    private fun initTabBar() {
        llShuttle.background = Utils.getRippleDrawable(getColor(R.color.white), 0)
        llAngle.background = Utils.getRippleDrawable(getColor(R.color.white), 0)
        llTrim.background = Utils.getRippleDrawable(getColor(R.color.white), 0)
        llBrightness.background = Utils.getRippleDrawable(getColor(R.color.white), 0)

        tvShuttle.setTextColor(getColor(R.color.black))
        tvAngle.setTextColor(getColor(R.color.black))
        tvTrim.setTextColor(getColor(R.color.black))
        tvBrightness.setTextColor(getColor(R.color.black))
    }

    private fun addFragmentToStack(fragment: Fragment) {
        if (fragmentList.size > 0 && fragment.tag != null && fragment.tag == fragmentList[fragmentList.size - 1].tag) {
            fragment.onResume()
        }
        val iterator = fragmentList.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            val elementTag = element.tag
            val fragmentTag = fragment.tag
            if (elementTag != null
                && elementTag == fragmentTag
            ) {
                iterator.remove()
            }
        }
        fragmentList.add(fragment)
    }

    private fun presentShuttleFragment() {
        initTabBar()
        currentFragmentTag = TAG_SHUTTLE
        llShuttle.setBackgroundResource(R.drawable.tab_item_selected)
        tvShuttle.setTextColor(getColor(R.color.purple2))

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, shuttleFragment, TAG_SHUTTLE)
            .commit()
        addFragmentToStack(shuttleFragment)
    }

    private fun presentAngleFragment() {
        initTabBar()
        currentFragmentTag = TAG_ANGLE
        llAngle.setBackgroundResource(R.drawable.tab_item_selected)
        tvAngle.setTextColor(getColor(R.color.purple2))

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, angleFragment, TAG_ANGLE)
            .commit()
        addFragmentToStack(angleFragment)
    }

    private fun presentTrimFragment() {
        initTabBar()
        currentFragmentTag = TAG_TRIM
        llTrim.setBackgroundResource(R.drawable.tab_item_selected)
        tvTrim.setTextColor(getColor(R.color.purple2))

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, trimFragment, TAG_TRIM)
            .commit()
        addFragmentToStack(trimFragment)
    }

    private fun presentBrightnessFragment() {
        initTabBar()
        currentFragmentTag = TAG_BRIGHTNESS
        llBrightness.setBackgroundResource(R.drawable.tab_item_selected)
        tvBrightness.setTextColor(getColor(R.color.purple2))

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, brightnessFragment, TAG_BRIGHTNESS)
            .commit()
        addFragmentToStack(brightnessFragment)
    }

    // TODO: TrimFragment.TrimFragmentListener

    override fun onTrimLoading(isLoading: Boolean) {
        if (isLoading)
            showLoading()
        else
            hideLoading()
    }

    override fun onTrimmed(list: ArrayList<Media>) {
        if (list.isEmpty())
            return
        val media = list[0]
        Handler(Looper.getMainLooper()).postDelayed({
            onVideoSetup(media.path ?: "")
        }, 500)
    }

    // TODO: BrightnessFragment.BrightnessFragmentListener

    override fun onBrightnessLoading(isLoading: Boolean) {
        if (isLoading)
            showLoading()
        else
            hideLoading()
    }

    override fun onApplyFilter(list: ArrayList<Media>) {
        if (list.isEmpty())
            return
        val media = list[0]
        Handler(Looper.getMainLooper()).postDelayed({
            onVideoSetup(media.path ?: "")
        }, 500)
    }

    // TODO: ShuttleFragment.ShuttleFragmentListener

    override fun onShuttleLoading(isLoading: Boolean) {
        if (isLoading)
            showLoading()
        else
            hideLoading()
    }

    override fun onApplyShuttle(list: ArrayList<Media>) {
        if (list.isEmpty())
            return
        val media = list[0]
        Handler(Looper.getMainLooper()).postDelayed({
            onVideoSetup(media.path ?: "")
        }, 500)
    }

    private val reverseHandler = Handler()

    override fun onPlaybackSpeed(speed: Float) {
        if (mediaPlayer == null)
            return;

        playbackSpeed = speed

        if (speed == 0f) {
            reverseHandler.removeCallbacks(runnable)

            pausePlayer()
        }
        else {
            if (speed > 0) {
                // Set the playback speed
                val params = PlaybackParams()
                params.speed = speed // 1.5x speed
                mediaPlayer!!.playbackParams = params

                resumePlayer()
            }
            else {
                // Set the playback speed
                val params = PlaybackParams()
                params.speed = 0f // 1.5x speed
                mediaPlayer!!.playbackParams = params

                reverseInterval = if (speed == -0.25f) {
                    25
                } else if (speed == -0.5f) {
                    50
                } else {
                    100
                }

                isScrolling = false
                isPlaying = true
                ivPlay.setImageResource(R.drawable.ic_pause)
                playingListener()

                reverseHandler.removeCallbacks(runnable)
                reverseHandler.postDelayed(runnable, 1000 / 30)
            }
        }
    }

    private var playbackSpeed: Float = 0f
    private var reverseInterval: Int = 0
    private val runnable = object : Runnable {
        override fun run() {
//            Log.e("playReverse", "===================== playReverse")
            playReverse(player = mediaPlayer!!, interval = reverseInterval)
            reverseHandler.postDelayed(this, 1000 / 30) // Update 30 times per second
        }
    }

}
