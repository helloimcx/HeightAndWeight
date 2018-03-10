package com.example.HomeworkOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * Author: kafca
 * Date: 2017/11/18
 * Description: Discover Fragment
 */

public class FmDiscoverNav extends Fragment implements InitView {
   @Bind(R.id.viewpager)
   ViewPager viewPager;
   @Bind(R.id.viewpagertab)
   SmartTabLayout viewPagerTab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        initView();
        initListener();
    }


    @Override
    public void initView() {
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("世界", FmDiscover.class)
                .add("我的", FmDiscover.class)
                .create());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void initListener() {

    }

}
