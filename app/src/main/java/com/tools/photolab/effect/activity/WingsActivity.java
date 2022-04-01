package com.tools.photolab.effect.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.tools.photolab.R;
import com.tools.photolab.effect.custom.stickerView.DrawableSticker;
import com.tools.photolab.effect.custom.stickerView.Sticker;
import com.tools.photolab.effect.custom.stickerView.StickerView;
import com.tools.photolab.effect.custom.DHANVINE_MultiTouchListener;
import com.tools.photolab.effect.callBack.MenuItemClickLister;
import com.tools.photolab.effect.adapter.NeonEffectListAdapter;
import com.tools.photolab.effect.adapter.StickerAdapter;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.custom.CustomTextView;

import com.tools.photolab.effect.erase_tool.StickerEraseActivity;
import com.tools.photolab.effect.callBack.StickerClickListener;
import com.tools.photolab.effect.crop_img.newCrop.MLCropAsyncTask;
import com.tools.photolab.effect.crop_img.newCrop.MLOnCropTaskCompleted;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;
import com.tools.photolab.effect.support.FastBlur;
import com.tools.photolab.effect.support.ImageUtils;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.tools.photolab.effect.activity.PixLabActivity.notifyMediaScannerService;


public class WingsActivity extends BaseActivity implements MenuItemClickLister, OnClickListener {

    public static ImageView setfront;
    public static Bitmap eraserResultBmp;
    private static Bitmap faceBitmap;
    public int mCount = 0;
    boolean isFirstTime = true;
    SeekBar sbBackgroundOpacity;
    private Bitmap selectedBit, cutBit;
    private Context mContext;
    private Animation slideUpAnimation, slideDownAnimation;
    private NeonEffectListAdapter neonEffectListAdapter;
    private int wing = 37;
    private RecyclerView recyclerNeonEffect, recyclerSticker;
    private ArrayList<String> wingsEffect = new ArrayList<String>();
    private ImageView iv_face, setback, setimg;
    private RelativeLayout mContentRootView;
    private Uri savedImageUri;
    private StickerView stickerView;
    private Sticker currentSticker;
    private LinearLayout linThirdDivisionOption, linEffect, linBackgroundBlur;
    private TabLayout tabLayout;
    private String oldSavedFileName;


    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wings_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(WingsActivity.this));
        mContext = this;
        selectedBit = faceBitmap;

        RelativeLayout mAdView = findViewById(R.id.adView);
        loadBannerAds(mAdView);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setimg.post(new Runnable() {
                    public void run() {
                        if (isFirstTime && selectedBit != null) {
                            isFirstTime = false;
                            //create bitmap for image
                            initBMPNew();
                        }
                    }
                });
            }
        }, 1000);

        //wings thum list created
        wingsEffect.add("none");
        for (int i = 1; i <= wing; i++) {
            wingsEffect.add("w_" + i);
        }
        Init();
        setTollbarData();
    }

    private void initBMPNew() {
        if (faceBitmap != null) {
            selectedBit = ImageUtils.getBitmapResize(mContext, faceBitmap, setimg.getWidth(), setimg.getHeight());
            mContentRootView.setLayoutParams(new LinearLayout.LayoutParams(selectedBit.getWidth(), selectedBit.getHeight()));
            if (selectedBit != null && iv_face != null) {
                iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, sbBackgroundOpacity.getProgress() == 0 ? 1 : sbBackgroundOpacity.getProgress()));
            }
            cutmaskNew();
        }
    }

    private void Init() {
        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        mContentRootView = findViewById(R.id.mContentRootView);
        setfront = findViewById(R.id.setfront);
        setback = findViewById(R.id.setback);
        iv_face = findViewById(R.id.iv_face);
        setimg = findViewById(R.id.setimg);

        iv_face.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickerView.getCurrentSticker() != null) {
                    stickerView.getCurrentSticker().release();
                }
            }
        });
        linEffect = (LinearLayout) findViewById(R.id.linEffect);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        createTabIcons();
        tabLayout.getTabAt(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onBottomTabSelected(tab);
            }
        });

        recyclerNeonEffect = (RecyclerView) findViewById(R.id.recyclerNeonEffect);
        recyclerNeonEffect.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        linBackgroundBlur = findViewById(R.id.linBackgroundBlur);
        setUpBottomList();

        AppCompatImageView ivCheckMark = (AppCompatImageView) findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(this);
        AppCompatImageView ivClose = (AppCompatImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
        recyclerSticker = (RecyclerView) findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        linThirdDivisionOption = (LinearLayout) findViewById(R.id.linThirdDivisionOption);
        initMainStickerViewMan();
        //sticker list create
        setStickerImages(30);

        tabLayout.setVisibility(View.VISIBLE);
        linEffect.setVisibility(View.GONE);
        linBackgroundBlur.setVisibility(View.GONE);

        findViewById(R.id.ivShowHomeOption).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });


        neonEffectListAdapter.addData(wingsEffect);

        iv_face.setRotationY(0.0f);

        setimg.post(new Runnable() {
            public void run() {
                initBMPNew();
            }
        });

        SeekBar sbFrameOpacity = findViewById(R.id.sbFrameOpacity);
        sbFrameOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (setback != null && setfront != null) {
                    float f = ((float) i) * 0.01f;
                    setback.setAlpha(f);
                    setfront.setAlpha(f);
                }
            }
        });
        sbBackgroundOpacity = findViewById(R.id.sbBackgroundOpacity);
        sbBackgroundOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (selectedBit != null && iv_face != null) {
                    iv_face.setImageBitmap(new FastBlur().processBlur(selectedBit, 1, seekBar.getProgress() == 0 ? 1 : seekBar.getProgress()));
                }
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }
        });
    }

    public void setStickerImages(int size) {
        final ArrayList<Integer> stickerArrayList = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            Resources resources = getResources();
            stickerArrayList.add(Integer.valueOf(resources.getIdentifier("sticker_n" + i, "drawable", getPackageName())));
        }
        recyclerSticker = findViewById(R.id.recyclerSticker);
        recyclerSticker.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerSticker.setAdapter(new StickerAdapter(this, stickerArrayList, new StickerClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                itemSelectFromList(linThirdDivisionOption, recyclerSticker, false);
                Drawable drawable = getResources().getDrawable(stickerArrayList.get(position));
                stickerView.addSticker(new DrawableSticker(drawable));
            }
        }));
    }

    private void onBottomTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(linEffect, tabLayout);

        } else if (tab.getPosition() == 1) {
            itemSelectFromList(linThirdDivisionOption, recyclerSticker, true);

        } else if (tab.getPosition() == 2) {
            StickerEraseActivity.b = cutBit;
            Intent intent = new Intent(this, StickerEraseActivity.class);
            intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_WINGS);
            startActivityForResult(intent, 1024);

        } else if (tab.getPosition() == 3) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(linBackgroundBlur, tabLayout);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBit = eraserResultBmp;
                setimg.setImageBitmap(cutBit);
            }
        }
    }

    private void createTabIcons() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textOne = (CustomTextView) view.findViewById(R.id.text);
        ImageView ImageOne = (ImageView) view.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_effect));
        ImageOne.setImageResource(R.drawable.ic_neon_effect_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view));

        View view3 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text3 = (CustomTextView) view3.findViewById(R.id.text);
        ImageView Image3 = (ImageView) view3.findViewById(R.id.image);
        text3.setText(getString(R.string.txt_stickers));
        Image3.setImageResource(R.drawable.ic_stickers);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_erase));
        ImageTwo.setImageResource(R.drawable.ic_erase);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text4 = (CustomTextView) view4.findViewById(R.id.text);
        ImageView Image4 = (ImageView) view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_background));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));
    }

    public void itemSelectFromList(final LinearLayout linLayout, final RecyclerView recyclerView, boolean upAnimation) {
        //recyclerView.setVisibility(View.VISIBLE);
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

    public void viewSlideUpDown(final View showLayout, final View hideLayout) {
        showLayout.setVisibility(View.VISIBLE);
        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        showLayout.startAnimation(slideUpAnimation);
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        hideLayout.startAnimation(slideDownAnimation);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout.setVisibility(View.GONE);
                // recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
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

    @Override
    public void onBackPressed() {
        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        if (linEffect.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linEffect);
        } else if (linBackgroundBlur.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, linBackgroundBlur);
        } else if (linThirdDivisionOption.getVisibility() == View.VISIBLE) {
            findViewById(R.id.ivClose).performClick();
        } else {
            showBackDialog();
        }
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

    public void setTollbarData() {
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(getResources().getString(R.string.wings));
        findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                showBackDialog();
            }
        });
        findViewById(R.id.tv_applay).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                new saveImageTaskMaking().execute();
            }
        });
    }

    private void showBackDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_leave);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) dialog.findViewById(R.id.btn_yes);
        TextView button2 = (TextView) dialog.findViewById(R.id.btn_no);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    public void openShareActivity() {
        Intent intent = new Intent(WingsActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void setUpBottomList() {
        neonEffectListAdapter = new NeonEffectListAdapter(mContext);
        neonEffectListAdapter.setClickListener(this);
        recyclerNeonEffect.setAdapter(neonEffectListAdapter);
        neonEffectListAdapter.addData(wingsEffect);
    }

    public void onMenuListClick(View view, int i) {
        if (i != 0) {
            Bitmap backspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/back/" + neonEffectListAdapter.getItemList().get(i)
                    + (neonEffectListAdapter.getItemList().get(i).startsWith("b") ? "_back.png" : ".png"));
            Bitmap fronspiral = ImageUtils.getBitmapFromAsset(mContext, "spiral/front/" + neonEffectListAdapter.getItemList().get(i) + "_front.png");
            setback.setImageBitmap(backspiral);
            setfront.setImageBitmap(fronspiral);
        } else {
            setback.setImageResource(0);
            setfront.setImageResource(0);
        }
        setback.setOnTouchListener(new DHANVINE_MultiTouchListener(this, true));
    }

    public void cutmaskNew() {

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final ProgressBar progressBar2 = progressBar;
        new CountDownTimer(5000, 1000) {
            public void onFinish() {
            }

            public void onTick(long j) {
                int unused = mCount = mCount + 1;
                if (progressBar2.getProgress() <= 90) {
                    progressBar2.setProgress(mCount * 5);
                }
            }
        }.start();

        new MLCropAsyncTask(new MLOnCropTaskCompleted() {
            public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int left, int top) {
                int[] iArr = {0, 0, selectedBit.getWidth(), selectedBit.getHeight()};
                int width = selectedBit.getWidth();
                int height = selectedBit.getHeight();
                int i = width * height;
                selectedBit.getPixels(new int[i], 0, width, 0, 0, width, height);
                int[] iArr2 = new int[i];
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                createBitmap.setPixels(iArr2, 0, width, 0, 0, width, height);
                cutBit = ImageUtils.getMask(mContext, selectedBit, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBit.getWidth(), cutBit.getHeight(), false);
                cutBit = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBit).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(WingsActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        setimg.setImageBitmap(cutBit);
                    }
                });


            }
        }, this, progressBar).execute(new Void[0]);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCheckMark:
            case R.id.ivClose:
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

  /*  private void tabSelected(int i) {
        tvNeonList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_unselected));
        tvNeonList.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        tvWingsList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_unselected));
        tvWingsList.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        tvShapeList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_unselected));
        tvShapeList.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        if (i == 1) {
            tvNeonList.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            tvNeonList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_selected));
        } else if (i == 2) {
            tvWingsList.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            tvWingsList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_selected));
        } else if (i == 3) {
            tvShapeList.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            tvShapeList.setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_tab_back_selected));
        }
    }*/

    private class saveImageTaskMaking extends android.os.AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
            stickerView.setLocked(true);
        }

        public Bitmap getBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }


        @Override
        protected Exception doInBackground(String... strings) {
            mContentRootView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = mContentRootView.getDrawingCache();
            Bitmap bitmap = getBitmapFromView(mContentRootView);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(WingsActivity.this, uri.getPath());

                } else {
                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + getString(R.string.app_folder));
                    if (!myDir.exists())
                        myDir.mkdirs();
                    File file = new File(myDir, fileName);
                    if (oldSavedFileName != null) {
                        File oldFile = new File(myDir, oldSavedFileName);
                        if (oldFile.exists()) oldFile.delete();
                    }
                    oldSavedFileName = fileName;
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        Uri uri = SupportedClass.addImageToGallery(WingsActivity.this, file.getAbsolutePath());
                        if (uri != null)
                            savedImageUri = uri;
                        notifyMediaScannerService(WingsActivity.this, myDir.getAbsolutePath());
                    } catch (Exception e) {
                        return e;
                    }finally {
                        mContentRootView.setDrawingCacheEnabled(false);
                    }
                }
            } catch (Exception e) {
                return e;
            } finally {
                mContentRootView.setDrawingCacheEnabled(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            if (e == null) {


                FullScreenAdManager.fullScreenAdsCheckPref(WingsActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        openShareActivity();
                    }
                });


            } else {
                Toast.makeText(WingsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
