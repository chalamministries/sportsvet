package com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.composer;


interface IAudioComposer {

    void setup();

    boolean stepPipeline();

    long getWrittenPresentationTimeUs();

    boolean isFinished();

    void release();
}
