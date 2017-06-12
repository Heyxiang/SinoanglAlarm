/**
 * @author Raghav Sood
 * @version 1
 * @date 26 January, 2013
 */
package com.sinoangel.sazalarm;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sinoangel.sazalarm.base.MyApplication;


/**
 * The Class CircularSeekBar.
 */
public class ZCircularSeekBar extends View {
    private static final String STATE_PARENT = "parent";
    private static final String STATE_ANGLE = "angle";

    /***
     * 事件监听
     */
    private OnCircleSeekBarChangeListener mOnCircleSeekBarChangeListener;

    /**
     * 圆环的宽度
     */
    private int mColorWheelStrokeWidth;

    /**
     * 游标所在圆环半径
     */
    private int mPointerRadius;

    /**
     * 内环的矩形
     */
    private RectF mColorSmallRectangle = new RectF();

    /**
     * 去掉画笔宽度的矩形
     */
    private RectF mColorWheelRectangle = new RectF();

    /**
     * 可显示的最大矩形
     */
    private RectF mColorBigRectangle = new RectF();

    /**
     * {@code true} 点击游标 {@code false} 停止
     *
     * @see #onTouchEvent(MotionEvent)
     */
    private boolean mUserIsMovingPointer = false;

    /**
     *
     */
    private float mTranslationOffset;

    /**
     * 圆环半径 Note: (Re)在onMeasure计算{@link #onMeasure(int, int)}
     */
    private float mColorWheelRadius;

    private float mAngle;
    private double pro;
    private int max = 144;
    private String color_attr;
    private String wheel_color_attr, wheel_unactive_color_attr,
            pointer_color_attr, pointer_halo_color_attr;
    private int init_position;
    private boolean block_end;
    private boolean block_start;
    private int last_radians;


    private int arc_finish_radians = 270;
    // 左下角开始
    private int start_arc = 135;

    private float[] pointerPosition;
    private RectF mColorCenterHaloRectangle = new RectF();
    private int end_wheel;

    public ZCircularSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public ZCircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ZCircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.HoloCircleSeekBar, defStyle, 0);

        initAttributes(a);
        a.recycle();

        arc_finish_radians = (int) calculateAngleFromText(init_position) - 90;

        if (arc_finish_radians > end_wheel)
            arc_finish_radians = end_wheel;
        mAngle = calculateAngleFromRadians(arc_finish_radians > end_wheel ? end_wheel
                : arc_finish_radians);
        pro = calculateTextFromAngle(arc_finish_radians);

        invalidate();
    }

    private void initAttributes(TypedArray a) {

        max = a.getInteger(R.styleable.HoloCircleSeekBar_max, max);

        color_attr = a.getString(R.styleable.HoloCircleSeekBar_ccolor);
        wheel_color_attr = a
                .getString(R.styleable.HoloCircleSeekBar_wheel_active_color);
        wheel_unactive_color_attr = a
                .getString(R.styleable.HoloCircleSeekBar_wheel_unactive_color);
        pointer_color_attr = a
                .getString(R.styleable.HoloCircleSeekBar_pointer_color);
        pointer_halo_color_attr = a
                .getString(R.styleable.HoloCircleSeekBar_pointer_halo_color);

        a.getString(R.styleable.HoloCircleSeekBar_text_color);

        a.getInteger(R.styleable.HoloCircleSeekBar_text_size, 95);

        init_position = a.getInteger(
                R.styleable.HoloCircleSeekBar_init_position, 0);

        start_arc = a.getInteger(R.styleable.HoloCircleSeekBar_start_angle, 0);
        end_wheel = a.getInteger(R.styleable.HoloCircleSeekBar_end_angle, 360);

        last_radians = end_wheel;

        if (init_position < start_arc)
            init_position = calculateTextFromStartAngle(start_arc);

        if (color_attr != null) {
            try {
                Color.parseColor(color_attr);
            } catch (IllegalArgumentException e) {
            }
            Color.parseColor(color_attr);
        }

        if (wheel_color_attr != null) {
            try {
                Color.parseColor(wheel_color_attr);
            } catch (IllegalArgumentException e) {
            }

        }
        if (wheel_unactive_color_attr != null) {
            try {
                Color.parseColor(wheel_unactive_color_attr);
            } catch (IllegalArgumentException e) {
            }

        }

        if (pointer_color_attr != null) {
            try {
                Color.parseColor(pointer_color_attr);
            } catch (IllegalArgumentException e) {
            }

        }

        if (pointer_halo_color_attr != null) {
            try {
                Color.parseColor(pointer_halo_color_attr);
            } catch (IllegalArgumentException e) {
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mTranslationOffset, mTranslationOffset);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timer_bk);

        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect((int) mColorBigRectangle.top, (int) mColorBigRectangle.left, (int) mColorBigRectangle.right, (int) mColorBigRectangle.bottom), null);

        Paint mArcColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcColor.setColor(ContextCompat.getColor(MyApplication.getInstance(), R.color.green_6e));
        mArcColor.setStyle(Paint.Style.STROKE);
        mArcColor.setStrokeWidth(mColorWheelStrokeWidth);

        // 滑动中的弧
        canvas.drawArc(mColorWheelRectangle, start_arc + 270,
                arc_finish_radians > end_wheel ? end_wheel - start_arc
                        : arc_finish_radians - start_arc, false, mArcColor);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(MyApplication.getInstance(), R.color.font_white));
        paint.setAntiAlias(true);
        canvas.drawCircle(pointerPosition[0],
                pointerPosition[1], mPointerRadius, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);


        mColorWheelStrokeWidth = min / 14;
        mPointerRadius = mColorWheelStrokeWidth / 2;

        mTranslationOffset = min * 0.5f;
        mColorWheelRadius = mTranslationOffset - mPointerRadius;

        mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius,
                mColorWheelRadius, mColorWheelRadius);

        mColorBigRectangle.set(-mTranslationOffset, -mTranslationOffset, mTranslationOffset, mTranslationOffset);

        float val = mTranslationOffset - mColorWheelStrokeWidth * 1.25f;
        mColorSmallRectangle.set(-val, -val, val, val);

        mColorCenterHaloRectangle.set(-mColorWheelRadius / 2,
                -mColorWheelRadius / 2, mColorWheelRadius / 2,
                mColorWheelRadius / 2);


        pointerPosition = calculatePointerPosition(mAngle);
    }

    //圆形游标 获取坐标点
    private float[] calculatePointerPosition(float angle) {
        float x = (float) (mColorWheelRadius * Math.cos(angle));
        float y = (float) (mColorWheelRadius * Math.sin(angle));
        return new float[]{x, y};
    }


    private double calculateTextFromAngle(float angle) {
        return angle * 2 / 5;
    }

    private int calculateTextFromStartAngle(float angle) {
        float m = angle;

        float f = (end_wheel - start_arc) / m;

        return (int) (max / f);
    }

    private double calculateAngleFromText(int position) {
        if (position == 0 || position >= max)
            return (float) 90;

        double f = (double) max / (double) position;

        double f_r = 360 / f;

        double ang = f_r + 90;

        return ang;

    }

    private int calculateRadiansFromAngle(float angle) {
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }
        int radians = (int) ((unit * 360) - ((360 / 4) * 3));
        if (radians < 0)
            radians += 360;
        return radians;
    }

    private float calculateAngleFromRadians(int radians) {
        return (float) (((radians + 270) * (2 * Math.PI)) / 360);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mTranslationOffset;
        float y = event.getY() - mTranslationOffset;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAngle = (float) Math.atan2(y, x);

                block_end = false;
                block_start = false;
                mUserIsMovingPointer = true;

                arc_finish_radians = calculateRadiansFromAngle(mAngle);

                if (!block_end && !block_start) {
                    pro = calculateTextFromAngle(arc_finish_radians);
                    invalidate();
                    if (mOnCircleSeekBarChangeListener != null)
                        mOnCircleSeekBarChangeListener.onProgressChanged(this,
                                pro, true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mUserIsMovingPointer) {
                    mAngle = (float) Math.atan2(y, x);
                    int radians = calculateRadiansFromAngle(mAngle);
                    if (last_radians > radians) {//减小
//                        AppUtils.outputLog(radians + "xiao" + last_radians);
                        if (last_radians == 1 && radians == 0) {
                            block_start = true;
                        } else if (radians == 0) {
                            block_end = true;
                            block_start = false;
                        } else {
                            last_radians = radians;
                            block_start = false;
                        }
                        //正向转过起点
                    } else if (last_radians < radians) {//增大
//                        AppUtils.outputLog(radians + "da" + last_radians);
                        //反向转过起点
                        if (last_radians == 359 && radians == 0) {
                            block_end = true;
                        } else if (radians == 0) {
                            block_start = true;
                            block_end = false;
                        } else {
                            last_radians = radians;
                            block_end = false;
                        }

                    }
                    if (block_end) {
                        arc_finish_radians = 360;
                        pro = max;
                    } else if (block_start) {
                        arc_finish_radians = 0;
                        pro = 0;
                    } else {
                        arc_finish_radians = calculateRadiansFromAngle(mAngle);
                        pro = calculateTextFromAngle(arc_finish_radians);
                    }

                    invalidate();

                    if (mOnCircleSeekBarChangeListener != null)
                        mOnCircleSeekBarChangeListener.onProgressChanged(this,
                                pro, true);


                }
                break;
            case MotionEvent.ACTION_UP:
                mUserIsMovingPointer = false;
                break;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable(STATE_PARENT, superState);
        state.putFloat(STATE_ANGLE, mAngle);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable(STATE_PARENT);
        super.onRestoreInstanceState(superState);

        mAngle = savedState.getFloat(STATE_ANGLE);
        arc_finish_radians = calculateRadiansFromAngle(mAngle);
        pro = calculateTextFromAngle(arc_finish_radians);

    }

    public void setOnSeekBarChangeListener(OnCircleSeekBarChangeListener l) {
        mOnCircleSeekBarChangeListener = l;
    }

    public interface OnCircleSeekBarChangeListener {
        void onProgressChanged(ZCircularSeekBar seekBar,
                               double progress, boolean fromUser);

    }

    public double getPross() {
        return pro;
    }

    public void setPross(double val) {
        pro = val;
        arc_finish_radians = (int) (val * 2.5);
        mAngle = calculateAngleFromRadians(arc_finish_radians > end_wheel ? end_wheel
                : arc_finish_radians);
        invalidate();
    }
}
