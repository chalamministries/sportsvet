package com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.player;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glViewport;

import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.EglUtil;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.GlFrameBufferObjectRenderer;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.GlFramebufferObject;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.GlPreviewFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.GlSurfaceTexture;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlLookUpTableFilter;
import com.google.android.exoplayer2.ExoPlayer;

public class GPUPlayerRenderer extends GlFrameBufferObjectRenderer implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = GPUPlayerRenderer.class.getSimpleName();

    private GlSurfaceTexture previewTexture;
    private boolean updateSurface = false;

    private int texName;

    private final float[] MVPMatrix = new float[16];
    private final float[] ProjMatrix = new float[16];
    private final float[] MMatrix = new float[16];
    private final float[] VMatrix = new float[16];
    private final float[] STMatrix = new float[16];


    private GlFramebufferObject filterFramebufferObject;
    private GlPreviewFilter previewFilter;

    private GlFilter glFilter;
    private boolean isNewFilter;
    private final GPUPlayerView glPreview;

    private float aspectRatio = 1f;

    private ExoPlayer simpleExoPlayer;

    GPUPlayerRenderer(GPUPlayerView glPreview) {
        super();
        Matrix.setIdentityM(STMatrix, 0);
        this.glPreview = glPreview;
    }

    void setGlFilter(final GlFilter filter) {
        glPreview.queueEvent(() -> {
            if (glFilter != null) {
                glFilter.release();
                if (glFilter instanceof GlLookUpTableFilter) {
                    ((GlLookUpTableFilter) glFilter).releaseLutBitmap();
                }
            }
            glFilter = filter;
            isNewFilter = true;
            glPreview.requestRender();
        });
    }

    @Override
    public void onSurfaceCreated(final EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        final int[] args = new int[1];

        GLES20.glGenTextures(args.length, args, 0);
        texName = args[0];


        previewTexture = new GlSurfaceTexture(texName);
        previewTexture.setOnFrameAvailableListener(this);


        GLES20.glBindTexture(previewTexture.getTextureTarget(), texName);
        // GL_TEXTURE_EXTERNAL_OES
        EglUtil.setupSampler(previewTexture.getTextureTarget(), GL_LINEAR, GL_NEAREST);
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);

        filterFramebufferObject = new GlFramebufferObject();
        // GL_TEXTURE_EXTERNAL_OES
        previewFilter = new GlPreviewFilter(previewTexture.getTextureTarget());
        previewFilter.setup();

        Surface surface = new Surface(previewTexture.getSurfaceTexture());
        this.simpleExoPlayer.setVideoSurface(surface);

        Matrix.setLookAtM(VMatrix, 0,
                0.0f, 0.0f, 5.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        );

        synchronized (this) {
            updateSurface = false;
        }

        if (glFilter != null) {
            isNewFilter = true;
        }

        GLES20.glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);

    }

    @Override
    public void onSurfaceChanged(final int width, final int height) {
        Log.d(TAG, "onSurfaceChanged width = " + width + "  height = " + height);
        filterFramebufferObject.setup(width, height);
        previewFilter.setFrameSize(width, height);
        if (glFilter != null) {
            glFilter.setFrameSize(width, height);
        }

        aspectRatio = (float) width / height;
        Matrix.frustumM(ProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 5, 7);
        Matrix.setIdentityM(MMatrix, 0);
    }

    @Override
    public void onDrawFrame(final GlFramebufferObject fbo) {

        synchronized (this) {
            if (updateSurface) {
                previewTexture.updateTexImage();
                previewTexture.getTransformMatrix(STMatrix);
                updateSurface = false;
            }
        }

        if (isNewFilter) {
            if (glFilter != null) {
                glFilter.setup();
                glFilter.setFrameSize(fbo.getWidth(), fbo.getHeight());
            }
            isNewFilter = false;
        }

        if (glFilter != null) {
            filterFramebufferObject.enable();
            glViewport(0, 0, filterFramebufferObject.getWidth(), filterFramebufferObject.getHeight());
        }

        GLES20.glClear(GL_COLOR_BUFFER_BIT);

        Matrix.multiplyMM(MVPMatrix, 0, VMatrix, 0, MMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, ProjMatrix, 0, MVPMatrix, 0);

        previewFilter.draw(texName, MVPMatrix, STMatrix, aspectRatio);

        if (glFilter != null) {
            fbo.enable();
            GLES20.glClear(GL_COLOR_BUFFER_BIT);
            glFilter.draw(filterFramebufferObject.getTexName(), fbo);
        }
    }

    @Override
    public synchronized void onFrameAvailable(final SurfaceTexture previewTexture) {
        updateSurface = true;
        glPreview.requestRender();
    }

    void setSimpleExoPlayer(ExoPlayer simpleExoPlayer) {
        this.simpleExoPlayer = simpleExoPlayer;
    }

    void release() {
        if (glFilter != null) {
            glFilter.release();
        }
        if (previewTexture != null) {
            previewTexture.release();
        }
    }

}