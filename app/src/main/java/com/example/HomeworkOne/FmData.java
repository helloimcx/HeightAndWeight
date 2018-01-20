package com.example.HomeworkOne;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lqr.optionitemview.OptionItemView;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: kafca
 * Date: 2018/1/20
 * Description:
 */

public class FmData extends Fragment implements InitView{
    @Bind(R.id.data_weight)
    OptionItemView data_weight;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_data,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        initView();
        initListener();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initListener() {
        data_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AcWeightGraph.class);
                startActivity(intent);
            }
        });
    }
}
