package com.fini.pro.sportsvet.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.utils.Utils

class MainActivity : AppCompatActivity() {

    enum class ActionType {
        NONE,
        CAMERA,
        GALLERY,
    }

    private var actionType = ActionType.NONE

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (false == result[Manifest.permission.READ_EXTERNAL_STORAGE]
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this@MainActivity, "External Storage Permission denied.", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        if (true == result[Manifest.permission.CAMERA]
            && true == result[Manifest.permission.RECORD_AUDIO]
        ) {
            if (actionType == ActionType.CAMERA)
                presentCamera()
            else if (actionType == ActionType.GALLERY)
                presentGallery()
        }
        else {
            Toast.makeText(this@MainActivity, "Permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.showStatusBar(this, lightStatusBar = true, true)

        initLayout()

        requestPermissions()
    }

    // TODO: My Method

    private fun initLayout() {
        val radius = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
        val llRecordVideo = findViewById<LinearLayout>(R.id.ll_record_video)
        llRecordVideo.background = Utils.getRippleDrawable(getColor(R.color.purple1), radius)
        llRecordVideo.setOnClickListener {
            actionType = ActionType.CAMERA
            presentCamera()
        }

        val llChooseLibrary = findViewById<LinearLayout>(R.id.ll_choose_library)
        llChooseLibrary.background = Utils.getRippleDrawable(getColor(R.color.white), getColor(R.color.purple1), radius)
        llChooseLibrary.setOnClickListener {
            actionType = ActionType.GALLERY
            presentGallery()
//            startActivity(Intent(this, DemoActivity::class.java))
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )
    }

    private fun checkPermissions() : Boolean {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the camera permission to record the video.", Toast.LENGTH_LONG).show()
            return false
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the app to record the audio.", Toast.LENGTH_LONG).show()
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the app to access external storage.", Toast.LENGTH_LONG).show()
            return false
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Allow the app to access photos, media, and files on your device.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun presentCamera() {
        if (!checkPermissions()) {
            requestPermissions()
        }
        else {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    private fun presentGallery() {
        if (!checkPermissions()) {
            requestPermissions()
        }
        else {
            startActivity(Intent(this, GalleryActivity::class.java))
        }
    }
}