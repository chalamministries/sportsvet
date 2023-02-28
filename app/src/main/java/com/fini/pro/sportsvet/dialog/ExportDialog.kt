package com.fini.pro.sportsvet.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils

class ExportDialog(
    context: Context
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_export)

        initLayout()
    }

    private fun initLayout() {
        val rlContent = findViewById<RelativeLayout>(R.id.rl_content)
        rlContent.background = Utils.getBackgroundDrawable(
            color = context.getColor(R.color.white),
            radius = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        )

        val tvDone = findViewById<TextView>(R.id.tv_done)
        tvDone.background = Utils.getRippleDrawable(
            color = context.getColor(R.color.purple1),
            radius = context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp)
        )
        tvDone.setOnClickListener {
            dismiss()
        }
    }

}