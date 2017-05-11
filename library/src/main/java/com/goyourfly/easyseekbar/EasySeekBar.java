package com.goyourfly.easyseekbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by gaoyufei on 2017/5/11.
 * This is a custom view aim to create a seekBar
 * which can countdown
 */

public class EasySeekBar extends View {
    private static final String TAG = "CountdownSeekBar";

    private static final String[] DEFAULT_SHOWN_TEXT = new String[]{"1", "2", "3", "4", "5"};
    private static final int DEFAULT_SEEK_BAR_RADIUS = 20;
    private static final int DEFAULT_TEXT_SIZE = 30;
    private static final int DEFAULT_TEXT_MARGIN_BOTTOM = 50;
    private static final int DEFAULT_TEXT_DEFAULT_COLOR = 0xFF999999;
    private static final int DEFAULT_BACKGROUND_POINT_RADIUS = 10;
    private static final int DEFAULT_LINE_HEIGHT = 10;
    private static final int DEFAULT_FOREGROUND_COLOR = 0xFFFF0000;
    private static final int DEFAULT_BACKGROUND_COLOR = 0XFFCCCCCC;
    private static final int DEFAULT_DISABLE_COVER = 0X99FFFFFF;
    private static final int DEFAULT_HORIZONTAL_PADDING = 40;

    private static final int STATE_IDLE = 0;
    private static final int STATE_TOUCH = 1;

    /**
     * 顶部要显示的文字
     */
    private CharSequence[] shownText = DEFAULT_SHOWN_TEXT;

    private int textSize = DEFAULT_TEXT_SIZE;
    /**
     * Text 距离底部距离
     */
    private int textMarginBottom = DEFAULT_TEXT_MARGIN_BOTTOM;

    private int textDefaultColor = DEFAULT_TEXT_DEFAULT_COLOR;
    /**
     * 拖拽点的半径
     */
    private int seekBarRadius = DEFAULT_SEEK_BAR_RADIUS;

    /**
     * 背景点半径
     */
    private int backgroundPointRadius = DEFAULT_BACKGROUND_POINT_RADIUS;


    /**
     * 线的高度
     */
    private int lineHeight = DEFAULT_LINE_HEIGHT;


    /**
     * 高亮颜色
     */
    private int foregroundColor = DEFAULT_FOREGROUND_COLOR;

    /**
     * 背景色
     */
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;


    /**
     * PaddingLeft
     */
    private int paddingLeft = DEFAULT_HORIZONTAL_PADDING;


    /**
     * PaddingRight
     */
    private int paddingRight = DEFAULT_HORIZONTAL_PADDING;


    private int paddingTop = DEFAULT_HORIZONTAL_PADDING;

    private int paddingBottom = DEFAULT_HORIZONTAL_PADDING;

    /**
     * 默认彩色画笔
     */
    private Paint mPaint;


    /**
     * ItemLocation
     */
    private Point[] perItemLocation;

    /**
     * 当前选中Index
     */
    private int currentIndex = 0;

    private int state = STATE_IDLE;

    /**
     * 当前TouchX
     */
    private float touchX = 0;

    private ValueAnimator mValueAnimator;

    private OnProgressChangeListener mListener;

    public EasySeekBar(Context context) {
        super(context);
        init();
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    public EasySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void init() {
        paddingLeft = dpToPx(16);
        paddingRight = dpToPx(16);


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(foregroundColor);
        mPaint.setStrokeWidth(lineHeight);
        mPaint.setTextSize(textSize);

    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs,
                R.styleable.EasySeekBar);
        seekBarRadius = attributes.getDimensionPixelSize(R.styleable.EasySeekBar_bar_radius, dpToPx(8));
        backgroundPointRadius = attributes.getDimensionPixelOffset(R.styleable.EasySeekBar_second_bar_radius, dpToPx(4));
        textSize = attributes.getDimensionPixelSize(R.styleable.EasySeekBar_text_size, dpToPx(12));
        textMarginBottom = attributes.getDimensionPixelOffset(R.styleable.EasySeekBar_text_margin, dpToPx(12));
        textDefaultColor = attributes.getColor(R.styleable.EasySeekBar_text_color, DEFAULT_TEXT_DEFAULT_COLOR);
        lineHeight = attributes.getDimensionPixelOffset(R.styleable.EasySeekBar_line_height, dpToPx(3));
        foregroundColor = attributes.getColor(R.styleable.EasySeekBar_highlight_color, DEFAULT_FOREGROUND_COLOR);
        backgroundColor = attributes.getColor(R.styleable.EasySeekBar_default_color, DEFAULT_BACKGROUND_COLOR);
        shownText = attributes.getTextArray(R.styleable.EasySeekBar_values);
        if (shownText == null)
            shownText = DEFAULT_SHOWN_TEXT;
        if (shownText.length < 2) {
            throw new ArrayIndexOutOfBoundsException("Array must more than 1");
        }

        int[] androidAttrs = new int[]{android.R.attr.paddingLeft, android.R.attr.paddingTop, android.R.attr.paddingRight, android.R.attr.paddingBottom};
        TypedArray androidAttributes = getContext().obtainStyledAttributes(
                attrs,
                androidAttrs
        );
        paddingLeft = androidAttributes.getDimensionPixelSize(0, 0);
        paddingTop = androidAttributes.getDimensionPixelSize(1, 0);
        paddingRight = androidAttributes.getDimensionPixelSize(2, 0);
        paddingBottom = androidAttributes.getDimensionPixelSize(3, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = textSize + textMarginBottom + paddingTop + paddingBottom + seekBarRadius * 2;
        setMeasuredDimension(widthMeasureSpec, height);
        doMeasure();
    }

    private void doMeasure() {
        int width = getMeasuredWidth();

        int itemSize = shownText.length;

        // 计算每个点所在X的位置
        int perItemSize = (width - paddingLeft - paddingRight) / (itemSize - 1);
        perItemLocation = new Point[itemSize];
        int y = paddingTop + textSize + textMarginBottom + seekBarRadius / 2;
        for (int i = 0; i < itemSize; i++) {
            perItemLocation[i] = new Point(paddingLeft + perItemSize * i, y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point startPoint = perItemLocation[0];
        Point endPoint = perItemLocation[perItemLocation.length - 1];

//      绘制背景线条
        mPaint.setColor(backgroundColor);
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, mPaint);

//      绘制点和文字
        for (int i = 0; i < perItemLocation.length; i++) {
            Point point = perItemLocation[i];

            if (i == currentIndex) {
                mPaint.setColor(foregroundColor);
            } else {
                mPaint.setColor(textDefaultColor);
            }
            canvas.drawText(shownText[i].toString(),
                    point.x - mPaint.measureText(shownText[i].toString()) / 2,
                    point.y - seekBarRadius / 2 - textMarginBottom, mPaint);

            if (i <= currentIndex) {
                mPaint.setColor(foregroundColor);
            } else {
                mPaint.setColor(backgroundColor);
            }
            canvas.drawCircle(point.x, point.y, backgroundPointRadius, mPaint);
        }

        mPaint.setColor(foregroundColor);
        Point point = perItemLocation[currentIndex];
        if (state == STATE_IDLE) {
            canvas.drawLine(startPoint.x, startPoint.y, point.x, point.y, mPaint);
            canvas.drawCircle(point.x, point.y, seekBarRadius, mPaint);
        } else if (state == STATE_TOUCH) {
            canvas.drawLine(startPoint.x, startPoint.y, touchX, point.y, mPaint);
            canvas.drawCircle(touchX, point.y, seekBarRadius, mPaint);
        }

        if (!isEnabled()) {
            canvas.drawColor(DEFAULT_DISABLE_COVER);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mValueAnimator != null && mValueAnimator.isRunning()) {
                    mValueAnimator.cancel();
                    mValueAnimator = null;
                }
                state = STATE_TOUCH;
                touchX = event.getX();
                currentIndex = getCurrentIndex(touchX);
                break;
            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                currentIndex = getCurrentIndex(touchX);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                performAnimate(touchX, perItemLocation[currentIndex].x);
                if (mListener != null) {
                    mListener.onProgressChanged(currentIndex, shownText.length, shownText[currentIndex]);
                }
                break;
        }
        invalidate();
        return true;
    }

    public void setValue(CharSequence[] value) {
        shownText = value;
        invalidate();
    }

    public int getProgress() {
        return currentIndex;
    }

    public int getMax() {
        return shownText.length;
    }

    public void setProgressChangeListener(OnProgressChangeListener listener) {
        mListener = listener;
    }

    private void performAnimate(float from, float to) {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        mValueAnimator = ValueAnimator.ofFloat(from, to);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                touchX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                state = STATE_IDLE;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                state = STATE_IDLE;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mValueAnimator.setDuration((long) (Math.abs(to - from) * 2));
        mValueAnimator.start();
    }

    private int getCurrentIndex(float touchX) {
        if (touchX < 0)
            return 0;
        for (int i = 0; i < perItemLocation.length - 1; i++) {
            float minLeft = touchX - perItemLocation[i].x;
            if (minLeft < 0)
                return 0;
            float minRight = touchX - perItemLocation[i + 1].x;
            if (minLeft * minRight < 0) {
                currentIndex = i;
                if (Math.abs(minLeft) - Math.abs(minRight) < 0) {
                    return i;
                } else {
                    return i + 1;
                }
            }
        }
        return perItemLocation.length - 1;

    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }

    public interface OnProgressChangeListener {
        void onProgressChanged(int progress, int max, CharSequence value);
    }
}
