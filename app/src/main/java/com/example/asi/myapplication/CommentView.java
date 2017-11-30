package com.example.asi.myapplication;

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


/**
 * @author asi
 * @version V1.0
 * @Description: 回复view
 * @date 2017/11/8 19:59
 */
public class CommentView extends View {

    private OnClickCommentListener mListener;
    private int mHeight;
    private int mWidth;
    private Paint mRoundPaint, mTextPaint, mBpPaint;
    private RectF rectf;
    private RectF rectBp;
    private float padding;//圆，圆角矩形padding
    private float defAngle;
    private int num = 0;
    private Bitmap mBp;
    private float bpSize, textWidth;
    private float inPadding, cPadding,bPadding;//水平padding竖直padding,文字和图标padding,num>0的padding
    String maxNum = "0";//
    private Paint.FontMetrics mFontMetrics;
    private float textSize;//字体大小

    public interface OnClickCommentListener {
        void onClick();
    }

    public CommentView(Context context) {
        this(context, null);
    }


    public CommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_comment);
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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick();
                    }
            }
        });
    }

    public void setOnClickCommentListener(OnClickCommentListener listener) {
        this.mListener = listener;
    }

    public void setNum(int num) {
        this.num = num;
        requestLayout();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        final float right = mWidth - padding;
        final float bottom = mHeight - padding;

        if (num != 0) {
            float y = mHeight / 2 - mFontMetrics.top / 2 - mFontMetrics.bottom / 2;
            canvas.drawText(maxNum, mWidth - padding - bPadding - textWidth / 2 , y, mTextPaint);
        rectBp.set(padding + bPadding, padding + inPadding, padding + bPadding + bpSize, bottom - inPadding);
        }
        else {
        rectBp.set(padding + inPadding, padding + inPadding, padding + inPadding + bpSize, bottom - inPadding);
        }

        rectf.set(padding, padding, right, bottom);

        canvas.drawRoundRect(rectf, defAngle, defAngle, mRoundPaint);
        canvas.drawBitmap(mBp, null, rectBp, mBpPaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (num == 0) {
            mWidth = (int) (2 * padding + inPadding * 2 + bpSize);
        } else {
            maxNum= String.valueOf(num);
            textWidth = mTextPaint.measureText(maxNum);
            mWidth = (int) (2 * padding  + bPadding * 2 + bpSize + cPadding + textWidth);
        }
        mHeight = (int) dptopx(25);
        setMeasuredDimension(mWidth, mHeight);
    }
}

