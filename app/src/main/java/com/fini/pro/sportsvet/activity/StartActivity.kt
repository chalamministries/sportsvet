package com.fini.pro.sportsvet.activity

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Utils.showStatusBar(this, lightStatusBar = true, true)

        initLayout()
    }

    // TODO: My Method

    private fun initLayout() {
        val ivLogo = findViewById<ImageView>(R.id.iv_logo)
        ivLogo.alpha = 0f
        ivLogo.animate().setDuration(1500).alpha(1f).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                runOnUiThread {
                    presentMain()
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    private fun presentMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}