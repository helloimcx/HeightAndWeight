package com.example.HomeworkOne;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.BaseActivity;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import Utils.JsonRecordBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
/**
 * Author: kafca
 * Date: 2018/1/17
 * Description: Show personal records graph
 */
public class AcWeightGraph extends BaseActivity{
    List<Entry> dataList;
    private LineData lineData;
    private IAxisValueFormatter formatter;
    @Bind(R.id.chart_weight)
    LineChart lineChart;
    @Bind(R.id.ivToolbarNavigation)
    ImageView go_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ac_weight_graph);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        showLoading();
        ButterKnife.bind(this);
        dataList = new ArrayList<>();
        getData();
    }

    @Override
    public void initListener() {
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getData() {
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/android_health_test/user/";
        OkGo.<String>get(url)
                .headers(myApplication.header())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        if(response.code()==200){
                            Gson gson = new Gson();
                            JsonRecordBean jsonRecordBean = gson.fromJson(response.body(),
                                    JsonRecordBean.class);
                            if (jsonRecordBean != null) {
                                ArrayList<JsonRecordBean.RecordBean> records = jsonRecordBean.get_records();
                                final int count_record = jsonRecordBean.get_count_record();
                                if (count_record <= 0) {
                                    AcWeightGraph.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toasty.warning(AcWeightGraph.this, "还没有记录哦~",
                                                    Toast.LENGTH_SHORT, true).show();
                                        }
                                    });
                                } else {
                                    final String[] xValues = new String[count_record];
                                    for (int i = 0; i <= count_record - 1; i++) {
                                        double weight = records.get(i).get_weight();
                                        String date = records.get(i).get_record_time().substring(5, 10);
                                        dataList.add(new Entry((float) i, (float) weight));
                                        xValues[i] = date;
                                    }
                                    LineDataSet dataSet = new LineDataSet(dataList, "体重kg"); // add entries to dataset
                                    dataSet.setColor(getResources().getColor(R.color.blue));
                                    dataSet.setValueTextColor(Color.BLACK);
                                    lineData = new LineData(dataSet);
                                    formatter = new IAxisValueFormatter() {
                                        @Override
                                        public String getFormattedValue(float value, AxisBase axis) {
                                            int index = (int) value;
                                            if(index==-1)
                                                index=0;
                                            else if(index==count_record)
                                                index = index - 1;
                                            return xValues[index];
                                        }
                                    };
                                }
                            }
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
                            hideLoading();
                        }
                        else {
                            Toasty.warning(AcWeightGraph.this,"请求失败!",
                                    Toast.LENGTH_SHORT).show();
                            hideLoading();
                        }
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        Toasty.warning(AcWeightGraph.this,"您的网络似乎开小差了...",
                                Toast.LENGTH_SHORT).show();
                        hideLoading();
                    }
                });
    }
}