package com.fini.pro.sportsvet.utils.video.presenter

import android.content.res.Configuration
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.fini.pro.sportsvet.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.videooperations.CallBackOfQuery
import com.fini.pro.sportsvet.utils.video.videooperations.Common
import com.fini.pro.sportsvet.utils.video.videooperations.FFmpegCallBack
import com.fini.pro.sportsvet.utils.video.videooperations.LogMessage
import java.io.*
import kotlin.collections.ArrayList


class TextOverlayFragment(val media: Media, private val label: Int, val callback: CallBack) :
    BottomSheetDialogFragment(), FFmpegCallBack {

    interface CallBack{
        fun onTextAdded(list: ArrayList<String>)
    }

    private lateinit var rootView: View

    private val TAG = "textOverlay"

    private lateinit var confirm: Button
    private lateinit var rangeSlider: RangeSlider
    private lateinit var sliderText: TextView
    private lateinit var originBitmap: Bitmap
    private lateinit var editText: EditText
    private lateinit var image: ImageView
    private lateinit var imvClose: ImageView
    private lateinit var imvIncrease: ImageView
    private lateinit var imvDecrease: ImageView
    private lateinit var tvScale: TextView
    private lateinit var progressBar: ProgressBar
    private var imageWidth = 0
    private var imageHeight = 0
    private var textSize = 0f
    private var isLoading = false

    private var startSec = label / 1000
    private var endSec = 0

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.popup_text_overlay, container, false)
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
        editText = rootView.findViewById(R.id.edit_text)
        imvIncrease = rootView.findViewById(R.id.zoom_in)
        imvDecrease = rootView.findViewById(R.id.zoom_out)
        tvScale = rootView.findViewById(R.id.txv_scale)
        progressBar = rootView.findViewById(R.id.progress_horizontal)
        rangeSlider = rootView.findViewById(R.id.range_slider)
        sliderText = rootView.findViewById(R.id.txv_slider)
        progressBar.visibility = View.INVISIBLE

        confirm.setOnClickListener {

            if(editText.text.isNotEmpty()){
                addText()
            }
        }

        imvClose.setOnClickListener {
            dismiss()
        }

        imvIncrease.setOnClickListener {
            if(textSize < 100){
                textSize ++
                tvScale.text = "" + textSize.toInt() + " sp"
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            }
        }

        imvDecrease.setOnClickListener {
            if(textSize > 1){
                textSize --
                tvScale.text = "" + textSize.toInt() + " sp"
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            }
        }

        editText.setHorizontallyScrolling(true)
        showFrame()

        initSlider()
    }



    private fun textBuilder(textInput: String): String {
        var result = ""

        val lines = textInput.lines()
        var maxWidth = 0
        for(line in lines){
            if(line.trim().length > maxWidth) maxWidth = line.length
        }

        for(l in lines.indices){
            val extra = ((maxWidth - lines[l].length) / 2)
            var editedLine = lines[l].trim()
            for(i in 0..extra){
                editedLine = "  " + editedLine
            }
            result = result + editedLine + "\n"
        }

        return result
    }

    private fun setupImageView(){
        image.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                image.viewTreeObserver.removeOnGlobalLayoutListener(this)
                imageWidth = image.width
                imageHeight = image.height
                Log.d(TAG, "imageView size" + ":" + imageWidth + ", " + imageHeight)

                textSize = ((media.width!! * 25) / (360.toDouble() * imageWidth.toDouble() / media.width!!)).toFloat()
                textSize = 30f
                tvScale.text = "" + textSize.toInt() + " sp"
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

            }
        })
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

    private fun initSlider(){
        val values: ArrayList<Float> = arrayListOf()
        values.add(0f)
        values.add(((media.duration!!.toFloat()) / 1000))
        rangeSlider.values = values
        rangeSlider.valueFrom = 0f
        rangeSlider.valueTo = (media.duration!!.toFloat()) / 1000

        startSec = rangeSlider.values[0].toInt()
        endSec = rangeSlider.values[1].toInt()
        sliderText.text = "Show text from ${startSec}s to ${endSec}s"

        rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            startSec = rangeSlider.values[0].toInt()
            endSec = rangeSlider.values[1].toInt()
            sliderText.text = "Show text from ${startSec}s to ${endSec}s"

        }

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

    private lateinit var output: File

    private var resultList: ArrayList<String> = arrayListOf()

    private fun addText(){
        showLoading(true)
        output = OptiUtils.createVideoFile(requireContext())
        val query = addTextCommand(media, editText.text.toString(), output.path)
        CallBackOfQuery().callQuery(query, this)
    }

    private fun addTextCommand(media: Media, textInput: String, output: String): Array<String> {

        // 6 sec >> 36 sec
        val command: ArrayList<String> = arrayListOf()
        var borderQuery = ":box=1:boxcolor=black@0.5:boxborderw=5"
        borderQuery = ""
        val fontPath = Common.getFileFromAssets(requireActivity(), "arial3.ttf").absolutePath
        val finalSize = ((textSize * media.width!!)/imageWidth).toInt() * 3
        val text = generateText(textBuilder(textInput))

        command.add("-y")
        command.add("-i")
        command.add(media.path!!)
        command.add("-vf")
        command.add("drawtext=textfile=$text:fontfile=$fontPath:x=(w-text_w)/2:y=(h-text_h)/2:fontsize=$finalSize:enable='between(t,$startSec,$endSec)':fontcolor=white")
//        command.add("-c:a")
//        command.add("copy")
        command.add("-preset:v")
        command.add("ultrafast")
        command.add(output)
        return command.toTypedArray()

//        Top left	x=0:y=0	x=10:y=10
//        Top center	x=(w-text_w)/2:y=0	x=(w-text_w)/2:y=10
//        Top right	x=w-tw:y=0	x=w-tw-10:y=10
//        Centered	x=(w-text_w)/2:y=(h-text_h)/2
//        Centered	x=(w-text_w)/2:y=(h-text_h)/2
//        Bottom left	x=0:y=h-th	x=10:y=h-th-10
//        Bottom center	x=(w-text_w)/2:y=h-th	x=(w-text_w)/2:y=h-th-10
//        Bottom right	x=w-tw:y=h-th	x=w-tw-10:y=h-th-10



        //-crf 22
        //-vsync vfr
        //-vsync passthrough
        //"-c:v", "libx264", "-c:a", "copy", "-movflags", "+faststart"

    }

    private fun generateText(textInput: String): String? {
        val list: File
        var writer: Writer? = null
        try {
            list = File.createTempFile("ffmpeg-list", ".txt")
            writer = BufferedWriter(OutputStreamWriter(FileOutputStream(list)))
            writer.write(textInput)
        } catch (e: IOException) {
            e.printStackTrace()
            return "/"
        } finally {
            try {
                if (writer != null) writer.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return list.absolutePath
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()
        showLoading(false)
        resultList.add(output.path)
        callback.onTextAdded(resultList)
        dismiss()
    }

    var failMessage = ""

    override fun process(logMessage: LogMessage) {
        super.process(logMessage)
        Log.d(TAG, logMessage.text)
        failMessage = logMessage.text

    }

    override fun failed() {
        super.failed()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(requireContext(), "Failed: " + failMessage, Toast.LENGTH_LONG).show()
            showLoading(false)
        }
    }

}
