package com.example.HomeworkOne;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.view.CropImageView;
import com.lqr.optionitemview.OptionItemView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
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
import Utils.PicassoImageLoader;
import Utils.PopupWindowUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mac on 2017/11/18.
 */

public class DiscoverFragment extends Fragment implements InitView {
    private List<Map<String, Object>> dataList;
    private int count_moment;
    private MyBroadCastReceiver myBroadCastReceiver;

    //请求的数据所在的页数
    private int request_page = 1;

    private ArrayList<JsonMomentBean.MomentBean> results;
    private PopupWindow mPopupWindow;
    private View menu;
    private ImageButton camera;
    private OptionItemView item_camera;
    private OptionItemView item_cancel;
    private CommonAdapter<Map<String, Object>> commonAdapter;
    public static final int REQUEST_IMAGE_PICKER = 1001;
    public static final int REQUEST_DISCOVERY_PUBLIC = 1002;
    private ImagePicker imagePicker;
    @Bind(R.id.moments_header)
    ImageView moment_header;
    @Bind(R.id.moments_list)
    ListView listView;
    @Bind(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dicover, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this,view);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        dataList = new ArrayList<Map<String, Object>>();

        //显示用户头像
        SharedPreferences share = getActivity().getSharedPreferences("Session", MODE_PRIVATE);
        String user_header_str = share.getString("header","null");
        Uri header_uri = Uri.parse(user_header_str);
        Picasso.with(getActivity()).load(header_uri).fit().centerCrop().into(moment_header);

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

        LayoutInflater factory = LayoutInflater.from(getActivity());
        menu = factory.inflate(R.layout.popup_moment, null);
        camera = (ImageButton) getActivity().findViewById(R.id.moment_camera);
        camera.setVisibility(View.VISIBLE);
        item_camera = (OptionItemView) menu.findViewById(R.id.moment_public);
        item_cancel = (OptionItemView) menu.findViewById(R.id.moment_cancel);
        initImagePicker();

        //注册BroadcastReceiver
        registerBroadcastReceiver();
    }

    @Override
    public void initListener() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu();
            }
        });
        item_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent,REQUEST_IMAGE_PICKER );
                mPopupWindow.dismiss();
            }
        });
        item_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }

    private void getData(final int page) {
        OkHttpClient okHttpClient = MainActivity.okHttpClient;
        final Request request = new Request.Builder().url("http://120.78.67.135:8000/moment/?page="+page)
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
                    Log.e("why", response.body().string());

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
                        Map<String, Object> map = new HashMap<String, Object>();
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
                                protected void convert(ViewHolder viewHolder, Map<String, Object> stringObjectMap, int i) {
                                    String header_url = "";
                                    try {
                                        header_url = stringObjectMap.get("header").toString();
                                    }catch (Exception e){
                                        //若用户没有设置头像，头像url置为"null"
                                        header_url = "null";
                                    }
                                    String name = stringObjectMap.get("name").toString();
                                    String content = stringObjectMap.get("content").toString();
                                    String image_url = stringObjectMap.get("url").toString();
                                    ImageView header = viewHolder.getView(R.id.moment_header);
                                    Uri header_uri = Uri.parse(header_url);
                                    Picasso.with(getActivity()).load(header_uri).fit().centerCrop().into(header);
                                    viewHolder.setText(R.id.moment_name,name);
                                    viewHolder.setText(R.id.moment_content,content);
                                    ImageView image = viewHolder.getView(R.id.moment_image);
                                    Uri image_uri = Uri.parse(image_url);
                                    Picasso.with(getActivity()).load(image_uri).fit().centerCrop().into(image);
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

    private void createFloatMenu(){
        ImageView icon = new ImageView(getActivity()); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.one));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());

        ImageView itemIcon = new ImageView(getActivity());
        itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.one));
        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();


        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(button1)
                // ...
                .attachTo(actionButton)
                .build();
    }

    private void showPopupMenu() {
        mPopupWindow = PopupWindowUtils.getPopupWindowAtLocation(
                menu, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getActivity().getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowUtils.makeWindowLight(getActivity());
            }
        });
        PopupWindowUtils.makeWindowDark(getActivity());
    }

    private void initImagePicker(){
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<com.lqr.imagepicker.bean.ImageItem> images = (ArrayList<com.lqr.imagepicker.bean.ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (images != null && images.size() > 0) {
                            com.lqr.imagepicker.bean.ImageItem imageItem = images.get(0);
                            Intent intent = new Intent(getActivity(),DiscoveryPublic.class);
                            intent.putExtra("path", imageItem.path);
                            intent.putExtra("name", imageItem.name);
                            startActivityForResult(intent, REQUEST_DISCOVERY_PUBLIC);
                        }
                    }
                }
            case REQUEST_DISCOVERY_PUBLIC:
                //刷新moment列表
                refreshLayout.startRefresh();
        }
    }

    //BroadcastReceiver
    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String header_url = intent.getStringExtra("header_url");
            Uri header_uri = Uri.parse(header_url);
            Picasso.with(getActivity()).load(header_uri).fit().centerCrop().into(moment_header);
        }
    }

    private void registerBroadcastReceiver(){
        myBroadCastReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UserHeader.BROADCAST_ACTION);
        getActivity().registerReceiver(myBroadCastReceiver, intentFilter);
    }

    //重写onDestory（）撤销BroadcastReceiver的注册
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadCastReceiver);
    }
}
