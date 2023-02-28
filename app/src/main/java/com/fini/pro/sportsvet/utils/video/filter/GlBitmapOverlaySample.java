package com.fini.pro.sportsvet.utils.video.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlOverlayFilter;


public class GlBitmapOverlaySample extends GlOverlayFilter {

    private final Bitmap bitmap;

    public GlBitmapOverlaySample(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

}
