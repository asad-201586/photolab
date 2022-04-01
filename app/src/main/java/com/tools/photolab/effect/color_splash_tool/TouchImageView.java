package com.tools.photolab.effect.color_splash_tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import androidx.core.internal.view.SupportMenu;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class TouchImageView extends ImageView {
    static final int CLICK = 3;
    static final int DRAG = 1;
    static final int NONE = 0;
    static final int ZOOM = 2;
    static final int ZOOMDRAG = 3;
    static float resRatio;
    BitmapShader bitmapShader;
    Path brushPath;
    Canvas canvas;
    Canvas canvasPreview;
    Paint circlePaint;
    Path circlePath;
    int coloring = -1;
    Context context;
    PointF curr = new PointF();
    int currentImageIndex = 0;
    boolean draw = false;
    Paint drawPaint;
    Path drawPath;
    Bitmap drawingBitmap;
    Rect dstRect;
    PointF last = new PointF();
    Paint logPaintColor;
    Paint logPaintGray;
    float[] m;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale = 5.0f;
    float minScale = 1.0f;
    int mode = 0;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    float oldX = 0.0f;
    float oldY = 0.0f;
    boolean onMeasureCalled = false;
    int opacity = 240;
    protected float origHeight;
    protected float origWidth;
    int pCount1 = -1;
    int pCount2 = -1;
    ArrayList<PointF> pathPoints;
    public boolean prViewDefaultPosition;
    Paint previewPaint;
    float radius = 150.0f;
    float saveScale = 1.0f;
    Bitmap splashBitmap;
    PointF start = new PointF();
    Paint tempPaint;
    Bitmap tempPreviewBitmap;
    int viewHeight;
    int viewWidth;
    float x;
    float y;

    private class MyAnimationListener implements AnimationListener {
        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

        private MyAnimationListener() {
        }

        public void onAnimationEnd(Animation animation) {
            if (TouchImageView.this.prViewDefaultPosition) {
                LayoutParams layoutParams = new LayoutParams(ColorSplashActivity.prView.getWidth(), ColorSplashActivity.prView.getHeight());
                layoutParams.setMargins(0, 0, 0, 0);
                ColorSplashActivity.prView.setLayoutParams(layoutParams);
                return;
            }
            LayoutParams layoutParams2 = new LayoutParams(ColorSplashActivity.prView.getWidth(), ColorSplashActivity.prView.getHeight());
            layoutParams2.setMargins(0, TouchImageView.this.viewHeight - ColorSplashActivity.prView.getWidth(), 0, 0);
            ColorSplashActivity.prView.setLayoutParams(layoutParams2);
        }
    }

    private class SaveCanvasLog extends AsyncTask<String, Integer, String> {
        private SaveCanvasLog() {
        }


        public void onPreExecute() {
            super.onPreExecute();
        }


        public String doInBackground(String... strArr) {
            TouchImageView.this.currentImageIndex++;
            StringBuilder sb = new StringBuilder();
            String str = "canvasLog";
            sb.append(str);
            sb.append(TouchImageView.this.currentImageIndex);
            String str2 = ".jpg";
            sb.append(str2);
            File file = new File(ColorSplashActivity.tempDrawPathFile, sb.toString());
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                TouchImageView.this.drawingBitmap.compress(CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TouchImageView.this.currentImageIndex > 5) {
                int i = TouchImageView.this.currentImageIndex - 5;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(i);
                sb2.append(str2);
                File file2 = new File(ColorSplashActivity.tempDrawPathFile, sb2.toString());
                if (file2.exists()) {
                    file2.delete();
                }
            }
            return "this string is passed to onPostExecute";
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(String str) {
            super.onPostExecute(str);
        }
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            ColorSplashActivity.prView.setVisibility(View.INVISIBLE);
            if (TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3) {
                TouchImageView.this.mode = 3;
            } else {
                TouchImageView.this.mode = 2;
            }
            TouchImageView.this.draw = false;
            return true;
        }

        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float f = TouchImageView.this.saveScale;
            TouchImageView.this.saveScale *= scaleFactor;
            if (TouchImageView.this.saveScale > TouchImageView.this.maxScale) {
                TouchImageView touchImageView = TouchImageView.this;
                touchImageView.saveScale = touchImageView.maxScale;
                scaleFactor = TouchImageView.this.maxScale / f;
            } else {
                float f2 = TouchImageView.this.saveScale;
                float f3 = TouchImageView.this.minScale;
            }
            if (TouchImageView.this.origWidth * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewWidth) || TouchImageView.this.origHeight * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewHeight)) {
                TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, (float) (TouchImageView.this.viewWidth / 2), (float) (TouchImageView.this.viewHeight / 2));
            } else {
                TouchImageView.this.matrix.postScale(scaleFactor, scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
            }
            TouchImageView.this.matrix.getValues(TouchImageView.this.m);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            TouchImageView.this.radius = ((float) (ColorSplashActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale;
            ColorSplashActivity.brushView.setShapeRadiusRatio(((float) (ColorSplashActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale);
            TouchImageView.this.updatePreviewPaint();
        }
    }


    public float getFixTrans(float f, float f2, float f3) {
        float f4;
        float f5;
        if (f3 <= f2) {
            f4 = f2 - f3;
            f5 = 0.0f;
        } else {
            f5 = f2 - f3;
            f4 = 0.0f;
        }
        if (f < f5) {
            return (-f) + f5;
        }
        if (f > f4) {
            return (-f) + f4;
        }
        return 0.0f;
    }

    public TouchImageView(Context context2) {
        super(context2);
        this.context = context2;
        sharedConstructing(context2);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public TouchImageView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        this.context = context2;
        sharedConstructing(context2);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }


    public void initDrawing() {
        this.splashBitmap = ColorSplashActivity.colorBitmap;
        this.drawingBitmap = Bitmap.createBitmap(ColorSplashActivity.grayBitmap);
        setImageBitmap(this.drawingBitmap);
        this.canvas = new Canvas(this.drawingBitmap);
        this.circlePath = new Path();
        this.drawPath = new Path();
        this.brushPath = new Path();
        this.circlePaint = new Paint(1);
        this.circlePaint.setColor(SupportMenu.CATEGORY_MASK);
        this.circlePaint.setStyle(Style.STROKE);
        this.circlePaint.setStrokeWidth(5.0f);
        this.drawPaint = new Paint(1);
        this.drawPaint.setStyle(Style.STROKE);
        this.drawPaint.setStrokeWidth(this.radius);
        this.drawPaint.setStrokeCap(Cap.ROUND);
        this.drawPaint.setStrokeJoin(Join.ROUND);
        setLayerType(1, null);
        this.tempPaint = new Paint();
        this.tempPaint.setStyle(Style.FILL);
        this.tempPaint.setColor(-1);
        this.previewPaint = new Paint();
        this.previewPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        this.tempPreviewBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        this.canvasPreview = new Canvas(this.tempPreviewBitmap);
        this.dstRect = new Rect(0, 0, 100, 100);
        this.logPaintGray = new Paint(this.drawPaint);
        this.logPaintGray.setShader(new BitmapShader(ColorSplashActivity.grayBitmap, TileMode.CLAMP, TileMode.CLAMP));
        this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.drawPaint.setShader(this.bitmapShader);
        this.logPaintColor = new Paint(this.drawPaint);
        new SaveCanvasLog().execute(new String[0]);
    }


    public void updatePaintBrush() {
        try {
            this.drawPaint.setStrokeWidth(this.radius * resRatio);
            this.drawPaint.setAlpha(this.opacity);
        } catch (Exception unused) {
        }
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updatePreviewPaint();
    }


    public void changeShaderBitmap() {
        this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.drawPaint.setShader(this.bitmapShader);
        updatePreviewPaint();
    }


    public void updatePreviewPaint() {
        if (ColorSplashActivity.colorBitmap.getWidth() > ColorSplashActivity.colorBitmap.getHeight()) {
            resRatio = ((float) ColorSplashActivity.displayWidth) / ((float) ColorSplashActivity.colorBitmap.getWidth());
            resRatio *= this.saveScale;
        } else {
            resRatio = this.origHeight / ((float) ColorSplashActivity.colorBitmap.getHeight());
            resRatio *= this.saveScale;
        }
        this.drawPaint.setStrokeWidth(this.radius * resRatio);
        this.drawPaint.setMaskFilter(new BlurMaskFilter(resRatio * 15.0f, Blur.NORMAL));
        this.drawPaint.getShader().setLocalMatrix(this.matrix);
    }

    private void sharedConstructing(Context context2) {
        super.setClickable(true);
        this.context = context2;
        this.mScaleDetector = new ScaleGestureDetector(context2, new ScaleListener());
        this.matrix = new Matrix();
        this.m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TouchImageView.this.mScaleDetector.onTouchEvent(motionEvent);
                TouchImageView.this.pCount2 = motionEvent.getPointerCount();
                TouchImageView.this.curr = new PointF(motionEvent.getX(), motionEvent.getY() - (((float) ColorSplashActivity.offsetBar.getProgress()) * 3.0f));
                TouchImageView touchImageView = TouchImageView.this;
                touchImageView.x = (touchImageView.curr.x - TouchImageView.this.m[2]) / TouchImageView.this.m[0];
                TouchImageView touchImageView2 = TouchImageView.this;
                touchImageView2.y = (touchImageView2.curr.y - TouchImageView.this.m[5]) / TouchImageView.this.m[4];
                int action = motionEvent.getAction();
                if (action != 6) {
                    if (action == 0) {
                        TouchImageView.this.drawPaint.setStrokeWidth(TouchImageView.this.radius * TouchImageView.resRatio);
                        TouchImageView.this.drawPaint.setMaskFilter(new BlurMaskFilter(TouchImageView.resRatio * 15.0f, Blur.NORMAL));
                        TouchImageView.this.drawPaint.getShader().setLocalMatrix(TouchImageView.this.matrix);
                        TouchImageView touchImageView3 = TouchImageView.this;
                        touchImageView3.oldX = 0.0f;
                        touchImageView3.oldY = 0.0f;
                        touchImageView3.last.set(TouchImageView.this.curr);
                        TouchImageView.this.start.set(TouchImageView.this.last);
                        if (!(TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3)) {
                            TouchImageView.this.draw = true;
                            ColorSplashActivity.prView.setVisibility(View.VISIBLE);
                        }
                        TouchImageView.this.circlePath.reset();
                        TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                        TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / 2.0f, Direction.CW);
                        TouchImageView.this.pathPoints = new ArrayList<>();
                        TouchImageView.this.pathPoints.add(new PointF(TouchImageView.this.x, TouchImageView.this.y));
                        TouchImageView.this.drawPath.moveTo(TouchImageView.this.x, TouchImageView.this.y);
                        TouchImageView.this.brushPath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                    } else if (action == 1) {
                        if (TouchImageView.this.mode == 1) {
                            TouchImageView.this.matrix.getValues(TouchImageView.this.m);
                        }
                        int abs = (int) Math.abs(TouchImageView.this.curr.y - TouchImageView.this.start.y);
                        if (((int) Math.abs(TouchImageView.this.curr.x - TouchImageView.this.start.x)) < 3 && abs < 3) {
                            TouchImageView.this.performClick();
                        }
                        if (TouchImageView.this.draw) {
                            TouchImageView.this.drawPaint.setStrokeWidth(TouchImageView.this.radius);
                            TouchImageView.this.drawPaint.setMaskFilter(new BlurMaskFilter(15.0f, Blur.NORMAL));
                            TouchImageView.this.drawPaint.getShader().setLocalMatrix(new Matrix());
                            TouchImageView.this.canvas.drawPath(TouchImageView.this.drawPath, TouchImageView.this.drawPaint);
                            new SaveCanvasLog().execute(new String[0]);
                        }
                        ColorSplashActivity.prView.setVisibility(View.INVISIBLE);
                        ColorSplashActivity.vector.add(new MyPath(TouchImageView.this.pathPoints, TouchImageView.this.coloring, TouchImageView.this.radius));
                        TouchImageView.this.circlePath.reset();
                        TouchImageView.this.drawPath.reset();
                        TouchImageView.this.brushPath.reset();
                        TouchImageView.this.draw = false;
                    } else if (action == 2) {
                        if (TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3 || !TouchImageView.this.draw) {
                            if (TouchImageView.this.pCount1 == 1 && TouchImageView.this.pCount2 == 1) {
                                TouchImageView.this.matrix.postTranslate(TouchImageView.this.curr.x - TouchImageView.this.last.x, TouchImageView.this.curr.y - TouchImageView.this.last.y);
                            }
                            TouchImageView.this.last.set(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                        } else {
                            TouchImageView.this.circlePath.reset();
                            TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                            TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / 2.0f, Direction.CW);
                            TouchImageView.this.pathPoints.add(new PointF(TouchImageView.this.x, TouchImageView.this.y));
                            TouchImageView.this.drawPath.lineTo(TouchImageView.this.x, TouchImageView.this.y);
                            TouchImageView.this.brushPath.lineTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                            TouchImageView.this.showBoxPreview();
                            double width = (double) ColorSplashActivity.prView.getWidth();
                            Double.isNaN(width);
                            Double.isNaN(width);
                            int i = (int) (width * 1.3d);
                            float f = (float) i;
                            if ((TouchImageView.this.curr.x > f || TouchImageView.this.curr.y > f || !TouchImageView.this.prViewDefaultPosition) && TouchImageView.this.curr.x <= f && TouchImageView.this.curr.y >= ((float) (TouchImageView.this.viewHeight - i)) && !TouchImageView.this.prViewDefaultPosition) {
                                TouchImageView touchImageView4 = TouchImageView.this;
                                touchImageView4.prViewDefaultPosition = true;
                                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-(touchImageView4.viewHeight - ColorSplashActivity.prView.getWidth())));
                                translateAnimation.setDuration(500);
                                translateAnimation.setFillAfter(false);
                                translateAnimation.setAnimationListener(new MyAnimationListener());
                                ColorSplashActivity.prView.startAnimation(translateAnimation);
                            } else {
                                TouchImageView touchImageView5 = TouchImageView.this;
                                touchImageView5.prViewDefaultPosition = false;
                                TranslateAnimation translateAnimation2 = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (touchImageView5.viewHeight - ColorSplashActivity.prView.getWidth()));
                                translateAnimation2.setDuration(500);
                                translateAnimation2.setFillAfter(false);
                                translateAnimation2.setAnimationListener(new MyAnimationListener());
                                ColorSplashActivity.prView.startAnimation(translateAnimation2);
                            }
                        }
                    }
                } else if (TouchImageView.this.mode == 2) {
                    TouchImageView.this.mode = 0;
                }
                TouchImageView touchImageView6 = TouchImageView.this;
                touchImageView6.pCount1 = touchImageView6.pCount2;
                TouchImageView touchImageView7 = TouchImageView.this;
                touchImageView7.setImageMatrix(touchImageView7.matrix);
                TouchImageView.this.invalidate();
                return true;
            }
        });
    }


    public void updateRefMetrix() {
        this.matrix.getValues(this.m);
    }


    public void showBoxPreview() {
        buildDrawingCache();
        try {
            Bitmap createBitmap = Bitmap.createBitmap(getDrawingCache());
            this.canvasPreview.drawRect(this.dstRect, this.tempPaint);
            this.canvasPreview.drawBitmap(createBitmap, new Rect(((int) this.curr.x) - 100, ((int) this.curr.y) - 100, ((int) this.curr.x) + 100, ((int) this.curr.y) + 100), this.dstRect, this.previewPaint);
            ColorSplashActivity.prView.setImageBitmap(this.tempPreviewBitmap);
            destroyDrawingCache();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void onDraw(Canvas canvas2) {
        float[] fArr = new float[9];
        this.matrix.getValues(fArr);
        int i = (int) fArr[2];
        int i2 = (int) fArr[5];
        super.onDraw(canvas2);
        float f = (float) i2;
        float f2 = this.origHeight;
        float f3 = this.saveScale;
        float f4 = (f2 * f3) + f;
        if (i2 < 0) {
            float f5 = (float) i;
            float f6 = (this.origWidth * f3) + f5;
            int i3 = this.viewHeight;
            if (f4 > ((float) i3)) {
                f4 = (float) i3;
            }
            canvas2.clipRect(f5, 0.0f, f6, f4);
        } else {
            float f7 = (float) i;
            float f8 = (this.origWidth * f3) + f7;
            int i4 = this.viewHeight;
            if (f4 > ((float) i4)) {
                f4 = (float) i4;
            }
            canvas2.clipRect(f7, f, f8, f4);
        }
        if (this.draw) {
            canvas2.drawPath(this.brushPath, this.drawPaint);
            canvas2.drawPath(this.circlePath, this.circlePaint);
        }
    }


    public void fixTrans() {
        this.matrix.getValues(this.m);
        float[] fArr = this.m;
        float f = fArr[2];
        float f2 = fArr[5];
        float fixTrans = getFixTrans(f, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTrans2 = getFixTrans(f2, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (!(fixTrans == 0.0f && fixTrans2 == 0.0f)) {
            this.matrix.postTranslate(fixTrans, fixTrans2);
        }
        this.matrix.getValues(this.m);
        updatePreviewPaint();
    }


    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (!this.onMeasureCalled) {
            Log.wtf("OnMeasured Call :", "OnMeasured Call");
            this.viewWidth = MeasureSpec.getSize(i);
            this.viewHeight = MeasureSpec.getSize(i2);
            int i3 = this.oldMeasuredHeight;
            if (i3 != this.viewWidth || i3 != this.viewHeight) {
                int i4 = this.viewWidth;
                if (i4 != 0) {
                    int i5 = this.viewHeight;
                    if (i5 != 0) {
                        this.oldMeasuredHeight = i5;
                        this.oldMeasuredWidth = i4;
                        if (this.saveScale == 1.0f) {
                            fitScreen();
                        }
                        this.onMeasureCalled = true;
                    }
                }
            }
        }
    }


    public void fitScreen() {
        Drawable drawable = getDrawable();
        if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            StringBuilder sb = new StringBuilder();
            sb.append("bmWidth: ");
            sb.append(intrinsicWidth);
            sb.append(" bmHeight : ");
            sb.append(intrinsicHeight);
            Log.d("bmSize", sb.toString());
            float f = (float) intrinsicWidth;
            float f2 = (float) intrinsicHeight;
            float min = Math.min(((float) this.viewWidth) / f, ((float) this.viewHeight) / f2);
            this.matrix.setScale(min, min);
            float f3 = (((float) this.viewHeight) - (f2 * min)) / 2.0f;
            float f4 = (((float) this.viewWidth) - (min * f)) / 2.0f;
            this.matrix.postTranslate(f4, f3);
            this.origWidth = ((float) this.viewWidth) - (f4 * 2.0f);
            this.origHeight = ((float) this.viewHeight) - (f3 * 2.0f);
            setImageMatrix(this.matrix);
            this.matrix.getValues(this.m);
            fixTrans();
        }
    }
}
