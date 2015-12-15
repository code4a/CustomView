# CustomView
CustomView

###效果图

![效果图](https://github.com/code4a/CustomView/blob/master/custom.jpg)

### ArcProgress

##### 扇形进度条

> 常用于时速表盘，压力表盘等

* 使用方式 `xml` 中

        <com.code4a.library.view.ArcProgress
          android:id="@+id/arc_progress"
          android:layout_width="100dp"
          android:layout_height="100dp"
          custom:arc_bottom_text="O2"
          custom:arc_progress="20"
          custom:arc_stroke_width="6dp" />

        <com.code4a.library.view.ArcProgress
          android:id="@+id/arc_progress1"
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:layout_toRightOf="@id/arc_progress"
          custom:arc_bottom_text="AIR"/>
        
* `java` 文件中

        mArcProgress1.setProgress(80);
        mArcProgress1.setStrokeWidth(DensityUtil.dp2px(getResources(), 4));
        mArcProgress1.setUnfinishedStrokeColor(Color.parseColor("#ff0000"));
        mArcProgress1.setFinishedStrokeColor(Color.parseColor("#00ff00"));
        mArcProgress1.setShadowStrokeColor(Color.parseColor("#0000ff"));

### HistogramView

##### 带刻度柱状图

> 常用于流量计等

* 使用方式 `xml` 中

        <com.code4a.library.view.HistogramView
          android:id="@+id/histogram_view"
          android:layout_below="@id/arc_progress"
          android:layout_margin="10dp"
          android:layout_width="55dp"
          android:layout_height="150dp" />
        
* `java` 文件中

        mHistogramView.setAnim(true);
        mHistogramView.setProgress(2.5);
        mHistogramView.setOrientation(LinearLayout.VERTICAL);
        mHistogramView.setRateBackgroundColor("#0f0f0f");
    
