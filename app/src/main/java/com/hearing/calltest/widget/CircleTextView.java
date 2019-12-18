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

    private String customText;
    private int customColor;
    private int customRadius;
    private int fontSize;

    private Paint circlePaint;
    private TextPaint textPaint;

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
        fontSize = ta.getInteger(R.styleable.CircleTextView_size, 16);
        customText = ta.getString(R.styleable.CircleTextView_text);
        customColor = ta.getColor(R.styleable.CircleTextView_color, Color.BLUE);
        customRadius = ta.getInteger(R.styleable.CircleTextView_radius, 30);
        ta.recycle();

        circlePaint = new Paint();
        circlePaint.setColor(customColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        textPaint = new TextPaint();
        textPaint.setColor(customColor);
        textPaint.setTextSize(fontSize);
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
        int width = 2 * customRadius - paddingLeft - paddingRight;
        int height = 2 * customRadius - paddingTop - paddingBottom;
        customRadius = Math.min(width, height) / 2;
        canvas.drawCircle(customRadius, customRadius, customRadius, circlePaint);

        // 将坐标原点移到控件中心
        canvas.translate(width / 2f, height / 2f);
        float textWidth = textPaint.measureText(customText);
        // 文字baseline在y轴方向的位置
        float baseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        canvas.drawText(customText, -textWidth / 2, baseLineY, textPaint);
    }

    public void setCustomText(String customText) {
        this.customText = customText;
        invalidate();
    }

    public void setCustomColor(int customColor) {
        this.customColor = customColor;
        circlePaint.setColor(customColor);
        invalidate();
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        textPaint.setTextSize(fontSize);
        invalidate();
    }

    public void setCustomRadius(int customRadius) {
        this.customRadius = customRadius;
        invalidate();
    }
}
