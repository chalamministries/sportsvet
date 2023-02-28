package com.fini.pro.sportsvet.utils

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.View
import android.view.WindowManager
import com.fini.pro.sportsvet.R
import java.util.*


class Utils {

    companion object {

        // TODO: Thumbnail management

        fun createThumbnail(activity: Activity?, path: String?): Bitmap? {
            var mediaMetadataRetriever: MediaMetadataRetriever? = null
            var bitmap: Bitmap? = null
            try {
                mediaMetadataRetriever = MediaMetadataRetriever()
                val uri = Uri.parse(path)
                mediaMetadataRetriever.setDataSource(activity, uri)
                bitmap = mediaMetadataRetriever.getFrameAtTime(
                    1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                mediaMetadataRetriever?.release()
            }
            return bitmap
        }

        // TODO: StatusBar

        fun showStatusBar(activity: Activity, lightStatusBar: Boolean, transparent: Boolean) {
            // Show Status Bar
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (lightStatusBar) {
                // Status Bar Color
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.statusBarColor = activity.getColor(R.color.white)
            } else {
                // View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR: make the text of staus bar to black
                // when android:windowLightStatusBar is set to true, status bar text color will be able to be seen when the status bar color is white, and when android:windowLightStatusBar is set to false, status bar text color will be designed to be seen when the status bar color is dark.

//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                activity.window.statusBarColor = activity.getColor(R.color.black)
            }
            if (transparent) {
//         Transparent Status Bar
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
        }

        fun showStatusBar(activity: Activity, statusBarColor: Int, lightStatusBar: Boolean) {
            // Show Status Bar
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (lightStatusBar) {
                // Status Bar Color
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.statusBarColor = activity.getColor(statusBarColor)
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity.window.statusBarColor = activity.getColor(statusBarColor)
            }

//        if (transparent) {
////         Transparent Status Bar
//            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//        activity.getWindow().setStatusBarColor(activity.getColor(R.color.appWhite));
        }

        fun showStatusBar(
            activity: Activity,
            statusBarColor: Int,
            lightStatusBar: Boolean,
            transparent: Boolean
        ) {
            // Show Status Bar
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (lightStatusBar) {
                // Status Bar Color
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.statusBarColor = activity.getColor(statusBarColor)
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity.window.statusBarColor = activity.getColor(statusBarColor)
            }
            if (transparent) {
//         Transparent Status Bar
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
            }
//        activity.getWindow().setStatusBarColor(activity.getColor(R.color.appWhite));
        }

        // TODO: Time Label

        fun timeLabel(totalSeconds: Long) : String {
            val label: String
            val seconds = totalSeconds % 60
            var minutes = totalSeconds / 60
            val hours = minutes / 60
            minutes %= 60
            label = if (hours == 0L)
                "${if (minutes > 9) minutes else "0$minutes"}:${if (seconds > 9) seconds else "0$seconds"}"
            else
                "${if (hours > 9) hours else "0$hours"}:${if (minutes > 9) minutes else "0$minutes"}:${if (seconds > 9) seconds else "0$seconds"}"

            return label
        }

        // TODO: Drawable

        fun getBackgroundDrawable(startColor: Int, endColor: Int, radius: Int): Drawable? {
            val defaultDrawable = GradientDrawable()
            defaultDrawable.cornerRadius = radius.toFloat()
            defaultDrawable.colors = intArrayOf(startColor, endColor)
            defaultDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(
                intArrayOf(),
                defaultDrawable //new ColorDrawable(color)
            )
            return stateListDrawable
        }
        fun getBackgroundDrawable(color: Int, radii: FloatArray?): Drawable? {
            val defaultDrawable = GradientDrawable()
            defaultDrawable.cornerRadii = radii
            defaultDrawable.setColor(color)
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(
                intArrayOf(),
                defaultDrawable //new ColorDrawable(color)
            )
            return stateListDrawable
        }

        fun getBackgroundDrawable(color: Int, radius: Int): Drawable? {
            val defaultDrawable = GradientDrawable()
            defaultDrawable.cornerRadius = radius.toFloat()
            defaultDrawable.setColor(color)
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(
                intArrayOf(),
                defaultDrawable //new ColorDrawable(color)
            )
            return stateListDrawable
        }

        fun getRippleDrawable(color: Int, radius: Int): Drawable? {
            val pressedColor = ColorStateList.valueOf(lightenOrDarken(color, 0.4))
            //            ColorDrawable defaultColor = new ColorDrawable(color);
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = radius.toFloat()
            gradientDrawable.setColor(color)
            val rippleColor = getRippleColor(color, radius)
            return RippleDrawable(pressedColor, gradientDrawable, rippleColor)
        }

        fun getRippleDrawable(color: Int, radii: FloatArray?): Drawable? {
            val pressedColor = ColorStateList.valueOf(lightenOrDarken(color, 0.4))
            //            ColorDrawable defaultColor = new ColorDrawable(color);
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadii = radii
            gradientDrawable.setColor(color)
            val rippleColor = getRippleColor(color, radii)
            return RippleDrawable(pressedColor, gradientDrawable, rippleColor)
        }

        fun getRippleDrawable(backgroundColor: Int, borderColor: Int, radius: Int): Drawable {
            val pressedColor = ColorStateList.valueOf(lightenOrDarken(backgroundColor, 0.4))
            //            ColorDrawable defaultColor = new ColorDrawable(color);
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = radius.toFloat()
            gradientDrawable.setColor(backgroundColor)
            gradientDrawable.setStroke(2, borderColor)
            val rippleColor = getRippleColor(backgroundColor, radius)
            return RippleDrawable(pressedColor, gradientDrawable, rippleColor)
        }

        private fun lightenOrDarken(color: Int, fraction: Double): Int {
            return if (canLighten(color, fraction)) {
                lighten(color, fraction)
            } else {
                darken(color, fraction)
            }
        }

        private fun getRippleColor(color: Int, radius: Int): Drawable {
            val outerRadii = FloatArray(8)
            Arrays.fill(outerRadii, radius.toFloat())
            val r = RoundRectShape(outerRadii, null, null)
            val shapeDrawable = ShapeDrawable(r)
            shapeDrawable.paint.color = color
            return shapeDrawable
        }

        private fun getRippleColor(color: Int, radii: FloatArray?): Drawable {
            val r = RoundRectShape(radii, null, null)
            val shapeDrawable = ShapeDrawable(r)
            shapeDrawable.paint.color = color
            return shapeDrawable
        }

        private fun canLighten(color: Int, fraction: Double): Boolean {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return (canLightenComponent(red, fraction)
                    && canLightenComponent(green, fraction)
                    && canLightenComponent(blue, fraction))
        }

        private fun canLightenComponent(colorComponent: Int, fraction: Double): Boolean {
            val red = Color.red(colorComponent)
            val green = Color.green(colorComponent)
            val blue = Color.blue(colorComponent)
            return red + red * fraction < 255 && green + green * fraction < 255 && blue + blue * fraction < 255
        }

        private fun lighten(color: Int, fraction: Double): Int {
            var red = Color.red(color)
            var green = Color.green(color)
            var blue = Color.blue(color)
            red = lightenColor(red, fraction)
            green = lightenColor(green, fraction)
            blue = lightenColor(blue, fraction)
            val alpha = Color.alpha(color)
            return Color.argb(alpha, red, green, blue)
        }

        private fun darken(color: Int, fraction: Double): Int {
            var red = Color.red(color)
            var green = Color.green(color)
            var blue = Color.blue(color)
            red = darkenColor(red, fraction)
            green = darkenColor(green, fraction)
            blue = darkenColor(blue, fraction)
            val alpha = Color.alpha(color)
            return Color.argb(alpha, red, green, blue)
        }

        private fun darkenColor(color: Int, fraction: Double): Int {
            return Math.max(color - color * fraction, 0.0).toInt()
        }

        private fun lightenColor(color: Int, fraction: Double): Int {
            return Math.min(color + color * fraction, 255.0).toInt()
        }
    }

}