package com.fini.pro.sportsvet.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.fini.pro.sportsvet.activity.GalleryActivity.Companion.LOADER_ID
import com.fini.pro.sportsvet.model.Video
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.getAspectRatio
import com.fini.pro.sportsvet.utils.getNameString
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.fini.pro.sportsvet.R
import java.util.concurrent.TimeUnit

class CameraActivity : AppCompatActivity() {

    private lateinit var llTool: LinearLayout
    private lateinit var llBottom: RelativeLayout
    private lateinit var rlGallery: RelativeLayout
    private lateinit var ivLastVideo: ImageView
    private lateinit var viewFinder: PreviewView
    private lateinit var ivStart: ImageView
    private lateinit var ivSettings: ImageView
    private lateinit var ivSwitch: ImageView
    private lateinit var ivTimer: ImageView
    private lateinit var ivFlash: ImageView
    private lateinit var tvTimer: TextView
    private lateinit var llTimerStatus: LinearLayout
    private lateinit var llSettings: LinearLayout
    private lateinit var llTimer: LinearLayout
    private lateinit var llSwitch: LinearLayout
    private lateinit var llFlash: LinearLayout

    private val cameraCapabilities = mutableListOf<CameraCapability>()

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private var enumerationDeferred: Deferred<Unit>? = null
    private var cameraIndex = 0
    private var qualityIndex = DEFAULT_QUALITY_IDX

    private lateinit var recordingState: VideoRecordEvent
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private var cameraPreview: Preview? = null
    private var camera: Camera? = null

    companion object {
        // default Quality selection if no input from UI
        const val DEFAULT_QUALITY_IDX = 0
        val TAG: String = CameraActivity::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMddHHmmss"
    }

    // Camera UI  states and inputs
    enum class UiState {
        IDLE,       // Not recording, all UI controls are active.
        RECORDING,  // Camera is recording, only display Pause/Resume & Stop button.
        FINALIZED,  // Recording just completes, disable all RECORDING UI controls.
//        RECOVERY    // For future use.
    }

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(this@CameraActivity).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(this@CameraActivity, camSelector)
                            QualitySelector
                                .getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
                                        .contains(quality)
                                }.also {
                                    cameraCapabilities.add(CameraCapability(camSelector, it))
                                }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        Utils.showStatusBar(this, lightStatusBar = true, true)

        initLayout()
        updateLayout(resources.configuration.orientation)

        lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            bindCaptureUsecase()
        }

        LoaderManager.getInstance(this).initLoader(
            LOADER_ID,
            null,
            loaderCallbacks
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        Log.e(TAG, "............. onConfigurationChanged")
        updateLayout(newConfig.orientation)
    }

    // TODO: My Method

    @SuppressLint("ClickableViewAccessibility")
    private fun initLayout() {
        llTool = findViewById(R.id.ll_tool)
        llBottom = findViewById(R.id.ll_bottom)
        rlGallery = findViewById(R.id.rl_gallery)
        rlGallery.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
        ivLastVideo = findViewById(R.id.iv_last_video)

        // Camera Preview
        viewFinder = findViewById(R.id.viewFinder)
//        viewFinder.afterMeasured {
//            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
//                .createPoint(.5f, .5f)
//            try {
//                val autoFocusAction = FocusMeteringAction.Builder(
//                    autoFocusPoint,
//                    FocusMeteringAction.FLAG_AF
//                ).apply {
//                    //start auto-focusing after 2 seconds
//                    setAutoCancelDuration(2, TimeUnit.SECONDS)
//                }.build()
//                camera?.cameraControl?.startFocusAndMetering(autoFocusAction)
//            } catch (e: CameraInfoUnavailableException) {
//                Log.d("ERROR", "cannot access camera", e)
//            }
//
//            viewFinder.setOnTouchListener { _, event ->
//                return@setOnTouchListener when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        true
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
//                            viewFinder.width.toFloat(), viewFinder.height.toFloat()
//                        )
//                        val autoFocusPoint1 = factory.createPoint(event.x, event.y)
//                        try {
//                            camera?.cameraControl?.startFocusAndMetering(
//                                FocusMeteringAction.Builder(
//                                    autoFocusPoint1,
//                                    FocusMeteringAction.FLAG_AF
//                                ).apply {
//                                    //focus only when the user tap the preview
//                                    disableAutoCancel()
//                                }.build()
//                            )
//                        } catch (e: CameraInfoUnavailableException) {
//                            Log.d("ERROR", "cannot access camera", e)
//                        }
//                        true
//                    }
//                    else -> false // Unhandled event.
//                }
//            }
//        }

        // Camera Controls
        ivStart = findViewById(R.id.iv_start)
        ivStart.setOnClickListener {
            if (!ivStart.isSelected) {
                if (!this@CameraActivity::recordingState.isInitialized
                    || recordingState is VideoRecordEvent.Finalize) {
                    enableUI(false)  // Our eventListener will turn on the Recording UI.
                    startRecording()
                }
//                else {
//                    when (recordingState) {
//                        is VideoRecordEvent.Start -> {
//                            currentRecording?.pause()
//                            ivStart.isSelected = true
//                        }
//                        is VideoRecordEvent.Pause -> currentRecording?.resume()
//                        is VideoRecordEvent.Resume -> currentRecording?.pause()
//                        else -> throw IllegalStateException("recordingState in unknown state")
//                    }
//                }
            }
            else {
                // stopping: hide it after getting a click before we go to viewing fragment
                ivStart.isSelected = false
                if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
                    return@setOnClickListener
                }

                val recording = currentRecording
                if (recording != null) {
                    recording.stop()
                    currentRecording = null
                }
                ivStart.setImageResource(R.drawable.camera_start)
                llTimerStatus.visibility = View.INVISIBLE
                enableUI(true)

                runOnUiThread {
                    LoaderManager.getInstance(this).restartLoader(
                        LOADER_ID,
                        null,
                        loaderCallbacks
                    )
                }
            }
        }

        val radius = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
        ivSettings = findViewById(R.id.iv_settings)
        ivSettings.background = Utils.getRippleDrawable(getColor(R.color.gray1), radius)
        ivSettings.setOnClickListener {

        }
        llSettings = findViewById(R.id.ll_settings)

        ivTimer = findViewById(R.id.iv_timer)
        ivTimer.background = Utils.getRippleDrawable(getColor(R.color.gray1), radius)
        ivTimer.setOnClickListener {
            if (llTimerStatus.visibility == View.VISIBLE) {
                llTimerStatus.visibility = View.INVISIBLE
            }
            else if (currentRecording != null) {
                llTimerStatus.visibility = View.VISIBLE
            }
        }
        llTimer = findViewById(R.id.ll_timer)

        ivSwitch = findViewById(R.id.iv_switch)
        ivSwitch.background = Utils.getRippleDrawable(getColor(R.color.gray1), radius)
        ivSwitch.setOnClickListener {
            ivFlash.isSelected = false
            cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
            // camera device change is in effect instantly:
            //   - reset quality selection
            //   - restart preview
            qualityIndex = DEFAULT_QUALITY_IDX
            enableUI(false)
            lifecycleScope.launch {
                bindCaptureUsecase()
            }
        }
        llSwitch = findViewById(R.id.ll_camera)

        ivFlash = findViewById(R.id.iv_flash)
        ivFlash.background = Utils.getRippleDrawable(getColor(R.color.gray1), radius)
        ivFlash.setOnClickListener {
            if (camera != null) {
                ivFlash.isSelected = !ivFlash.isSelected
                camera!!.cameraControl.enableTorch(ivFlash.isSelected)
            }
            if (ivFlash.isSelected) {
                ivFlash.setImageResource(R.drawable.camera_flash)
            }
            else {
                ivFlash.setImageResource(R.drawable.camera_flash_off)
            }
        }
        llFlash = findViewById(R.id.ll_flash)

        tvTimer = findViewById(R.id.tv_timer)
        llTimerStatus = findViewById(R.id.ll_timer_status)
        llTimerStatus.visibility = View.INVISIBLE
    }

    private fun updateLayout(orientation: Int) {
        val dimen10 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        val dimen20 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._20sdp)
        val dimen30 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
        val dimen40 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp)
        val dimen45 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._45sdp)
        val dimen55 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._55sdp)
        val dimen60 = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._60sdp)

        val llToolLayout = llTool.layoutParams as RelativeLayout.LayoutParams
        val toolItemLayout: LinearLayout.LayoutParams
        val llBottomLayout: RelativeLayout.LayoutParams
        val ivStartLayout: RelativeLayout.LayoutParams
        val llTimerStatusLayout: RelativeLayout.LayoutParams
        val rlGalleryLayout: RelativeLayout.LayoutParams

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            llToolLayout.topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp)

            toolItemLayout = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            toolItemLayout.weight = 1f

            llBottomLayout = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._120sdp))
            llBottomLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

            ivStartLayout = RelativeLayout.LayoutParams(dimen60, dimen60)
            ivStartLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            ivStartLayout.addRule(RelativeLayout.CENTER_HORIZONTAL)
            ivStartLayout.bottomMargin = dimen45

            llTimerStatusLayout = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            llTimerStatusLayout.bottomMargin = dimen60
            llTimerStatusLayout.marginStart = dimen20
            llTimerStatusLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

            rlGalleryLayout = RelativeLayout.LayoutParams(dimen40, dimen40)
            rlGalleryLayout.bottomMargin = dimen55
            rlGalleryLayout.marginEnd = dimen20
            rlGalleryLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            rlGalleryLayout.addRule(RelativeLayout.ALIGN_PARENT_END)
        }
        else { // Landscape
            llToolLayout.topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)

            toolItemLayout = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            toolItemLayout.gravity = Gravity.CENTER
            toolItemLayout.marginStart = dimen20
            toolItemLayout.marginEnd = dimen20

            llBottomLayout = RelativeLayout.LayoutParams(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._100sdp), RelativeLayout.LayoutParams.MATCH_PARENT)
            llBottomLayout.addRule(RelativeLayout.ALIGN_PARENT_END)

            ivStartLayout = RelativeLayout.LayoutParams(dimen60, dimen60)
            ivStartLayout.addRule(RelativeLayout.ALIGN_PARENT_END)
            ivStartLayout.addRule(RelativeLayout.CENTER_VERTICAL)
            ivStartLayout.marginEnd = dimen30

            llTimerStatusLayout = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            llTimerStatusLayout.bottomMargin = dimen30
            llTimerStatusLayout.marginStart = dimen10
            llTimerStatusLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

            rlGalleryLayout = RelativeLayout.LayoutParams(dimen40, dimen40)
            rlGalleryLayout.topMargin = dimen30
            rlGalleryLayout.marginEnd = dimen40
            rlGalleryLayout.addRule(RelativeLayout.ALIGN_PARENT_END)
        }

        llSettings.layoutParams = toolItemLayout
        llTimer.layoutParams = toolItemLayout
        llSwitch.layoutParams = toolItemLayout
        llFlash.layoutParams = toolItemLayout

        llBottom.layoutParams = llBottomLayout

        ivStart.layoutParams = ivStartLayout

        llTimerStatus.layoutParams = llTimerStatusLayout

        rlGallery.layoutParams = rlGalleryLayout
    }

    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int) : CameraSelector {
        if (cameraCapabilities.size == 0) {
            Log.i(TAG, "Error: This device does not have any camera, bailing out")
            finish()
        }
        return (cameraCapabilities[idx % cameraCapabilities.size].camSelector)
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status)
            recordingState = event

        updateUI(event)

//        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
//            lifecycleScope.launch {
//                navController.navigate(
//                    CaptureFragmentDirections.actionCaptureToVideoViewer(
//                        event.outputResults.outputUri
//                    )
//                )
//            }
//        }
    }

    /**
     * UpdateUI according to CameraX VideoRecordEvent type:
     *   - user starts capture.
     *   - this app disables all UI selections.
     *   - this app enables capture run-time UI (pause/resume/stop).
     *   - user controls recording with run-time UI, eventually tap "stop" to end.
     *   - this app informs CameraX recording to stop with recording.stop() (or recording.close()).
     *   - CameraX notify this app that the recording is indeed stopped, with the Finalize event.
     *   - this app starts VideoViewer fragment to view the captured result.
     */
    private fun updateUI(event: VideoRecordEvent) {
//        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString()
//        else event.getNameString()

        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> {
                showUI(UiState.RECORDING, event.getNameString())
            }
            is VideoRecordEvent.Finalize-> {
                showUI(UiState.FINALIZED, event.getNameString())
            }
            is VideoRecordEvent.Pause -> {

            }
            is VideoRecordEvent.Resume -> {

            }
        }

        val stats = event.recordingStats
//        val size = stats.numBytesRecorded / 1000
        val time = TimeUnit.NANOSECONDS.toSeconds(stats.recordedDurationNanos)
//        var text = "${state}: recorded ${size}KB, in ${time}second"
//        if (event is VideoRecordEvent.Finalize)
//            text = "${text}\nFile saved to: ${event.outputResults.outputUri}"

//        Log.i(TAG, "recording event: $text")
        updateTimeLabel(time = time)
    }

    private fun updateTimeLabel(time: Long) {
        val seconds = time % 60
        var minutes = time / 60
        val hours = minutes / 60
        minutes %= 60
        val timeLabel = "${if (hours > 9) hours else "0$hours"}:${if (minutes > 9) minutes else "0$minutes"}:${if (seconds > 9) seconds else "0$seconds"}"
        tvTimer.text = timeLabel
    }

    /**
     * initialize UI for recording:
     *  - at recording: hide audio, qualitySelection,change camera UI; enable stop button
     *  - otherwise: show all except the stop button
     */
    private fun showUI(state: UiState, status: String = "idle") {
        when(state) {
            UiState.IDLE -> {
                llSettings.visibility = View.VISIBLE
//                llTimer.visibility = View.VISIBLE
                llSwitch.visibility = View.VISIBLE
//                llFlash.visibility = View.VISIBLE
                llTimerStatus.visibility = View.INVISIBLE

                ivStart.setImageResource(R.drawable.camera_start)
                ivStart.isSelected = false
            }
            UiState.RECORDING -> {
                llSettings.visibility = View.INVISIBLE
//                llTimer.visibility = View.INVISIBLE
                llSwitch.visibility = View.INVISIBLE
//                llFlash.visibility = View.INVISIBLE
                llTimerStatus.visibility = View.VISIBLE

                ivStart.setImageResource(R.drawable.camera_play)
                ivStart.isSelected = true
            }
            UiState.FINALIZED -> {
                llSettings.visibility = View.VISIBLE
//                llTimer.visibility = View.VISIBLE
                llSwitch.visibility = View.VISIBLE
//                llFlash.visibility = View.VISIBLE
                llTimerStatus.visibility = View.INVISIBLE

                ivStart.setImageResource(R.drawable.camera_start)
                ivStart.isSelected = false
            }
//            else -> {
//                val errorMsg = "Error: showUI($state) is not supported"
//                Log.e(TAG, errorMsg)
//                return
//            }
        }
        Log.e(TAG, status)
    }

    /**
     * Enable/disable UI:
     *    User could select the capture parameters when recording is not in session
     *    Once recording is started, need to disable able UI to avoid conflict.
     */
    private fun enableUI(enable: Boolean) {
        arrayOf(ivSettings, ivSwitch).forEach {
            it.isEnabled = enable
        }
        // disable the camera button if no device to switch
        if (cameraCapabilities.size <= 1) {
            ivSwitch.isEnabled = false
        }
    }

    /**
     *   Always bind preview + video capture use case combinations in this sample
     *   (VideoCapture can work on its own). The function should always execute on
     *   the main thread.
     */
    private suspend fun bindCaptureUsecase() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        val cameraSelector = getCameraSelector(cameraIndex)

        // create the user required QualitySelector (video resolution): we know this is
        // supported, a valid qualitySelector will be created.
        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

//        viewFinder.updateLayoutParams<ConstraintLayout.LayoutParams> {
//            val orientation = resources.configuration.orientation
//            dimensionRatio = getAspectRatioString(
//                quality,
//                (orientation == Configuration.ORIENTATION_PORTRAIT)
//            )
//        }

        cameraPreview = Preview.Builder()
            .setTargetAspectRatio(getAspectRatio(quality))
            .build().apply {
                setSurfaceProvider(viewFinder.surfaceProvider)
            }

        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                videoCapture,
                cameraPreview
            )
        }
        catch (exc: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            Log.e(TAG, "Use case binding failed", exc)
        }
        enableUI(true)
    }

    /**
     * Kick start the video recording
     *   - config Recorder to capture to MediaStoreOutput
     *   - register RecordEvent Listener
     *   - apply audio request from user
     *   - start recording!
     * After this function, user could start/pause/resume/stop recording and application listens
     * to VideoRecordEvent for the current recording status.
     */
    @SuppressLint("MissingPermission")
    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "SportsVet-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture.output
            .prepareRecording(this, mediaStoreOutput)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }

    // TODO: Gallery Management

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            if (id == LOADER_ID) {
                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.ALBUM,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DISPLAY_NAME,
                )
                val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"
                return CursorLoader(this@CameraActivity, uri, projection, null, null, sortOrder)
            }
            return Loader(this@CameraActivity)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data == null)
                return

//            var index = 0
            while (data.moveToNext()) {// && index < Int.MAX_VALUE) {
                val idIndex = data.getColumnIndex(MediaStore.Video.Media._ID)
                val id: Long = data.getLong(if (idIndex >= 0) idIndex else 0)

                val addedIndex = data.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val added = data.getLong(if (addedIndex >= 0) addedIndex else 0)

                val pathIndex = data.getColumnIndex(MediaStore.Video.Media.DATA)
                val path = data.getString(pathIndex)

                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val video = Video(uri = contentUri, path = path, added = added)

                runOnUiThread {
                    ivLastVideo.setImageBitmap(video.thumbnail(this@CameraActivity))
                }

//                index++
                break
            }
            data.moveToPosition(-1) // Restore cursor back to the beginning
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {

        }

    }

}