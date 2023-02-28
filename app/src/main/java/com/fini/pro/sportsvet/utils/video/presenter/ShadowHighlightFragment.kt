package com.fini.pro.sportsvet.utils.video.presenter

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fini.pro.sportsvet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.fini.pro.sportsvet.utils.video.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ShadowHighlightFragment(val media: Media, val label: Int, val callBack: CallBack) : BottomSheetDialogFragment(){

    interface CallBack{
        fun onShadowAdded(list: ArrayList<String>)
        fun onProcessing(isLoading: Boolean)
    }

    private lateinit var rootView: View

    private val TAG = "ShadowHighligh"

    private lateinit var confirm: Button
    private lateinit var originBitmap: Bitmap
    private lateinit var resultedBitmap: Bitmap
    private lateinit var image: ImageView
    private lateinit var imvClose: ImageView
    private lateinit var imvIncrease: ImageView
    private lateinit var imvDecrease: ImageView
    private lateinit var tvScale: TextView
    private lateinit var progressBar: ProgressBar
    private var imageWidth = 0
    private var imageHeight = 0

    private var circleDiameter = 0
    private var circleX = 0f
    private var circleY = 0f
    private var scale = 1
    private var x = 0f
    private var y = 0f
    private var isLoading = false
    private var framePath = ""


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_shadow, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onViewCreated(view, savedInstanceState)
        OptiUtils.resizeHeight(dialog!!)

        imvClose = rootView.findViewById(R.id.imv_close)
        confirm = rootView.findViewById(R.id.btnShow)
        image = rootView.findViewById(R.id.imv_frame)
        imvIncrease = rootView.findViewById(R.id.zoom_in)
        imvDecrease = rootView.findViewById(R.id.zoom_out)
        tvScale = rootView.findViewById(R.id.txv_scale)
        progressBar = rootView.findViewById(R.id.progress_horizontal)
        progressBar.visibility = View.INVISIBLE

        confirm.setOnClickListener {
            saveBitmap(resultedBitmap)
            startProcessing()
        }

        imvClose.setOnClickListener {
            dismiss()
        }

        imvIncrease.setOnClickListener {
            if(scale < 10){
                scale ++
                tvScale.text = scale.toString() + "X"
                circleX = x - ((circleDiameter * scale) / 2)
                circleY = y - ((circleDiameter * scale) / 2)
                drawMask()
            }
        }

        imvDecrease.setOnClickListener {
            if(scale > 1){
                scale --
                tvScale.text = scale.toString() + "X"
                circleX = x - ((circleDiameter * scale) / 2)
                circleY = y - ((circleDiameter * scale) / 2)
                drawMask()
            }
        }

        showFrame()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupImageView(){
        image.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                imageWidth = image.width
                imageHeight = image.height
                Log.d(TAG, "imageView size" + ":" + imageWidth + ", " + imageHeight)

                // show mask
                circleDiameter = (originBitmap.width / 5)
                x = (originBitmap.width / 2).toFloat()
                y = (originBitmap.height / 2).toFloat()
                circleX =
                    (originBitmap.width / 2).toFloat() - ((circleDiameter * scale) / 2).toFloat()
                circleY =
                    (originBitmap.height / 2).toFloat() - ((circleDiameter * scale) / 2).toFloat()
                drawMask()

            }
        })

        image.setOnTouchListener { _, event ->

            if(event.action == MotionEvent.ACTION_DOWN && !isLoading){
                if (event.x.toInt() in 1 until imageWidth && event.y.toInt() in 1 until imageHeight) {
                    x = event.x * originBitmap.width.toFloat() / imageWidth.toFloat()
                    y = event.y * originBitmap.height.toFloat() / imageHeight.toFloat()
                    //Log.d(tag, "" + event.action + ":" + x + ", " + y)

                    circleX = x - ((circleDiameter * scale) / 2)
                    circleY = y - ((circleDiameter * scale) / 2)
                    drawMask()
                }
            }

            true
        }

    }

    private fun drawMask() {

        image.setImageDrawable(null)
        resultedBitmap = Bitmap.createBitmap(
            originBitmap.width,
            originBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val tempCanvas = Canvas(resultedBitmap)
        tempCanvas.drawBitmap(originBitmap, 0f, 0f, null)

        tempCanvas.drawBitmap(mask(), 0f, 0f, null)

        Canvas().drawBitmap(resultedBitmap, 0f, 0f, Paint())

        image.setImageBitmap(resultedBitmap)

    }

    private fun mask(): Bitmap {

        val transBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(resources, R.drawable.trans_mask),
            originBitmap.width,
            originBitmap.height,
            false
        )

        val circleBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.circle_mask
            ), circleDiameter * scale, circleDiameter * scale, false
        )

        val result = Bitmap.createBitmap(
            transBitmap.width,
            transBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val tempCanvas = Canvas(result)

        tempCanvas.drawBitmap(transBitmap, 0f, 0f, null)
        tempCanvas.drawBitmap(circleBitmap, circleX, circleY, null)

        //Draw result after performing masking
        Canvas().drawBitmap(result, 0f, 0f, Paint())
        //reverse(mask)

        //return result
        return replaceColor(result)!!
    }

    private fun replaceColor(src: Bitmap?): Bitmap? {
        if (src == null) {
            return null
        }
        // Source image size
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        for (x in pixels.indices) {
            if (pixels[x] == Color.BLACK) pixels[x] = Color.TRANSPARENT

        }
        // create result bitmap output
        val result = Bitmap.createBitmap(width, height, src.config)
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    @Throws(IOException::class)

    private fun saveBitmap(bmp: Bitmap){
        MainScope().launch(Dispatchers.IO) {
            val bytes = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val file = OptiUtils.createImageFile(requireContext())
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
            framePath = file.path
        }

    }

    private fun showFrame() {
        val uri = Uri.fromFile(File(media.path!!))
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireActivity(), uri)
        originBitmap = retriever.getFrameAtTime((label * 1000).toLong())!!

        Log.d(TAG, "bitmap :" + originBitmap.width + ", " + originBitmap.height)
        image.setImageBitmap(originBitmap)
        resizeImageView(image, originBitmap)
    }

    private fun resizeImageView(view: ImageView, bitmap: Bitmap){
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                view.layoutParams.height = ((bitmap.height * view.width) / bitmap.width)
                view.requestLayout()
                setupImageView()
            }
        })
    }

    private fun showLoading(loading: Boolean){
        isLoading = loading
        callBack.onProcessing(isLoading)

        if(loading){
            progressBar.visibility = View.VISIBLE
            dialog!!.setCanceledOnTouchOutside(false)
            dialog!!.setCancelable(false)
            imvClose.isEnabled = false
            imvIncrease.isEnabled = false
            imvDecrease.isEnabled = false
            confirm.isEnabled = false

        }else{
            progressBar.visibility = View.INVISIBLE
            dialog!!.setCanceledOnTouchOutside(true)
            dialog!!.setCancelable(true)
            imvClose.isEnabled = true
            imvIncrease.isEnabled = true
            imvDecrease.isEnabled = true
            confirm.isEnabled = true
        }
    }

    private fun startProcessing(){
        showLoading(true)

        Splitter(requireContext(), label, object : Splitter.SplitterCallBack {

            override fun onVideoSplitted(list: ArrayList<String>) {

                ImageToVideo(requireContext(), object : ImageToVideo.CallBack {
                    override fun onCreatingVideo(path: String) {
                        list.add(1, path)
                        val mediaList: ArrayList<Media> = arrayListOf()
                        for(i in 0 until list.size){
                            val media2 = Media()
                            media2.hasAudio = media.hasAudio
                            media2.width = media.width
                            media2.height = media.height
                            media2.thumbList = media.thumbList
                            media2.distance = media.distance
                            media2.duration = media.duration
                            media2.startPosition = media.startPosition
                            media2.endPosition = media.endPosition
                            media2.maxTrim = media.maxTrim
                            media2.minTrim = media.minTrim
                            media2.speed = media.speed
                            media2.mediaShape = media.mediaShape
                            media2.audioPath = media.audioPath
                            media2.path = list[i]
                            media2.id = i
                            mediaList.add(media2)
                        }

                        ShadowMerger(requireContext(), object : ProcedureCallBack{
                            override fun onTrimmed(list: ArrayList<Media>) {
                            }

                            override fun onMerged(file: File) {
                                val pathList : ArrayList<String> = arrayListOf()
                                pathList.add(file.path)
                                callBack.onShadowAdded(pathList)
                                dismiss()
                            }

                            override fun onResized(list: ArrayList<Media>) {
                            }

                            override fun onFailed(message: String) {
                            }

                            override fun onLoading(isLoading: Boolean) {
                                showLoading(isLoading)
                            }

                        }).combineVideosProcess(mediaList)
                    }

                    override fun loading(isLoading: Boolean) {
                        showLoading(isLoading)
                    }

                }).apply {
                    rotate = originBitmap.height < originBitmap.width
                    createVideoByImages(framePath, 5, media.width!!, media.height!!)
                }
            }

            override fun loading(isLoading: Boolean) {
                showLoading(isLoading)
            }

        }).split(media)
    }

    private fun startProcessing1(){
        showLoading(true)

        Splitter(requireContext(), label, object : Splitter.SplitterCallBack {

            override fun onVideoSplitted(list: ArrayList<String>) {

                ImageVideoMerger(requireContext(), object : ImageVideoMerger.CallBack {

                    override fun onCreatingVideo(path: String) {
                        showLoading(false)
                        val resList: ArrayList<String> = arrayListOf()
                        resList.add(path)
                        callBack.onShadowAdded(resList)
                        dismiss()

                    }

                    override fun loading(isLoading: Boolean) {
                        showLoading(isLoading)
                    }

                }).mergeImageWithVideo(list[0], framePath)
            }

            override fun loading(isLoading: Boolean) {
                showLoading(isLoading)
            }

        }).split(media)
    }

}
