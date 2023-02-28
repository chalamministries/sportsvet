package com.fini.pro.sportsvet.utils.video.presenter

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.LruCache
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import java.io.*
import java.net.URL
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*


object OptiUtils {

    var callBack: CallBack? = null

    interface CallBack{
        fun onShared(path: String)
    }

    private fun InputStream.toFile(path: String) {
        File(path).outputStream().use { this.copyTo(it) }
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun refreshGalleryAlone(context: Context) {
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isVideoHaveAudioTrack(path: String): Boolean {
        var audioTrack = false

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val hasAudioStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
        audioTrack = hasAudioStr == "yes"

        return audioTrack
    }

    fun showGlideToast(activity: Activity, content: String) {
//        GlideToast.makeToast(
//            activity,
//            content,
//            GlideToast.LENGTHTOOLONG,
//            GlideToast.FAILTOAST,
//            GlideToast.TOP
//        ).show()
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun createVideoFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(Date())
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.VIDEO_FORMAT, storageDir)
    }

    fun createVideoFile(context: Context, fileName: String): File {
        Log.d("file_directory", Environment.getExternalStorageDirectory().path)
        Log.d("file_directory", context.getExternalFilesDir(null)!!.path)
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.VIDEO_FORMAT, storageDir)
    }

    fun createSaveVideoFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.VIDEO_FORMAT, storageDir)
    }


    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.IMAGE_FORMAT, storageDir)
    }

    fun createAudioFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.AUDIO_FORMAT, storageDir)
    }

    fun createFile(context: Context, format: String): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, format, storageDir)
    }

    fun createAudioFile(context: Context, fileName: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.AUDIO_FORMAT, storageDir)
    }

    fun clearCachedImages(context: Context) {
        MainScope().launch(Dispatchers.IO) {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
            if (dir.isDirectory) {
                try {
                    val children = dir.list()
                    for (i in children.indices) {
                        Log.d("clear_cached", "Cleared :" + children[i]!!.toString())
                        File(dir, children[i]).delete()
                    }
                } catch (e: Exception) {
                    Log.d("clear_cached", e.message.toString())
                }

            }
        }
    }


    fun createVideoFile2(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = timeStamp + "_"
        val path = "/storage/emulated/0/" + OptiConstant.APP_NAME + File.separator
        Log.d("videoPath", path)
        val folder = File(path)
        if (!folder.exists()) folder.mkdirs()

        return File.createTempFile(fileName, OptiConstant.VIDEO_FORMAT, folder)
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()

        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

    fun writeIntoFile(context: Context, uri: Uri): File {
        val file = OptiUtils.createVideoFile(context)
        var videoAsset: AssetFileDescriptor? = null
        try {
            videoAsset = context.contentResolver.openAssetFileDescriptor(
                uri,
                "r"
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val `in`: FileInputStream
        try {
            `in` = videoAsset!!.createInputStream()
            var out: OutputStream? = null
            out = FileOutputStream(file)

            // Copy the bits from instream to outstream
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun download(link: String, path: String) {
        URL(link).openStream().use { input ->
            FileOutputStream(File(path)).use { output ->
                input.copyTo(output)
            }
        }
    }

    fun addToGallery(context: Context, videoFile: File) {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val values = ContentValues(3)
        values.put(MediaStore.Video.Media.TITLE, fileName)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATA, videoFile.absolutePath)
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
    }


    fun getVideoDuration(context: Context, file: File): Long{
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMillis = time?.toLong()
        retriever.release()
        return timeInMillis!!
    }

    fun resizeHeight(dialog: Dialog?){

        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { fullScreen ->
                val behaviour = BottomSheetBehavior.from(fullScreen)
                val layoutParams = fullScreen.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                fullScreen.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

    }

    fun audioFormat(path: String): String? {
        if(path.toLowerCase().contains(".mp3")) return ".mp3"
        if(path.toLowerCase().contains(".m4a")) return ".m4a"
        if(path.toLowerCase().contains(".3gp")) return ".3gp"
        return null
    }


    fun createSavePDFFile1(): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val imageFileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val path = Environment.getExternalStorageDirectory().toString() + File.separator + OptiConstant.APP_NAME +
                File.separator  + "Documents" + File.separator
        val folder = File(path)
        if (!folder.exists())
            folder.mkdirs()

        return File.createTempFile(imageFileName, OptiConstant.PDF_FORMAT, folder)
    }

    fun createSavePDFFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.PDF_FORMAT, storageDir)
    }

    fun createSaveImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName: String = OptiConstant.APP_NAME + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!storageDir?.exists()!!) storageDir.mkdirs()
        return File.createTempFile(fileName, OptiConstant.PNG_FORMAT, storageDir)
    }

    fun downloadRecyclerView(context: Context, view: RecyclerView): Bitmap {
        val recyclerViewBm = getScreenshotFromRecyclerView(view)
        try {
            MainScope().launch(Dispatchers.IO) {

                val pdfFile = createSavePDFFile(context)
                val fOut = FileOutputStream(pdfFile)
                val document = PdfDocument()
                val pageInfo = PageInfo.Builder(recyclerViewBm!!.width, recyclerViewBm.height, 1).create()
                val page = document.startPage(pageInfo)
                recyclerViewBm.prepareToDraw()
                val c: Canvas = page.canvas
                c.drawBitmap(recyclerViewBm, 0f, 0f, null)
                document.finishPage(page)
                document.writeTo(fOut)
                document.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "File saved : " + pdfFile.path, Toast.LENGTH_LONG).show()
                    onSaveListener?.let { it(pdfFile.path) }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return recyclerViewBm!!
    }

    private var onSaveListener: ((String) -> Unit)? = null

    fun setSaveListener(listener: (String) -> Unit) {
        onSaveListener = listener
    }

    private fun getScreenshotFromRecyclerView(view: RecyclerView): Bitmap? {
        val adapter = view.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmaCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(view, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmaCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }
            bigBitmap = Bitmap.createBitmap(view.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val bigCanvas = Canvas(bigBitmap)
            bigCanvas.drawColor(Color.WHITE)
            for (i in 0 until size) {
                val bitmap = bitmaCache[i.toString()]
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }

    fun requestStoragePermission(context: Context) {
        val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions((context as Activity), PERMISSIONS, 1)

//        if (Build.VERSION.SDK_INT >= 35) {
//            val intent = Intent()
//            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
//            val uri = Uri.fromParts("package", context.packageName, null)
//            intent.data = uri
//            context.startActivity(intent)
//
//        } else {
//            //below android 11
//
//        }
    }

    fun hasStoragePermissions(context: Context): Boolean{
        val PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) || hasPermissions(
            context,
            *PERMISSIONS
        )
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

}


