package com.example.HomeworkOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.AcHttpRequest;
import com.example.HomeworkOne.globalConfig.MyApplication;
import com.google.gson.Gson;
import com.lqr.optionitemview.OptionItemView;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;

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
    @Bind(R.id.last_record_time)
    OptionItemView last_time;
    @Bind(R.id.compare_weight)
    OptionItemView compare_weight;
    @Bind(R.id.ivToolbarNavigation)
    ImageView go_back;
    @Bind(R.id.avi)
    AVLoadingIndicatorView avLoadingIndicatorView;

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

        //request for last record
        MyApplication myApplication = (MyApplication) getApplication();
        String url = myApplication.getHost()+"/android_health_test/record/last/";
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
                                "您的网络似乎开小差了...",Toast.LENGTH_SHORT,true).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    Gson gson = new Gson();
                    JsonRecordBean.RecordBean jsonRecordBean = null;
                    try {
                        jsonRecordBean = gson.fromJson(response.body().string(),
                                JsonRecordBean.RecordBean.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (jsonRecordBean != null) {
                        final String date = jsonRecordBean.get_record_time();
                        final Double last_weight = jsonRecordBean.get_weight();
                        final double compare = getWeight() - last_weight;
                        AcCompareLast.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                last_time.setRightText(date.substring(0,10));
                                if (compare < 0) {
                                    compare_weight.setRightText("瘦了：" + (-compare) + "kg");
                                } else if (compare == 0) {
                                    compare_weight.setRightText("无变化");
                                } else
                                    compare_weight.setRightText("增重了：" + compare + "kg");
                                avLoadingIndicatorView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
                else{
                    AcCompareLast.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.warning(AcCompareLast.this,"还没有上一条记录哦~",
                                    Toast.LENGTH_SHORT,true).show();
                            avLoadingIndicatorView.setVisibility(View.GONE);
                        }
                    });
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
