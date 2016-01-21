package com.andway.bubble;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by WayneSuper on 21/01/2016.
 */
public class BubbleLoadingView extends View {
    private static final long ANIMATION_TIME = 600L;
    private Paint leftPaint;
    private Paint rightPaint;
    private int leftColor = 0xf032ee;
    private int rightColor = 0x34ef32;
    private int mBubbleRadius;
    private int leftRadius;
    private int rightRadius;
    private int mBubbleGap = 3; //两个泡泡之间的间距
    private int leftPos;  //左边泡泡圆心位置
    private int rightPos; //右边泡泡圆心位置
    private boolean isFirst = true;

    public BubbleLoadingView(Context context) {
        this(context, null);
    }

    public BubbleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        leftRadius = rightRadius = mBubbleRadius;
        leftPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        leftPaint.setStyle(Paint.Style.FILL);
        leftPaint.setColor(leftColor);

        rightPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        rightPaint.setStyle(Paint.Style.FILL);
        rightPaint.setColor(rightColor);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = null;
        try {
            mBubbleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BubbleLoadingView, defStyleAttr, 0);
            leftColor = a.getColor(R.styleable.BubbleLoadingView_leftBubbleColor, leftColor);
            rightColor = a.getColor(R.styleable.BubbleLoadingView_rightBubbleColor, rightColor);
            mBubbleRadius = (int) a.getDimension(R.styleable.BubbleLoadingView_bubbleRadius, mBubbleRadius);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } finally {
            if (a != null)
                a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int heightMeasureSpec) {
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        int temp = getPaddingTop() + getPaddingBottom() + 2 * mBubbleRadius + 2 * mBubbleRadius;
        height = Math.min(size, temp);
        return height;
    }

    private int measureWidth(int widthMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        int temp = getPaddingLeft() + getPaddingRight() - mBubbleGap + 2 * mBubbleRadius + 2 * mBubbleRadius;
        width = Math.min(size, temp);
        return width;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftPos = getPaddingLeft() + mBubbleRadius;
        rightPos = getMeasuredWidth() - mBubbleRadius;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(leftPos, getMeasuredHeight() / 2, leftRadius, leftPaint);
        canvas.drawCircle(rightPos, getMeasuredHeight() / 2, rightRadius, rightPaint);
        if (isFirst) {
            createAnimation();
            isFirst = false;
        }
    }


    public void createAnimation() {
        ValueAnimator mRadiusAnimator = ValueAnimator.ofFloat(mBubbleRadius / 4, mBubbleRadius * 3 / 4);
        mRadiusAnimator.setDuration(ANIMATION_TIME);
        mRadiusAnimator.setRepeatCount(-1);
        mRadiusAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float cValue = (float) valueAnimator.getAnimatedValue();
                leftPos = (int) (mBubbleRadius + cValue);
                leftRadius = (int) (mBubbleRadius - cValue);
                rightPos = (int) (2 * mBubbleRadius - mBubbleGap + cValue);
                rightRadius = (int) (cValue);
                invalidate();
            }
        });
        mRadiusAnimator.start();
    }
}
