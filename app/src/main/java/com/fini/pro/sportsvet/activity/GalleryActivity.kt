package com.fini.pro.sportsvet.activity

import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fini.pro.sportsvet.R
import com.fini.pro.sportsvet.adapter.GalleryAdapter
import com.fini.pro.sportsvet.model.Video
import com.fini.pro.sportsvet.utils.Utils

class GalleryActivity : AppCompatActivity() {

    companion object {
        const val LOADER_ID = 1000
    }

//    private lateinit var refreshVideos: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: GalleryAdapter
    private var dateList = arrayListOf<String>()
    private var videosMap = HashMap<String, ArrayList<Video>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        Utils.showStatusBar(this, lightStatusBar = true, false)

        initLayout()
    }

    override fun onResume() {
        super.onResume()
        Log.e("GalleryActivity", "onResume..............")

        LoaderManager.getInstance(this).initLoader(
            LOADER_ID,
            null,
            loaderCallbacks
        )
//        LoaderManager.getInstance(this).restartLoader(
//            LOADER_ID,
//            null,
//            loaderCallbacks
//        )
    }

    // TODO: My Method

    private fun initLayout() {
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
        findViewById<ImageView>(R.id.iv_record).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        progressBar = findViewById(R.id.progress_horizontal)

        // RecyclerView
//        refreshVideos = findViewById(R.id.refresh_videos)
//        refreshVideos.setOnRefreshListener {
//            LoaderManager.getInstance(this).restartLoader(
//                LOADER_ID,
//                null,
//                loaderCallbacks
//            )
//        }

        adapter = GalleryAdapter(
            context = this,
            dateList = dateList,
            items = videosMap,
            onSelectVideo = { video ->
                presentVideoEdit(video = video)
            }
        )
        val recyclerView = findViewById<RecyclerView>(R.id.rv_videos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun presentVideoEdit(video: Video) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("video", video)
        startActivity(intent)
    }

    // TODO: LoaderManager.LoaderCallbacks

    private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            if (id == LOADER_ID) {
                runOnUiThread {
                    progressBar.visibility = View.VISIBLE
                }
                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.ALBUM,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DISPLAY_NAME,
                )
                val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"
                return CursorLoader(this@GalleryActivity, uri, projection, null, null, sortOrder)
            }
            return Loader(this@GalleryActivity)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            if (data == null)
                return

            runOnUiThread {
                progressBar.visibility = View.GONE
            }

            videosMap = HashMap()
            dateList = arrayListOf()
            var index = 0
            while (data.moveToNext() && index < Int.MAX_VALUE) {
                val idIndex = data.getColumnIndex(MediaStore.Video.Media._ID)
                val id: Long = data.getLong(if (idIndex >= 0) idIndex else 0)

                val addedIndex = data.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)
                val added = data.getLong(if (addedIndex >= 0) addedIndex else 0)

                val pathIndex = data.getColumnIndex(MediaStore.Video.Media.DATA)
                val path = data.getString(pathIndex)

                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val video = Video(uri = contentUri, path = path, added = added)

                val key = video.addedAt()
                if (!dateList.contains(key))
                    dateList.add(key)
                val videos = arrayListOf<Video>()
                if (videosMap[key] != null)
                    videos.addAll(videosMap[key]!!)
                videos.add(video)
                videosMap[key] = videos

                index++
            }
            data.moveToPosition(-1) // Restore cursor back to the beginning
            adapter.setItems(dateList, videosMap)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
            adapter.setItems(arrayListOf(), HashMap())
        }

    }
}