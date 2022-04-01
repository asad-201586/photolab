package com.tools.photolab.effect.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.tools.photolab.R;
import com.tools.photolab.effect.custom.stickerView.DrawableSticker;
import com.tools.photolab.effect.custom.stickerView.Sticker;
import com.tools.photolab.effect.custom.stickerView.StickerView;
import com.tools.photolab.effect.adapter.FiltersForegroundAdapter;
import com.tools.photolab.effect.adapter.StickerCategoryListAdapter;
import com.tools.photolab.effect.adapter.StyleAdapter;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.custom.CustomTextView;
import com.tools.photolab.effect.custom.MultiTouchListener;

import com.tools.photolab.effect.callBack.FilterPixItemClickListener;
import com.tools.photolab.effect.callBack.PIXStyleClickListener;
import com.tools.photolab.effect.callBack.StickerClickListener;
import com.tools.photolab.effect.model.PathModelPix;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PixLabActivity extends BaseActivity implements View.OnClickListener {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    public ArrayList<PathModelPix> arrIcon, arrMask;
    public Animation slideUpAnimation, slideDownAnimation;
    private RecyclerView mRecyclerPIXStyle, mRecyclerForegroundFilter, recyclerStickerCategory, recyclerSticker;
    private String oldSavedFileName;
    private Uri savedImageUri;
    private static Bitmap bmpPic = null;
    private Bitmap  filteredForegroundBitmap2, filteredForegroundBitmap3, filteredForegroundBitmap4;
    private int rotateImage = 0, pos = 0,  lastSelectedPosTab = 0, displayWidth;
    private FiltersForegroundAdapter filtersForegroundAdapter;
    private ImageView mMovImage, mMainFrame;
    private StickerView stickerView;
    private LinearLayout linThirdDivisionOption;
    private Sticker currentSticker;
    private RelativeLayout rel_pix;

    private TabLayout tabLayout;

    static public void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"}, null);
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        bmpPic = bitmap;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pix_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(PixLabActivity.this));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;

        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);
        arrIcon = new ArrayList<>();
        arrMask = new ArrayList<>();
        mRecyclerPIXStyle = (RecyclerView) findViewById(R.id.recyclerPIXStyle);
        mRecyclerForegroundFilter = (RecyclerView) findViewById(R.id.recyclerForegroundFilter);
        recyclerStickerCategory = (RecyclerView) findViewById(R.id.recyclerStickerCategory);
        recyclerStickerCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        mMovImage = (ImageView) findViewById(R.id.iv_mov);
        mMainFrame = (ImageView) findViewById(R.id.main_frame);
        recyclerSticker = (RecyclerView) findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        mMovImage.setOnTouchListener(new MultiTouchListener());
        linThirdDivisionOption = (LinearLayout) findViewById(R.id.linThirdDivisionOption);
        rel_pix = (RelativeLayout) findViewById(R.id.rel_pix);
        linThirdDivisionOption.setVisibility(View.GONE);
        AppCompatImageView ivClose = (AppCompatImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        ImageView mIvSave = (ImageView) findViewById(R.id.iv_save);
        AppCompatImageView ivCheckMark = (AppCompatImageView) findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(this);
        //ceate all option
        createTabIcons();
        tabLayout.getTabAt(0);
        initMainStickerViewMan();
        pos = getIntent().getIntExtra(Constants.KEY_SELECTED_PIX_STYLE, 0);
        if (getIntent() != null && getIntent().hasExtra(Constants.KEY_FROM_MAIN)) {
            if (getIntent().getStringExtra(Constants.KEY_FROM_MAIN).equalsIgnoreCase(getString(R.string.txt_gallery))) {
                arrIcon = getIconAllFrames();
                arrMask = getMaskAll();
            }
        }
        int Measuredwidth = 0;
        int Measuredheight = 0;
        Point size = new Point();
        WindowManager w = getWindowManager();

        w.getDefaultDisplay().getSize(size);
        Measuredwidth = size.x;
        Measuredheight = size.y;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Measuredwidth, Measuredwidth);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        stickerView.setLayoutParams(layoutParams);
        rel_pix.setLayoutParams(layoutParams);



        if (bmpPic != null) {
            filteredForegroundBitmap2 = bmpPic.copy(Bitmap.Config.ARGB_8888, true);
            filteredForegroundBitmap3 = bmpPic.copy(Bitmap.Config.ARGB_8888, true);
            filteredForegroundBitmap4 = bmpPic.copy(Bitmap.Config.ARGB_8888, true);


            mRecyclerPIXStyle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerForegroundFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bmpPic, 128, 128);
            prepareThumbnailBackground(thumbBitmap);

            StyleAdapter pixStyleAdapter = new StyleAdapter(PixLabActivity.this, arrIcon, new PIXStyleClickListener() {
                @Override
                public void onFilterClicked(int position) {

                    if (pos != position) {
                        pos = position;
                        makePixLab(arrMask.get(pos));
                    }
                }
            });
            mRecyclerPIXStyle.setAdapter(pixStyleAdapter);
            pixStyleAdapter.setSelectedPos(pos);
            mRecyclerPIXStyle.scrollToPosition(pos);

            mIvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImage();
                }
            });


            (findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            //sticker list create
            setStickerImages(30);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        lastSelectedPosTab = 0;
                        mRecyclerPIXStyle.setVisibility(View.VISIBLE);
                        mRecyclerForegroundFilter.setVisibility(View.GONE);
                        recyclerStickerCategory.setVisibility(View.GONE);
                    } else if (tab.getPosition() == 1) {
                        lastSelectedPosTab = 1;

                        mRecyclerPIXStyle.setVisibility(View.GONE);
                        mRecyclerForegroundFilter.setVisibility(View.VISIBLE);
                        recyclerStickerCategory.setVisibility(View.GONE);
                    } else if (tab.getPosition() == 2) {
                        lastSelectedPosTab = 2;

                        mRecyclerPIXStyle.setVisibility(View.GONE);
                        mRecyclerForegroundFilter.setVisibility(View.GONE);
                        recyclerStickerCategory.setVisibility(View.VISIBLE);
                    } else if (tab.getPosition() == 3) {
                       /* mRecyclerPIXStyle.setVisibility(View.GONE);
                        mRecyclerForegroundFilter.setVisibility(View.GONE);
                        recyclerStickerCategory.setVisibility(View.GONE);*/
                        ColorPicker();

                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            mRecyclerPIXStyle.getLayoutManager().scrollToPosition(pos);
            makePixLab(arrMask.get(pos));


            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(filteredForegroundBitmap3, displayWidth, displayWidth, true);
            mMovImage.setImageBitmap(createScaledBitmap);


        } else {
            finish();
        }

    }

    public void setStickerImages(int size) {
        final ArrayList<Integer> stickerArrayList = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Resources resources = getResources();
            stickerArrayList.add(Integer.valueOf(resources.getIdentifier("sticker_n" + i, "drawable", getPackageName())));
        }
        recyclerStickerCategory = findViewById(R.id.recyclerStickerCategory);
        recyclerStickerCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerStickerCategory.setAdapter(new StickerCategoryListAdapter(this, stickerArrayList, new StickerClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Drawable drawable = getResources().getDrawable(stickerArrayList.get(position));
                stickerView.addSticker(new DrawableSticker(drawable));
            }
        }));

    }


    public void ColorPicker() {
        tabLayout.getTabAt(lastSelectedPosTab).select();
        ColorPickerDialogBuilder
                .with(PixLabActivity.this)
                .setTitle("Choose color")
                .initialColor(-726683729)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //  toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                        //  Toast.makeText(PixMakingActivity.this, ""+Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(getString(R.string.txt_ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        mMainFrame.setColorFilter(selectedColor);
                    }
                })
                .setNegativeButton(getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    public void makePixLab(PathModelPix pathModel) {
        mMainFrame.setImageResource(pathModel.getPathInt());
        mMainFrame.setScaleX(1.1f);
        mMainFrame.setScaleY(1.1f);
    }

    private void initMainStickerViewMan() {
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                removeStickerWithDeleteIcon();
                Log.e("TAG", "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull final Sticker sticker) {
                stickerOptionTaskPerformMan(sticker);
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.e("TAG", "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.e("TAG", "onDoubleTapped: double tap will be with two click");
            }
        });
    }

    public void stickerOptionTaskPerformMan(Sticker sticker) {
        stickerView.setLocked(false);
        currentSticker = sticker;
        stickerView.sendToLayer(stickerView.getStickerPosition(currentSticker));
        Log.e("TAG", "onStickerTouchedDown");
    }

    private void removeStickerWithDeleteIcon() {
        stickerView.remove(currentSticker);
        currentSticker = null;
        if (stickerView.getStickerCount() == 0) {

        } else {
            currentSticker = stickerView.getLastSticker();
        }
    }

    private void createTabIcons() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textOne = (CustomTextView) view.findViewById(R.id.text);
        ImageView ImageOne = (ImageView) view.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_frame));
        ImageOne.setImageResource(R.drawable.ic_module);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_filter));
        ImageTwo.setImageResource(R.drawable.ic_foreground);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView text3 = (CustomTextView) view3.findViewById(R.id.text);
        ImageView Image3 = (ImageView) view3.findViewById(R.id.image);
        text3.setText(getString(R.string.txt_stickers));
        Image3.setImageResource(R.drawable.ic_stickers);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_pix_tab, null);
        CustomTextView text4 = (CustomTextView) view4.findViewById(R.id.text);
        ImageView Image4 = (ImageView) view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_frame_color));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));
    }

    public void prepareThumbnailBackground(final Bitmap thumbBitmaps) {
        Runnable r = new Runnable() {
            public void run() {

                ThumbnailsManager.clearThumbs();
                final List<ThumbnailItem> thumbnailItemList = new ArrayList<>();

                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbBitmaps;
                thumbnailItem.filterName = getString(R.string.filter_normal);
                ThumbnailsManager.addThumb(thumbnailItem);
                thumbnailItemList.add(thumbnailItem);


                List<Filter> filters = FilterPack.getFilterPack(PixLabActivity.this);
                for (int i = 0; i < filters.size() - 3; i++) {
                    Filter filter = filters.get(i);
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbBitmaps;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(PixLabActivity.this));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        filtersForegroundAdapter = new FiltersForegroundAdapter(thumbnailItemList, rotateImage, new FilterPixItemClickListener() {
                            @Override
                            public void onFilterClicked(Filter filter) {
                                filteredForegroundBitmap2 = filteredForegroundBitmap4.copy(Bitmap.Config.ARGB_8888, true);
                                filteredForegroundBitmap3 = filter.processFilter(filteredForegroundBitmap2);
                                //  mMovImage.setImageBitmap(filteredForegroundBitmap3);
                                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(filteredForegroundBitmap3, displayWidth, displayWidth, true);
                                mMovImage.setImageBitmap(createScaledBitmap);
                            }
                        });
                        mRecyclerForegroundFilter.setAdapter(filtersForegroundAdapter);
                    }
                });
            }
        };

        new Thread(r).start();
    }


    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        if (upAnimation) {
            linLayout.setVisibility(View.VISIBLE);
            slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            linLayout.startAnimation(slideUpAnimation);
            recyclerView.scrollToPosition(0);
        } else {
            slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            linLayout.startAnimation(slideDownAnimation);
            slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    linLayout.setVisibility(View.GONE);
                    // recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    protected void onDestroy() {
        mMainFrame.setImageBitmap(null);
        mMovImage.setImageBitmap(null);
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivCheckMark:
                if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                    if (currentSticker == null)
                        currentSticker = stickerView.getCurrentSticker();

                    if (recyclerSticker.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    }
                }
                break;

            case R.id.ivClose:
                //onBackPressed();
                if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
                    if (currentSticker == null)
                        currentSticker = stickerView.getCurrentSticker();

                    if (recyclerSticker.getVisibility() == View.VISIBLE) {
                        itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                    }
                }
                break;

        }
    }

    private void saveImage() {

        new saveImageTaskMaking().execute();
    }

    public Uri addImageToGallery(final String filePath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void openShareActivity() {
        Intent intent = new Intent(PixLabActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private class saveImageTaskMaking extends AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stickerView.setLocked(true);
        }

        @Override
        protected Exception doInBackground(String... strings) {
            Bitmap bitmap = Bitmap.createBitmap(rel_pix.getWidth(), rel_pix.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rel_pix.draw(canvas);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+ getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(PixLabActivity.this, uri.getPath());

                } else {
                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File file = new File(myDir, fileName);
                    if (oldSavedFileName != null) {
                        File oldFile = new File(myDir, oldSavedFileName);
                        if (oldFile.exists()) oldFile.delete();
                    }
                    oldSavedFileName = fileName;

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Uri uri = addImageToGallery(file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(PixLabActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
                return e;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {

                FullScreenAdManager.fullScreenAdsCheckPref(PixLabActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        openShareActivity();
                    }
                });


            } else {
                Toast.makeText(PixLabActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
