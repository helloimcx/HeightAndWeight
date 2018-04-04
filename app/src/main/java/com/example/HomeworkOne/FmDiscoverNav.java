package com.example.HomeworkOne;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.view.CropImageView;
import com.lqr.optionitemview.OptionItemView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import MyInterface.InitView;
import Utils.PicassoImageLoader;
import Utils.PopupWindowUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

/**
 * Author: kafca
 * Date: 2017/11/18
 * Description: Discover Fragment
 */

public class FmDiscoverNav extends Fragment implements InitView {
    private PopupWindow mPopupWindow;
    private View menu;
    private ImageButton camera;
    private OptionItemView item_camera;
    private OptionItemView item_cancel;
    private FragmentPagerItemAdapter adapter;
    public static final int REQUEST_IMAGE_PICKER = 1001;
    public static final int REQUEST_DISCOVERY_PUBLIC = 1002;

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
        LayoutInflater factory = LayoutInflater.from(getActivity());
        menu = factory.inflate(R.layout.popup_moment, null);
        camera = getActivity().findViewById(R.id.moment_camera);
        camera.setVisibility(View.VISIBLE);
        item_camera = menu.findViewById(R.id.moment_public);
        item_cancel = menu.findViewById(R.id.moment_cancel);
        initImagePicker();

        adapter = new FragmentPagerItemAdapter(
                getActivity().getSupportFragmentManager(), FragmentPagerItems.with(getActivity())
                .add("世界", FmDiscover.class)
                .add("我的", FmMyDiscover.class)
                .create());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
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

    private void initImagePicker(){
        WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new PicassoImageLoader());   //设置图片加载器
        imagePicker.setMultiMode(false);
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(width+100);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(height);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(width+100);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(height);//保存文件的高度。单位像素
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    if (data != null) {
                        ArrayList<ImageItem> images = (ArrayList<com.lqr.imagepicker.bean.ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
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
                int position = FragmentPagerItem.getPosition(getArguments());
                Fragment page = adapter.getPage(position);
                TwinklingRefreshLayout refreshLayout = page.getActivity().findViewById(R.id.refreshLayout);
                refreshLayout.startRefresh();
        }
    }

}
