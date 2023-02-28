package com.fini.pro.sportsvet.utils.video.filter;


import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter;

public interface FilterAdjuster {
    public void adjust(GlFilter filter, int percentage);
}
