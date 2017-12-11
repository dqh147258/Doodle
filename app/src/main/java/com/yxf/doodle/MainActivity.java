package com.yxf.doodle;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.yxf.doodleview.DoodleView;

public class MainActivity extends Activity {
    DoodleView doodleView;
    private static final String IMAGE_PATH = "/sdcard/doodle/doodle.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doodleView = (DoodleView) findViewById(R.id.doodle_view);
        doodleView.loadImage(IMAGE_PATH);
        doodleView.setOnOverflowListener(new DoodleView.OnOverflowListener() {
            @Override
            public void onOverflow(Bitmap overflowImage) {
                Toast.makeText(MainActivity.this, "write full", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "删除");
        menu.add(0, 2, 0, "保存");
        menu.add(0, 3, 0, "清空");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                doodleView.deleteItemByCursor();
                break;
            case 2:
                doodleView.saveDoodleAsImage(IMAGE_PATH);
                break;
            case 3:
                doodleView.setItemSize(200, 240);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
