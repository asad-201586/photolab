package com.tools.photolab.effect.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.tools.photolab.R;
import com.tools.photolab.effect.adapter.CreatePhotosAdapter;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.callBack.PhotoPixClickListener;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyCreatePhotosActivity extends BaseActivity {

    RecyclerView mRecyclerMyPhotos;
    ArrayList<File> currFileList;
    CreatePhotosAdapter myPhotosAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photos);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(MyCreatePhotosActivity.this));
        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);

        findViewById(R.id.relDataNotFound).setVisibility(View.GONE);

        (findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mRecyclerMyPhotos = (RecyclerView) findViewById(R.id.recyclerMyPhotos);
        mRecyclerMyPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        currFileList = listFiles();
        setAdapter();
    }


    private void refreshView() {
        if (currFileList.size() > 0) {
            mRecyclerMyPhotos.setVisibility(View.VISIBLE);
            findViewById(R.id.relDataNotFound).setVisibility(View.GONE);
        } else {
            mRecyclerMyPhotos.setVisibility(View.GONE);
            findViewById(R.id.relDataNotFound).setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter() {
        myPhotosAdapter = new CreatePhotosAdapter(MyCreatePhotosActivity.this, currFileList, new PhotoPixClickListener() {
            @Override
            public void onPhotoClick(final String filePath) {


                FullScreenAdManager.fullScreenAdsCheckPref(MyCreatePhotosActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_PHOTO_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        Intent intent = new Intent(MyCreatePhotosActivity.this, CreatePhotoViewActivity.class);
                        intent.putExtra(Constants.KEY_URI_IMAGE, filePath.toString());
                        // startActivity(intent);
                        startActivityForResult(intent, 1);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }
        });
        mRecyclerMyPhotos.setAdapter(myPhotosAdapter);
        refreshView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                currFileList = listFiles();
                setAdapter();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


    @Override
    protected void onResume() {
        ArrayList<File> fileList = listFiles();
        if (currFileList.size() != fileList.size()) {
            currFileList = fileList;
            resetAdapter();
        }
        super.onResume();
    }

    private void resetAdapter() {
        myPhotosAdapter.notifyDataSetChanged();
        refreshView();
    }

    public ArrayList<File> listFiles() {
        ArrayList<File> fileList = new ArrayList<>();
        File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
        File[] files = myDir.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
            }
        }
        // sort with ascending order
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                long k = file2.lastModified() - file1.lastModified();
                if (k > 0) {
                    return 1;
                } else if (k == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(myDir)));
        return fileList;
    }


    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
