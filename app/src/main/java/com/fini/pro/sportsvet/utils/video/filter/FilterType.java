package com.fini.pro.sportsvet.utils.video.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.fini.pro.sportsvet.R;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlBilateralFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlBoxBlurFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlBrightnessFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlBulgeDistortionFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlCGAColorspaceFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlContrastFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlCrosshatchFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlExposureFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlFilterGroup;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlGammaFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlGaussianBlurFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlGrayScaleFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlHalftoneFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlHazeFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlHighlightShadowFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlHueFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlInvertFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlLookUpTableFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlLuminanceFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlLuminanceThresholdFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlMonochromeFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlOpacityFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlPixelationFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlPosterizeFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlRGBFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSaturationFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSepiaFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSharpenFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSolarizeFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSphereRefractionFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlSwirlFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlToneCurveFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlToneFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlVibranceFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlVignetteFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlWatermarkFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlWeakPixelInclusionFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlWhiteBalanceFilter;
import com.fini.pro.sportsvet.utils.video.filter.daasuu.gpuv.egl.filter.GlZoomBlurFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public enum FilterType {
    DEFAULT,
    BILATERAL_BLUR,
    BOX_BLUR,
    BRIGHTNESS,
    BULGE_DISTORTION,
    CGA_COLORSPACE,
    CONTRAST,
    CROSSHATCH,
    EXPOSURE,
    FILTER_GROUP_SAMPLE,
    GAMMA,
    GAUSSIAN_FILTER,
    GRAY_SCALE,
    HALFTONE,
    HAZE,
    HIGHLIGHT_SHADOW,
    HUE,
    INVERT,
    LOOK_UP_TABLE_SAMPLE,
    LUMINANCE,
    LUMINANCE_THRESHOLD,
    MONOCHROME,
    OPACITY,
    OVERLAY,
    PIXELATION,
    POSTERIZE,
    RGB,
    SATURATION,
    SEPIA,
    SHARP,
    SOLARIZE,
    SPHERE_REFRACTION,
    SWIRL,
    TONE_CURVE_SAMPLE,
    TONE,
    VIBRANCE,
    VIGNETTE,
    WATERMARK,
    WEAK_PIXEL,
    WHITE_BALANCE,
    ZOOM_BLUR,
    BITMAP_OVERLAY_SAMPLE
    ;


    public static List<FilterType> createFilterList() {
        return Arrays.asList(FilterType.values());
    }

    public static List<FilterType> customFilterList() {
        return Arrays.asList(
                BRIGHTNESS,
                CONTRAST,
                GAMMA,
                HAZE,
                SHARP,
                VIBRANCE
        );
    }

    // TODO: 2/16/2023 FilterRangeClass

    // TODO: 2/16/2023

    public static int getIconBy(FilterType filterType) {
        switch (filterType) {
            case BRIGHTNESS:
                return R.drawable.filter_brightness;
            case CONTRAST:
                return R.drawable.filter_contrast;
            case GAMMA:
                return R.drawable.filter_gamma;
            case HAZE:
                return R.drawable.filter_haze;
            case SHARP:
                return R.drawable.filter_sharp;
            case VIBRANCE:
                return R.drawable.filter_vibrance;
        }
        return R.drawable.filter_brightness;
    }

    public static String getLabelBy(FilterType filterType) {
        switch (filterType) {
            case BRIGHTNESS:
                return "Brightness";
            case CONTRAST:
                return "Contrast";
            case GAMMA:
                return "Gamma";
            case HAZE:
                return "Haze";
            case SHARP:
                return "Sharp";
            case VIBRANCE:
                return "Vibrance";
        }
        return "Brightness";
    }

    public static GlFilter createGlFilter(FilterType filterType, Context context, Double value) {
        switch (filterType) {
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case BRIGHTNESS:
                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
                glBrightnessFilter.setBrightness(value.floatValue());
                return glBrightnessFilter;
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case CONTRAST:
                GlContrastFilter glContrastFilter = new GlContrastFilter();
                glContrastFilter.setContrast(value.floatValue());
                return glContrastFilter;
            case CROSSHATCH:
                return new GlCrosshatchFilter();
            case EXPOSURE:
                return new GlExposureFilter();
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAMMA:
                GlGammaFilter glGammaFilter = new GlGammaFilter();
                glGammaFilter.setGamma(value.floatValue());
                return glGammaFilter;
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case HALFTONE:
                return new GlHalftoneFilter();
            case HAZE:
                GlHazeFilter glHazeFilter = new GlHazeFilter();
                glHazeFilter.setSlope(value.floatValue());
                return glHazeFilter;
            case HIGHLIGHT_SHADOW:
                return new GlHighlightShadowFilter();
            case HUE:
                return new GlHueFilter();
            case INVERT:
                return new GlInvertFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
                return new GlLookUpTableFilter(bitmap);
            case LUMINANCE:
                return new GlLuminanceFilter();
            case LUMINANCE_THRESHOLD:
                return new GlLuminanceThresholdFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case OPACITY:
                return new GlOpacityFilter();
            case PIXELATION:
                return new GlPixelationFilter();
            case POSTERIZE:
                return new GlPosterizeFilter();
            case RGB:
                GlRGBFilter glRGBFilter = new GlRGBFilter();
                glRGBFilter.setRed(0f);
                return glRGBFilter;
            case SATURATION:
                return new GlSaturationFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(value.floatValue());
                return glSharpenFilter;
            case SOLARIZE:
                return new GlSolarizeFilter();
            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case SWIRL:
                return new GlSwirlFilter();
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();
            case TONE:
                return new GlToneFilter();
            case VIBRANCE:
                GlVibranceFilter glVibranceFilter = new GlVibranceFilter();
                glVibranceFilter.setVibrance(value.floatValue());
                return glVibranceFilter;
            case VIGNETTE:
                return new GlVignetteFilter();
            case WATERMARK:
                return new GlWatermarkFilter(BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_bitmap), GlWatermarkFilter.Position.RIGHT_BOTTOM);
            case WEAK_PIXEL:
                return new GlWeakPixelInclusionFilter();
            case WHITE_BALANCE:
                GlWhiteBalanceFilter glWhiteBalanceFilter = new GlWhiteBalanceFilter();
                glWhiteBalanceFilter.setTemperature(value.floatValue());
                glWhiteBalanceFilter.setTint(2f);
                return glWhiteBalanceFilter;
            case ZOOM_BLUR:
                return new GlZoomBlurFilter();
            case BITMAP_OVERLAY_SAMPLE:
                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_bitmap));
            default:
                return new GlFilter();
        }
    }

    public static GlFilter createGlFilter(FilterType filterType, Context context) {
        switch (filterType) {
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case BRIGHTNESS:
                GlBrightnessFilter glBrightnessFilter = new GlBrightnessFilter();
                glBrightnessFilter.setBrightness(0.2f);
                return glBrightnessFilter;
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case CONTRAST:
                GlContrastFilter glContrastFilter = new GlContrastFilter();
                glContrastFilter.setContrast(2.5f);
                return glContrastFilter;
            case CROSSHATCH:
                return new GlCrosshatchFilter();
            case EXPOSURE:
                return new GlExposureFilter();
            case FILTER_GROUP_SAMPLE:
                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAMMA:
                GlGammaFilter glGammaFilter = new GlGammaFilter();
                glGammaFilter.setGamma(2f);
                return glGammaFilter;
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case HALFTONE:
                return new GlHalftoneFilter();
            case HAZE:
                GlHazeFilter glHazeFilter = new GlHazeFilter();
                glHazeFilter.setSlope(-0.5f);
                return glHazeFilter;
            case HIGHLIGHT_SHADOW:
                return new GlHighlightShadowFilter();
            case HUE:
                return new GlHueFilter();
            case INVERT:
                return new GlInvertFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
                return new GlLookUpTableFilter(bitmap);
            case LUMINANCE:
                return new GlLuminanceFilter();
            case LUMINANCE_THRESHOLD:
                return new GlLuminanceThresholdFilter();
            case MONOCHROME:
                return new GlMonochromeFilter();
            case OPACITY:
                return new GlOpacityFilter();
            case PIXELATION:
                return new GlPixelationFilter();
            case POSTERIZE:
                return new GlPosterizeFilter();
            case RGB:
                GlRGBFilter glRGBFilter = new GlRGBFilter();
                glRGBFilter.setRed(0f);
                return glRGBFilter;
            case SATURATION:
                return new GlSaturationFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
            case SOLARIZE:
                return new GlSolarizeFilter();
            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case SWIRL:
                return new GlSwirlFilter();
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();
            case TONE:
                return new GlToneFilter();
            case VIBRANCE:
                GlVibranceFilter glVibranceFilter = new GlVibranceFilter();
                glVibranceFilter.setVibrance(3f);
                return glVibranceFilter;
            case VIGNETTE:
                return new GlVignetteFilter();
            case WATERMARK:
                return new GlWatermarkFilter(BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_bitmap), GlWatermarkFilter.Position.RIGHT_BOTTOM);
            case WEAK_PIXEL:
                return new GlWeakPixelInclusionFilter();
            case WHITE_BALANCE:
                GlWhiteBalanceFilter glWhiteBalanceFilter = new GlWhiteBalanceFilter();
                glWhiteBalanceFilter.setTemperature(2400f);
                glWhiteBalanceFilter.setTint(2f);
                return glWhiteBalanceFilter;
            case ZOOM_BLUR:
                return new GlZoomBlurFilter();
            case BITMAP_OVERLAY_SAMPLE:
                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_bitmap));
            default:
                return new GlFilter();
        }
    }

    public static FilterAdjuster createFilterAdjuster(FilterType filterType) {
        switch (filterType) {
            case BILATERAL_BLUR:
                return (filter, percentage) -> ((GlBilateralFilter) filter).setBlurSize(range(percentage, 0.0f, 1.0f));
            case BOX_BLUR:
                return (filter, percentage) -> ((GlBoxBlurFilter) filter).setBlurSize(range(percentage, 0.0f, 1.0f));
            case BRIGHTNESS:
                return (filter, percentage) -> ((GlBrightnessFilter) filter).setBrightness(range(percentage, -1.0f, 1.0f));
            case CONTRAST:
                return (filter, percentage) -> ((GlContrastFilter) filter).setContrast(range(percentage, 0.0f, 2.0f));
            case CROSSHATCH:
                return (filter, percentage) -> {
                    ((GlCrosshatchFilter) filter).setCrossHatchSpacing(range(percentage, 0.0f, 0.06f));
                    ((GlCrosshatchFilter) filter).setLineWidth(range(percentage, 0.0f, 0.006f));
                };
            case EXPOSURE:
                return (filter, percentage) -> ((GlExposureFilter) filter).setExposure(range(percentage, -10.0f, 10.0f));
            case GAMMA:
                return (filter, percentage) -> ((GlGammaFilter) filter).setGamma(range(percentage, 0.0f, 3.0f));
            case HAZE:
                return (filter, percentage) -> {
                    ((GlHazeFilter) filter).setDistance(range(percentage, -0.3f, 0.3f));
                    ((GlHazeFilter) filter).setSlope(range(percentage, -0.3f, 0.3f));
                };
            case HIGHLIGHT_SHADOW:
                return (filter, percentage) -> {
                    ((GlHighlightShadowFilter) filter).setShadows(range(percentage, 0.0f, 1.0f));
                    ((GlHighlightShadowFilter) filter).setHighlights(range(percentage, 0.0f, 1.0f));
                };
            case HUE:
                return (filter, percentage) -> ((GlHueFilter) filter).setHue(range(percentage, 0.0f, 360.0f));
            case LUMINANCE_THRESHOLD:
                return (filter, percentage) -> ((GlLuminanceThresholdFilter) filter).setThreshold(range(percentage, 0.0f, 1.0f));
            case MONOCHROME:
                return (filter, percentage) -> ((GlMonochromeFilter) filter).setIntensity(range(percentage, 0.0f, 1.0f));
            case OPACITY:
                return (filter, percentage) -> ((GlOpacityFilter) filter).setOpacity(range(percentage, 0.0f, 1.0f));
            case PIXELATION:
                return (filter, percentage) -> ((GlPixelationFilter) filter).setPixel(range(percentage, 1.0f, 100.0f));
            case POSTERIZE:
                return (filter, percentage) -> {
                    // In theorie to 256, but only first 50 are interesting
                    ((GlPosterizeFilter) filter).setColorLevels((int) range(percentage, 1, 50));
                };
            case RGB:
                return (filter, percentage) -> ((GlRGBFilter) filter).setRed(range(percentage, 0.0f, 1.0f));
            case SATURATION:
                return (filter, percentage) -> ((GlSaturationFilter) filter).setSaturation(range(percentage, 0.0f, 2.0f));
            case SHARP:
                return (filter, percentage) -> ((GlSharpenFilter) filter).setSharpness(range(percentage, -4.0f, 4.0f));
            case SOLARIZE:
                return (filter, percentage) -> ((GlSolarizeFilter) filter).setThreshold(range(percentage, 0.0f, 1.0f));
            case SWIRL:
                return (filter, percentage) -> ((GlSwirlFilter) filter).setAngle(range(percentage, 0.0f, 2.0f));
            case VIBRANCE:
                return (filter, percentage) -> ((GlVibranceFilter) filter).setVibrance(range(percentage, -1.2f, 1.2f));
            case VIGNETTE:
                return (filter, percentage) -> ((GlVignetteFilter) filter).setVignetteStart(range(percentage, 0.0f, 1.0f));
            case WHITE_BALANCE:
                return (filter, percentage) -> ((GlWhiteBalanceFilter) filter).setTemperature(range(percentage, 2000.0f, 8000.0f));
            default:
                return null;
        }
    }

    private static float range(int percentage, float start, float end) {
        return (end - start) * percentage / 100.0f + start;
    }
}
