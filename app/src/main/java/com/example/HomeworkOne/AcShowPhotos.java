package com.example.HomeworkOne;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bm.library.PhotoView;
import com.squareup.picasso.Picasso;

import MyInterface.InitView;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Author: kafca
 * Date: 2018/4/17
 * Description: 展示列表图片大图
 */
public class AcShowPhotos extends Activity implements InitView{
    @Bind(R.id.pv)
    PhotoView photoView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_show_photos);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        ButterKnife.bind(AcShowPhotos.this);
        Intent intent = getIntent();
        String image_url = intent.getStringExtra("image_url");
        Picasso.with(AcShowPhotos.this).load(Uri.parse(image_url)).fit().centerInside().into(photoView);
    }

    @Override
    public void initListener() {
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcShowPhotos.this.finish();
            }
        });
    }
}
