package com.yxf.doodleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by quehuang.du on 2017/12/2.
 */

class SelectView extends View {
    DoodleView parent;

    int cursorWidth = 10;

    private enum State {
        clear,
        cursor,
        select
    }

    State mState = State.clear;

    int lastX, lastY;
    int cursorX, cursorY;
    int selectXIndex, selectYIndex;

    boolean isDown = false;
    boolean isLongClick = false;
    private static final int LONG_CLICK_TIME = 600;
    Runnable longClickTask;

    boolean isShowCursor = true;
    Runnable showCursorTask = new Runnable() {
        @Override
        public void run() {
            isShowCursor = !isShowCursor;
            invalidate();
        }
    };
    private static final int SHOW_CURSOR_TIME = 700;
    private static final int HIDE_CURSOR_TIME = 300;


    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                isLongClick = false;
                longClickTask = new LongClickTask((int) event.getX(), (int) event.getY());
                postDelayed(longClickTask, LONG_CLICK_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                isDown = false;
                break;
            case MotionEvent.ACTION_UP:
                if (!isLongClick) {
                    onClick(SelectView.this, (int) event.getX(), (int) event.getY());
                }
            case MotionEvent.ACTION_CANCEL:
                isDown = false;
                break;
        }
        if (!isDown) {
            removeCallbacks(longClickTask);
        }
        return true;
    }

    private void onLongClick(SelectView selectView, int x, int y) {
        selectItem(x, y);
    }

    private void onClick(View v, int x, int y) {
        setCursor(x, y);
    }

    public void setCursor(int x, int y) {
        mState = State.cursor;
        int xOffset = parent.horizontalOffset;
        int yOffset = parent.verticalOffset;
        int itemWidth = parent.itemWidth;
        int itemHeight = parent.itemHeight;
        int cX = (x - xOffset) / itemWidth + (((x - xOffset) % itemWidth > itemWidth / 2) ? 1 : 0);
        int cY = (y - yOffset) / itemHeight + (((y - yOffset) % itemHeight > itemHeight / 2) ? 1 : 0);
        cursorX = xOffset + cX * itemWidth;
        cursorY = yOffset + cY * itemHeight;
        int index = cY * parent.itemXCount + cX;
        int size = parent.bitmaps.size();
        if (index > size) {
            index = size;
            cX = index % parent.itemXCount;
            cY = index / parent.itemXCount;
            cursorX = xOffset + cX * itemWidth;
            cursorY = yOffset + cY * itemHeight;
        }
        parent.changeCursorIndex(index);
        invalidate();
    }

    public void selectItem(int x, int y) {
        mState = State.select;
        int xOffset = parent.horizontalOffset;
        int yOffset = parent.verticalOffset;
        int itemWidth = parent.itemWidth;
        int itemHeight = parent.itemHeight;
        int cX = (x - xOffset) / itemWidth + (((x - xOffset) % itemWidth > 0) ? 1 : 0) - 1;
        int cY = (y - yOffset) / itemHeight + (((y - yOffset) % itemHeight > 0) ? 1 : 0) - 1;
        selectXIndex = cX;
        selectYIndex = cY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint;
        int xOffset = parent.horizontalOffset;
        int yOffset = parent.verticalOffset;
        int itemWidth = parent.itemWidth;
        int itemHeight = parent.itemHeight;
        switch (mState) {
            case clear:

                break;
            case select:
                /*paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15);
                paint.setColor(Color.RED);
                int left = xOffset + selectXIndex * itemWidth;
                int top = yOffset + selectYIndex * itemHeight;
                int right = left + itemWidth;
                int bottom = top + itemHeight;
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRoundRect(rect, 5, 5, paint);*/
                break;
            case cursor:
                if (isShowCursor) {
                    paint = new Paint();
                    paint.setStyle(Paint.Style.FILL);
                    paint.setStrokeWidth(cursorWidth);
                    paint.setColor(Color.BLACK);
                    canvas.drawLine(cursorX, cursorY, cursorX, cursorY + itemHeight, paint);
                }
                removeCallbacks(showCursorTask);
                if (isShowCursor) {
                    postDelayed(showCursorTask, SHOW_CURSOR_TIME);
                } else {
                    postDelayed(showCursorTask, HIDE_CURSOR_TIME);
                }

                break;
        }
    }


    class LongClickTask implements Runnable {

        int x, y;

        public LongClickTask(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            isLongClick = true;
            onLongClick(SelectView.this, x, y);
        }
    }
}
