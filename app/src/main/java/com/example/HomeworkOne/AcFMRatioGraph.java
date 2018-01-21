package com.example.HomeworkOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.AcHttpRequest;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.intellij.lang.annotations.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.JsonFMRatioBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Author: kafca
 * Date: 2018/1/21
 * Description: show the ratio of female and male users
 */

public class AcFMRatioGraph extends AcHttpRequest{
    @Bind(R.id.chart_fm_ratio)
    PieChart pieChart;
    @Bind(R.id.ivToolbarNavigation)
    ImageView go_back;
    @Bind(R.id.avi)
    AVLoadingIndicatorView avLoadingIndicatorView;
    private int count_female, count_male;
    private List<PieEntry> dataList;
    private IAxisValueFormatter formatter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_fm_ratio_graph);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.bind(this);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        dataList = new ArrayList<>();
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/data/fm_ratio/";
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
                AcFMRatioGraph.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.warning(AcFMRatioGraph.this,"您的网络似乎开小差了...",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code()==200){
                    Gson gson = new Gson();
                    JsonFMRatioBean jsonFMRatioBean = null;
                    try {
                        jsonFMRatioBean = gson.fromJson(response.body().string(),
                                JsonFMRatioBean.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (jsonFMRatioBean!=null){
                        count_female = jsonFMRatioBean.getFemale();
                        count_male = jsonFMRatioBean.getMale();
                    }
                    dataList.add(new PieEntry((float)count_female,"女"));
                    dataList.add(new PieEntry((float)count_male,"男"));
                    AcFMRatioGraph.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initChart();
                            setData(dataList);
                            avLoadingIndicatorView.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    AcFMRatioGraph.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.warning(AcFMRatioGraph.this,"服务器出错！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initChart(){
        pieChart.setDescription(null);
        pieChart.setUsePercentValues(true);
        pieChart.setExtraOffsets(5, 10, 5, 5);
//        pieChart.setDrawSliceText(false);//设置隐藏饼图上文字，只显示百分比
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleColor(getResources().getColor(R.color.white));
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(45f); //半径
        //pieChart.setHoleRadius(0)  //实心圆
        pieChart.setTransparentCircleRadius(48f);// 半透明圈
        pieChart.setDrawCenterText(true);//饼状图中间可以添加文字
        pieChart.setCenterText("男女用户比例");
        pieChart.setUsePercentValues(true);//设置显示成比例
        pieChart.setRotationAngle(0); // 初始旋转角度
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1000, Easing.EasingOption.EaseInOutQuad); //设置动画
        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);  //左下边显示
        mLegend.setFormSize(14f);//比例块字体大小
        mLegend.setXEntrySpace(2f);//设置距离饼图的距离，防止与饼图重合
        mLegend.setYEntrySpace(2f);
        //设置比例块换行...
        mLegend.setWordWrapEnabled(true);
        mLegend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        mLegend.setTextColor(getResources().getColor(R.color.blue));
        mLegend.setForm(Legend.LegendForm.SQUARE);//设置比例块形状，默认为方块
    }

    private void setData(List<PieEntry> counts) {
        PieDataSet dataSet = new PieDataSet(counts,"");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.red2));
        colors.add(getResources().getColor(R.color.blue));
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(getResources().getColor(R.color.white));
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
}
