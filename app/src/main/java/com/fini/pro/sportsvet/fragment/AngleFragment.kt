package com.fini.pro.sportsvet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils

/**
 * A simple [Fragment] subclass.
 * Use the factory method to
 * create an instance of this fragment.
 */
class AngleFragment : Fragment() {

    private lateinit var tvAngle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_angle, container, false)

        initLayout(view = view)

        return view
    }

    private fun initLayout(view: View) {
        tvAngle = view.findViewById(R.id.tv_angle)

        val llSnapshot = view.findViewById<LinearLayout>(R.id.ll_snapshot)
        llSnapshot.background = Utils.getRippleDrawable(requireContext().getColor(R.color.purple1), resources.getDimensionPixelSize(
            com.intuit.sdp.R.dimen._3sdp))
        llSnapshot.setOnClickListener {

        }
    }
}