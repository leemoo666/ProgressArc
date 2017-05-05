package com.wyc.progressarc;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by 李小明 on 17/5/4.
 * 邮箱:287907160@qq.com
 */

public class ProgressArc extends View {
    private Paint paint; //圆环画笔
    private Paint arcPaint; //圆弧画笔
    private Paint textPaint; //文字画笔
    private int roundWidth;//圆环宽度

    //    private int[] doughnutColors = new int[]{Color.parseColor("#3BB7FF"), Color.parseColor("#1B8BFF")};
    private int[] doughnutColors = new int[]{Color.parseColor("#3BB7FF"), Color.parseColor("#1B8BFF"), Color.parseColor("#3BB7FF")};

    public ProgressArc(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressArc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressArc, 0, 0);

        progressValue = a.getInt(R.styleable.ProgressArc_progressValue, 0);
        totalValue = a.getInt(R.styleable.ProgressArc_totalValue, 0);
        float textSize = a.getDimension(R.styleable.ProgressArc_textSize, 12);
        int textColor = a.getColor(R.styleable.ProgressArc_textColor, Color.BLUE);

        roundWidth = 20;
        paint = new Paint();
        paint.setColor(Color.GRAY); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿

        arcPaint = new Paint();
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setColor(Color.BLUE); //设置圆环的颜色
        arcPaint.setStyle(Paint.Style.STROKE); //设置空心
        arcPaint.setStrokeWidth(roundWidth); //设置圆环的宽度
        arcPaint.setAntiAlias(true);  //消除锯齿

        textPaint = new Paint();
        textPaint.setColor(textColor); //设置文字的颜色
        textPaint.setStrokeWidth(3); //设置文字的畫筆的宽度
        textPaint.setTextSize(textSize);

        setProgress(progressValue, totalValue);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 200;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 200;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2, doughnutColors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, getWidth() / 2, getHeight() / 2);
        sweepGradient.setLocalMatrix(matrix);

        arcPaint.setShader(sweepGradient);
        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (centre - roundWidth / 2); //圆环的半径
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画圆弧
         * */
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

//        Logger.i("draw current = " + currentArc);
        canvas.drawArc(oval, -90, currentArc, false, arcPaint);  //根据进度画圆弧

        /**
         * 绘制文字
         */
        float textWidth = textPaint.measureText(progressValue + "/" + totalValue);
        int textHeight = (int) (Math.ceil(textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent) + 2);
        canvas.drawText(progressValue + "/" + totalValue, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textHeight / 4, textPaint);

    }

    private int progressValue = 0;
    private int totalValue = 0;

    private int currentArc;

    public void setProgress(int progressValue, int totalValue) {
        this.progressValue = progressValue;
        this.totalValue = totalValue;

        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) progressValue / totalValue);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentArc = (int) (360 * (float) animation.getAnimatedValue());
//                Logger.i("current = " + currentArc);
                invalidate();
            }
        });
        animator.start();

    }


}
