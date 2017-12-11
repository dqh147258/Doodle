package com.yxf.doodleview;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by quehuang.du on 2017/12/2.
 */

public class DoodleView extends FrameLayout {

    public static final int ANIMATE_TIME = 200;

    public static final String TAG = "DoodleView";

    List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    TaskExecutor taskExecutor = new TaskExecutor();

    CellLayout cellLayout;
    SelectView selectView;
    DrawView drawView;
    ImageView animationView;

    Context context;

    int mHeight, mWidth;

    int itemWidth, itemHeight;
    int verticalOffset, horizontalOffset;
    int xOffset, yOffset;
    int verticalTouchOffset, horizontalTouchOffset;
    int itemXCount, itemYCount;

    int paddingColor = getResources().getColor(android.R.color.holo_blue_light);

    int cellBackgroundColor = Color.TRANSPARENT;

    int cellBottomLineColor = Color.rgb(1, 1, 1);

    int cursorIndex;

    OnOverflowListener overflowListener;

    //Animate View
    int animateLeft, animateTop, animateWidth, animateHeight;
    private int cellLayoutBackgroundColor = Color.WHITE;


    //public method


    public void setItemSize(int width, int height) {
        itemWidth = width;
        itemHeight = height;
        initView();
    }

    public void setItemSizeByUser(final int width, final int height) {
        taskExecutor.addTask(new Runnable() {
            @Override
            public void run() {
                setItemSize(width, height);
            }
        }, TaskExecutor.USER_TASK_PRIORITY);
    }

    public void setPaddingColor(int color) {
        paddingColor = color;
        invalidate();
    }

    public void setCellBackgroundColor(final int color) {
        cellBackgroundColor = color;
    }

    public void setCellLayoutBackgroundColor(final int color) {
        cellLayoutBackgroundColor = color;
        cellLayout.setBackgroundColor(color);
    }

    public void setCursor(int x, int y) {
        selectView.setCursor(x, y);
    }

    public void deleteItemByCursor() {
        if (cursorIndex == 0) {
            return;
        }
        bitmaps.remove(--cursorIndex);
        setCursorIndex(cursorIndex);
        updateCellLayout();
    }

    public void setPaintWidth(int width) {
        drawView.paintWidth = width;
    }

    public void setPaintColor(int color) {
        drawView.paintColor = color;
    }

    public void setCursorWidth(int width) {
        selectView.cursorWidth = width;
    }

    public void clear() {
        bitmaps.clear();
        if (mWidth == 0) {
            taskExecutor.addTask(new Runnable() {
                @Override
                public void run() {
                    clear();
                }
            }, TaskExecutor.USER_TASK_PRIORITY);
            requestLayout();
            return;
        }
        setCursorIndex(0);
        updateCellLayout();
    }

    public void setOnOverflowListener(OnOverflowListener listener) {
        overflowListener = listener;
    }


    public Bitmap getDoodleImage() {
        Bitmap bitmap = Bitmap.createBitmap(cellLayout.getWidth(), cellLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        cellLayout.dispatchDraw(canvas);
        return bitmap;
    }

    /*@RequiresPermission(allOf = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    })*/
    public boolean saveDoodleAsImage(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return false;
        }
        Bitmap bm = getDoodleImage();
        try {
            if (file.exists()) {
                file.delete();
            }
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    Log.e("Doodle", "create file folder failed");
                    return false;
                }
            }
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*@RequiresPermission(allOf = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    })*/
    public boolean loadImage(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        try {
            FileInputStream in = new FileInputStream(f);
            loadImage(BitmapFactory.decodeStream(in));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadImage(final Bitmap bm) {
        if (mWidth == 0) {
            taskExecutor.addTask(new Runnable() {
                @Override
                public void run() {
                    loadImage(bm);
                }
            }, TaskExecutor.SYSTEM_TASK_PRIORITY);
            requestLayout();
            return;
        }
        if (itemWidth == 0 || itemHeight == 0) {
            Log.e(TAG, "itemWidth : " + itemWidth + ", itemHeight : " + itemHeight);
            return;
        }

        int xCount = bm.getWidth() / itemWidth;
        int yCount = bm.getHeight() / itemHeight;
        if (xCount == 0 || yCount == 0) {
            Log.e(TAG, "xCount : " + xCount + ", yCount : " + yCount);
            return;
        }
        int x;
        int y;
        int index = 0;
        bitmaps.clear();
        while (true) {
            int i = index % xCount;
            int j = index / xCount;
            if (j >= yCount) {
                break;
            }
            int left = i * itemWidth;
            int top = j * itemHeight;
            x = left + itemWidth / 2;
            y = top + itemHeight - 2;
            int color = bm.getPixel(x, y);
            if (color == cellBottomLineColor) {
                Bitmap child = Bitmap.createBitmap(bm, left, top, itemWidth, itemHeight);
                bitmaps.add(child);
                index++;
            } else {
                Log.v(TAG, "the color of empty space is : " + color + " , the color of bottom line is : " + cellBottomLineColor);
                break;
            }
        }
        if (index != 0) {
            setCursorIndex(index);
            updateCellLayout();
        }
    }


    public DoodleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        cellLayout = new CellLayout(context);
        setCellLayoutBackgroundColor(Color.WHITE);
        selectView = new SelectView(context);
        drawView = new DrawView(context);
        animationView = new ImageView(context);
        initData();
        setItemSize(100, 120);
    }

    private void initData() {

    }

    public DoodleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoodleView(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width, height;
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        cellLayout.layout(horizontalOffset, verticalOffset, width - horizontalOffset, height - verticalOffset);
        animationView.layout(animateLeft, animateTop, animateLeft + animateWidth, animateTop + animateHeight);
        post(new Runnable() {
            @Override
            public void run() {
                taskExecutor.execute();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView() {
        if (getMeasuredWidth() <= 0) {
            taskExecutor.addTask(new Runnable() {
                @Override
                public void run() {
                    initView();
                }
            }, TaskExecutor.INIT_TASK_PRIORITY);
            requestLayout();
        } else {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
            calculateItemParams();
            removeAllViews();
            addView(cellLayout);
            addView(selectView);
            addView(drawView);
            addView(animationView);
            drawView.parent = this;
            selectView.parent = this;
            setPaddingColor(paddingColor);
            if (bitmaps.size() > 0) {
                updateCellLayout();
                setCursorIndex(bitmaps.size());
            }
            requestLayout();
        }
    }


    private void calculateItemParams() {
        if (itemWidth > 0 && itemHeight > 0 && mWidth > 0 && mHeight > 0) {
            itemXCount = mWidth / itemWidth;
            itemYCount = mHeight / itemHeight;
            xOffset = horizontalOffset = (mWidth - itemWidth * itemXCount) / 2;
            yOffset = verticalOffset = (mHeight - itemHeight * itemYCount) / 2;
            if (mWidth / (float) mHeight > itemWidth / (float) itemHeight) {
                verticalTouchOffset = 0;
                horizontalTouchOffset = (mWidth - mHeight * itemWidth / itemHeight) / 2;
            } else {
                horizontalTouchOffset = 0;
                verticalTouchOffset = (mHeight - mWidth * itemHeight / itemWidth) / 2;
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle save = new Bundle();
        save.putBoolean("hasExtra", true);
        save.putInt("itemWidth", itemWidth);
        save.putInt("itemHeight", itemHeight);
        save.putInt("paddingColor", paddingColor);
        save.putInt("cellBackgroundColor", cellBackgroundColor);
        save.putInt("cellLayoutBackgroundColor",cellLayoutBackgroundColor);
        save.putInt("cursorIndex", cursorIndex);
        save.putInt("paintWidth", drawView.paintWidth);
        save.putInt("paintColor", drawView.paintColor);
        save.putInt("cursorWidth", selectView.cursorWidth);
        Bundle images = new Bundle();
        int size = bitmaps.size();
        images.putInt("size", size);
        for (int i = 0; i < size; i++) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, out);
            images.putByteArray(String.valueOf(i), out.toByteArray());
        }
        save.putBundle("images", images);
        save.putParcelable("superExtra", super.onSaveInstanceState());
        return save;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            Bundle save = (Bundle) state;
            boolean hasExtra = save.getBoolean("hasExtra", false);
            if (hasExtra) {
                Parcelable superExtra = save.getParcelable("superExtra");
                super.onRestoreInstanceState(superExtra);
                itemWidth = save.getInt("itemWidth", itemWidth);
                itemHeight = save.getInt("itemHeight", itemHeight);
                paddingColor = save.getInt("paddingColor", paddingColor);
                cellBackgroundColor = save.getInt("cellBackgroundColor", cellBackgroundColor);
                cellLayoutBackgroundColor = save.getInt("cellLayoutBackgroundColor", cellLayoutBackgroundColor);
                cellLayout.setBackgroundColor(cellLayoutBackgroundColor);
                cursorIndex = save.getInt("cursorIndex", cursorIndex);
                drawView.paintWidth = save.getInt("paintWidth", drawView.paintWidth);
                drawView.paintColor = save.getInt("paintColor", drawView.paintColor);
                selectView.cursorWidth = save.getInt("cursorWidth", selectView.cursorWidth);
                Bundle images = save.getBundle("images");
                if (images != null) {
                    bitmaps.clear();
                    int size = images.getInt("size", 0);
                    for (int i = 0; i < size; i++) {
                        byte[] data = images.getByteArray(String.valueOf(i));
                        bitmaps.add(BitmapFactory.decodeByteArray(data, 0, data.length));
                    }
                }
                setItemSize(itemWidth, itemHeight);
            } else {
                super.onRestoreInstanceState(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = drawView.dispatchTouchEvent(ev);
        if (!result) {
            selectView.dispatchTouchEvent(ev);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(paddingColor);
        canvas.drawRect(0, 0, getWidth(), yOffset, p);
        canvas.drawRect(0, getHeight() - yOffset, getWidth(), getHeight(), p);
        canvas.drawRect(0, yOffset, xOffset, getHeight() - yOffset, p);
        canvas.drawRect(getWidth() - xOffset, yOffset, getWidth(), getHeight() - yOffset, p);
        super.dispatchDraw(canvas);
    }


    private void selectItem(int x, int y) {
        selectView.selectItem(x, y);
    }

    void changeCursorIndex(int index) {
        if (index > bitmaps.size()) {
            index = bitmaps.size();
        }
        cursorIndex = index;
    }

    void setCursorIndex(int index) {
        int x = index % itemXCount;
        int y = index / itemXCount;
        if (x == 0 && y != 0) {
            x = itemXCount;
            y--;
        }
        setCursor(xOffset + x * itemWidth, yOffset + y * itemHeight);
    }

    void saveDrawPathBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawView.draw(canvas);
        Bitmap bp = Bitmap.createBitmap(itemWidth, itemHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bp);
        Rect src = new Rect(horizontalTouchOffset, verticalTouchOffset, mWidth - horizontalTouchOffset, mHeight - verticalTouchOffset);
        Rect dest = new Rect(0, 0, itemWidth, itemHeight);
        canvas.drawColor(cellBackgroundColor);
        canvas.drawBitmap(bitmap, src, dest, null);
        Paint p = new Paint();
        p.setStrokeWidth(2);
        p.setColor(cellBottomLineColor);
        canvas.drawLine(0, itemHeight - 1, itemWidth, itemHeight - 1, p);
        Bitmap drawBit = Bitmap.createBitmap(mWidth - horizontalTouchOffset * 2, mHeight - verticalTouchOffset * 2, Bitmap.Config.ARGB_8888);
        dest = new Rect(0, 0, mWidth - horizontalTouchOffset * 2, mHeight - verticalTouchOffset * 2);
        canvas = new Canvas(drawBit);
        canvas.drawBitmap(bitmap, src, dest, null);
        int maxNumber = itemXCount * itemYCount;
        if (bitmaps.size() >= maxNumber) {
            dismissAnimateDrawView(drawBit);
            if (overflowListener != null) {
                overflowListener.onOverflow(drawBit.copy(Bitmap.Config.ARGB_8888, true));
            }
        } else {
            bitmaps.add(cursorIndex, bp);
            animateDrawView(drawBit);
        }
    }

    private void updateCellLayout() {
        cellLayout.removeAllViews();
        int size = bitmaps.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bitmaps.get(i));
            int x = i % itemXCount;
            int y = i / itemXCount;
            int left = itemWidth * x;
            int top = itemHeight * y;
            setRelativeLocation(cellLayout, imageView, left, top, itemWidth, itemHeight);
        }
    }

    void setRelativeLocation(RelativeLayout parent, View child, int left, int top, int width, int height) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        lp.setMargins(left, top, 0, 0);
        parent.addView(child);
        child.setLayoutParams(lp);
    }

    // padding animator
    void showPadding() {
        final PaddingSettings settings = new PaddingSettings();
        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATE_TIME);
        ValueAnimator setXOffset = ValueAnimator.ofInt(0, horizontalOffset);
        setXOffset.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setXOffset((Integer) animation.getAnimatedValue());
            }
        });
        ValueAnimator setYOffset = ValueAnimator.ofInt(0, verticalOffset);
        setYOffset.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setYOffset((Integer) animation.getAnimatedValue());
            }
        });

        set.playTogether(
                setXOffset,
                setYOffset
        );
        Tools.playAnimationSequentially(
                drawView.getDismissPaddingAnimator(),
                set
        );
    }

    void dismissPadding() {
        final PaddingSettings settings = new PaddingSettings();
        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATE_TIME);
        ValueAnimator setXOffset = ValueAnimator.ofInt(horizontalOffset, 0);
        setXOffset.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setXOffset((Integer) animation.getAnimatedValue());
            }
        });
        ValueAnimator setYOffset = ValueAnimator.ofInt(verticalOffset, 0);
        setYOffset.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setYOffset((Integer) animation.getAnimatedValue());
            }
        });
        set.playTogether(
                setXOffset,
                setYOffset
        );
        Tools.playAnimationSequentially(
                set,
                drawView.getShowPaddingAnimator()
        );
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

    //animate view animator
    private void animateDrawView(Bitmap bitmap) {
        final int ANIMATE_TIME = 300;
        int aWidth = mWidth - horizontalTouchOffset * 2;
        int aHeight = mHeight - verticalTouchOffset * 2;
        int targetLeft = cursorIndex % itemXCount * itemWidth + horizontalOffset;
        int targetTop = cursorIndex / itemXCount * itemHeight + verticalOffset;
        animationView.setImageBitmap(bitmap);
        final AnimateViewSettings settings = new AnimateViewSettings();
        ValueAnimator setLeft = ValueAnimator.ofInt(horizontalTouchOffset, targetLeft);
        setLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setLeft((Integer) animation.getAnimatedValue());
            }
        });
        ValueAnimator setTop = ValueAnimator.ofInt(verticalTouchOffset, targetTop);
        setTop.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setTop((Integer) animation.getAnimatedValue());
            }
        });
        ValueAnimator setWidth = ValueAnimator.ofInt(aWidth, itemWidth);
        setWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setWidth((Integer) animation.getAnimatedValue());
            }
        });
        ValueAnimator setHeight = ValueAnimator.ofInt(aHeight, itemHeight);
        setHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                settings.setHeight((Integer) animation.getAnimatedValue());
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.playTogether(setLeft, setTop, setWidth, setHeight);
        set.setDuration(ANIMATE_TIME);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                updateCellLayout();
                setCursorIndex(cursorIndex + 1);
                animationView.setImageBitmap(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Toast.makeText(context, "Animation has been cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    private void dismissAnimateDrawView(Bitmap bitmap) {
        final int ANIMATE_TIME = 300;
        int aWidth = mWidth - horizontalTouchOffset * 2;
        int aHeight = mHeight - verticalTouchOffset * 2;
        int targetLeft = cursorIndex % itemXCount * itemWidth + horizontalOffset;
        int targetTop = cursorIndex / itemXCount * itemHeight + verticalOffset;
        animationView.setImageBitmap(bitmap);
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(ANIMATE_TIME);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                animationView.setAlpha(value);
            }
        });
        animator.start();
    }

    private class AnimateViewSettings {
        public void setLeft(int left) {
            animateLeft = left;
            requestLayout();
        }

        public void setTop(int top) {
            animateTop = top;
            requestLayout();
        }

        public void setWidth(int width) {
            animateWidth = width;
            requestLayout();
        }

        public void setHeight(int height) {
            animateHeight = height;
            requestLayout();
        }

        public int getLeft() {
            return animateLeft;
        }

        public int getTop() {
            return animateTop;
        }

        public int getWidth() {
            return animateWidth;
        }

        public int getHeight() {
            return animateHeight;
        }
    }

    public interface OnOverflowListener {
        void onOverflow(Bitmap overflowImage);
    }
}
