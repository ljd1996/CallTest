package com.hearing.calltest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.hearing.calltest.R;

/**
 * @author liujiadong
 * @since 2019/8/17
 */
public class CircleTextView extends View {

    private String mCustomText;
    private int mCustomColor;
    private int mCustomRadius;
    private int mFontSize;

    private Paint mCirclePaint;
    private TextPaint mTextPaint;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context, attrs);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);
        mFontSize = ta.getInteger(R.styleable.CircleTextView_size, 16);
        mCustomText = ta.getString(R.styleable.CircleTextView_text);
        mCustomColor = ta.getColor(R.styleable.CircleTextView_color, Color.BLUE);
        mCustomRadius = ta.getInteger(R.styleable.CircleTextView_radius, 30);
        ta.recycle();

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCustomColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mCustomColor);
        mTextPaint.setTextSize(mFontSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        // 在wrap_content的情况下默认长度为200
        int minSize = 400;
        // 当布局参数设置为wrap_content时，设置默认值
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minSize, minSize);
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(minSize, heightSpecSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSpecSize, minSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 处理padding属性
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        int width = 2 * mCustomRadius - paddingLeft - paddingRight;
        int height = 2 * mCustomRadius - paddingTop - paddingBottom;
        mCustomRadius = Math.min(width, height) / 2;
        canvas.drawCircle(mCustomRadius, mCustomRadius, mCustomRadius, mCirclePaint);

        // 将坐标原点移到控件中心
        canvas.translate(width / 2f, height / 2f);
        float textWidth = mTextPaint.measureText(mCustomText);
        // 文字baseline在y轴方向的位置
        float baseLineY = Math.abs(mTextPaint.ascent() + mTextPaint.descent()) / 2;
        canvas.drawText(mCustomText, -textWidth / 2, baseLineY, mTextPaint);
    }

    public void setCustomText(String customText) {
        this.mCustomText = customText;
        invalidate();
    }

    public void setCustomColor(int customColor) {
        this.mCustomColor = customColor;
        mCirclePaint.setColor(customColor);
        invalidate();
    }

    public void setFontSize(int fontSize) {
        this.mFontSize = fontSize;
        mTextPaint.setTextSize(fontSize);
        invalidate();
    }

    public void setCustomRadius(int customRadius) {
        this.mCustomRadius = customRadius;
        invalidate();
    }
}
