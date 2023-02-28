package com.fini.pro.sportsvet.utils.video.presenter

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.video.model.Media
import com.fini.pro.sportsvet.utils.video.videooperations.Common
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.fini.pro.sportsvet.utils.video.videooperations.CallBackOfQuery
import com.fini.pro.sportsvet.utils.video.videooperations.FFmpegCallBack
import com.fini.pro.sportsvet.utils.video.videooperations.LogMessage
import java.io.File
import java.lang.StringBuilder

class TextFragment(val media: Media, val callback: TextCallBack, private val label: Int) : BottomSheetDialogFragment(),
    FFmpegCallBack {

    interface TextCallBack{
        fun onTextAdded(list: ArrayList<String>)
        fun loading(isLoading: Boolean)
    }

    private var startSec = label / 1000
    private var endSec = 0
    init {
        endSec = startSec + 5
    }

    private lateinit var rootView: View
    private lateinit var close: ImageView
    private lateinit var confirm: ImageView
    private lateinit var etText: EditText
    var textSize = 0
    var maxWidth = 22
    var maxLines = 30

    // dismiss if rotate
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_add_text, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onViewCreated(view, savedInstanceState)

        close = view.findViewById(R.id.iv_close)
        confirm = view.findViewById(R.id.iv_done)
        etText = view.findViewById(R.id.etText)


        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        close.setOnClickListener {
            dismiss()
        }

        confirm.setOnClickListener {
            if(etText.text.isEmpty()){
                etText.error = "Enter valid text !!"
                return@setOnClickListener
            }

            addText()

        }

        textSize = (media.width!! * 25) / 360

    }

    private lateinit var output: File
    private var tagName = "TextOverlay"

    private var resultList: ArrayList<String> = arrayListOf()

    private fun addText(){
        output = OptiUtils.createVideoFile(requireContext())
        val testString = etText.text.toString()
        val query = addTextCommand(media, testString, output.path)

        CallBackOfQuery().callQuery(query, this)
        callback.loading(true)
    }

    private fun addTextCommand(media: Media, textInput: String, output: String): Array<String> {
        val command: ArrayList<String> = arrayListOf()
        val borderQuery = ":box=1:boxcolor=black@0.5:boxborderw=5"
        val fontPath = Common.getFileFromAssets(requireActivity(), "arial3.ttf").absolutePath

//        Top left	x=0:y=0	x=10:y=10
//        Top center	x=(w-text_w)/2:y=0	x=(w-text_w)/2:y=10
//        Top right	x=w-tw:y=0	x=w-tw-10:y=10
//        Centered	x=(w-text_w)/2:y=(h-text_h)/2
//        Bottom left	x=0:y=h-th	x=10:y=h-th-10
//        Bottom center	x=(w-text_w)/2:y=h-th	x=(w-text_w)/2:y=h-th-10
//        Bottom right	x=w-tw:y=h-th	x=w-tw-10:y=h-th-10

//        Log.d("text_command", "size: " + media.width + ", " + media.height)
//        Log.d("text_command", "text size: " + textSize)
        Log.d("text_command", "text size: " + label)
        Log.d("text_command", "text size: " + startSec)
        Log.d("text_command", "text size: " + endSec)
        command.add("-y")
        command.add("-i")
        command.add(media.path!!)
        command.add("-vf")
        //command.add("drawtext=text=$textInput:x=(w-text_w)/2:y=(h-text_h)/2:fontsize=24:fontcolor=white${borderQuery.trim()}")
        command.add("drawtext=text=$textInput:fontfile=$fontPath:x=(w-text_w)/2:y=(h-text_h)/2:fontsize=$textSize:enable='between(t,$startSec,$endSec)':fontcolor=white${borderQuery.trim()}")
//        command.add("-c:a")
//        command.add("copy")
//        command.add("-preset")
//        command.add("ultrafast")


        command.add("-c:v")
        command.add("libx264")
        command.add("-c:a")
        command.add("copy")
        command.add("-movflags")
        command.add("+faststart")

        command.add(output)
        //ffmpeg -i input.mp4 -vf "drawtext=fontfile=/path/to/font.ttf:text='Stack Overflow':fontcolor=white:fontsize=24:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2:enable='between(t,5,10)'" -codec:a copy output.mp4
        return command.toTypedArray()
        //return cmd

    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun success() {
        super.success()
        resultList.add(output.path)
        callback.onTextAdded(resultList)
        dismiss()
    }

    var failMessage = ""
    override fun process(logMessage: LogMessage) {
        super.process(logMessage)
        failMessage = logMessage.text
        close.isEnabled = false
        confirm.isEnabled = false

    }

    override fun failed() {
        super.failed()
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(requireContext(), "Failed: " + failMessage, Toast.LENGTH_LONG).show()
            Log.d(tagName, failMessage)
            callback.loading(false)
            close.isEnabled = true
            confirm.isEnabled = true
        }
    }


    private fun multiLines(text: String, width: Int, height: Int): String{
        Log.d(tagName, text)

        Log.d("text_command", "max width: " + maxWidth)
        val spaces: ArrayList<Int> = arrayListOf()
        val words: ArrayList<String> = arrayListOf()
        val lines : ArrayList<String> = arrayListOf()

        val array = text.toCharArray()
        for(i in 0 until array.size){
            if(array[i].toString() == " ") spaces.add(i)
        }

        words.add(text.substring(0 , spaces[0]))
        Log.d(tagName, words[words.size - 1])
        for(i in 0 until spaces.size){
            if(i+1 < spaces.size) {
                words.add(text.substring(spaces[i] , spaces[i+1]))
                Log.d(tagName, words[words.size - 1])
            }
        }

        words.add(text.substring(spaces[spaces.size - 1] , text.length - 1))
        Log.d(tagName, words[words.size - 1])

        var newLine = ""
        for(i in 0 until words.size){

            if((newLine + words[i]).toCharArray().size <= maxWidth){
                newLine = newLine + words[i]

            }else{
                lines.add(newLine)
                newLine = words[i]
            }
        }

        if(!lines.contains(newLine)) lines.add(newLine)

        Log.d(tagName, lines.size.toString())

        val stringBuilder = StringBuilder()

        for(i in 0 until lines.size){
            if(i < maxLines) stringBuilder.append(lines[i].trim()  + "\n")
        }

        Log.d(tagName, stringBuilder.toString())
        return stringBuilder.toString()
    }
}
