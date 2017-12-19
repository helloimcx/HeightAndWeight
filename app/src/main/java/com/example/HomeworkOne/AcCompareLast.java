package com.example.HomeworkOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.HomeworkOne.BaseActivity.AcHttpRequest;
import com.google.gson.Gson;
import com.lqr.optionitemview.OptionItemView;

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
    @Bind(R.id.last_record_time)
    OptionItemView time;
    @Bind(R.id.compare_weight)
    OptionItemView compare_weight;

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
    }

    @Override
    public void initListener() {
        super.initListener();
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
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                JsonRecordBean.RecordBean jsonRecordBean = gson.fromJson(response.body().string(),
                        JsonRecordBean.RecordBean.class);
                final String date = jsonRecordBean.get_record_time();
                final Double last_weight = jsonRecordBean.get_weight();
                AcCompareLast.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.success(AcCompareLast.this,
                                date+'\n'+last_weight,
                                Toast.LENGTH_SHORT,true).show();
                    }
                });
            }
        });
    }
}
