package com.tools.photolab.effect.motion_tool;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.ads.AdsNetwork;
import com.tools.photolab.R;
import com.tools.photolab.effect.activity.BaseActivity;
import com.tools.photolab.effect.activity.ShareActivity;
import com.tools.photolab.effect.ads.FullScreenAdManager;
import com.tools.photolab.effect.erase_tool.StickerEraseActivity;
import com.tools.photolab.effect.support.Constants;
import com.tools.photolab.effect.support.MyExceptionHandlerPix;
import com.tools.photolab.effect.support.SupportedClass;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import static com.tools.photolab.effect.activity.PixLabActivity.notifyMediaScannerService;


public class MotionEffectActivity extends BaseActivity {
    static final int DRAG = 1;
    static final LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams(Utils.dpToPx(30), Utils.dpToPx(30));
    static final int NONE = 0;
    public static final int SEEKBAR_WIDTH = 10;
    public static final int SEEKBAR_WIDTH_MAX = 65;
    static final int ZOOM = 2;
    private static Activity mainPhotoActivity = null;


    public float f149XX = -1.0f;


    public float f150YY = -1.0f;
    float _centerX;
    float _centerY;
    float _refX;
    float _refY;
    double _rotation = 0.0d;
    double alpha = Math.toRadians(this.motionDirection);


    float f151ax = ((float) this.mLeft);


    float f152ay = ((float) this.mTop);

    public Bitmap bWhite = null;
    int changeY = 0;
    CropAsyncTaskPaint cropAsyncTaskPaint = null;
    int currentColor = -1;
    private Bitmap currentOverLay = null;

    public int currentProgressDensity = 3;

    public int currentProgressOpacity = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;


    float f153d;


    float f154dx;


    float f155dy;


    int f156i = 0;
    ImageViewTouch imageViewCenter;
    ImageView iv_erase;

    public boolean isReady = false;
    float[] lastEvent;
    View lastSelectedOverlay = null;
    private LinearLayout ll_styles;

    public int mCount = 0;
    Bitmap mCropped = null;
    int mLeft;
    private SeekBar mStokenWidthSeekBarColor = null;
    private SeekBar mStokenWidthSeekBarErase = null;
    private SeekBar mStokenWidthSeekBarRotate = null;
    int mTop;
    private Bitmap mainBitmap = null;

    public Bitmap mainBitmapColor = null;
    ImageView mainImage;

    public Matrix matrix = null;
    Matrix matriximageViewCenter = null;
    PointF mid = new PointF();
    int mode = 0;
    int motionCount = 3;
    double motionDirection = 30.0d;
    int motionDistance = 100;
    float newRot;
    float oldDist = 1.0f;

    public static Bitmap oraginal;


    float f157r = -1.0f;
    private RelativeLayout rSliderColor;
    private RelativeLayout rSliderErase;
    private RelativeLayout rSliderRotat;
    Matrix savedMatrix = new Matrix();
    float scale = 1.0f;
    float scale6;
    int sdf = 100;

    public TextView seekBarNameColor;

    public TextView seekBarNameErase;

    public TextView seekBarNameRotate;
    ImageView tv_applay;
    PointF start = new PointF();
    PointF startMove = new PointF();
    int underHeight = 0;


    float f158xC;
    float f159yC;

    static {
        int dpToPx = Utils.dpToPx(5);
        LAYOUT_PARAMS.setMargins(dpToPx, dpToPx, dpToPx, dpToPx);
    }

    public static void setFaceBitmap(Bitmap bitmap) {
        oraginal = bitmap;
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_motion_tool);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandlerPix(MotionEffectActivity.this));
        RelativeLayout mAdView = findViewById(R.id.adView);
        AdsNetwork.showAdmobBanner(this,mAdView);
        //loadBannerAds(mAdView);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.crop_progress_bar);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.drip_effect_toolbar);
        /*toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("uuuuuuuuuu", "pppppppppp");
                Utils.isChanged = false;
                onClose();
            }
        });*/
        this.imageViewCenter = (ImageViewTouch) findViewById(R.id.imageViewCenter);
        this.mainImage = (ImageView) findViewById(R.id.mainImage);
        this.iv_erase = (ImageView) findViewById(R.id.iv_erase);
        this.tv_applay = (ImageView) findViewById(R.id.tv_applay);
//        this.oraginal = StoreManager.getCurrentOriginalBitmap(this);
        this.imageViewCenter.setImageBitmap(this.oraginal);
        this.imageViewCenter.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        this.imageViewCenter.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = 21)
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    imageViewCenter.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    imageViewCenter.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (f156i == 0) {
                    Bitmap unused = bWhite = Bitmap.createScaledBitmap(oraginal, oraginal.getWidth(), oraginal.getHeight(), true);
                    Bitmap unused2 = mainBitmapColor = bWhite;
                    mainImage.setImageBitmap(mainBitmapColor);
                    matriximageViewCenter = imageViewCenter.getImageViewMatrix();
                    f156i++;
                    mainImage.setImageMatrix(matriximageViewCenter);
                    if (matrix == null) {
                        Matrix unused3 = matrix = matriximageViewCenter;
                    }
                    progressBar.setVisibility(0);
                    new CountDownTimer(21000, 1000) {
                        public void onFinish() {
                        }

                        public void onTick(long j) {
                            int unused = mCount = mCount + 1;
                            if (progressBar.getProgress() <= 90) {
                                progressBar.setProgress(mCount * 5);
                            }
                        }
                    }.start();
                    new MLCropAsyncTask(new MLOnCropTaskCompleted() {
                        public void onTaskCompleted(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
                            if (bitmap != null) {
                                mCropped = bitmap;
                                mLeft = i;
                                mTop = i2;
                                f151ax = (float) mLeft;
                                f152ay = (float) mTop;
                                boolean unused = isReady = true;
                                int i3 = currentColor;
                                Color.parseColor("#FFFFFF");
                                methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
                            }
                        }
                    }, MotionEffectActivity.this, progressBar).execute(new Void[0]);
                }
            }
        });
        setStokeWidthRotate();
        setStokeWidthDensity();
        setStokeWidthOpacity();

        tv_applay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainBitmap != null) {
//                    StoreManager.setCurrentBitmap(MotionEffectActivity.this, mainBitmap);
//                    StoreManager.setCurrentEffecdedBitmap(MotionEffectActivity.this, mainBitmap);
                    new saveImageTaskMaking().execute();
                }
//                startFromMotion(MotionEffectActivity.this, false, true, getIntent().getBooleanExtra(AppConfig.ARG_FROM_COLLAGE, false), getIntent().getBooleanExtra(AppConfig.ARG_FROM_IMAGE_ACTIVITY_EDIT, false));

            }
        });
        iv_erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StickerEraseActivity.b = mCropped;
                Intent intent = new Intent(MotionEffectActivity.this, StickerEraseActivity.class);
                intent.putExtra(Constants.KEY_OPEN_FROM, Constants.VALUE_OPEN_FROM_MOTION);
                startActivityForResult(intent, 1024);
            }
        });
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showBackDialog();
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
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
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

    public static Bitmap eraserResultBmp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1024) {
            if (eraserResultBmp != null) {
                mCropped = eraserResultBmp;
                mainImage.setImageBitmap(mainBitmap);
                isReady = true;
                methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
    }

    private String oldSavedFileName = "";
    private Uri savedImageUri;

    private class saveImageTaskMaking extends android.os.AsyncTask<String, String, Exception> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Exception doInBackground(String... strings) {
            Bitmap bitmap = mainBitmap;
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
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(MotionEffectActivity.this, uri.getPath());

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
                    Uri uri = SupportedClass.addImageToGallery(MotionEffectActivity.this, file.getAbsolutePath());
                    if (uri != null)
                        savedImageUri = uri;
                    notifyMediaScannerService(MotionEffectActivity.this, myDir.getAbsolutePath());

                }
            } catch (Exception e) {
//            Exception e = new UnsupportedOperationException();
                return e;
            } finally {
                //mContentRootView.setDrawingCacheEnabled(false);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {


                FullScreenAdManager.fullScreenAdsCheckPref(MotionEffectActivity.this, FullScreenAdManager.ALL_PREFS.ATTR_ON_SHARE_SCREEN, new FullScreenAdManager.GetBackPointer() {
                    @Override
                    public void returnAction() {
                        openShareActivity();
                    }
                });


            } else {
                Toast.makeText(MotionEffectActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void openShareActivity() {
        Intent intent = new Intent(MotionEffectActivity.this, ShareActivity.class);
        intent.putExtra(Constants.KEY_URI_IMAGE, savedImageUri.toString());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }


    public void setStokeWidthRotate() {
        if (this.mStokenWidthSeekBarRotate == null) {
            this.mStokenWidthSeekBarRotate = (SeekBar) findViewById(R.id.sliderRotate);
            this.rSliderRotat = (RelativeLayout) findViewById(R.id.rSliderRotate);
            this.seekBarNameRotate = (TextView) findViewById(R.id.progressRotate);
            this.rSliderRotat.setVisibility(0);
            this.mStokenWidthSeekBarRotate.setMax(360);
            this.mStokenWidthSeekBarRotate.setProgress(30);
            this.seekBarNameRotate.setText("30");
            this.mStokenWidthSeekBarRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    TextView access$800 = seekBarNameRotate;
                    access$800.setText("" + i);
                    _rotation = Math.toRadians((double) i);
                    methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
                }
            });
        }
    }

    public void setStokeWidthDensity() {
        if (this.mStokenWidthSeekBarColor == null) {
            this.mStokenWidthSeekBarColor = (SeekBar) findViewById(R.id.sliderDensity);
            this.rSliderColor = (RelativeLayout) findViewById(R.id.rSliderDensity);
            this.seekBarNameColor = (TextView) findViewById(R.id.progressDensity);
            this.rSliderColor.setVisibility(0);
            this.mStokenWidthSeekBarColor.setMax(10);
            this.mStokenWidthSeekBarColor.setProgress(3);
            this.seekBarNameColor.setText("3");
            this.mStokenWidthSeekBarColor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    int unused = currentProgressDensity = i;
                    if (currentProgressDensity < 0) {
                        int unused2 = currentProgressDensity = 0;
                    }
                    TextView access$1000 = seekBarNameColor;
                    access$1000.setText("" + i);
                    methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
                }
            });
        }
    }

    public void setStokeWidthOpacity() {
        if (this.mStokenWidthSeekBarErase == null) {
            this.mStokenWidthSeekBarErase = (SeekBar) findViewById(R.id.sliderOpacity);
            this.rSliderErase = (RelativeLayout) findViewById(R.id.rSliderOpacity);
            this.seekBarNameErase = (TextView) findViewById(R.id.progressOpacity);
            this.rSliderErase.setVisibility(0);
            this.mStokenWidthSeekBarErase.setMax(100);
            int i = (this.currentProgressOpacity * 100) / 255;
            TextView textView = this.seekBarNameErase;
            textView.setText("" + i);
            this.mStokenWidthSeekBarErase.setProgress(i);
            this.mStokenWidthSeekBarErase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    int unused = currentProgressOpacity = (i * 255) / 100;
                    if (currentProgressOpacity < 0) {
                        int unused2 = currentProgressOpacity = 0;
                    }
                    TextView access$1200 = seekBarNameErase;
                    access$1200.setText("" + i);
                    methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
                }
            });
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        methodCropAsyncTaskPaint(motionEvent.getX(), motionEvent.getY(), 0.0f, 0.0f);
        return true;
    }

    private double getAngle(float f, float f2) {
        char c;
        if (f < ((float) this.mLeft)) {
            c = f2 > ((float) this.mTop) ? (char) 3 : 4;
        } else {
            c = f2 > ((float) this.mTop) ? (char) 2 : 1;
        }
        switch (c) {
            case 1:
                return 90.0d - Math.toDegrees(Math.atan2((double) (((float) this.mTop) - f2), (double) (f - ((float) this.mTop))));
            case 2:
                return Math.toDegrees(Math.atan2((double) (f2 - ((float) this.mTop)), (double) (f - ((float) this.mLeft)))) + 90.0d;
            case 3:
                return 270.0d - Math.toDegrees(Math.atan2((double) (f2 - ((float) this.mTop)), (double) (((float) this.mTop) - f)));
            case 4:
                return Math.toDegrees(Math.atan2((double) (((float) this.mTop) - f2), (double) (((float) this.mLeft) - f))) + 270.0d;
            default:
                return 0.0d;
        }
    }


    public boolean onTouch5(View view, MotionEvent motionEvent) {
        this._centerX = (float) this.mLeft;
        this._centerY = (float) this.mTop;
        motionEvent.getAction();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this._refX = motionEvent.getX();
            this._refY = motionEvent.getY();
            return true;
        }
        if (actionMasked == 2) {
            float x = motionEvent.getX() - this._centerX;
            float y = this._centerY - motionEvent.getY();
            if (!(x == 0.0f || y == 0.0f)) {
                double ComputeAngle = ComputeAngle(x, y);
                double ComputeAngle2 = ComputeAngle(this._refX - this._centerX, this._centerY - this._refY);
                double d = this._rotation;
                double d2 = (double) ((float) (ComputeAngle2 - ComputeAngle));
                Double.isNaN(d2);
                this._rotation = d + d2;
                methodCropAsyncTaskPaint(0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
        return true;
    }


    public double ComputeAngle(float f, float f2) {
        double atan2 = Math.atan2((double) (((float) this.mTop) - f2), (double) (f - ((float) this.mLeft)));
        return atan2 < 0.0d ? atan2 + 360.0d : atan2;
    }

    public boolean onTouch1(View view, MotionEvent motionEvent) {
        if (!this.isReady) {
            return true;
        }
        Matrix matrix2 = this.matriximageViewCenter;
        Matrix matrix3 = new Matrix();
        this.mainImage.getImageMatrix().invert(matrix3);
        float[] fArr = {motionEvent.getX(), motionEvent.getY()};
        matrix3.mapPoints(fArr);
        float f = fArr[0];
        float f2 = fArr[1];
        int i = (int) f;
        int i2 = (int) f2;
        ImageView imageView = (ImageView) view;
        Log.d("hhhhhhhhhhh3=", "" + imageView.getHeight());
        switch (motionEvent.getAction() & 255) {
            case 0:
                this.savedMatrix.set(this.matrix);
                this.start.set(motionEvent.getX(), motionEvent.getY());
                this.startMove.set((float) i, (float) i2);
                this.mode = 1;
                break;
            case 1:
            case 6:
                this.mode = 0;
                methodCropAsyncTaskPaint(f, f2, motionEvent.getX() - this.start.x, motionEvent.getY() - this.start.y);
                break;
            case 2:
                if (this.mode != 1) {
                    if (this.mode == 2) {
                        float spacing = spacing(motionEvent);
                        if (spacing > 10.0f) {
                            this.matrix.set(this.savedMatrix);
                            this.scale = spacing / this.oldDist;
                            this.f149XX = this.mid.x;
                            this.f150YY = this.mid.y;
                            this.matrix.postScale(this.scale, this.scale, this.mid.x, this.mid.y);
                            if (this.lastEvent != null) {
                                this.newRot = rotation(motionEvent);
                                this.f157r = this.newRot - this.f153d;
                                this.matrix.postRotate(this.f157r, (float) (imageView.getMeasuredWidth() / 2), (float) (imageView.getMeasuredHeight() / 2));
                            }
                            this.scale6 = motionEvent.getX(0) - motionEvent.getX(1);
                            break;
                        }
                    }
                } else {
                    this.matrix.set(this.savedMatrix);
                    this.matrix.postTranslate(motionEvent.getX() - this.start.x, motionEvent.getY() - this.start.y);
                    this.start.set(motionEvent.getX(), motionEvent.getY());
                    this.startMove.set((float) i, (float) i2);
                    break;
                }
                break;
            case 5:
                this.oldDist = spacing(motionEvent);
                this.scale6 = motionEvent.getX(0) - motionEvent.getX(1);
                this.savedMatrix.set(this.matrix);
                midPoint(this.mid, motionEvent);
                this.mode = 2;
                methodCropAsyncTaskPaint(f, f2, motionEvent.getX() - this.start.x, motionEvent.getY() - this.start.y);
                this.lastEvent = new float[4];
                this.lastEvent[0] = motionEvent.getX(0);
                this.lastEvent[1] = motionEvent.getX(1);
                this.lastEvent[2] = motionEvent.getY(0);
                this.lastEvent[3] = motionEvent.getY(1);
                this.f153d = rotation(motionEvent);
                break;
        }
        return true;
    }

    private float rotation(MotionEvent motionEvent) {
        return (float) Math.toDegrees(Math.atan2((double) (motionEvent.getY(0) - motionEvent.getY(1)), (double) (motionEvent.getX(0) - motionEvent.getX(1))));
    }

    private float spacing(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private void midPoint(PointF pointF, MotionEvent motionEvent) {
        pointF.set((motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f, (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accept_drip_menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_accept) {
            if (this.mainBitmap != null) {
                new saveImageTaskMaking().execute();
            }
        }
        return true;
    }


    public void onBackPressed() {
        showBackDialog();
    }


    public void methodCropAsyncTaskPaint(float f, float f2, float f3, float f4) {
        this.alpha = this._rotation;
        paintBitmaps();
        CropAsyncTaskPaint cropAsyncTaskPaint2 = this.cropAsyncTaskPaint;
        this.f151ax += f;
        this.f152ay += f2;
    }

    private Bitmap paintBitmaps() {
        Paint paint = null;
        if (!this.isReady) {
            return null;
        }
        Log.d("alpha=", "" + this.alpha);
        Bitmap copy = this.oraginal.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Bitmap changeAlpha = Utils.changeAlpha(this.mCropped, this.currentProgressOpacity);
        int i = this.mLeft;
        int i2 = this.mTop;
        double cos = Math.cos(this.alpha);
        double sin = Math.sin(this.alpha);
        Log.d("alphacos=", "" + cos);
        Log.d("alphasin=", "" + sin);
        if (this.currentProgressDensity > 0) {
            int dpToPx = Utils.dpToPx(180 / this.currentProgressDensity);
            int i3 = this.currentProgressDensity;
            while (i3 > 0) {
                double d = (double) this.mLeft;
                double d2 = (double) (dpToPx * i3);
                Double.isNaN(d2);
                Double.isNaN(d);
                double d3 = (double) this.mTop;
                Double.isNaN(d2);
                Double.isNaN(d3);
                canvas = canvas;
                canvas.drawBitmap(changeAlpha, (float) ((int) (d + (d2 * cos))), (float) ((int) (d3 - (d2 * sin))), (Paint) null);
                i3--;
                paint = null;
            }
        }
        canvas.drawBitmap(this.mCropped, (float) this.mLeft, (float) this.mTop, paint);
        this.mainBitmap = copy;
        this.mainImage.setImageBitmap(this.mainBitmap);
        return copy;
    }
}
