package com.tools.photolab.effect.color_splash_tool;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.ads.AdsNetwork;
import com.tools.photolab.BuildConfig;
import com.tools.photolab.R;
import com.tools.photolab.effect.Common;
import com.tools.photolab.effect.activity.BaseActivity;
import com.tools.photolab.effect.activity.ShareActivity;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.SupportedClass;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;
import com.tools.photolab.effect.support.SupportedClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;

import static com.tools.photolab.effect.activity.PixLabActivity.notifyMediaScannerService;


public class ColorSplashActivity extends BaseActivity implements OnClickListener, OnSeekBarChangeListener {

    public static final int REQUEST_CODE_CAMERA = 0x2;
    public static final int REQUEST_CODE_GALLERY = 0x3;
    public static BrushView brushView;
    public static Bitmap colorBitmap;
    public static int displayHight;
    public static int displayWidth;
    public static Bitmap grayBitmap;
    public static SeekBar offsetBar;
    public static SeekBar opacityBar;
    public static ImageView prView;
    public static SeekBar radiusBar;
    public static String tempDrawPath;
    public static File tempDrawPathFile;
    public static TouchImageView tiv;
    public static Vector vector;
    private String oldSavedFileName;

    static {
        String sb = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Auto Background Changer";
        tempDrawPath = sb;
    }

    public String mSelectedImagePath;
    public String mSelectedOutputPath;
    public Uri mSelectedImageUri;
    private RelativeLayout imageViewContainer;
    private ImageView colorBtn, grayBtn, offsetBtn, offsetDemo, recolorBtn, zoomBtn;
    private String imageSavePath;
    private Bitmap hand;
    private LinearLayout offsetLayout;
    private Runnable runnableCode;
    private ProgressDialog saveLoader;


    public ColorSplashActivity() {
        this.imageSavePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Auto Background Changer";
    }

    public static Uri getFileUri(Activity activity, String filePath) {
        Uri outputUri;
        if (Build.VERSION.SDK_INT >= 24) {
            outputUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", new File(filePath));
        } else {
            outputUri = Uri.fromFile(new File(filePath));
        }
        return outputUri;

    }

    public void update() {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_colorsplash_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(ColorSplashActivity.this));

        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        //loadBannerAds(mAdView);

        imageViewContainer = findViewById(R.id.imageViewContainer);
        this.saveLoader = new ProgressDialog(this);
        ProgressDialog videoLoader = new ProgressDialog(this);
        videoLoader.setMessage("Video is LOADING");
        videoLoader.setIndeterminate(true);
        videoLoader.setCancelable(false);
        videoLoader.setTitle("Please wait...");
        vector = new Vector();
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        displayWidth = point.x;
        displayHight = point.y;
        this.hand = BitmapFactory.decodeResource(getResources(), R.drawable.hand);
        this.hand = Bitmap.createScaledBitmap(this.hand, 120, 120, true);
        tiv = (TouchImageView) findViewById(R.id.drawingImageView);
        prView = (ImageView) findViewById(R.id.preview);
        colorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.me);
        grayBitmap = toGrayScale(colorBitmap);

        this.offsetLayout = (LinearLayout) findViewById(R.id.offsetLayout);
        ImageView newBtn = (ImageView) findViewById(R.id.newBtn);
        ImageView resetBtn = (ImageView) findViewById(R.id.resetBtn);
        ImageView undoBtn = (ImageView) findViewById(R.id.undoBtn);
        ImageView fitBtn = (ImageView) findViewById(R.id.fitBtn);
        ImageView saveBtn = (ImageView) findViewById(R.id.saveBtn);
        ImageView shareBtn = (ImageView) findViewById(R.id.shareBtn);
        this.colorBtn = (ImageView) findViewById(R.id.colorBtn);
        this.recolorBtn = (ImageView) findViewById(R.id.recolorBtn);
        this.grayBtn = (ImageView) findViewById(R.id.grayBtn);
        this.zoomBtn = (ImageView) findViewById(R.id.zoomBtn);
        ImageView ic_back = (ImageView) findViewById(R.id.ic_back);
        this.offsetBtn = (ImageView) findViewById(R.id.offsetBtn);
        Button offsetOk = (Button) findViewById(R.id.offsetOk);
        this.offsetDemo = (ImageView) findViewById(R.id.offsetDemo);
        newBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(this);
        fitBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        this.colorBtn.setOnClickListener(this);
        this.recolorBtn.setOnClickListener(this);
        this.grayBtn.setOnClickListener(this);
        this.zoomBtn.setOnClickListener(this);
        this.offsetBtn.setOnClickListener(this);
        offsetOk.setOnClickListener(this);
        ic_back.setOnClickListener(this);
        offsetBar = (SeekBar) findViewById(R.id.offsetBar);
        radiusBar = (SeekBar) findViewById(R.id.widthSeekBar);
        opacityBar = (SeekBar) findViewById(R.id.opacitySeekBar);
        brushView = (BrushView) findViewById(R.id.magnifyingView);
        brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
        radiusBar.setMax(300);
        radiusBar.setProgress((int) tiv.radius);
        offsetBar.setProgress(0);
        offsetBar.setMax(100);
        opacityBar.setMax(240);
        opacityBar.setProgress(tiv.opacity);
        radiusBar.setOnSeekBarChangeListener(this);
        opacityBar.setOnSeekBarChangeListener(this);
        offsetBar.setOnSeekBarChangeListener(this);
        File file = new File(this.imageSavePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        clearTempBitmap();
        tiv.initDrawing();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("show", "no").commit();
        update();
        final Handler handler = new Handler();
        this.runnableCode = new Runnable() {
            public void run() {
                handler.postDelayed(runnableCode, 2000);
            }
        };
        handler.post(this.runnableCode);
    }



    public void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        if (tempDrawPathFile.isDirectory() || tempDrawPathFile.list() != null) {
            for (String file : tempDrawPathFile.list()) {
                new File(tempDrawPathFile, file).delete();
            }
        }
    }

    public Bitmap toGrayScale(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }

    public void onClick(View view) {
        Log.wtf("Click : ", "Inside onclick");
        switch (view.getId()) {
            case R.id.colorBtn:
                break;
            case R.id.fitBtn:
                TouchImageView touchImageView = tiv;
                touchImageView.saveScale = 1.0f;
                touchImageView.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                return;
            case R.id.grayBtn:
                tiv.mode = 0;
                this.recolorBtn.setBackgroundColor(-1);
                this.colorBtn.setBackgroundColor(-1);
                this.zoomBtn.setBackgroundColor(-1);
                this.grayBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                tiv.splashBitmap = toGrayScale(colorBitmap);
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = -2;
                return;
            case R.id.ic_back:
                showBackDialog();
                return;
            case R.id.newBtn:
                selectImage();
                return;
            case R.id.offsetBtn:
                this.offsetBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                this.offsetLayout.setVisibility(View.VISIBLE);
                return;
            case R.id.offsetOk:
                this.offsetBtn.setBackgroundColor(-1);
                this.offsetLayout.setVisibility(View.INVISIBLE);
                return;
            case R.id.recolorBtn:
                tiv.mode = 0;
                this.colorBtn.setBackgroundColor(-1);
                this.zoomBtn.setBackgroundColor(-1);
                this.recolorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                this.grayBtn.setBackgroundColor(-1);
                new AmbilWarnaDialog(this, Color.parseColor("#4149b6"), true, new OnAmbilWarnaListener() {
                    public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                    }

                    public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                        ColorSplashActivity colorSplashActivity = ColorSplashActivity.this;
                        ColorSplashActivity.grayBitmap = colorSplashActivity.toGrayScale(ColorSplashActivity.colorBitmap);
                        Canvas canvas = new Canvas(ColorSplashActivity.grayBitmap);
                        Paint paint = new Paint();
                        paint.setColorFilter(new ColorMatrixColorFilter(new float[]{((float) ((i >> 16) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((i >> 8) & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) (i & 255)) / 256.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, ((float) ((i >> 24) & 255)) / 256.0f, 0.0f}));
                        canvas.drawBitmap(ColorSplashActivity.grayBitmap, 0.0f, 0.0f, paint);
                        ColorSplashActivity.tiv.splashBitmap = ColorSplashActivity.grayBitmap;
                        ColorSplashActivity.tiv.updateRefMetrix();
                        ColorSplashActivity.tiv.changeShaderBitmap();
                        ColorSplashActivity.tiv.coloring = i;
                    }
                }).show();
                return;
            case R.id.resetBtn:
                resetImage();
                return;
            case R.id.saveBtn:
                saveImage();
                return;
            case R.id.undoBtn:
                int i = tiv.currentImageIndex - 1;
                String sb2 = tempDrawPath + "/canvasLog" + i + ".jpg";
                Log.wtf("Current Image ", sb2);
                if (new File(sb2).exists()) {
                    tiv.drawingBitmap = null;
                    Options options = new Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(sb2, options);
                    TouchImageView touchImageView2 = tiv;
                    touchImageView2.setImageBitmap(touchImageView2.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    String sb3 = tempDrawPath + "canvasLog" + tiv.currentImageIndex + ".jpg";
                    File file = new File(sb3);
                    if (file.exists()) {
                        file.delete();
                    }
                    TouchImageView touchImageView3 = tiv;
                    touchImageView3.currentImageIndex--;
                    Vector vector2 = vector;
                    vector2.remove(vector2.size() - 1);
                }
                return;
            case R.id.zoomBtn:
                tiv.mode = 1;
                this.recolorBtn.setBackgroundColor(-1);
                this.grayBtn.setBackgroundColor(-1);
                this.colorBtn.setBackgroundColor(-1);
                this.zoomBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                return;
            default:
                return;
        }
        tiv.mode = 0;
        this.recolorBtn.setBackgroundColor(-1);
        this.grayBtn.setBackgroundColor(-1);
        this.zoomBtn.setBackgroundColor(-1);
        this.colorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
        TouchImageView touchImageView4 = tiv;
        touchImageView4.splashBitmap = colorBitmap;
        touchImageView4.updateRefMetrix();
        tiv.changeShaderBitmap();
        tiv.coloring = -1;
    }

    public void saveImage() {

        if (tiv.drawingBitmap != null) {
            AdsNetwork.shoAdmobInters(this);
            String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));

                    Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                    FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                    tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null) {
                        Intent intent = new Intent(ColorSplashActivity.this, ShareActivity.class);
                        intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                        startActivity(intent);
                        finish();
                    }
                    notifyMediaScannerService(ColorSplashActivity.this, uri.getPath());

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
                    tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    //
                    Uri uri = SupportedClass.addImageToGallery(ColorSplashActivity.this, file.getAbsolutePath());
                    if (uri != null) {
                        Intent intent = new Intent(ColorSplashActivity.this, ShareActivity.class);
                        intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                        startActivity(intent);
                        finish();
                    }
                    notifyMediaScannerService(ColorSplashActivity.this, myDir.getAbsolutePath());
                }
            } catch (Exception e) {
                return;
//                        Log.i("Error",e.getMessage());
            } finally {
                imageViewContainer.setDrawingCacheEnabled(false);
            }
        }

        /*FullScreenAdManager.fullScreenAdsCheckPref(ColorSplashActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_SAVED_IMAGE_CLICKED, new FullScreenAdManager.GetBackPointer() {
            @Override
            public void returnAction() {
                if (tiv.drawingBitmap != null) {
                    String fileName = getString(R.string.app_file) + System.currentTimeMillis() + Constants.KEY_JPG;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                            ContentResolver contentResolver = getContentResolver();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_folder2));

                            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                            FileOutputStream fos = (FileOutputStream) contentResolver.openOutputStream(Objects.requireNonNull(uri));
                            tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            Objects.requireNonNull(fos);
                            if (uri != null) {
                                Intent intent = new Intent(ColorSplashActivity.this, ShareActivity.class);
                                intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                                startActivity(intent);
                                finish();
                            }
                            notifyMediaScannerService(ColorSplashActivity.this, uri.getPath());

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
                            tiv.drawingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            //
                            Uri uri = SupportedClass.addImageToGallery(ColorSplashActivity.this, file.getAbsolutePath());
                            if (uri != null) {
                                Intent intent = new Intent(ColorSplashActivity.this, ShareActivity.class);
                                intent.putExtra(Constants.KEY_URI_IMAGE, uri.toString());
                                startActivity(intent);
                                finish();
                            }
                            notifyMediaScannerService(ColorSplashActivity.this, myDir.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        return;
//                        Log.i("Error",e.getMessage());
                    } finally {
                        imageViewContainer.setDrawingCacheEnabled(false);
                    }
                }

            }
        });*/

    }

    public void saveImage(Bitmap bitmap) {
        String currentPath = this.imageSavePath + "/" + System.currentTimeMillis() + ".jpg";
        File file = new File(currentPath);
        Bitmap grayScale = toGrayScale(bitmap);
        Bitmap copy = grayScale.copy(Bitmap.Config.ARGB_8888, true);
        float width = ((float) copy.getWidth()) / ((float) tiv.drawingBitmap.getWidth());
        Canvas canvas = new Canvas(copy);
        Paint paint4 = new Paint(1);
        int i = -10;
        int r4 = 1;
        Paint paint;
        Paint paint2;
        Paint paint3 = null;
        while (vector.size() > 0) {
            MyPath myPath = (MyPath) vector.elementAt(0);
            if (i == myPath.color) {
                paint = paint4;
            } else {
                if (myPath.color == -1) {
                    paint3 = new Paint();
                    paint3.setShader(new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP));
                } else {
                    if (myPath.color == -2) {
                        paint3 = new Paint();
                        paint3.setShader(new BitmapShader(grayScale, TileMode.CLAMP, TileMode.CLAMP));
                    } else {
                        Bitmap copy2 = grayScale.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas canvas2 = new Canvas(copy2);
                        Paint paint5 = new Paint();
                        float[] fArr = new float[20];
                        fArr[0] = ((float) ((myPath.color >> 16) & 255)) / 256.0f;
                        fArr[r4] = 0.0f;
                        fArr[2] = 0.0f;
                        fArr[3] = 0.0f;
                        fArr[4] = 0.0f;
                        fArr[5] = 0.0f;
                        fArr[6] = ((float) ((myPath.color >> 8) & 255)) / 256.0f;
                        fArr[7] = 0.0f;
                        fArr[8] = 0.0f;
                        fArr[9] = 0.0f;
                        fArr[10] = 0.0f;
                        fArr[11] = 0.0f;
                        fArr[12] = ((float) (myPath.color & 255)) / 256.0f;
                        fArr[13] = 0.0f;
                        fArr[14] = 0.0f;
                        fArr[15] = 0.0f;
                        fArr[16] = 0.0f;
                        fArr[17] = 0.0f;
                        fArr[18] = ((float) ((myPath.color >> 24) & 255)) / 256.0f;
                        fArr[19] = 0.0f;
                        paint5.setColorFilter(new ColorMatrixColorFilter(fArr));
                        canvas2.drawBitmap(grayScale, 0.0f, 0.0f, paint5);
                        Paint paint6 = new Paint(1);
                        paint6.setShader(new BitmapShader(copy2, TileMode.CLAMP, TileMode.CLAMP));
                        int i2 = myPath.color;
                        paint6.setStyle(Style.STROKE);
                        paint6.setStrokeWidth(myPath.r * width);
                        paint6.setStrokeCap(Cap.ROUND);
                        paint6.setStrokeJoin(Join.ROUND);
                        paint6.setMaskFilter(new BlurMaskFilter(width * 30.0f, Blur.NORMAL));
                        canvas.drawPath(myPath.convertPath(width), paint6);
                    }
                }
                paint2 = paint3;
                paint = paint2;
            }
            i = myPath.color;
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(myPath.r * width);
            paint.setStrokeCap(Cap.ROUND);
            paint.setStrokeJoin(Join.ROUND);
            paint.setMaskFilter(new BlurMaskFilter(width * 30.0f, Blur.NORMAL));
            canvas.drawPath(myPath.convertPath(width), paint);
            paint4 = paint;
            r4 = 1;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copy.compress(CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            grayScale.recycle();
            copy.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            MyMediaConnectorClient myMediaConnectorClient = new MyMediaConnectorClient(currentPath);
            MediaScannerConnection mediaScannerConnection = new MediaScannerConnection(this, myMediaConnectorClient);
            myMediaConnectorClient.setScanner(mediaScannerConnection);
            mediaScannerConnection.connect();
            Intent intent = new Intent(ColorSplashActivity.this, ShareActivity.class);
            intent.putExtra(Constants.KEY_URI_IMAGE, currentPath);
            startActivity(intent);
        }
    }

    public void resetImage() {
        final Dialog progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.dialog_reset);
        progressDialog.setCancelable(false);
        Window window = progressDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView button = (TextView) progressDialog.findViewById(R.id.cancel);
        TextView button2 = (TextView) progressDialog.findViewById(R.id.save);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressDialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                progressDialog.dismiss();
                ColorSplashActivity.grayBitmap = toGrayScale(ColorSplashActivity.colorBitmap);
                clearTempBitmap();
                ColorSplashActivity.tiv.initDrawing();
                ColorSplashActivity.tiv.saveScale = 1.0f;
                ColorSplashActivity.tiv.fitScreen();
                ColorSplashActivity.tiv.updatePreviewPaint();
                ColorSplashActivity.tiv.updatePaintBrush();
                grayBtn.setBackgroundColor(-1);
                zoomBtn.setBackgroundColor(-1);
                colorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                recolorBtn.setBackgroundColor(-1);
                ColorSplashActivity.vector.clear();
            }
        });
        progressDialog.show();
    }

    public void onBackPressed() {
        showBackDialog();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        if (id == R.id.offsetBar) {
            Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(copy);
            Paint paint = new Paint(1);
            paint.setColor(-16711936);
            canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
            canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
            this.offsetDemo.setImageBitmap(copy);
        } else if (id == R.id.opacitySeekBar) {
            BrushView brushView2 = brushView;
            brushView2.isBrushSize = false;
            brushView2.setShapeRadiusRatio(tiv.radius);
            brushView.brushSize.setPaintOpacity(opacityBar.getProgress());
            brushView.invalidate();
            TouchImageView touchImageView = tiv;
            touchImageView.opacity = i + 15;
            touchImageView.updatePaintBrush();
        } else if (id == R.id.widthSeekBar) {
            BrushView brushView3 = brushView;
            brushView3.isBrushSize = true;
            brushView3.brushSize.setPaintOpacity(255);
            brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
            String sb = radiusBar.getProgress() + "";
            Log.wtf("radious :", sb);
            brushView.invalidate();
            tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
            tiv.updatePaintBrush();
        }
    }

    private void selectImage() {
        showPicImageDialog();
    }

    public void showPicImageDialog() {
        final Dialog pixDialog = new Dialog(this);
        pixDialog.setContentView(R.layout.dialog_select_photo);
        pixDialog.setCancelable(false);
        Window window = pixDialog.getWindow();
        window.setLayout(((SupportedClass.getWidth(ColorSplashActivity.this) / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        pixDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LinearLayout camera_item = pixDialog.findViewById(R.id.camera_item);
        LinearLayout gallery_item = pixDialog.findViewById(R.id.gallery_item);
        ImageView btnDismiss =  pixDialog.findViewById(R.id.cancel);
        gallery_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.FROM = Common.GALLERY;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.txt_select_picture)), REQUEST_CODE_GALLERY);
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
            }
        });
        camera_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.FROM = Common.GALLERY;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                if (pixDialog.isShowing() && !isFinishing()) {
                    pixDialog.dismiss();
                }
            }
        });
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pixDialog.dismiss();
            }
        });

        pixDialog.show();
    }

    private File createImageFile() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + BuildConfig.APPLICATION_ID + "/CamPic/");
        storageDir.mkdirs();
        File image = null;
        try {
            image = new File(storageDir, getString(R.string.app_folder3));
            if (image.exists())
                image.delete();
            image.createNewFile();

            mSelectedOutputPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
        dialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA) {
            mSelectedImagePath = mSelectedOutputPath;
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                File fileImageClick = new File(mSelectedImagePath);
                if (fileImageClick.exists()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        mSelectedImageUri = Uri.fromFile(fileImageClick);
                    } else {
                        mSelectedImageUri = FileProvider.getUriForFile(ColorSplashActivity.this, BuildConfig.APPLICATION_ID + ".provider", fileImageClick);
                    }
                    onPhotoTakenApp();
                }
            }
        } else if (data != null && data.getData() != null) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                mSelectedImageUri = data.getData();
                if (mSelectedImageUri != null) {
                    mSelectedImagePath = Constants.convertMediaUriToPath(ColorSplashActivity.this, mSelectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectedImagePath = mSelectedOutputPath;
            }
            if (SupportedClass.stringIsNotEmpty(mSelectedImagePath)) {
                onPhotoTakenApp();
            }

        } else {
            Log.e("TAG", "");
        }
    }

    public void onPhotoTakenApp() {
        imageViewContainer.post(new Runnable() {
            @Override
            public void run() {
                try {
                    colorBitmap = Constants.getBitmapFromUri(ColorSplashActivity.this, mSelectedImageUri, (float) imageViewContainer.getMeasuredWidth(), (float) imageViewContainer.getMeasuredHeight());
                    grayBitmap = toGrayScale(colorBitmap);
                    clearTempBitmap();
                    tiv.initDrawing();
                    tiv.saveScale = 1.0f;
                    tiv.fitScreen();
                    tiv.updatePreviewPaint();
                    tiv.updatePaintBrush();
                    grayBtn.setBackgroundColor(-1);
                    zoomBtn.setBackgroundColor(-1);
                    colorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                    recolorBtn.setBackgroundColor(-1);
                    vector.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() != R.id.offsetBar) {
            brushView.setVisibility(View.VISIBLE);
            return;
        }
        this.offsetDemo.setVisibility(View.VISIBLE);
        Bitmap copy = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Paint paint = new Paint(1);
        paint.setColor(-16711936);
        canvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, paint);
        canvas.drawBitmap(this.hand, 95.0f, 150.0f, null);
        this.offsetDemo.setImageBitmap(copy);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.offsetBar) {
            this.offsetDemo.setVisibility(View.INVISIBLE);
        } else {
            brushView.setVisibility(View.INVISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onResume() {
//        this.busWrapper.register(this);
//        this.networkEvents.register();
        super.onResume();
    }

    public void onPause() {
//        this.busWrapper.unregister(this);
//        this.networkEvents.unregister();
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    class ResetDialog extends Dialog {
        Context ctx;

        public ResetDialog(Context context) {
            super(context);
            this.ctx = context;
        }

        public ResetDialog(Context context, int i) {
            super(context, i);
            this.ctx = context;
        }

        protected ResetDialog(Context context, boolean z, OnCancelListener onCancelListener) {
            super(context, z, onCancelListener);
            this.ctx = context;
        }

        public void show() {
            requestWindowFeature(1);
            View inflate = LayoutInflater.from(this.ctx).inflate(R.layout.dialog_reset, null);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            super.setContentView(inflate);
            TextView button = (TextView) inflate.findViewById(R.id.cancel);
            TextView button2 = (TextView) inflate.findViewById(R.id.save);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ResetDialog.this.dismiss();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    ResetDialog.this.dismiss();
                    ColorSplashActivity.grayBitmap = toGrayScale(ColorSplashActivity.colorBitmap);
                    clearTempBitmap();
                    ColorSplashActivity.tiv.initDrawing();
                    ColorSplashActivity.tiv.saveScale = 1.0f;
                    ColorSplashActivity.tiv.fitScreen();
                    ColorSplashActivity.tiv.updatePreviewPaint();
                    ColorSplashActivity.tiv.updatePaintBrush();
                    grayBtn.setBackgroundColor(-1);
                    zoomBtn.setBackgroundColor(-1);
                    colorBtn.setBackgroundColor(getResources().getColor(R.color.selected));
                    recolorBtn.setBackgroundColor(-1);
                    ColorSplashActivity.vector.clear();
                }
            });
            super.show();
        }
    }

    private class SaveThread extends AsyncTask<Bitmap, Integer, Void> {
        private SaveThread() {
        }


        public void onPreExecute() {
            super.onPreExecute();
            saveLoader.setMessage("Saving in HD quality");
            saveLoader.setIndeterminate(true);
            saveLoader.setCancelable(false);
            saveLoader.show();
        }


        public Void doInBackground(Bitmap... bitmapArr) {
            saveImage(bitmapArr[0]);
            return null;
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            if (saveLoader.isShowing()) {
                saveLoader.dismiss();
            }
        }
    }


}
