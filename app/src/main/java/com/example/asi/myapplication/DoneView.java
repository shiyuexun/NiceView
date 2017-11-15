package com.example.asi.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.asi.myapplication.good.GoodView;

/**
 * @author asi
 * @version V1.0
 * @Description: 加关注view
 * @date 2017/11/8 19:59
 */
public class DoneView extends View {

    public interface OnDoneClickListener {
        void onClick(boolean isDone);
    }

    private OnDoneClickListener mListener;
    private int mHeight;
    private int mWidth;
    private Paint mRoundPaint, mTextPaint, mBpPaint;
    private RectF rectf;
    private RectF rectBp;
    private float padding;//圆，圆角矩形padding
    private float defAngle;
    private int num = 0;
    private ValueAnimator animator_circle_to_round;
    private float mCurDistance;//移动距离
    private int mDefault__distance;
    private Bitmap mBp;
    private Bitmap mBped;
    private float bpSize, textWidth;
    private float inPadding, cPadding;//水平padding，竖直padding,文字和图标padding
    String maxNum = "9.9k";//最多数字
    private Paint.FontMetrics mFontMetrics;
    private float textSize;//字体大小
    private float mLinWidth;
    private Bitmap pic;
    private boolean isDone;//选择状态

    private GoodView mGoodView;

    public DoneView(Context context) {
        this(context, null);
    }


    public DoneView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.like_view);
        BitmapDrawable bitmap = (BitmapDrawable) ta.getDrawable(R.styleable.like_view_bp);
        if (bitmap != null) {
            mBp = bitmap.getBitmap();
        } else {
            mBp = BitmapFactory.decodeResource(getResources(), R.mipmap.vp_zan_click);
        }
        BitmapDrawable bitmaped = (BitmapDrawable) ta.getDrawable(R.styleable.like_view_bped);
        if (bitmaped != null) {
            mBped = bitmaped.getBitmap();
        } else {
            mBped = BitmapFactory.decodeResource(getResources(), R.mipmap.vp_zan_clicked);
        }

        initView();
        initData();
    }

    private void initData() {
        bpSize = dptopx(13);
        textSize = dptopx(9.86f);
        inPadding = dptopx(5f);
        cPadding = dptopx(4.3f);
        defAngle = dptopx(11.5f);
        mLinWidth = dptopx(1);
        padding = dptopx(1);
        mTextPaint.setTextSize(textSize);
        mRoundPaint.setStrokeWidth(mLinWidth);

        mFontMetrics = mTextPaint.getFontMetrics();
        textWidth = mTextPaint.measureText(maxNum);
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

        mTextPaint.setColor(Color.parseColor("#3c3c3c"));
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        mGoodView = new GoodView(getContext());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animator_circle_to_round != null && animator_circle_to_round.isRunning()) {
                    return;
                }
                isDone = !isDone;
                if (mListener!=null){
                    mListener.onClick(isDone);
                }
                if (!isDone) {
                    if (num == 1) {
                        num--;
                        set_rect_to_circle_animation();
                    } else {
                        num--;
                        pic = mBp;
                        invalidate();
                    }

                } else {
                    if (num == 0) {
                        num++;
                        set_circle_to_recte_animation();
                    } else {
                        num++;
                        mGoodView.show(DoneView.this);
                        pic = mBped;
                        invalidate();
                    }
                }
            }
        });
    }

    /**
     * 设置圆角矩形过度到圆的动画
     */
    private void set_rect_to_circle_animation() {
        animator_circle_to_round = ValueAnimator.ofInt(mDefault__distance, 0);
        animator_circle_to_round.removeAllUpdateListeners();
        animator_circle_to_round.setDuration(300);
        animator_circle_to_round.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurDistance = (int) animation.getAnimatedValue();
                //在靠拢的过程中设置文字的透明度，使文字逐渐消失的效果
                int alpha = (int) ((mCurDistance * 255) / mDefault__distance);
                mTextPaint.setAlpha(alpha);
                if (mCurDistance == 0) {
                    pic = mBp;
                } else {
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
        animator_circle_to_round = ValueAnimator.ofInt(0, mDefault__distance);
        animator_circle_to_round.removeAllUpdateListeners();
        animator_circle_to_round.setDuration(300);
        animator_circle_to_round.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurDistance = (int) animation.getAnimatedValue();
                //在靠拢的过程中设置文字的透明度，使文字逐渐消失的效果
                int alpha = (int) ((mCurDistance * 255) / mDefault__distance);
                mTextPaint.setAlpha(alpha);
                if (mCurDistance == mDefault__distance) {
                    pic = mBped;
                    mGoodView.show(DoneView.this);
                } else {
                    pic = mBp;
                }
                invalidate();
            }
        });
        animator_circle_to_round.start();
    }

    public void setNum(int num) {
        this.num = num;
        if (num > 0) {
            mCurDistance = mDefault__distance;
        } else {
            mCurDistance = 0;
        }
        invalidate();
    }

    public void setDone(boolean done) {
        this.isDone = done;
        pic = isDone ? mBped : mBp;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float left = mWidth - padding - 2 * defAngle;
        final float right = mWidth - padding;
        final float bottom = mHeight - padding;

        if (mCurDistance != 0) {
            float y = mHeight / 2 - mFontMetrics.top / 2 - mFontMetrics.bottom / 2;
            canvas.drawText(String.valueOf(num), mWidth - padding - inPadding - textWidth / 2, y, mTextPaint);
        }

        rectf.set(left - mCurDistance, padding, right, bottom);
        rectBp.set(left + inPadding - mCurDistance, padding + inPadding, right - inPadding - mCurDistance, bottom - inPadding);
        canvas.drawRoundRect(rectf, defAngle, defAngle, mRoundPaint);
        canvas.drawBitmap(pic, null, rectBp, mBpPaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = (int) (2 * padding + mLinWidth * 2 + inPadding * 2 + bpSize + cPadding + textWidth);
        mHeight = (int) dptopx(25);
        mDefault__distance = (int) (mWidth - 2 * padding - 2 * defAngle);
        setMeasuredDimension(mWidth, mHeight);
    }
}

