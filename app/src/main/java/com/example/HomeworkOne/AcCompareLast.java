package com.example.HomeworkOne;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.lqr.optionitemview.OptionItemView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import MyInterface.InitView;
import Utils.JsonRecordBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Author: kafca
 * Date: 2017/12/19
 * Description: Manage the comparison between the last two records
 */

public class AcCompareLast extends Activity implements InitView {
    private double weight;
    List<Entry> dataList;
    private LineData lineData;
    private IAxisValueFormatter formatter;
    @Bind(R.id.last_record_time)
    OptionItemView last_time;
    @Bind(R.id.compare_weight)
    OptionItemView compare_weight;
    @Bind(R.id.ivToolbarNavigation)
    ImageView go_back;
    @Bind(R.id.avi)
    AVLoadingIndicatorView avLoadingIndicatorView;
    @Bind(R.id.chart)
    LineChart lineChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ac_compare_last);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        setWeight(getIntent().getDoubleExtra("weight",0));
        dataList = new ArrayList<>();
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        getData();
    }

    @Override
    public void initListener() {

        // go back to MainActivity
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getData(){
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/android_health_test/user/";
        OkGo.<String>get(url)
                .headers(myApplication.header())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        if (response.code() == 200) {
                            Gson gson = new Gson();
                            JsonRecordBean jsonRecordBean = gson.fromJson(response.body(),
                                    JsonRecordBean.class);
                            if (jsonRecordBean != null) {
                                ArrayList<JsonRecordBean.RecordBean> records = jsonRecordBean.get_records();
                                final int count_record = jsonRecordBean.get_count_record();
                                if (count_record <= 1) {
                                    AcCompareLast.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toasty.warning(AcCompareLast.this, "还没有上一条记录哦~",
                                                    Toast.LENGTH_SHORT, true).show();
                                        }
                                    });
                                } else {
                                    final String []xValues = new String[count_record];
                                    for (int i = 0; i <= count_record - 1; i++) {
                                        double weight = records.get(i).get_weight();
                                        String date = records.get(i).get_record_time().substring(5, 10);
                                        dataList.add(new Entry((float)i,(float)weight));
                                        xValues[i] = date;
                                    }
                                    LineDataSet dataSet = new LineDataSet(dataList, "体重kg");
                                    dataSet.setColor(getResources().getColor(R.color.blue));
                                    dataSet.setValueTextColor(Color.BLACK);
                                    lineData = new LineData(dataSet);
                                    formatter = new IAxisValueFormatter() {
                                        @Override
                                        public String getFormattedValue(float value, AxisBase axis) {
                                            return xValues[(int) value];
                                        }
                                    };

                                    final String date = records.get(count_record - 2).get_record_time();
                                    Double last_weight = records.get(count_record - 2).get_weight();
                                    final double compare = getWeight() - last_weight;
                                    AcCompareLast.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Legend legend = lineChart.getLegend();
                                            legend.setTextSize(14f);
                                            legend.setTextColor(getResources().getColor(R.color.gray0));
                                            lineChart.setData(lineData);
                                            Description description = new Description();
                                            description.setText("体重随时间变化曲线");
                                            description.setTextSize(14);
                                            description.setTextColor(getResources().getColor(R.color.gray0));
                                            lineChart.setDescription(description);
                                            lineChart.setMinimumWidth(50);
                                            lineChart.invalidate();
                                            XAxis xAxis = lineChart.getXAxis();
                                            xAxis.setGranularity(1f);
                                            xAxis.setValueFormatter(formatter);

                                            last_time.setRightText(date.substring(0, 10));
                                            if (compare < 0) {
                                                String str = -compare+"";
                                                if (str.length()>=4)
                                                    str = str.substring(0,4);
                                                compare_weight.setRightText("瘦了：" +str+"kg");
                                            } else if (compare == 0) {
                                                compare_weight.setRightText("无变化");
                                            } else{
                                                String str = compare+"";
                                                if (str.length()>=4)
                                                    str = str.substring(0,4);
                                                compare_weight.setRightText("重了：" +str+"kg");
                                            }
                                            avLoadingIndicatorView.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            } else {
                                AcCompareLast.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.warning(AcCompareLast.this, "还没有上一条记录哦~",
                                                Toast.LENGTH_SHORT, true).show();
                                        avLoadingIndicatorView.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        Toasty.warning(AcCompareLast.this,
                                "您的网络似乎开小差了...", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
