package com.yxf.doodleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by quehuang.du on 2017/12/2.
 */

class CellLayout extends RelativeLayout {
    int mXCount,mYCount;

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context) {
        this(context, null);
    }

    private void init() {

    }

    public void setItemCount(int xCount, int yCount) {
        mXCount = xCount;
        mYCount = yCount;
        updateView();
    }

    public void updateView() {
        removeAllViews();


        postInvalidate();
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}
