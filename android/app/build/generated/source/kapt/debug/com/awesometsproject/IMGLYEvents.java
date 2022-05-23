package com.awesometsproject; 
import android.util.Log;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ly.img.android.pesdk.backend.model.EventSetInterface;
import ly.img.android.pesdk.backend.model.state.manager.StateHandler;
import ly.img.android.pesdk.backend.model.state.manager.StateObservable;
import ly.img.android.pesdk.utils.CallSet;
import ly.img.android.pesdk.utils.ThreadUtils;
import ly.img.android.pesdk.utils.Trace;
import ly.img.android.pesdk.backend.model.EventCall;
@ly.img.android.pesdk.annotations.EventDispatcher
public class IMGLYEvents extends ly.img.android.pesdk.backend.model.state.manager.ImglyEventDispatcher {


    @androidx.annotation.Keep
    public IMGLYEvents(StateHandler stateHandler) {
        super(stateHandler);
    }

    @Override
    protected Class<?> getEventSetClass(Object object) {
        Class<?> modelClass = object.getClass();
        Class<?> eventSetClass = com.awesometsproject.events.$EventAccessorMap.classAccessorsMap.get(modelClass);
        while (eventSetClass == null) {
            modelClass = modelClass.getSuperclass();
            eventSetClass = com.awesometsproject.events.$EventAccessorMap.classAccessorsMap.get(modelClass);
            if (modelClass == null || modelClass == Object.class) {
                break;
            }
        }
        return eventSetClass;
    }
    
             
    public static final String EditorShowState_LAYER_TOUCH_START="EditorShowState.LAYER_TOUCH_START";

    public static final String TextDesignLayerSettings_PADDING="TextDesignLayerSettings.PADDING";

    public static final String FocusSettings_MODE="FocusSettings.MODE";

    public static final String OverlaySettings_STATE_REVERTED="OverlaySettings.STATE_REVERTED";

    public static final String HistoryState_CURRENT_STATE_UPDATED="HistoryState.CURRENT_STATE_UPDATED";

    public static final String ProgressState_LOADING_FINISH="ProgressState.LOADING_FINISH";

    public static final String EditorLoadSettings_IMAGE_IS_BROKEN="LoadState.SOURCE_IS_BROKEN";

    public static final String EditorSaveState_EXPORT_DONE="EditorSaveState.EXPORT_DONE";

    public static final String TextLayerSettings_POSITION="TextLayerSettings.POSITION";

    public static final String WatermarkSettings_INSET="WatermarkSettings.INSET";

    public static final String ImageStickerLayerSettings_STATE_REVERTED="ImageStickerLayerSettings.STATE_REVERTED";

    public static final String TransformSettings_ROTATION="TransformSettings.ROTATION";

    public static final String UiStateAudio__SELECTED_CATEGORY_CHANGED="UiStateAudio..SELECTED_CATEGORY_CHANGED";

    public static final String ColorAdjustmentSettings_PREVIEW_DIRTY="ColorAdjustmentSettings.PREVIEW_DIRTY";

    public static final String FilterSettings_INTENSITY="FilterSettings.INTENSITY";

    public static final String FilterSettings_STATE_REVERTED="FilterSettings.STATE_REVERTED";

    public static final String TextLayerSettings_STATE_REVERTED="TextLayerSettings.STATE_REVERTED";

    public static final String ImageStickerLayerSettings_FLIP_VERTICAL="ImageStickerLayerSettings.FLIP_VERTICAL";

    public static final String AbstractSaveSettings_OUTPUT_URI="AbstractSaveSettings.OUTPUT_URI";

    public static final String TransformSettings_STATE_REVERTED="TransformSettings.STATE_REVERTED";

    public static final String UiStateMenu_ENTER_GROUND="UiStateMenu.ENTER_GROUND";

    public static final String UiStateMenu_CANCEL_CLICKED="UiStateMenu.CANCEL_CLICKED";

    public static final String ProgressState_LOADING_START="ProgressState.LOADING_START";

    public static final String ColorPipetteState_STATE_REVERTED="ColorPipetteState.STATE_REVERTED";

    public static final String TrimSettings_MIN_TIME="TrimSettings.MIN_TIME";

    public static final String LayerListSettings_PREVIEW_DIRTY="LayerListSettings.PREVIEW_DIRTY";

    public static final String EditorShowState_RESUME="EditorShowState.RESUME";

    public static final String ColorAdjustmentSettings_STATE_REVERTED="ColorAdjustmentSettings.STATE_REVERTED";

    public static final String TextDesignLayerSettings_COLOR="TextDesignLayerSettings.COLOR";

    public static final String FrameSettings_STATE_REVERTED="FrameSettings.STATE_REVERTED";

    public static final String TransformSettings_TRANSFORMATION="TransformSettings.TRANSFORMATION";

    public static final String ImageStickerLayerSettings_EDIT_MODE="ImageStickerLayerSettings.EDIT_MODE";

    public static final String TextDesignLayerSettings_INVERT="TextDesignLayerSettings.INVERT";

    public static final String TransformSettings_CROP_RECT_TRANSLATE="TransformSettings.CROP_RECT_TRANSLATE";

    public static final String ColorAdjustmentSettings_EXPOSURE="ColorAdjustmentSettings.EXPOSURE";

    public static final String EditorShowState_STAGE_OVERLAP="EditorShowState.STAGE_OVERLAP";

    public static final String ColorAdjustmentSettings_CLARITY="ColorAdjustmentSettings.CLARITY";

    public static final String TextDesignLayerSettings_TEXT="TextDesignLayerSettings.TEXT";

    public static final String VideoCompositionSettings_VIDEO_START_TIME="VideoCompositionSettings.VIDEO_START_TIME";

    public static final String ColorAdjustmentSettings_CONTRAST="ColorAdjustmentSettings.CONTRAST";

    public static final String ProgressState_EXPORT_FINISH="ProgressState.EXPORT_FINISH";

    public static final String ColorAdjustmentSettings_SHARPNESS="ColorAdjustmentSettings.SHARPNESS";

    public static final String LayerListSettings_ADD_LAYER="LayerListSettings.ADD_LAYER";

    public static final String TransformSettings_ORIENTATION_OFFSET="TransformSettings.ORIENTATION_OFFSET";

    public static final String EditorSaveState_EXPORT_START="EditorSaveState.EXPORT_START";

    public static final String TransformSettings_ASPECT="TransformSettings.ASPECT";

    public static final String EditorShowState_UI_OVERLAY_INVALID="EditorShowState.UI_OVERLAY_INVALID";

    public static final String EditorShowState_CHANGE_SIZE="EditorShowState.CHANGE_SIZE";

    public static final String UiConfigComposition_CONFIG_DIRTY="UiConfigComposition.CONFIG_DIRTY";

    public static final String UiStateMenu_ACCEPT_CLICKED="UiStateMenu.ACCEPT_CLICKED";

    public static final String TrimSettings_START_TIME="TrimSettings.START_TIME";

    public static final String ProgressState_EXPORT_START="ProgressState.EXPORT_START";

    public static final String EditorShowState_CANVAS_MODE="EditorShowState.CANVAS_MODE";

    public static final String OverlaySettings_POSITION="OverlaySettings.POSITION";

    public static final String TransformSettings_ORIENTATION="TransformSettings.ORIENTATION";

    public static final String VideoState_PRESENTATION_TIME="VideoState.PRESENTATION_TIME";

    public static final String FocusSettings_INTENSITY="FocusSettings.INTENSITY";

    public static final String LayerListSettings_REMOVE_LAYER="LayerListSettings.REMOVE_LAYER";

    public static final String TextLayerSettings_CONFIG="TextLayerSettings.CONFIG";

    public static final String VideoState_REQUEST_SEEK="VideoState.REQUEST_SEEK";

    public static final String HistoryState_HISTORY_LEVEL_LIST_CREATED="HistoryState.HISTORY_LEVEL_LIST_CREATED";

    public static final String ImageStickerLayerSettings_SOLID_COLOR="ImageStickerLayerSettings.SOLID_COLOR";

    public static final String ImageStickerLayerSettings_PLACEMENT_INVALID="ImageStickerLayerSettings.PLACEMENT_INVALID";

    public static final String UiStateMenu_REFRESH_PANEL="UiStateMenu.REFRESH_PANEL";

    public static final String ImageStickerLayerSettings_COLORIZE_COLOR="ImageStickerLayerSettings.COLORIZE_COLOR";

    public static final String UiStateMenu_CLOSE_CLICKED="UiStateMenu.CLOSE_CLICKED";

    public static final String AudioOverlaySettings_START_TIME="AudioOverlaySettings.START_TIME";

    public static final String EditorShowState_IS_READY="EditorShowState.IS_READY";

    public static final String VideoCompositionSettings_VIDEO_ADDED="VideoCompositionSettings.VIDEO_ADDED";

    public static final String TrimSettings_MUTE_STATE="TrimSettings.MUTE_STATE";

    public static final String LayerListSettings_RESELECTED_LAYER="LayerListSettings.RESELECTED_LAYER";

    public static final String ColorAdjustmentSettings_BLACKS="ColorAdjustmentSettings.BLACKS";

    public static final String HistoryState_REDO="HistoryState.REDO";

    public static final String TrimSettings_MAX_TIME="TrimSettings.MAX_TIME";

    public static final String LayerListSettings_ACTIVE_LAYER="LayerListSettings.ACTIVE_LAYER";

    public static final String LayerListSettings_BRING_TO_FRONT="LayerListSettings.BRING_TO_FRONT";

    public static final String EditorLoadSettings_SOURCE_IMAGE="LoadSettings.SOURCE";

    public static final String EditorShowState_PREVIEW_DIRTY="EditorShowState.PREVIEW_DIRTY";

    public static final String FrameSettings_FRAME_SCALE="FrameSettings.FRAME_SCALE";

    public static final String LayerListSettings_STATE_REVERTED="LayerListSettings.STATE_REVERTED";

    public static final String VideoState_SEEK_STOP="VideoState.SEEK_STOP";

    public static final String EditorShowState_IMAGE_RECT="EditorShowState.IMAGE_RECT";

    public static final String VideoState_REQUEST_NEXT_FRAME="VideoState.REQUEST_NEXT_FRAME";

    public static final String VideoState_SEEK_START="VideoState.SEEK_START";

    public static final String UiStateMenu_TOOL_STACK_CHANGED="UiStateMenu.TOOL_STACK_CHANGED";

    public static final String UiConfigOverlay_CONFIG_DIRTY="UiConfigOverlay_CONFIG_DIRTY";

    public static final String TextLayerSettings_TEXT_SIZE="TextLayerSettings.TEXT_SIZE";

    public static final String UiStateMenu_LEAVE_TOOL="UiStateMenu.LEAVE_TOOL";

    public static final String AudioOverlaySettings_STATE_REVERTED="AudioOverlaySettings.STATE_REVERTED";

    public static final String TextLayerSettings_PLACEMENT_INVALID="TextLayerSettings.PLACEMENT_INVALID";

    public static final String VideoCompositionSettings_VIDEO_REMOVED="VideoCompositionSettings.VIDEO_REMOVED";

    public static final String VideoCompositionSettings_STATE_REVERTED="VideoCompositionSettings.STATE_REVERTED";

    public static final String BrushSettings_SIZE="BrushSettings.SIZE";

    public static final String EditorSaveState_EXPORT_START_IN_BACKGROUND="EditorSaveState.EXPORT_START_IN_BACKGROUND";

    public static final String LayerListSettings_SELECTED_LAYER="LayerListSettings.SELECTED_LAYER";

    public static final String HistoryState_UNDO="HistoryState.UNDO";

    public static final String LoadState_SOURCE_IS_BROKEN="LoadState.SOURCE_IS_BROKEN";

    public static final String BrushSettings_COLOR="BrushSettings.COLOR";

    public static final String TextLayerSettings_EDIT_MODE="TextLayerSettings.EDIT_MODE";

    public static final String FocusSettings_GRADIENT_RADIUS="FocusSettings.GRADIENT_RADIUS";

    public static final String ColorPipetteState_COLOR="ColorPipetteState.COLOR";

    public static final String ColorAdjustmentSettings_GAMMA="ColorAdjustmentSettings.GAMMA";

    public static final String UiConfigAudioComposition_CONFIG_DIRTY="UiConfigAudioComposition.CONFIG_DIRTY";

    public static final String VideoState_VIDEO_STOP="VideoState.VIDEO_STOP";

    public static final String EditorShowState_PAUSE="EditorShowState.PAUSE";

    public static final String UiStateMenu_SAVE_CLICKED="UiStateMenu.SAVE_CLICKED";

    public static final String ImageStickerLayerSettings_FLIP_HORIZONTAL="ImageStickerLayerSettings.FLIP_HORIZONTAL";

    public static final String UiConfigTrim_CONFIG_DIRTY="UiConfigTrim_CONFIG_DIRTY";

    public static final String FilterSettings_FILTER="FilterSettings.FILTER";

    public static final String BrushSettings_HARDNESS="BrushSettings.HARDNESS";

    public static final String LoadSettings_SOURCE="LoadSettings.SOURCE";

    public static final String ProgressState_PREVIEW_BUSY="ProgressState.PREVIEW_BUSY";

    public static final String EditorShowState_PREVIEW_IS_READY="EditorShowState.PREVIEW_IS_READY";

    public static final String TextDesignLayerSettings_FLIP_HORIZONTAL="TextDesignLayerSettings.FLIP_HORIZONTAL";

    public static final String ColorPipetteState_POSITION="ColorPipetteState.POSITION";

    public static final String LoadState_SOURCE_INFO="LoadState.SOURCE_INFO";

    public static final String TextLayerSettings_FLIP_HORIZONTAL="TextLayerSettings.FLIP_HORIZONTAL";

    public static final String LayerListSettings_LAYER_LIST="LayerListSettings.LAYER_LIST";

    public static final String TextDesignLayerSettings_PLACEMENT_INVALID="TextDesignLayerSettings.PLACEMENT_INVALID";

    public static final String UiConfigAspect_CONFIG_DIRTY="UiConfigAspect_CONFIG_DIRTY";

    public static final String TextLayerSettings_FLIP_VERTICAL="TextLayerSettings.FLIP_VERTICAL";

    public static final String BrushSettings_STATE_REVERTED="BrushSettings.STATE_REVERTED";

    public static final String TextLayerSettings_COLOR_FILTER="TextLayerSettings.COLOR_FILTER";

    public static final String ColorAdjustmentSettings_HIGHLIGHT="ColorAdjustmentSettings.HIGHLIGHT";

    public static final String HistoryState_HISTORY_CREATED="HistoryState.HISTORY_CREATED";

    public static final String VideoCompositionSettings_VIDEO_LIST_CHANGED="VideoCompositionSettings.VIDEO_LIST_CHANGED";

    public static final String WatermarkSettings_ALIGNMENT="WatermarkSettings.ALIGNMENT";

    public static final String EditorLoadSettings_SOURCE_IMAGE_INFO="LoadState.SOURCE_INFO";

    public static final String WatermarkSettings_IMAGE="WatermarkSettings.IMAGE";

    public static final String OverlaySettings_BACKDROP="OverlaySettings.BACKDROP";

    public static final String EditorShowState_SHUTDOWN="EditorShowState.SHUTDOWN";

    public static final String EditorShowState_TRANSFORMATION="EditorShowState.TRANSFORMATION";

    public static final String WatermarkSettings_SIZE="WatermarkSettings.SIZE";

    public static final String TrimSettings_END_TIME="TrimSettings.END_TIME";

    public static final String TextDesignLayerSettings_EDIT_MODE="TextDesignLayerSettings.EDIT_MODE";

    public static final String FocusSettings_STATE_REVERTED="FocusSettings.STATE_REVERTED";

    public static final String TextDesignLayerSettings_STATE_REVERTED="TextDesignLayerSettings.STATE_REVERTED";

    public static final String TextDesignLayerSettings_COLOR_FILTER="TextDesignLayerSettings.COLOR_FILTER";

    public static final String EditorShowState_LAYER_DOUBLE_TAPPED="EditorShowState.LAYER_DOUBLE_TAPPED";

    public static final String FrameSettings_FRAME_OPACITY="FrameSettings.FRAME_OPACITY";

    public static final String UiStateMenu_LEAVE_AND_REVERT_TOOL="UiStateMenu.LEAVE_AND_REVERT_TOOL";

    public static final String OverlaySettings_INTENSITY="OverlaySettings.INTENSITY";

    public static final String ImageStickerLayerSettings_POSITION="ImageStickerLayerSettings.POSITION";

    public static final String OverlaySettings_BLEND_MODE="OverlaySettings.BLEND_MODE";

    public static final String OverlaySettings_PLACEMENT_INVALID="OverlaySettings.PLACEMENT_INVALID";

    public static final String ProgressState_PREVIEW_IDLE="ProgressState.PREVIEW_IDLE";

    public static final String EditorShowState_EDIT_MODE="EditorShowState.EDIT_MODE";

    public static final String VideoState_VIDEO_SELECTED="VideoState.VIDEO_SELECTED";

    public static final String FrameSettings_FRAME_CONFIG="FrameSettings.FRAME_CONFIG";

    public static final String TextDesignLayerSettings_CONFIG="TextDesignLayerSettings.CONFIG";

    public static final String ColorAdjustmentSettings_TEMPERATURE="ColorAdjustmentSettings.TEMPERATURE";

    public static final String UiConfigFilter_CONFIG_DIRTY="UiConfigFilter_CONFIG_DIRTY";

    public static final String ImageStickerLayerSettings_COLOR_FILTER="ImageStickerLayerSettings.COLOR_FILTER";

    public static final String ColorAdjustmentSettings_SATURATION="ColorAdjustmentSettings.SATURATION";

    public static final String LoadState_SOURCE_IS_UNSUPPORTED="LoadState.SOURCE_IS_UNSUPPORTED";

    public static final String UiConfigAdjustment_CONFIG_DIRTY="UiConfigAdjustment_CONFIG_DIRTY";

    public static final String EditorShowState_LAYER_TOUCH_END="EditorShowState.LAYER_TOUCH_END";

    public static final String VideoState_VIDEO_START="VideoState.VIDEO_START";

    public static final String TrimSettings_STATE_REVERTED="TrimSettings.STATE_REVERTED";

    public static final String UiStateMenu_CANCEL_AND_LEAVE="UiStateMenu.CANCEL_AND_LEAVE";

    public static final String AudioOverlaySettings_AUDIO_LEVEL="AudioOverlaySettings.AUDIO_LEVEL";

    public static final String ColorPipetteState_SMOOTH_COLOR="ColorPipetteState.SMOOTH_COLOR";

    public static final String LoadState_IS_READY="LoadState.IS_READY";

    public static final String VideoCompositionSettings_VIDEO_SELECTED="VideoState.VIDEO_SELECTED";

    public static final String AudioOverlaySettings_AUDIO_OVERLAY_SELECTED="AudioOverlaySettings.AUDIO_OVERLAY_SELECTED";

    public static final String UiState_TOOL_MODE="UiState.TOOL_MODE";

    public static final String ProgressState_EXPORT_PROGRESS="ProgressState.EXPORT_PROGRESS";

    public static final String ColorAdjustmentSettings_SHADOW="ColorAdjustmentSettings.SHADOW";

    public static final String EditorShowState_PREVIEW_RENDERED="EditorShowState.PREVIEW_RENDERED";

    public static final String TransformSettings_CROP_RECT="TransformSettings.CROP_RECT";

    public static final String TransformSettings_HORIZONTAL_FLIP="TransformSettings.HORIZONTAL_FLIP";

    public static final String ImageStickerLayerSettings_CONFIG="ImageStickerLayerSettings.CONFIG";

    public static final String ColorAdjustmentSettings_WHITES="ColorAdjustmentSettings.WHITES";

    public static final String AbstractSaveSettings_JPEG_QUALITY="AbstractSaveSettings.JPEG_QUALITY";

    public static final String EditorShowState_CANCELED_LAYER_EVENT="EditorShowState.CANCELED_LAYER_EVENT";

    public static final String LoadSettings_STATE_REVERTED="LoadSettings.STATE_REVERTED";

    public static final String SmartStickerConfig_WEATHER_PROVIDER_UPDATE="SmartStickerConfig.WEATHER_PROVIDER_UPDATE";

    public static final String UiStateMenu_ACCEPT_AND_LEAVE="UiStateMenu.ACCEPT_AND_LEAVE";

    public static final String UiStateVideoLibrary__SELECTED_CATEGORY_CHANGED="UiStateVideoLibrary..SELECTED_CATEGORY_CHANGED";

    public static final String FocusSettings_POSITION="FocusSettings.POSITION";

    public static final String TextDesignLayerSettings_FLIP_VERTICAL="TextDesignLayerSettings.FLIP_VERTICAL";

    public static final String LayerListSettings_BACKGROUND_COLOR="LayerListSettings.BACKGROUND_COLOR";

    public static final String TextDesignLayerSettings_SEED="TextDesignLayerSettings.SEED";

    public static final String ColorAdjustmentSettings_BRIGHTNESS="ColorAdjustmentSettings.BRIGHTNESS";

    public static final String TextDesignLayerSettings_POSITION="TextDesignLayerSettings.POSITION";

    public static final String UiConfigVideoLibrary_CONFIG_DIRTY="UiConfigVideoLibrary.CONFIG_DIRTY";

    public static final String UiStateSticker_SELECTED_CATEGORY_CHANGED="UiStateSticker.SELECTED_CATEGORY_CHANGED";

    public static final String TextLayerSettings_BOUNDING_BOX="TextLayerSettings.BOUNDING_BOX";

    public static final String UiStateMenu_ENTER_TOOL="UiStateMenu.ENTER_TOOL";

public void init(ly.img.android.pesdk.backend.model.state.manager.SettingsHolderInterface settingsHolder) {
        
    new ly.img.android.pesdk.assets.font.text_design.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.text_design.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.font.shared.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.overlay.basic.INIT(settingsHolder);
            
    new ly.img.android.pesdk.backend.overlay.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.font.basic.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.video_composition.INIT(settingsHolder);
            
    new ly.img.android.pesdk.backend.sticker_smart.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.filter.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.all.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.audio_composition.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.sticker.shapes.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.adjustment.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.sticker.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.focus.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.frame.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.text.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.video_library.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.overlay.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.filter.basic.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.video_trim.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.video_main.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.transform.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.font.smart_sticker.INIT(settingsHolder);
            
    new ly.img.android.pesdk.backend.frame.INIT(settingsHolder);
            
    new ly.img.android.pesdk.backend.text_design.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.photo_main.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.sticker.animated.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.sticker.emoticons.INIT(settingsHolder);
            
    new ly.img.android.pesdk.ui.brush.INIT(settingsHolder);
            
    new ly.img.android.pesdk.assets.frame.basic.INIT(settingsHolder);
            
}
        
        }