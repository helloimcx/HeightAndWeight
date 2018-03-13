package com.example.HomeworkOne;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.squareup.picasso.Picasso;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MyInterface.InitView;
import Utils.JsonMomentBean;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: kafca
 * Date: 2017/11/18
 * Description: Discover Fragment
 */

public class FmMyDiscover extends Fragment implements InitView {
    private List<Map<String, Object>> dataList;
    private int count_moment;

    //请求的数据所在的页数
    private int request_page = 1;

    private ArrayList<JsonMomentBean.MomentBean> results;
    private CommonAdapter<Map<String, Object>> commonAdapter;
    @Bind(R.id.moments_list)
    ListView listView;
    @Bind(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dicover, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        dataList = new ArrayList<>();

        //获取moment数据
        getData(1);

        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            //下拉刷新
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dataList.clear();
                        //回到第一页
                        request_page = 1;
                        getData(request_page);
                        refreshLayout.finishRefreshing();
                    }
                },2000);
            }

            //上拉加载更多
            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //请求下一页
                        request_page++;
                        getData(request_page);
                        refreshLayout.finishLoadmore();
                    }
                },2000);
            }
        });
    }

    @Override
    public void initListener() {

    }

    private void getData(final int page) {
        OkHttpClient okHttpClient = MainActivity.okHttpClient;
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        String host = myApplication.getHost();
        boolean all = new Bundler().get().getBoolean("all");
        String url = url = host+"/moment/me/?page="+page;
        final Request request = new Request.Builder().url(url)
                .addHeader("cookie", MainActivity.sessionid)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "您的网络似乎开了小差...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Gson gson = new Gson();
                try {
                    int response_code = response.code();
                    //若下一页数据为空，回到当前页
                    if(response_code == 404){
                        request_page--;
                        return;
                    }
                    JsonMomentBean jsonMomentBean = gson.fromJson(response.body().string(),
                            JsonMomentBean.class);
                    count_moment = jsonMomentBean.getCount();
                    results = jsonMomentBean.getResults();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (count_moment == 0) {
                    return;
                } else {
                    //该页的数据量
                    int count;
                    if(page <= count_moment / 10){
                        count = 10;
                    }
                    else {
                        count = count_moment % 10;
                    }

                    for (int i = 0; i <= count-1; i++) {
                        String header = results.get(i).getAccount_header();
                        String name = results.get(i).getAccount_name();
                        String content = results.get(i).getMoment_content();
                        String url = results.get(i).getMoment_url();
                        boolean is_public = results.get(i).getMomentIsPublic();
                        if(is_public){
                            Map<String, Object> map = new HashMap<>();
                            map.put("header", header);
                            map.put("name", name);
                            map.put("content", content);
                            map.put("url", url);
                            dataList.add(map);
                        }
                    }
                }

                //首次打开或刷新时适配adapter
                if(page == 1){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commonAdapter = new CommonAdapter<Map<String, Object>>(
                                    getActivity(),R.layout.moment_item,
                                    dataList
                            ) {
                                @Override
                                protected void convert(ViewHolder viewHolder, Map<String, Object> stringObjectMap, int i) {
                                    String header_url = "";
                                    try {
                                        header_url = stringObjectMap.get("header").toString();
                                    }catch (Exception e){
                                        //若用户没有设置头像，头像url置为default header
                                        header_url = "https://ws1.sinaimg.cn/large/006bShEGly1forsphz7zaj3069069wea.jpg";
                                    }
                                    String name = stringObjectMap.get("name").toString();
                                    String content = stringObjectMap.get("content").toString();
                                    String image_url = stringObjectMap.get("url").toString();
                                    ImageView header = viewHolder.getView(R.id.moment_header);
                                    Uri header_uri = Uri.parse(header_url);
                                    Picasso.with(getActivity()).load(header_uri).placeholder(R.mipmap.default_header).fit().centerCrop().into(header);
                                    viewHolder.setText(R.id.moment_name,name);
                                    viewHolder.setText(R.id.moment_content,content);
                                    ImageView image = viewHolder.getView(R.id.moment_image);
                                    Uri image_uri = Uri.parse(image_url);
                                    Picasso.with(getActivity()).load(image_uri).placeholder(R.mipmap.default_moment)
                                            .fit().centerCrop().into(image);
                                }
                            };
                            listView.setAdapter(commonAdapter);
                        }
                    });
                }
                else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commonAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
