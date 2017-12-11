package com.yxf.doodleview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by quehuang.du on 2017/12/2.
 */

class DrawView extends View {

    Path mPath = new Path();

    Paint mPaint = new Paint();

    DoodleView parent;
    int paddingColor = Color.argb(0x44, 0x64, 0x64, 0x64);
    int paintColor = Color.GREEN;


    boolean isInDrawModel = false;

    Runnable resetDrawTask = new Runnable() {
        @Override
        public void run() {
            exitTouchModel();
        }
    };
    private static final int RESET_PATH_TASK_TIME = 800;


    float lastX, lastY;

    int xOffset;
    int yOffset;

    int paintWidth = 12;

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context) {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isInTouchRange(ev.getX(), ev.getY())) {
            if (isInDrawModel) {
                lastX = ev.getX();
                lastY = ev.getY();
                if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
            } else {
                return false;
            }
        }
        if (!isInDrawModel) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = ev.getX();
                    lastY = ev.getY();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(ev.getX() - lastX) > 5 || Math.abs(ev.getY() - lastY) > 5) {
                        lastX = ev.getX();
                        lastY = ev.getY();
                        enterTouchModel();
                    }
                    break;
                default:
                    return false;
            }
        }
        if (isInDrawModel) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    removeCallbacks(resetDrawTask);
                    mPath.moveTo(ev.getX(), ev.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    removeCallbacks(resetDrawTask);
                    if (!isInTouchRange(lastX, lastY)) {
                        lastX = ev.getX();
                        lastY = ev.getY();
                        mPath.moveTo(lastX, lastY);
                    } else {
                        lastX = ev.getX();
                        lastY = ev.getY();
                        mPath.lineTo(lastX, lastY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    removeCallbacks(resetDrawTask);
                    postDelayed(resetDrawTask, RESET_PATH_TASK_TIME);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    removeCallbacks(resetDrawTask);
                    postDelayed(resetDrawTask, RESET_PATH_TASK_TIME);
                    break;
            }
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInDrawModel) {
            mPaint.setStrokeWidth(paintWidth);
            mPaint.setColor(paintColor);
            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(paddingColor);
            canvas.drawRect(0, 0, getWidth(), yOffset, p);
            canvas.drawRect(0, getHeight() - yOffset, getWidth(), getHeight(), p);
            canvas.drawRect(0, yOffset, xOffset, getHeight() - yOffset, p);
            canvas.drawRect(getWidth() - xOffset, yOffset, getWidth(), getHeight() - yOffset, p);
            canvas.drawPath(mPath, mPaint);
        }
    }


    boolean isInTouchRange(float x, float y) {
        if (parent == null) {
            return false;
        }
        return !(x < parent.horizontalTouchOffset || x > parent.mWidth - parent.horizontalTouchOffset
                || y < parent.verticalTouchOffset || y > parent.mHeight - parent.verticalTouchOffset);
    }

    private void enterTouchModel() {
        mPath.moveTo(lastX, lastY);
        isInDrawModel = true;
        parent.dismissPadding();
    }

    private void exitTouchModel() {
        parent.saveDrawPathBitmap();
        isInDrawModel = false;
        mPath.reset();
        parent.showPadding();
    }


    public Animator getShowPaddingAnimator() {
        final PaddingSettings settings = new PaddingSettings();
        ValueAnimator animator;
        if (parent.horizontalTouchOffset > parent.verticalTouchOffset) {
            animator = ValueAnimator.ofInt(0, parent.horizontalTouchOffset);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    settings.setXOffset((Integer) animation.getAnimatedValue());
                }
            });
        } else {
            animator = ValueAnimator.ofInt(0, parent.verticalTouchOffset);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    settings.setYOffset((Integer) animation.getAnimatedValue());
                }
            });
        }
        animator.setDuration(DoodleView.ANIMATE_TIME);
        return animator;
    }

    public Animator getDismissPaddingAnimator() {
        final PaddingSettings settings = new PaddingSettings();
        ValueAnimator animator;
        if (parent.horizontalTouchOffset > parent.verticalTouchOffset) {
            animator = ValueAnimator.ofInt(parent.horizontalTouchOffset, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    settings.setXOffset((Integer) animation.getAnimatedValue());
                }
            });
        } else {
            animator = ValueAnimator.ofInt(parent.verticalTouchOffset, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    settings.setYOffset((Integer) animation.getAnimatedValue());
                }
            });
        }
        animator.setDuration(DoodleView.ANIMATE_TIME);
        return animator;
    }

    private class PaddingSettings {
        public void setXOffset(int offset) {
            xOffset = offset;
            invalidate();
        }

        public void setYOffset(int offset) {
            yOffset = offset;
            invalidate();
        }

        public int getXOffset() {
            return xOffset;
        }

        public int getYOffset() {
            return yOffset;
        }
    }
}
