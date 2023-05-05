package com.fini.pro.sportsvet.utils.dialView;

import static java.lang.Math.PI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.annotation.NonNull;

import com.fini.pro.sportsvet.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DialView extends View {

    private static final int CENTER_OFFSET_VERTICAL = 40;
    private static final float VELOCITY_THRESHOLD = 0.05f;

    /**
     * User is not touching the list
     */
    private static final int TOUCH_STATE_RESTING = 0;

    /**
     * User is touching the list and right now it's still a "click"
     */
    private static final int TOUCH_STATE_CLICK = 1;

    /**
     * User is scrolling the list
     */
    private static final int TOUCH_STATE_SCROLL = 2;

    /**
     * Velocity tracker used to get fling velocities
     */
    private VelocityTracker velocityTracker;

    /**
     * Current touch state
     */
    private int touchState = TOUCH_STATE_RESTING;

    private static final int RADIANS_PER_SECOND = 1;

    private double tickCount;
    private double currentTheta;
    private final Rect bounds = new Rect();
    private double minAngleTheta;
    private double maxAngleTheta;
    private double tickGapAngle;
    private long currentTime;
    private int maxValue;
    private int minValue;
    private int lineInterval;
    private int textSize;
    private int leastCount;
    private int centerPadding;
    private int angleToCompare;
    private int startColor;
    private int endColor;
    private int paintTextColor;
    private int paintLineColor;
    private int paintArcColor;

    private int centerX;
    private int centerY;
    private int radius;
    private Paint paintInnerCircle;
    private Paint paintArc;
    private Paint paintLines;
    private Paint paintText;

    private OnDialValueChangeListener onDialValueChangeListener;

    public void setOnDialValueChangeListener(OnDialValueChangeListener listener) {
        this.onDialValueChangeListener = listener;
    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private float initVelocity = 0.5f;

    /**
     * Physics implementation
     */

    Runnable dynamicsRunnable = new Runnable() {
        @Override
        public void run() {
            if (Math.abs(initVelocity) < VELOCITY_THRESHOLD) {
                return;
            }
            long newTime = System.nanoTime();
            long deltaNano = (newTime - currentTime);
            double deltaSecs = ((double) deltaNano) / 1000000000;
            currentTime = newTime;
            float finalVelocity;
            /**
             * Knob deceleration
             */
            float deceleration = 10f;
            if (initVelocity > 0) {
                finalVelocity = (float) (initVelocity - deceleration * deltaSecs);
            } else {
                finalVelocity = (float) (initVelocity + deceleration * deltaSecs);
            }
            if (initVelocity * finalVelocity < 0) {
                return;
            }
            rotate(finalVelocity * deltaSecs);
            invalidate();
            DialView.this.postDelayed(dynamicsRunnable, 1000 / 60);
            initVelocity = finalVelocity;
        }
    };

    /**
     * @param attrs   are the attributes containing the values given by user
     * @param context context of the activity to use this view class
     */

    private void init(AttributeSet attrs, Context context) {
        paintInnerCircle = new Paint();
        paintArc = new Paint();
        paintLines = new Paint();
        paintText = new Paint();

        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        paintLines.setAntiAlias(true);
        paintText.setTextAlign(Paint.Align.RIGHT);


        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialView);
            lineInterval = typedArray.getInt(R.styleable.DialView_lineInterval, 0);
            maxValue = typedArray.getInt(R.styleable.DialView_maxValue, 0);
            minValue = typedArray.getInt(R.styleable.DialView_minValue, 0);
            leastCount = typedArray.getInt(R.styleable.DialView_leastCount, 0);
            centerPadding = typedArray.getInt(R.styleable.DialView_centerPadding, 0);
            textSize = typedArray.getInt(R.styleable.DialView_textSize, 0);
//            dialDirection = typedArray.getInt(R.styleable.DialView_dialDirection, 0);
            tickGapAngle = ((double) typedArray.getFloat(R.styleable.DialView_tickGapAngle, 0) / (double) 180) * PI;

            startColor = typedArray.getColor(R.styleable.DialView_startColor, 0);
            endColor = typedArray.getColor(R.styleable.DialView_endColor, 0);
            paintLineColor = typedArray.getColor(R.styleable.DialView_paintLineColor, 0);
            paintTextColor = typedArray.getColor(R.styleable.DialView_paintTextColor, 0);
            paintArcColor = typedArray.getColor(R.styleable.DialView_paintArcColor, 0);
            typedArray.recycle();
        }

        if (minValue >= maxValue) {
            maxValue = minValue;
            minValue = 0;
        }

        paintInnerCircle.setStyle(Paint.Style.FILL);
        paintInnerCircle.setFilterBitmap(true);
        paintInnerCircle.setShader(new LinearGradient(0, 0, 0, getHeight(), endColor, startColor, Shader.TileMode.CLAMP));
        paintLines.setColor(paintLineColor);
        paintText.setColor(paintTextColor);
        paintArc.setColor(paintArcColor);

        currentTheta = - PI;
        angleToCompare = 180;

        paintText.setTextSize(textSize);
    }

    /**
     * @param widthMeasureSpec  is the width of canvas
     * @param heightMeasureSpec is the height of canvas
     *                          used to calculate all the radius and centerX and centerY
     *                          MaxAngleTheta calculated
     */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        tickCount = ((maxValue - minValue) / leastCount) + 1;
        maxAngleTheta = (((tickCount - 1) * tickGapAngle));
        minAngleTheta = 0;

        centerX = getMeasuredWidth() / 2;
        radius = getMeasuredWidth() / 2 - centerPadding;
        centerY = getMeasuredHeight();// + CENTER_OFFSET_VERTICAL;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        makeRadGrad(canvas);
    }

    /**
     * @param canvas to draw the circle and lines
     */

    private void makeRadGrad(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, paintInnerCircle);
//        canvas.drawCircle(centerX, centerY, radius, paintArc);

        for (double i = minValue; i < tickCount; i++) {
            double angle = i * tickGapAngle;

            double lineTheta = angle + PI;
            float startX = (float) (((radius + 10) * Math.cos(lineTheta)) + centerX);
            float startY = (float) (((radius + 10) * Math.sin(lineTheta)) + centerY);

            int lineWidth = 0;
            int lineHeight = 0;
            if (i == 0 || i == 25|| i == 50 || i == 75 || i == 100) { // 0, 25, 50, 75, 100
                lineWidth = 8;
                lineHeight = 50;
                paintLineColor = getContext().getColor(R.color.purple1);

                float textPointX = (float) (((radius + lineHeight + 20) * Math.cos(lineTheta)) + centerX);
                float textPointY = (float) (((radius + lineHeight + 20) * Math.sin(lineTheta)) + centerY);

                addingTextValuesToDial(canvas, lineTheta, (int) i, textPointX, textPointY);
            }
            else if (i % 5 == 0) {
                lineWidth = 5;
                lineHeight = 40;
                paintLineColor = getContext().getColor(R.color.purple2);
            }

            float endX = (float) (((radius + lineHeight) * Math.cos(lineTheta)) + centerX);
            float endY = (float) (((radius + lineHeight) * Math.sin(lineTheta)) + centerY);

            if (lineHeight > 0) {
                paintLines.setColor(paintLineColor);
                paintLines.setStrokeWidth(lineWidth);
                canvas.drawLine(startX, startY, endX, endY, paintLines);
            }

            double newTheta = angle - currentTheta;
            int newThetaInDegree = (int) (newTheta / PI * 180);
            NumberFormat formatter = new DecimalFormat("00");
            String value = formatter.format(i);
            if (i == 1 || i == 2) {
                value = formatter.format(0);
            }

            if (newThetaInDegree == angleToCompare) {
//                Log.e("Current Value", angle + ", " + currentTheta + ", " + i);
                if (onDialValueChangeListener != null) {
                    onDialValueChangeListener.onDialValueChanged(value, maxValue);
                }
            }
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getContext().getColor(R.color.purple2));

        Path path = new Path();
        path.moveTo((float) ((radius * Math.cos(currentTheta)) + centerX), (float) ((radius * Math.sin(currentTheta)) + centerY));
        path.lineTo((float) ((15 * Math.cos(currentTheta + PI / 2)) + centerX), (float) ((15 * Math.sin(currentTheta + PI / 2)) + centerY));
        path.lineTo((float) ((15 * Math.cos(currentTheta - PI / 2)) + centerX), (float) ((15 * Math.sin(currentTheta - PI / 2)) + centerY));
        path.lineTo((float) ((radius * Math.cos(currentTheta)) + centerX), (float) ((radius * Math.sin(currentTheta)) + centerY));
        path.close();

        canvas.drawPath(path, paint);
        canvas.drawCircle(centerX, centerY, 30, paint);

//        float needleEndX = (float) ((radius * Math.cos(currentTheta)) + centerX);
//        float needleEndY = (float) ((radius * Math.sin(currentTheta)) + centerY);
//
//        paintLines.setColor(paintLineColor);
//        paintLines.setStrokeWidth(10); // Line Width
//        canvas.drawLine(centerX, centerY, needleEndX, needleEndY, paintLines);
    }

    /**
     * @param canvas is the canvas on which the text will be drawn
     * @param angle  is the angle at which the text is to be drawn
     * @param realValue  is the difference from minimum value to max value of dial
     *               divided by line interval
     * @param startX starting x from where the text will start
     * @param startY starting Y from where the text will start
     */

    private void addingTextValuesToDial(
            Canvas canvas,
            double angle,
            int realValue,
            float startX,
            float startY
    ) {
        double newAngle = angle / PI * 180;
        int angleInteger = (int) newAngle;
        paintText.getTextBounds(String.valueOf(realValue), 0, 1, bounds);

        // to be extracted afterwards when initialised by user
        // for bottom
        if (angleInteger >= angleToCompare && angleInteger < 360) {
            canvas.drawText(String.valueOf(realValue), startX + 8 * 3 / 4, startY - 11 * 2 / 5, paintText);
        }
        else {
            canvas.drawText(String.valueOf(realValue), (float) (startX + 8 * 0.9), startY - 11 * 2 / 4, paintText);
        }
    }


    private float lastTouchXCircle;
    private float lastTouchYCircle;
    private float xcircle;
    private float ycircle;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.getParent() != null) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }
                startTouch(event);
                return true;

            case MotionEvent.ACTION_MOVE:
                if (this.getParent() != null) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }
                duringTouch(event);
                return true;

            case MotionEvent.ACTION_UP:
                if (this.getParent() != null) {
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                }
                processTouch(event);
                return true;
            default:
                return false;
        }
    }

    private void duringTouch(final MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        xcircle = eventX - centerX;
        ycircle = centerY - eventY;

        double originalAngle = Math.atan2(lastTouchYCircle, lastTouchXCircle);
        double newAngle = Math.atan2(ycircle, xcircle);

        double delta = originalAngle - newAngle;

        rotate(delta);
        touchState = TOUCH_STATE_SCROLL;
        processTouch(event);
    }

    /**
     * @param event when user starts touch so that there is initial touch of x and y
     *              initialising the velocity and adding movement
     */

    private void startTouch(final MotionEvent event) {
        // user is touching the list -> no more fling
        removeCallbacks(dynamicsRunnable);

        //for bottom
        lastTouchXCircle = event.getX() - centerX;
        lastTouchYCircle = centerY - event.getY();

        // obtain a velocity tracker and feed it its first event
        velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);

        // we don't know if it's a click or a scroll yet, but until we know
        // assume it's a click
        touchState = TOUCH_STATE_CLICK;
        // getting the event of dialview
//        if (onValueChangeListener != null) {
//            onValueChangeListener.onMotionEvent(event);
//        }
    }

    /**
     * @param delta method used to rotate the dial between the max angle and min angle theta
     */

    private void rotate(double delta) {
        currentTheta += delta;

        int angleToCompare = 0;
        Log.e("currentTheta - 0", String.valueOf(currentTheta * 180 / PI));
        if ((currentTheta <= minAngleTheta + (angleToCompare * PI / 180)
                && currentTheta >= (angleToCompare * PI / 180) - (maxAngleTheta))) {
            invalidate();
            lastTouchXCircle = xcircle;
            lastTouchYCircle = ycircle;
        } else {
            if (currentTheta > PI / 2)
                currentTheta = - PI;//3 * PI / 2;
            else if (currentTheta > 0)
                currentTheta = 0;//(angleToCompare * PI / 180) - maxAngleTheta;
        }
        Log.e("currentTheta - 1", String.valueOf(currentTheta * 180 / PI));
    }

    /**
     * @param event method used when user moves the dial in motion
     *              to add the velocity and calculate the velocity and
     */

    public boolean processTouch(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                break;

            case MotionEvent.ACTION_UP:
                float velocity = 0;
                if (touchState == TOUCH_STATE_SCROLL) {
                    velocityTracker.computeCurrentVelocity(RADIANS_PER_SECOND);

                    velocity = -1 * velocityTracker.getXVelocity();
                }
                endTouch(velocity);
                break;

            default:
                // todo nothing
                break;
        }
        return true;
    }

    private void endTouch(final float velocity) {
        currentTheta = - PI;
        // recycle the velocity tracker
        velocityTracker.recycle();
        velocityTracker = null;
        currentTime = System.nanoTime();

        //for bottom
        initVelocity = -1 * velocity;

        post(dynamicsRunnable);

        // reset touch state
        touchState = TOUCH_STATE_RESTING;

        invalidate();
    }

    public interface OnDialValueChangeListener {
        void onDialValueChanged(String value, int maxValue);
    }

}
