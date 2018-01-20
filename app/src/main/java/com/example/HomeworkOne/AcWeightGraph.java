package com.example.HomeworkOne;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.AcHttpRequest;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.JsonRecordBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Author: kafca
 * Date: 2018/1/17
 * Description: Show personal records graph
 */
public class AcWeightGraph extends AcHttpRequest{
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
        setContentView(R.layout.ac_weight_graph);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.bind(this);
        dataList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id", 0);
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/android_health_test/record/user/" + user_id + "/";
        httpGet(url);
    }

    @Override
    public void initListener() {
        super.initListener();
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void httpGet(@NotNull String url) {
        super.httpGet(url);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AcWeightGraph.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.warning(AcWeightGraph.this,"您的网络似乎开小差了...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    Gson gson = new Gson();
                    JsonRecordBean jsonRecordBean = null;
                    try {
                        jsonRecordBean = gson.fromJson(response.body().string(),
                                JsonRecordBean.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    AcWeightGraph.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lineChart.setData(lineData);
                            Description description = new Description();
                            description.setText("体重随时间变化曲线");
                            description.setTextSize(14);
                            lineChart.setDescription(description);
                            lineChart.setMinimumWidth(50);
                            lineChart.invalidate();
                            XAxis xAxis = lineChart.getXAxis();
                            xAxis.setGranularity(1f);
                            xAxis.setValueFormatter(formatter);
                        }
                    });
                }
                else {
                    AcWeightGraph.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.warning(AcWeightGraph.this,"服务器出错！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}