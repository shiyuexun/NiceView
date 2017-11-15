package com.example.asi.myapplication;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
* @Description: 加关注view
* @author asi
* @date 2017/11/8 19:59
* @version V1.0
*/
public class FollowView extends View {

    public interface OnFollowListener {
        void onFollow();
    }

    private float PADDING_WIDTH;
    private float PADDING_HEIGHT;
    private OnFollowListener mListener;

    private ValueAnimator mObjectAnimator;

    private float mCurAniValue ;    //当前属性动画数值
    private static final String mFollowStr = "+关注";

    private Paint mTexPaint;
    private Paint mOvalPaint;
    private Paint mBitmapPaint;
    private boolean mIsFollow; //当前状态是关注

    private int mMeasuredHeight, mMeasuredWidth;//控件宽高
    private Rect mTextBounds = new Rect(); // 当前文字的区域
    private Rect mIvBounds = new Rect(); // 当前文字的区域
    private RectF mOvalBounds = new RectF(); // 当前文字的区域
    private Bitmap mBitmap;
    //为基线到字体上边框的距离
    private float mTextTop;
    //为基线到字体下边框的距离
    private float mTextBottom;
    //背景圆角矩形padding
    private float mBgPadding;
    //控件最小宽高
    private int minWidth, minHeight;

    private boolean isChanging;
    //关注图片绘制区域宽高
    private int mIvWidth, mIvHeight;

    public FollowView(Context context) {
        this(context, null);
    }

    public FollowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FollowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimator();
        initView();
        initData();
        postInvalidate();
    }

    private void initData() {

        mIvWidth = (int) dptopx(11.6f);
        mIvHeight = (int) dptopx(7.5f);
        PADDING_WIDTH = dptopx(9.28f);
        PADDING_HEIGHT = dptopx(6.09f);
        mBgPadding = dptopx(0.6f);
        minWidth = (int) dptopx(49.28f);
        minHeight = (int) dptopx(26.09f);

        Paint.FontMetrics fontMetrics = mTexPaint.getFontMetrics();
        mTextTop = fontMetrics.top;
        mTextBottom = fontMetrics.bottom;
        mTexPaint.getTextBounds(mFollowStr, 0, mFollowStr.length(), mTextBounds);
    }

    private float dptopx(float dp) {
        Resources resources = getContext().getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    private void initView() {
        mTexPaint = new Paint();
        mBitmapPaint = new Paint();
        mOvalPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mTexPaint.setAntiAlias(true);
        mOvalPaint.setAntiAlias(true);
        mTexPaint.setColor(Color.parseColor("#3c3c3c"));
        mOvalPaint.setColor(Color.parseColor("#EEEFEF"));
        mTexPaint.setTextSize(dptopx(10f));
        mTexPaint.setStyle(Paint.Style.FILL);
        mOvalPaint.setStrokeWidth(dptopx(0.6f));
        mOvalPaint.setStyle(Paint.Style.STROKE);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为left
        mTexPaint.setTextAlign(Paint.Align.LEFT);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_followed);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!mIsFollow) {
                        mIsFollow = true;
                        setEnabled(false);
                        changeFollow();
                        if (mListener != null) {
                            mListener.onFollow();
                        }
                    } else {
                        setEnabled(false);
                    }

            }
        });

    }

    private void initAnimator() {
        mObjectAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mObjectAnimator.setDuration(200);
        mObjectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurAniValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        mObjectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isChanging = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isChanging = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 数量发生变化
     */
    private void changeFollow() {
        //先结束当前动画
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
        }

        if (mObjectAnimator != null) {
            mObjectAnimator.start();
        } else {
            //初始化调用该方法， 重新布局
            invalidate();
        }
    }

    public void setOnFollowListener(OnFollowListener onFollowListener) {
        this.mListener = onFollowListener;
    }

    /**
     * 数量发生变化
     */
    public void setFollow(boolean isFollow) {
        if (mIsFollow == isFollow) {
            return;
        }
        //先结束当前动画
        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
            mObjectAnimator.end();
        }
        mIsFollow = isFollow;
        isChanging = false;
        setEnabled(!isFollow);
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int left = (mMeasuredWidth - mIvWidth) / 2;
        int top = (mMeasuredHeight - mIvHeight) / 2;

        int y = (int) (mMeasuredHeight / 2 - mTextTop / 2 - mTextBottom / 2);//基线中间点的y轴计算公式
        int x = (mMeasuredWidth - mTextBounds.width()) / 2;

        if (!mIsFollow) {
            //只绘关注文字
            canvas.drawText(mFollowStr, x, y, mTexPaint);
        } else {
            mIvBounds.set(left, top, left + mIvWidth, top + mIvHeight);

            if (!isChanging){
                canvas.drawBitmap(mBitmap, null, mIvBounds, mBitmapPaint);
            }else{
                canvas.save();
                canvas.clipRect(PADDING_WIDTH, PADDING_HEIGHT, mMeasuredWidth - PADDING_WIDTH, mMeasuredHeight - PADDING_HEIGHT);
                float offset = x * mCurAniValue;
                if (mCurAniValue != 1) {
                    //文字消失动画
                    drawOut(canvas, x - offset, y);
                }
                mIvBounds.offset((int) (x - offset), 0);
                //图片进入动画绘制
                drawIn(canvas);
                canvas.restore();
            }

        }
        mOvalBounds.set(mBgPadding, mBgPadding, mMeasuredWidth - mBgPadding, mMeasuredHeight - mBgPadding);
        canvas.drawRoundRect(mOvalBounds, dptopx(29), dptopx(29), mOvalPaint);

    }

    /**
     * @param canvas
     */
    private void drawIn(Canvas canvas) {
        mBitmapPaint.setAlpha((int) (mCurAniValue * 255));
        canvas.drawBitmap(mBitmap, null, mIvBounds, mBitmapPaint);
    }

    /**
     * @param canvas
     * @param x
     * @param y
     */
    private void drawOut(Canvas canvas, float x, float y) {
        mTexPaint.setAlpha(255 - (int) (mCurAniValue * 255));
        canvas.drawText(mFollowStr, x, y, mTexPaint);
        mTexPaint.setAlpha(255);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mObjectAnimator.end();
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我将大小取最大值,你也可以取其他值
                mySize = Math.max(size, mySize);
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = Math.max(size, mySize);
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredWidth = getMySize(minWidth, widthMeasureSpec);
        mMeasuredHeight = getMySize(minHeight, heightMeasureSpec);
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
    }
}

