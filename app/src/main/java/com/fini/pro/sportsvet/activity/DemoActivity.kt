package com.fini.pro.sportsvet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.dialView.DialView

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        initLayout()
    }

    // TODO: My Method

    private fun initLayout() {
        val valueLabel = findViewById<TextView>(R.id.tv_value)
        val dialView = findViewById<DialView>(R.id.dial_view)
        dialView.setOnDialValueChangeListener(object : DialView.OnDialValueChangeListener {
            override fun onDialValueChanged(value: String?, maxValue: Int) {
                if (value == null)
                    return
                val intValue = value.toInt()
                valueLabel.text = "$intValue"
            }
        })
    }
}