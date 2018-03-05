#  一个涂鸦笔记本的自定义View第三方库

## 目录

[TOC]

## 简介

还记得曾经QQ的涂鸦功能吗？现在这个第三方库也支持涂鸦，而且功能更加强大，支持切换画笔颜色，切换背景色，字体背景色，画笔粗细以及用光标删除,插入。来看看效果吧。
![introduction](http://resource-1255703580.cossh.myqcloud.com/Doodle/introduction.gif)
## 集成
- 以源码组件形式集成

```shell
dependencies {
 	compile project(':doodleview')
}
```

- 以第三方库形式集成

```shell
dependencies {
  	compile fileTree(include: ['*.jar'], dir: 'libs')
	compile files('libs/com.yxf.doodleview-1.0.jar')
}
```

## 初始化

```java 
DoodleView doodleView;
doodleView = (DoodleView) findViewById(R.id.doodle_view);
doodleView.setItemSize(80, 96);//这条必须有,设置文字格子大小
doodleView.setPaintWidth(10);//设置画笔粗细
doodleView.setCursorWidth(6);//设置光标粗细
```

## 其他方法说明
- `doodleView.saveDoodleAsImage(imagePath) ` 将涂鸦转化为图片并储存在imagePath路径下
- `doodleView.loadImage(imagePath)` 还原储存的图片为涂鸦文字
- `doodleView.getDoodleImage()` 将涂鸦转化成图片
- `doodleView.setCellLayoutBackgroundColor(cellLayoutBackgroundColor)` 设置涂鸦背景(以上存储方式不会将涂鸦背景一起存进图片中)
- `doodleView.deleteItemByCursor()` 借助光标删除涂鸦文字
- `doodleView.clear()` 清空涂鸦的所有文字
- `doodleView.setCellBackgroundColor(color)` 设置文字背景
- `doodleView.setPaintColor(color)` 设置画笔颜色

## 功能演示
- 改变画笔颜色

  ![change_paint_color](http://resource-1255703580.cossh.myqcloud.com/Doodle/change_paint_color.gif)

- 改变画笔粗细

  ![change_paint_width](http://resource-1255703580.cossh.myqcloud.com/Doodle/change_paint_width.gif)



- 改变文字背景颜色

  ![change_cell_color](http://resource-1255703580.cossh.myqcloud.com/Doodle/change_cell_color.gif)

- 改变涂鸦背景颜色

  ![change_background_color](http://resource-1255703580.cossh.myqcloud.com/Doodle/change_background_color.gif)

- 删除

  ![delete](http://resource-1255703580.cossh.myqcloud.com/Doodle/delete.gif)

- 储存恢复

![save_and_restore](http://resource-1255703580.cossh.myqcloud.com/Doodle/save_and_restore.gif)

## 源码及jar包
[DoodleView](https://github.com/dqh147258/Doodle)