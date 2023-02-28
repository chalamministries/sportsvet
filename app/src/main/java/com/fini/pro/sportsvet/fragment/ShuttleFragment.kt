package com.fini.pro.sportsvet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils
import com.fini.pro.sportsvet.utils.dialView.DialView
import com.fini.pro.sportsvet.utils.dialView.DialView.OnDialValueChangeListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
class ShuttleFragment : Fragment() {

    private lateinit var dialView: DialView
    private lateinit var valueLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shuttle, container, false)

        initLayout(view = view)

        return view;
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
            }
        })

        // Apply Button
        val applyButton = view.findViewById<TextView>(R.id.tv_apply)
        applyButton.background = Utils.getRippleDrawable(requireContext().getColor(R.color.purple1), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._3sdp))
        applyButton.setOnClickListener {

        }
    }
}