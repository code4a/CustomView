package com.code4a.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 气体流量计控件
 */
public class HistogramView extends View implements Runnable {

    private static final String TAG = HistogramView.class.getSimpleName();

    private int comWidth; // 控件宽度
    private int comHeight;// 控件高度

    private View rateView;// 进度条

    private String rateBackgroundColor;// 进图条背景颜色

    private int rateBackgroundId; // 进图条背景图片id

    private Bitmap rataBackgroundBitmap;

    private int rateHeight; // 进度条的高

    private int rateWidth; // 进度条的宽

    private int rateMargin = 3;
    private int scaleNum = 7;
    private int scaleRangeUnit = 8;
    private int scaleTextStartPos = 8;
    private int scaleStartPos = scaleTextStartPos + 2;
    private int scaleEndPos = scaleStartPos + 5;

    private int rateAnimValue;// 进度条动画高度

    private int orientation; // 设置柱状图方向

    private double progress;// 设置进度 1为最大值

    private boolean isAnim = true; // 是否动画显示统计条

    private Handler handler = new Handler();// 动画handler

    private int animRate = 1; // 动画速度 以每1毫秒计

    private int animTime = 1;// 动画延迟执行时间

    private Canvas canvas;// 画布

    public HistogramView(Context context) {
        super(context);
        init(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // 坐标轴 轴线 画笔：
    private Paint axisLinePaint;
    // 坐标轴水平内部 虚线画笔
    private Paint hLinePaint;
    // 绘制文本的画笔
    private Paint titlePaint;
    // 矩形画笔 柱状图的样式信息
    private Paint recBackgroundPaint;

    private void init(Context context, AttributeSet attrs) {
        axisLinePaint = new Paint();
        hLinePaint = new Paint();
        titlePaint = new Paint();
        recBackgroundPaint = new Paint();

        axisLinePaint.setColor(Color.RED);
        hLinePaint.setColor(Color.rgb(92, 141, 171));
        titlePaint.setColor(Color.rgb(92, 141, 171));
        recBackgroundPaint.setColor(Color.rgb(32, 57, 83));

        hLinePaint.setAntiAlias(true); // 防锯齿
        hLinePaint.setDither(true); // 防抖动
        hLinePaint.setStrokeWidth(5);
    }

    private String[] yTitlesStrings = new String[] { "6", "5", "4", "3", "2", "1", "0" };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 初始化控件和进度的条相关参数
        comWidth = w;
        comHeight = h;
        Log.i(TAG, "comWidth = " + comWidth + " ,comHeight = " + comHeight);
        if (orientation == LinearLayout.HORIZONTAL) {
            rateWidth = (int) (w * (progress / 6));
            rateHeight = h - rateMargin;
        } else {
            rateHeight = (int) (h * (progress / 6));
            rateWidth = w - rateMargin;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        drawXYLine(canvas);

        drawXYScale(canvas);

        drawBackgroundRect();
        drawProgressRect();

    }

    private void drawBackgroundRect() {
        if (orientation == LinearLayout.HORIZONTAL) {// TODO
            // canvas.drawRect(0, 0, rateWidth, comHeight, recBackgroundPaint);
        } else {// 垂直方向无动画柱状图
            canvas.drawRect(scaleEndPos, 0, comWidth, comHeight, recBackgroundPaint);
        }
    }

    private void drawProgressRect() {
        Paint rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        Log.d(TAG, "onDraw  rateBackgroundColor====" + rateBackgroundColor);
        if (rateBackgroundColor != null) {
            drawViewWithColor(rectPaint, isAnim);
        } else if (rateBackgroundId != -1) {
            drawViewWithBitmap(rectPaint, isAnim);
        }
    }

    /**
     * 
     * drawViewWithColor:(绘制颜色进度条). <br/>
     * 
     * @param paint
     * @param isAnim
     * @since 1.0
     */
    private void drawViewWithColor(Paint paint, boolean isAnim) {
        // paint.setColor(Color.rgb(63, 197, 241));
        paint.setColor(Color.parseColor(rateBackgroundColor));
        Log.d(TAG, "rateBackgroundColor====" + rateBackgroundColor);
        if (isAnim) {
            handler.postDelayed(this, animTime);
            if (orientation == LinearLayout.HORIZONTAL) {// 水平方向柱状图
                canvas.drawRect(0, 0, rateAnimValue, comHeight, paint);
            } else {// 垂直方向柱状图
                float topValue = comHeight - rateAnimValue;
                drawXAxisText(canvas, topValue, true);
                canvas.drawRect(scaleEndPos + rateMargin, topValue, comWidth - rateMargin, comHeight, paint);
            }
        } else {
            if (orientation == LinearLayout.HORIZONTAL) {// 水平方向无动画柱状图
                canvas.drawRect(0, 0, rateWidth, comHeight, paint);
            } else {// 垂直方向无动画柱状图
                float topValue = comHeight - rateHeight;
                drawXAxisText(canvas, topValue, false);
                canvas.drawRect(scaleEndPos + rateMargin, topValue, comWidth - rateMargin, comHeight, paint);
            }
        }

    }

    /**
     * 
     * drawViewWithBitmap:(绘制图片进度条). <br/>
     * 
     * @author msl
     * @param paint
     * @param isAnim
     * @since 1.0
     */
    private void drawViewWithBitmap(Paint paint, boolean isAnim) {
        Log.d(TAG, "bitmap====" + rataBackgroundBitmap);
        RectF dst = null;
        if (isAnim) {
            handler.postDelayed(this, animTime);
            if (orientation == LinearLayout.HORIZONTAL) {// 水平方向柱状图
                dst = new RectF(0, 0, rateAnimValue, comHeight);
                canvas.drawBitmap(rataBackgroundBitmap, null, dst, paint);
            } else {// 垂直方向柱状图
                dst = new RectF(scaleEndPos + rateMargin, comHeight - rateAnimValue, comWidth - rateMargin, comHeight);
                canvas.drawBitmap(rataBackgroundBitmap, null, dst, paint);
            }
        } else {
            if (orientation == LinearLayout.HORIZONTAL) {// 水平方向无动画柱状图
                dst = new RectF(0, 0, rateWidth, comHeight);
                canvas.drawBitmap(rataBackgroundBitmap, null, dst, paint);
            } else {// 垂直方向无动画柱状图
                dst = new RectF(scaleEndPos + rateMargin, comHeight - rateHeight, comWidth - rateMargin, comHeight);
                canvas.drawBitmap(rataBackgroundBitmap, null, dst, paint);
            }
        }
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public View getRateView() {
        return rateView;
    }

    public void setRateView(View rateView) {
        this.rateView = rateView;
    }

    public int getRateHeight() {
        return rateHeight;
    }

    public void setRateHeight(int rateHeight) {
        this.rateHeight = rateHeight;
    }

    public int getRateWidth() {
        return rateWidth;
    }

    public void setRateWidth(int rateWidth) {
        this.rateWidth = rateWidth;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isAnim() {
        return isAnim;
    }

    public void setAnim(boolean isAnim) {
        this.isAnim = isAnim;
    }

    public int getAnimRate() {
        return animRate;
    }

    public void setAnimRate(int animRate) {
        this.animRate = animRate;
    }

    public String getRateBackgroundColor() {
        return rateBackgroundColor;
    }

    public void setRateBackgroundColor(String rateBackgroundColor) {
        this.rateBackgroundColor = rateBackgroundColor;
        rateBackgroundId = -1;
        rataBackgroundBitmap = null;
    }

    public int getRateBackgroundId() {
        return rateBackgroundId;
    }

    public void setRateBackgroundId(int rateBackground) {
        this.rateBackgroundId = rateBackground;
        rataBackgroundBitmap = BitmapFactory.decodeResource(getResources(), rateBackgroundId);
        rateBackgroundColor = null;
    }

    /**
     * 绘制X轴刻度文字
     * 
     * @param canvas
     *            画布
     * @param width
     *            画布宽度
     * @return 刻度数量
     */
    private void drawXAxisText(Canvas canvas, float yValue, boolean isAnim) {
        if (orientation == LinearLayout.HORIZONTAL) { // TODO
            String xTitle = isAnim ? String.format("%.1f", (double) rateAnimValue / comWidth * 6) : getProgress() + "";
            // String.format("%.2f", f)
            // canvas.drawLine(0, scaleEndPos, comWidth, scaleEndPos,
            // axisLinePaint);//X轴
            // canvas.drawLine(0, 0, 0, comHeight, axisLinePaint);//Y轴
        } else {
            String xTitle = isAnim ? String.format("%.1f", (double) rateAnimValue / comHeight * 6) : getProgress() + "";
            // 计算X轴中心坐标点
            float xAxisPos = (comWidth + scaleEndPos) / 2;
            float yAxisPos = yValue - scaleRangeUnit;
            titlePaint.setTextAlign(Align.CENTER);
            titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
            titlePaint.setTextSize(32);
            titlePaint.setColor(Color.rgb(237, 237, 237));
            canvas.drawText(xTitle, xAxisPos, yAxisPos, titlePaint);
        }
    }

    /**
     * 画出Y轴刻度
     * 
     * @param canvas
     *            画布
     * @return Y轴刻度总高度
     */
    private void drawXYScale(Canvas canvas) {
        if (orientation == LinearLayout.HORIZONTAL) { // TODO
            // canvas.drawLine(0, scaleEndPos, comWidth, scaleEndPos,
            // axisLinePaint);//X轴
            // canvas.drawLine(0, 0, 0, comHeight, axisLinePaint);//Y轴
        } else {
            // 2 绘制Y轴坐标刻度
            int leftHeight = comHeight - 2 * scaleRangeUnit;// 左侧外周的 需要划分的高度：
            int hPerHeight = leftHeight / (scaleNum - 1);

            hLinePaint.setTextAlign(Align.CENTER);
            for (int i = 0; i < scaleNum; i++) { // 画出刻度线
                canvas.drawLine(scaleStartPos, scaleRangeUnit + i * hPerHeight, scaleEndPos, scaleRangeUnit + i * hPerHeight, hLinePaint);
            }

            // 3 绘制 Y周坐标值
            FontMetrics metrics = titlePaint.getFontMetrics();
            int descent = (int) metrics.descent;
            titlePaint.setTextAlign(Align.RIGHT);
            titlePaint.setTypeface(Typeface.SANS_SERIF);
            titlePaint.setTextSize(18);
            titlePaint.setColor(Color.rgb(92, 141, 171));
            for (int i = 0; i < yTitlesStrings.length; i++) {
                canvas.drawText(yTitlesStrings[i], scaleTextStartPos, scaleRangeUnit + i * hPerHeight + descent, titlePaint);
            }
        }
    }

    /**
     * 画横纵坐标线
     * 
     * @param canvas
     *            画布
     * @param width
     *            画布的宽度
     */
    private void drawXYLine(Canvas canvas) {
        // 1 绘制坐标线：
        if (orientation == LinearLayout.HORIZONTAL) {
            canvas.drawLine(0, scaleEndPos, comWidth, scaleEndPos, axisLinePaint);// X轴
            // canvas.drawLine(0, 0, 0, comHeight, axisLinePaint);//Y轴
        } else {
            canvas.drawLine(scaleEndPos, 0, scaleEndPos, comHeight, axisLinePaint);// Y轴
            // canvas.drawLine(scaleEndPos, comHeight, comWidth, comHeight,
            // axisLinePaint);//X轴
        }
    }

    @Override
    public void run() {
        if (orientation == LinearLayout.HORIZONTAL && (rateAnimValue <= rateWidth)) {
            rateAnimValue += animRate;
            invalidate();
        } else if (orientation == LinearLayout.VERTICAL && (rateAnimValue <= rateHeight)) {
            rateAnimValue += animRate;
            invalidate();
        } else {
            handler.removeCallbacks(this);
            rateAnimValue = 0;
        }
    }

}
