package com.code4a;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.code4a.base.BaseActivity;
import com.code4a.library.utils.DensityUtil;
import com.code4a.library.view.ArcProgress;
import com.code4a.library.view.HistogramView;

public class ViewTestActivity extends BaseActivity {

    private ArcProgress mArcProgress;
    private ArcProgress mArcProgress1;
    private HistogramView mHistogramView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        initView();

        mArcProgress1.setProgress(80);
        mArcProgress1.setStrokeWidth(DensityUtil.dp2px(getResources(), 4));
        mArcProgress1.setUnfinishedStrokeColor(Color.parseColor("#ff0000"));
        mArcProgress1.setFinishedStrokeColor(Color.parseColor("#00ff00"));
        mArcProgress1.setShadowStrokeColor(Color.parseColor("#0000ff"));

        mHistogramView.setAnim(true);
        mHistogramView.setProgress(2.5);
        mHistogramView.setOrientation(LinearLayout.VERTICAL);
        mHistogramView.setRateBackgroundColor("#0f0f0f");
    }

    private void initView() {
        mArcProgress = (ArcProgress)findViewById(R.id.arc_progress);
        mArcProgress1 = (ArcProgress)findViewById(R.id.arc_progress1);
        mHistogramView = (HistogramView)findViewById(R.id.histogram_view);
    }
}
