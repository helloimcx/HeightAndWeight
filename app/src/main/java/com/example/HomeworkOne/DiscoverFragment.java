package com.example.HomeworkOne;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.gson.Gson;
import com.lqr.optionitemview.OptionItemView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import MyInterface.InitView;
import Utils.JsonMomentBean;
import Utils.JsonRecordBean;
import Utils.PopupWindowUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.PRINT_SERVICE;

/**
 * Created by mac on 2017/11/18.
 */

public class DiscoverFragment extends Fragment implements InitView {
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList;
    private int count_moment;
    private ArrayList<JsonMomentBean.MomentBean> results;
    private PopupWindow mPopupWindow;
    private View menu;
    private ImageButton camera;
    private OptionItemView item_camera;
    private OptionItemView item_cancel;
    @Bind(R.id.moments_list)
    ListView listView;

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
        getData();
        LayoutInflater factory = LayoutInflater.from(getActivity());
        menu = factory.inflate(R.layout.popup_moment, null);
        camera = (ImageButton) getActivity().findViewById(R.id.moment_camera);
        camera.setVisibility(View.VISIBLE);
        item_camera = (OptionItemView) menu.findViewById(R.id.moment_public);
        item_cancel = (OptionItemView) menu.findViewById(R.id.moment_cancel);
        //createFloatMenu();
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

    private void getData() {
        OkHttpClient okHttpClient = MainActivity.okHttpClient;
        final Request request = new Request.Builder().url("http://120.78.67.135:8000/moment/")
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
                    for (int i = count_moment - 1; i >= 0; i--) {
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
//                    simpleAdapter = new SimpleAdapter(getActivity(), dataList, R.layout.moment_item,
//                            new String[]{"header", "name", "content", "url"}, new int[]{R.id.moment_header,
//                            R.id.moment_name,
//                            R.id.moment_content,
//                            R.id.moment_image
//                    });
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(new CommonAdapter<Map<String, Object>>(getActivity(),R.layout.moment_item,
                                dataList) {

                            @Override
                            protected void convert(ViewHolder viewHolder, Map<String, Object> stringObjectMap, int i) {
                                String header_url = stringObjectMap.get("header").toString();
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
                        });
                    }
                });
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
}
