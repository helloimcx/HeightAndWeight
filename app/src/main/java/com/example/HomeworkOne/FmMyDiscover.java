package com.example.HomeworkOne;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.like.LikeButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.HomeworkOne.globalConfig.MyApplication;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import MyInterface.InitView;
import Utils.JsonLikeBean;
import Utils.JsonMomentBean;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import butterknife.Bind;
import butterknife.ButterKnife;

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
    RecyclerView recyclerView;
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
    }

    @Override
    public void initListener() {
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

    private void getData(final int page) {
        final OkHttpClient okHttpClient = MainActivity.okHttpClient;
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        final String host = myApplication.getHost();
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
            public void onResponse(final Call call, final Response response) throws IOException {
                final Gson gson = new Gson();
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
                        int moment_id = results.get(i).getMoment_id();
                        String header = results.get(i).getAccount_header();
                        String name = results.get(i).getAccount_name();
                        String content = results.get(i).getMoment_content();
                        String url = results.get(i).getMoment_url();
                        boolean is_public = results.get(i).getMomentIsPublic();
                        boolean has_liked = results.get(i).get_has_liked();
                        int likes_count = results.get(i).getLikes_count();
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(moment_id+"", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("has_liked", has_liked);
                        editor.putInt("likes_count", likes_count);
                        editor.apply();
                        Map<String, Object> map = new HashMap<>();
                        map.put("moment_id", moment_id);
                        map.put("header", header);
                        map.put("name", name);
                        map.put("content", content);
                        map.put("url", url);
                        dataList.add(map);
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
                                protected void convert(final ViewHolder viewHolder, Map<String, Object> stringObjectMap, int i) {
                                    String header_url = "";
                                    try {
                                        header_url = stringObjectMap.get("header").toString();
                                    }catch (Exception e){
                                        //若用户没有设置头像，头像url置为default header
                                        header_url = "https://ws1.sinaimg.cn/large/006bShEGly1forsphz7zaj3069069wea.jpg";
                                    }
                                    final String name = stringObjectMap.get("name").toString();
                                    String content;
                                    try{
                                        content = stringObjectMap.get("content").toString();
                                    }catch (Exception e){
                                        content = "";
                                    }
                                    final String image_url = stringObjectMap.get("url").toString();
                                    ImageView header = viewHolder.getView(R.id.moment_header);
                                    Uri header_uri = Uri.parse(header_url);
                                    Picasso.with(getActivity()).load(header_uri).placeholder(R.mipmap.default_header).fit().centerCrop().into(header);
                                    viewHolder.setText(R.id.moment_name,name);
                                    viewHolder.setText(R.id.moment_content,content);
                                    ImageView image = viewHolder.getView(R.id.moment_image);
                                    Uri image_uri = Uri.parse(image_url);
                                    Picasso.with(getActivity()).load(image_uri).placeholder(R.mipmap.default_moment)
                                            .fit().centerCrop().into(image);
                                    //图片点击放大
                                    image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), AcShowPhotos.class);
                                            intent.putExtra("image_url", image_url);
                                            startActivity(intent);
                                        }
                                    });

                                    final int moment_id = (int) stringObjectMap.get("moment_id");
                                    final LikeButton likeButton = viewHolder.getView(R.id.star_button);
                                    final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(moment_id+"",Context.MODE_PRIVATE);
                                    final boolean has_liked = sharedPreferences.getBoolean("has_liked", false);
                                    int likes_count = sharedPreferences.getInt("likes_count", 0);
                                    if(has_liked)
                                        likeButton.setLiked(true);
                                    else
                                        likeButton.setLiked(false);
                                    viewHolder.setText(R.id.likes_count, likes_count+"");

                                    // 点赞和取消点赞
                                    likeButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            final Request request_like = new Request.Builder()
                                                    .url(host+"/moment/like/"+moment_id+"/")
                                                    .addHeader("cookie", MainActivity.sessionid)
                                                    .build();
                                            Call call_like = okHttpClient.newCall(request_like);
                                            call_like.enqueue(new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toasty.warning(getActivity(),"网络异常").show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onResponse(Call call, final Response response) throws IOException {
                                                    if (response.code()!=200 && response.code()!=202)
                                                        return;
                                                    JsonLikeBean jsonLikeBean = gson.fromJson(response.body().string(), JsonLikeBean.class);
                                                    final int likes_count = jsonLikeBean.getCount();
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            viewHolder.setText(R.id.likes_count, likes_count+"");
                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            if(response.code()==200){
                                                                likeButton.setLiked(true);
                                                                editor.putBoolean("has_liked", true);
                                                            }
                                                            else{
                                                                likeButton.setLiked(false);
                                                                editor.putBoolean("has_liked", false);
                                                            }
                                                            editor.putInt("likes_count", likes_count);
                                                            editor.apply();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            };
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(commonAdapter);
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
