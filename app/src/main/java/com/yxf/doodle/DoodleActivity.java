package com.yxf.doodle;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.yxf.doodleview.DoodleView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DoodleActivity extends Activity {
    DoodleView doodleView;
    private String imagePath;
    private int cellLayoutBackgroundColor = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String imgPath = getSavePath();
        imagePath = imgPath;
        if (imagePath != null) {
            imagePath = imagePath.replace("thumbnail", "");
        }
        doodleView = (DoodleView) findViewById(R.id.doodle_view);
        boolean changed = false;
        if (savedInstanceState != null) {
            changed = savedInstanceState.getBoolean("changed", false);
        }
        if (!changed) {
            doodleView.setItemSize(80, 96);
            doodleView.setPaintWidth(10);
            doodleView.setCursorWidth(6);
            if (imagePath != null) {
                SharedPreferences sp = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                cellLayoutBackgroundColor = sp.getInt(imagePath, Color.WHITE);
                doodleView.setCellLayoutBackgroundColor(cellLayoutBackgroundColor);
                Map<String, ?> map = sp.getAll();
                Iterator<? extends Map.Entry<String, ?>> i = map.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<String, ?> entry = i.next();
                    String path = entry.getKey();
                    if (path != null) {
                        File file = new File(path);
                        if (!file.exists()) {
                            editor.remove(path);
                        }
                    }
                }
                editor.commit();
                doodleView.loadImage(imagePath);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("changed", true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_doodle, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_backspace:
                doodleView.deleteItemByCursor();
                break;
            case R.id.menu_item_save:
                saveDoodle();
                break;
            case R.id.menu_item_delete:
                doodleView.clear();
                break;
            case R.id.menu_item_cell_color_black:
                setCellColor(Color.BLACK);
                break;
            case R.id.menu_item_cell_color_blue:
                setCellColor(Color.BLUE);
                break;
            case R.id.menu_item_cell_color_green:
                setCellColor(Color.GREEN);
                break;
            case R.id.menu_item_cell_color_orange:
                setCellColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
            case R.id.menu_item_cell_color_pink:
                setCellColor(getResources().getColor(R.color.pink));
                break;
            case R.id.menu_item_cell_color_purple:
                setCellColor(getResources().getColor(R.color.purple));
                break;
            case R.id.menu_item_cell_color_red:
                setCellColor(Color.RED);
                break;
            case R.id.menu_item_cell_color_transparency:
                setCellColor(Color.TRANSPARENT);
                break;
            case R.id.menu_item_cell_color_white:
                setCellColor(Color.WHITE);
                break;
            case R.id.menu_item_cell_color_yellow:
                setCellColor(Color.YELLOW);
                break;
            case R.id.menu_item_paint_color_black:
                setPaintColor(Color.BLACK);
                break;
            case R.id.menu_item_paint_color_blue:
                setPaintColor(Color.BLUE);
                break;
            case R.id.menu_item_paint_color_green:
                setPaintColor(Color.GREEN);
                break;
            case R.id.menu_item_paint_color_orange:
                setPaintColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
            case R.id.menu_item_paint_color_pink:
                setPaintColor(getResources().getColor(R.color.pink));
                break;
            case R.id.menu_item_paint_color_purple:
                setPaintColor(getResources().getColor(R.color.purple));
                break;
            case R.id.menu_item_paint_color_red:
                setPaintColor(Color.RED);
                break;
            case R.id.menu_item_paint_color_white:
                setPaintColor(Color.WHITE);
                break;
            case R.id.menu_item_paint_color_yellow:
                setPaintColor(Color.YELLOW);
                break;
            case R.id.menu_item_background_color_black:
                setCellLayoutBackgroundColor(Color.BLACK);
                break;
            case R.id.menu_item_background_color_blue:
                setCellLayoutBackgroundColor(Color.BLUE);
                break;
            case R.id.menu_item_background_color_green:
                setCellLayoutBackgroundColor(Color.GREEN);
                break;
            case R.id.menu_item_background_color_orange:
                setCellLayoutBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                break;
            case R.id.menu_item_background_color_pink:
                setCellLayoutBackgroundColor(getResources().getColor(R.color.pink));
                break;
            case R.id.menu_item_background_color_purple:
                setCellLayoutBackgroundColor(getResources().getColor(R.color.purple));
                break;
            case R.id.menu_item_background_color_red:
                setCellLayoutBackgroundColor(Color.RED);
                break;
            case R.id.menu_item_background_color_transparency:
                setCellLayoutBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.menu_item_background_color_white:
                setCellLayoutBackgroundColor(Color.WHITE);
                break;
            case R.id.menu_item_background_color_yellow:
                setCellLayoutBackgroundColor(Color.YELLOW);
                break;
            case R.id.menu_item_paint_width_5:
                setPaintWidth(5);
                break;
            case R.id.menu_item_paint_width_8:
                setPaintWidth(8);
                break;
            case R.id.menu_item_paint_width_10:
                setPaintWidth(10);
                break;
            case R.id.menu_item_paint_width_12:
                setPaintWidth(12);
                break;
            case R.id.menu_item_paint_width_15:
                setPaintWidth(15);
                break;
            case R.id.menu_item_paint_width_20:
                setPaintWidth(20);
                break;
            case R.id.menu_item_paint_width_30:
                setPaintWidth(30);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDoodle() {
        int rate = 4;
        if (imagePath == null) {
            imagePath = getSavePath();
        }
        Bitmap bm = doodleView.getDoodleImage();
        Bitmap thumbnail = Bitmap.createBitmap(bm.getWidth() / rate, bm.getHeight() / rate, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumbnail);
        canvas.drawColor(cellLayoutBackgroundColor);
        Rect src = new Rect(0, 0, bm.getWidth(), bm.getHeight());
        Rect dest = new Rect(0, 0, thumbnail.getWidth(), thumbnail.getHeight());
        canvas.drawBitmap(bm, src, dest, null);
        String thumbnailPath;
        String[] strArray = imagePath.split("\\.");
        if (strArray.length > 1) {
            thumbnailPath = strArray[0];
            for (int i = 1; i < strArray.length - 1; i++) {
                thumbnailPath = thumbnailPath + "." + strArray[i];
            }
            thumbnailPath = thumbnailPath + "thumbnail" + "." + strArray[strArray.length - 1];
        } else {
            thumbnailPath = imagePath + "thumbnail";
        }
        saveBitmap(thumbnail, thumbnailPath);
        saveBitmap(bm, imagePath);
        SharedPreferences sp = getSharedPreferences(this.getClass().getName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(imagePath, cellLayoutBackgroundColor);
        editor.commit();
        Intent intent = getIntent();
        Bundle b = new Bundle();
        b.putString("handwritePath", thumbnailPath);
        intent.putExtras(b);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static boolean saveBitmap(Bitmap bm, String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            return false;
        } else {
            try {
                if (file.exists()) {
                    file.delete();
                }

                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    Log.e("Doodle", "create file folder failed");
                    return false;
                } else {
                    file.createNewFile();
                    FileOutputStream e = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, e);
                    e.flush();
                    e.close();
                    return true;
                }
            } catch (FileNotFoundException var5) {
                var5.printStackTrace();
                return false;
            } catch (IOException var6) {
                var6.printStackTrace();
                return false;
            }
        }
    }

    private void setCellColor(int color) {
        doodleView.setCellBackgroundColor(color);
    }

    private void setCellLayoutBackgroundColor(int color) {
        cellLayoutBackgroundColor = color;
        doodleView.setCellLayoutBackgroundColor(color);
    }

    private void setPaintColor(int color) {
        doodleView.setPaintColor(color);
    }

    private void setPaintWidth(int width) {
        doodleView.setPaintWidth(width);
    }


    public String getSavePath() {
        String paintPath ;
        paintPath = "/sdcard/doodle/test.png";
        return paintPath;
    }
}
