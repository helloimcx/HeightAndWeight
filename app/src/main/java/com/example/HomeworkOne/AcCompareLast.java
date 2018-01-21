package com.example.HomeworkOne;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.lqr.optionitemview.OptionItemView;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.JsonRecordBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Author: kafca
 * Date: 2017/12/19
 * Description: Manage the comparison between the last two records
 */

public class AcCompareLast extends AcHttpRequest {
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
        setContentView(R.layout.ac_compare_last);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.bind(this);
        setWeight(getIntent().getDoubleExtra("weight",0));
        dataList = new ArrayList<>();

        //request for last record
        SharedPreferences sharedPreferences = getSharedPreferences("Session",MODE_PRIVATE);
        int user_id = sharedPreferences.getInt("user_id", 0);
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/android_health_test/record/user/" + user_id + "/";
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        httpGet(url);
    }

    @Override
    public void initListener() {
        super.initListener();

        // go back to MainActivity
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
                AcCompareLast.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.warning(AcCompareLast.this,
                                "您的网络似乎开小差了...", Toast.LENGTH_SHORT, true).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
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
                            LineDataSet dataSet = new LineDataSet(dataList, "体重kg"); // add entries to dataset
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
        });
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
