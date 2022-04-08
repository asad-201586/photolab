package com.tools.photolab.effect.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.AdsNetwork;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import com.google.android.material.tabs.TabLayout;
import com.tools.photolab.R;
import com.tools.photolab.effect.callBack.MenuItemClickLister;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.custom.CustomTextView;

import com.tools.photolab.effect.drip_tool.adapter.DripBackgroundAdapter;
import com.tools.photolab.effect.drip_tool.adapter.DripItemAdapter;
import com.tools.photolab.effect.drip_tool.imagescale.CollageView;
import com.tools.photolab.effect.drip_tool.imagescale.MultiTouchListener;
import com.tools.photolab.effect.drip_tool.utils.ColorUtils;
import com.tools.photolab.effect.drip_tool.utils.CustomFrameLayout;
import com.tools.photolab.effect.drip_tool.utils.Utils;
import com.tools.photolab.effect.erase_tool.StickerEraseActivity;
import com.tools.photolab.effect.crop_img.newCrop.MLCropAsyncTask;
import com.tools.photolab.effect.crop_img.newCrop.MLOnCropTaskCompleted;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;
import com.tools.photolab.effect.support.ImageUtils;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

import static com.tools.photolab.effect.activity.PixLabActivity.notifyMediaScannerService;


public class DripEffectActivity extends BaseActivity implements MenuItemClickLister {
    public static Bitmap eraserResultBmp;
    private static Bitmap faceBitmap;

    public int mCount = 0;
    CollageView imgpost;
    CollageView dripView;
    CollageView frameFront;
    CollageView frameBack;
    CustomFrameLayout frameMain;
    RecyclerView rcbackground;
    private boolean isFirstTime = true, isReady = false;
    private float[] lastEvent;
    private float XX = -1.0f, YY = -1.0f, ax = 0.0f, ay = 0.0f, d, oldDist = 1.0f, r = -1.0f, scale = 1.0f;
    private int[] COLORS;
    private int currentColor = -1, i = 0, leftDx = 0, mode = 0, underHeight = 0;
    private RecyclerView rvDripFilters;
    private TabLayout tabLayout;
    private HorizontalScrollView horizontalSV;
    private Bitmap bWhite = null, currentOverLay = null, mainBitmapColor = null, mainBitmap = null;
    private DripItemAdapter dripItemAdapter;
    private View lastSelectedColor = null;
    //    private ImageView mainImage;
    private LinearLayout ll_styles;
    private ArrayList<String> neonEffect = new ArrayList<String>();
    private OnClickListener lsn_color = new OnClickListener() {
        public void onClick(View view) {
            int parseInt = Integer.parseInt(view.getTag().toString());
            if (parseInt == -2) {
                currentColor = -1;
            } else {
                currentColor = COLORS[parseInt];
            }
            if (lastSelectedColor != null) {
                lastSelectedColor.setBackground(ContextCompat.getDrawable(DripEffectActivity.this, R.drawable.select_circle_trans));
            }
            view.setBackground(ContextCompat.getDrawable(DripEffectActivity.this, R.drawable.select_circle));
            mainBitmapColor = Utils.changeBitmapColor(bWhite, currentColor);
            // setBackground(parseInt);
            imgpost.setImageBitmap(mainBitmapColor);
            frameMain.setBackgroundColor(currentColor);
            imgpost.setBackgroundColor(currentColor);
            dripView.setColorFilter(currentColor);
            lastSelectedColor = view;
        }
    };
    private Bitmap selectedBit, cutBit;
    private Uri savedImageUri;
    private String oldSavedFileName;

    public static void setFaceBitmap(Bitmap bitmap) {
        faceBitmap = bitmap;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_drip_effect);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(DripEffectActivity.this));
        imgpost = findViewById(R.id.imgpost);
        dripView = findViewById(R.id.dripView);
        frameFront = findViewById(R.id.frameFront);
        frameBack = findViewById(R.id.frameBack);
        frameMain = findViewById(R.id.frameMain);


        dripView.setOnTouchListenerCustom(new MultiTouchListener());
        frameFront.setOnTouchListenerCustom(new MultiTouchListener());


        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        this.ll_styles = (LinearLayout) findViewById(R.id.styles);
        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        //loadBannerAds(mAdView);


        new Handler().postDelayed(new Runnable() {
            public void run() {
                frameFront.post(new Runnable() {
                    public void run() {
                        if (isFirstTime) {
                            isFirstTime = false;
                            initBMP();
                        }
                    }
                });
            }
        },  1000);


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

        findViewById(R.id.ivShowHomeOption).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackPressed();
            }
        });

       // neonEffect.add("none");
        for (int i = 1; i <= 26; i++) {
            neonEffect.add("drip_" + i);
        }

        horizontalSV = findViewById(R.id.horizontalSV);
        rvDripFilters = (RecyclerView) findViewById(R.id.rvDripFilters);
        rvDripFilters.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        setUpBottomList();
        showColorPicker();

        frameFront.post(new Runnable() {
            public void run() {
                initBMP();
            }
        });
        setBGImages("bg_", 42);
        tabLayout.setVisibility(View.VISIBLE);
        horizontalSV.setVisibility(View.GONE);
        rvDripFilters.setVisibility(View.GONE);

    }

    public final ArrayList<?> setBGImages(String str, int i) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        int i2 = 1;
        if (1 <= i) {
            while (true) {
                Resources resources = getResources();
                arrayList.add(Integer.valueOf(resources.getIdentifier(str + i2, "drawable", getPackageName())));
                if (i2 == i) {
                    break;
                }
                i2++;
            }
        }
        rcbackground = (RecyclerView) findViewById(R.id.rcbackground);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DripEffectActivity.this, RecyclerView.HORIZONTAL, false);
        rcbackground.setLayoutManager(linearLayoutManager);
        Intrinsics.checkExpressionValueIsNotNull(rcbackground, "rcbackground");
        rcbackground.setAdapter(new DripBackgroundAdapter(this, arrayList));
        return arrayList;
    }


    private void initBMP() {
        if (faceBitmap != null) {
            selectedBit = ImageUtils.getBitmapResize(DripEffectActivity.this, faceBitmap, 1024, 1024);
            bWhite = Bitmap.createScaledBitmap(Utils.getBitmapFromAsset(DripEffectActivity.this, "drip/white.png"), selectedBit.getWidth(), selectedBit.getHeight(), true);
            mainBitmapColor = bWhite;
            Glide.with(this).load(Integer.valueOf(R.drawable.drip_1)).into(dripView);
            frameBack.setImageBitmap(selectedBit);
            cutmaskNew();
        }
    }

    public void openShareActivity() {
        Intent intent = new Intent(DripEffectActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
        AdsNetwork.shoAdmobInters(this);
    }

    private void onBottomTabSelected(TabLayout.Tab tab) {
        if (tab.getPosition() == 0) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(rvDripFilters, tabLayout);

        } else if (tab.getPosition() == 1) {
            StickerEraseActivity.b = cutBit;
            Intent intent = new Intent(this, StickerEraseActivity.class);
            intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_DRIP);
            startActivityForResult(intent, 1024);

        } else if (tab.getPosition() == 2) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(horizontalSV, tabLayout);

        } else if (tab.getPosition() == 3) {
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            viewSlideUpDown(rcbackground, tabLayout);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                cutBit = eraserResultBmp;
                frameFront.setImageBitmap(cutBit);
                isReady = true;
                Bitmap bitmapFromAsset = Utils.getBitmapFromAsset(DripEffectActivity.this, "drip/" + dripItemAdapter.getItemList().get(dripItemAdapter.selectedPos) + ".png");
                if (!"none".equals(dripItemAdapter.getItemList().get(0))) {
                    currentOverLay = bitmapFromAsset;
                }
            }
        }
    }

    public void viewSlideUpDown(final View showLayout, final View hideLayout) {
        showLayout.setVisibility(View.VISIBLE);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        showLayout.startAnimation(slideUpAnimation);
        Animation slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        hideLayout.startAnimation(slideDownAnimation);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void createTabIcons() {
        View view = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textOne = (CustomTextView) view.findViewById(R.id.text);
        ImageView ImageOne = (ImageView) view.findViewById(R.id.image);
        textOne.setText(getString(R.string.txt_drip));
        ImageOne.setImageResource(R.drawable.ic_drip_svg);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view));

        View view2 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView textTwo = (CustomTextView) view2.findViewById(R.id.text);
        ImageView ImageTwo = (ImageView) view2.findViewById(R.id.image);
        textTwo.setText(getString(R.string.txt_erase));
        ImageTwo.setImageResource(R.drawable.ic_erase);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        View view4 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text4 = (CustomTextView) view4.findViewById(R.id.text);
        ImageView Image4 = (ImageView) view4.findViewById(R.id.image);
        text4.setText(getString(R.string.txt_frame_color));
        Image4.setImageResource(R.drawable.ic_backchange);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));

        View view5 = LayoutInflater.from(this).inflate(R.layout.custom_neon_tab, null);
        CustomTextView text5 = (CustomTextView) view5.findViewById(R.id.text);
        ImageView Image5 = (ImageView) view5.findViewById(R.id.image);
        text5.setText(getString(R.string.txt_background));
        Image5.setImageResource(R.drawable.ic_background);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view5));

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
                cutBit = ImageUtils.getMask(DripEffectActivity.this, selectedBit, createBitmap, width, height);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, cutBit.getWidth(), cutBit.getHeight(), false);
                cutBit = resizedBitmap;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Palette p = Palette.from(cutBit).generate();
                        if (p.getDominantSwatch() == null) {
                            Toast.makeText(DripEffectActivity.this, getString(R.string.txt_not_detect_human), Toast.LENGTH_SHORT).show();
                        }
                        frameFront.setImageBitmap(cutBit);
                        isReady = true;
                        Bitmap bitmapFromAsset = Utils.getBitmapFromAsset(DripEffectActivity.this, "drip/" + dripItemAdapter.getItemList().get(0) + ".png");
                        if (!"none".equals(dripItemAdapter.getItemList().get(0))) {
                            currentOverLay = bitmapFromAsset;
                        }

                    }
                });
            }
        }, this, progressBar).execute(new Void[0]);

    }

    public final void setBackground(int i, int i2) {
        if (i2 == 0) {
            imgpost.setOnTouchListenerCustom((MultiTouchListener) null);
            imgpost.setBackgroundColor(Color.parseColor("#FFFFFF"));
            frameMain.setBackgroundColor(Color.parseColor("#FFFFFF"));
            imgpost.setImageResource(0);
            dripView.setColorFilter(Color.parseColor("#FFFFFF"));
            return;
        }
        imgpost.setOnTouchListenerCustom(new MultiTouchListener());
        imgpost.setBackgroundColor(Color.parseColor("#FFFFFF"));
        frameMain.setBackgroundColor(Color.parseColor("#FFFFFF"));
        dripView.setColorFilter(Color.parseColor("#FFFFFF"));
        RequestManager with = Glide.with((FragmentActivity) this);
        Resources resources = getResources();
        Intrinsics.checkExpressionValueIsNotNull(with.load(Integer.valueOf(resources.getIdentifier("bg_" + (i2 + 1), "drawable", getPackageName()))).into(imgpost), "Glide.with(this).load(re…ckageName)).into(imgpost)");
    }


    public void onMenuListClick(View view, int i) {
        Bitmap bitmapFromAsset = Utils.getBitmapFromAsset(DripEffectActivity.this, "drip/" + dripItemAdapter.getItemList().get(i) + ".png");
        if (!"none".equals(dripItemAdapter.getItemList().get(i))) {
            currentOverLay = bitmapFromAsset;
            dripView.setImageBitmap(currentOverLay);
            return;
        }
        currentOverLay = null;
    }

    public void setUpBottomList() {
        dripItemAdapter = new DripItemAdapter(DripEffectActivity.this);
        dripItemAdapter.setClickListener(this);
        rvDripFilters.setAdapter(dripItemAdapter);
        dripItemAdapter.addData(neonEffect);
    }


    private float spacing(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
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

    private void showColorPicker() {
        LayoutParams LAYOUT_PARAMS = new LayoutParams(Utils.dpToPx(40), Utils.dpToPx(40));
        LAYOUT_PARAMS.setMargins(Utils.dpToPx(2), Utils.dpToPx(2), Utils.dpToPx(2), Utils.dpToPx(2));
        this.COLORS = ColorUtils.obtainColorArray(getResources(), R.array.color_option);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(LAYOUT_PARAMS);
        imageView.setImageResource(R.drawable.color_default);
        imageView.setTag("-2");
        this.ll_styles.addView(imageView);
        imageView.setOnClickListener(this.lsn_color);
        for (int i2 = 0; i2 < this.COLORS.length; i2++) {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
            ImageView imageView2 = new ImageView(this);
            imageView2.setLayoutParams(LAYOUT_PARAMS);
            imageView2.setImageBitmap(icon);
            imageView2.setColorFilter(COLORS[i2], Mode.SRC_ATOP);
            imageView2.setTag(i2);
            this.ll_styles.addView(imageView2);
            imageView2.setOnClickListener(this.lsn_color);
        }
    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        if (horizontalSV.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, horizontalSV);
        } else if (rvDripFilters.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, rvDripFilters);
        } else if (rcbackground.getVisibility() == View.VISIBLE) {
            viewSlideUpDown(tabLayout, rcbackground);
        } else {
            showBackDialog();
        }
    }


    private class saveImageTaskMaking extends android.os.AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.ivShowHomeOption).setVisibility(View.GONE);
        }


        public Bitmap getBitmapFromView(View view) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }

        @Override
        protected Exception doInBackground(String... strings) {
            frameMain.setDrawingCacheEnabled(true);
            Bitmap bitmap = getBitmapFromView(frameMain);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+File.separator+getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(DripEffectActivity.this, uri.getPath());
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

                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    Uri uri = SupportedClass.addImageToGallery(DripEffectActivity.this, file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(DripEffectActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
//            Exception e = new UnsupportedOperationException();
                return e;
            } finally {
//                mContentRootView.setDrawingCacheEnabled(false);
                frameMain.setDrawingCacheEnabled(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            findViewById(R.id.ivShowHomeOption).setVisibility(View.VISIBLE);
            if (e == null) {
                openShareActivity();
//                FullScreenAdManager.fullScreenAdsCheckPref(DripEffectActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
//                    @Override
//                    public void returnAction() {
//                        openShareActivity();
//                    }
//                });


            } else {
                Toast.makeText(DripEffectActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
