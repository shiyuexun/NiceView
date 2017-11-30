package com.example.asi.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.asi.myapplication.good.GoodView;

/**
 * @author asi
 * @version V1.0
 * @Description: 点赞view
 * @date 2017/11/8 19:59
 */
public class LikeView extends View {

    public interface OnClickLikeListener {
        void onClick(boolean isDone,int num);
    }

    private LikeView.OnClickLikeListener mListener;
    private int mHeight;
    private int mWidth;
    private Paint mRoundPaint, mTextPaint, mBpPaint;
    private RectF rectf;
    private RectF rectBp;
    private float padding;//圆，圆角矩形padding
    private float defAngle;
    private int num = 0;
    private String formatStr = "0";
    private ValueAnimator animator_circle_to_round;
    private float mcurRoundDistance;//当前圆环移动距离
    private float mcurBpDistance;//当前图片移动距离
    private float mRoundDistance;//圆环移动总距离
    private float mBpDistance;//图片移动总距离
    private Bitmap mBp;
    private Bitmap mBped;
    private float bpSize, textWidth;
    private float inPadding, cPadding, bPadding;//水平和竖直padding,文字和图标padding，文字大于0的左右padding。
    private Paint.FontMetrics mFontMetrics;
    private float textSize;//字体大小
    private Bitmap pic;
    private boolean isDone;//选择状态

    private GoodView mGoodView;

    public LikeView(Context context) {
        this(context, null);

    }


    public LikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBp = BitmapFactory.decodeResource(getResources(), R.mipmap.vp_zan_click);
        mBped = BitmapFactory.decodeResource(getResources(), R.mipmap.vp_zan_clicked);
        initView();
        initData();
    }

    private void initData() {
        bpSize = dptopx(13);
        textSize = dptopx(9.86f);
        inPadding = dptopx(5f);
        bPadding = dptopx(8f);
        cPadding = dptopx(4.3f);
        defAngle = dptopx(11.5f);
        padding = dptopx(1);
        mTextPaint.setTextSize(textSize);
        mRoundPaint.setStrokeWidth(padding);

        mFontMetrics = mTextPaint.getFontMetrics();

        pic = isDone ? mBped : mBp;
    }


    private float dptopx(float dp) {
        Resources resources = getContext().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    private void initView() {
        mRoundPaint = new Paint();
        mTextPaint = new Paint();
        mBpPaint = new Paint();
        rectf = new RectF();
        rectBp = new RectF();

        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setColor(Color.parseColor("#EEEFEF"));
        mRoundPaint.setStyle(Paint.Style.STROKE);

        mBpPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);

        mBpPaint.setFilterBitmap(true);
        mBpPaint.setDither(true);

        mTextPaint.setColor(Color.parseColor("#3c3c3c"));
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        mGoodView = new GoodView(getContext());

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (animator_circle_to_round != null && animator_circle_to_round.isRunning()) {
                        return;
                    }
                    isDone = !isDone;

                    if (!isDone) {
                        if (num == 1) {
                            num--;
                            cal();
                            set_rect_to_circle_animation();
                        } else {
                            num--;
                            pic = mBp;
                            cal();
                            invalidate();
                        }

                    } else {
                        if (num == 0) {
                            num++;
                            cal();
                            set_circle_to_recte_animation();
                        } else {
                            num++;
                            cal();
                            mGoodView.show(LikeView.this);
                            pic = mBped;
                            invalidate();
                        }

                    }
                    if (mListener != null) {
                        mListener.onClick(isDone,num);

                }
            }
        });
    }





    /**
     * 设置圆角矩形过度到圆的动画
     */
    private void set_rect_to_circle_animation() {
        animator_circle_to_round = ValueAnimator.ofFloat(mRoundDistance, 0);
        animator_circle_to_round.removeAllUpdateListeners();
        animator_circle_to_round.setDuration(300);
        animator_circle_to_round.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mcurRoundDistance = (float) animation.getAnimatedValue();
                mcurBpDistance = mcurRoundDistance*mBpDistance/mRoundDistance;
                //在靠拢的过程中设置文字的透明度，使文字逐渐消失的效果
                int alpha = (int) ((mcurRoundDistance * 255) / mRoundDistance);
                mTextPaint.setAlpha(alpha);
                if (mcurRoundDistance == 0) {
                    pic = mBp;
                }else{
                    pic = mBped;
                }
                invalidate();
            }
        });
        animator_circle_to_round.start();
    }

    /**
     * 设置圆过度到圆角矩形的动画
     */
    private void set_circle_to_recte_animation() {
        animator_circle_to_round = ValueAnimator.ofFloat(0, mRoundDistance);
        animator_circle_to_round.removeAllUpdateListeners();
        animator_circle_to_round.setDuration(300);
        animator_circle_to_round.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mcurRoundDistance = (float) animation.getAnimatedValue();
                mcurBpDistance = mcurRoundDistance*mBpDistance/mRoundDistance;
                //在靠拢的过程中设置文字的透明度，使文字逐渐消失的效果
                int alpha = (int) ((mcurRoundDistance * 255) / mRoundDistance);
                mTextPaint.setAlpha(alpha);
                if (mcurRoundDistance == mRoundDistance) {
                    pic = mBped;
                    mGoodView.show(LikeView.this);
                }else{
                    pic = mBp;
                }
                invalidate();
            }
        });
        animator_circle_to_round.start();
    }

    public void setNum(int num) {
        this.num = num;
        cal();
        invalidate();
    }

    public void setDone(boolean done) {
        this.isDone = done;
        pic = isDone ? mBped : mBp;
        invalidate();
    }

    public void setOnLickClickListener(LikeView.OnClickLikeListener listener) {
        this.mListener = listener;
    }


    public void setDone(boolean done, int num, LikeView.OnClickLikeListener listener) {
        this.isDone = done;
        pic = isDone ? mBped : mBp;
        this.num = num;
        this.mListener = listener;
        cal();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        final float right = mWidth - padding;
        final float bottom = mHeight - padding;
        final float left = right - inPadding - bpSize - inPadding;


        if (mcurRoundDistance != 0) {
            float y = mHeight / 2 - mFontMetrics.top / 2 - mFontMetrics.bottom / 2;
            canvas.drawText(formatStr, mWidth - padding - bPadding - textWidth / 2, y, mTextPaint);
        }
        rectBp.set(left + inPadding - mcurBpDistance, padding + inPadding, left + inPadding - mcurBpDistance + bpSize, bottom - inPadding);
        rectf.set(left - mcurRoundDistance, padding, right, bottom);
        canvas.drawRoundRect(rectf, defAngle, defAngle, mRoundPaint);
        canvas.drawBitmap(pic, null, rectBp, mBpPaint);

    }

    private void cal() {
        formatStr = String.valueOf(num);
        textWidth = mTextPaint.measureText(formatStr);
        mRoundDistance = textWidth + cPadding + (bPadding - inPadding) * 2;
        mBpDistance = textWidth + cPadding + bPadding - inPadding;
        if (num > 0) {
            mcurRoundDistance = mRoundDistance;
            mcurBpDistance = mBpDistance;
        } else {
            mcurRoundDistance = 0;
            mcurBpDistance = 0;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = (int) dptopx(55);
        mHeight = (int) dptopx(25);
        setMeasuredDimension(mWidth, mHeight);
    }
}

