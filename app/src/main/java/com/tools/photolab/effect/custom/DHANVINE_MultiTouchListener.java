package com.tools.photolab.effect.custom;

import android.app.Activity;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.tools.photolab.effect.activity.FramesActivity;
import com.tools.photolab.effect.activity.NeonActivity;
import com.tools.photolab.effect.custom.DHANVINE_ScaleGestureDetector.OnScaleGestureListener;
import com.tools.photolab.effect.custom.DHANVINE_ScaleGestureDetector.SimpleOnScaleGestureListener;
import com.tools.photolab.effect.activity.WingsActivity;

public class DHANVINE_MultiTouchListener implements OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    Boolean forspiral;
    public boolean isRotateEnabled;
    public boolean isScaleEnabled;
    public boolean isTranslateEnabled;
    private int mActivePointerId;
    private float mPrevX;
    private float mPrevY;
    private DHANVINE_ScaleGestureDetector mScaleGestureDetector;
    public float maximumScale;
    public float minimumScale;
    private Rect rect;
    OnRotateListner rotateListner;

    public interface OnRotateListner {
        float getRotation(float f);
    }

    private class ScaleGestureListener extends SimpleOnScaleGestureListener implements OnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private DHANVINE_Vector2D mPrevSpanVector;

        public void onScaleEnd(View view, DHANVINE_ScaleGestureDetector dHANVINE_ScaleGestureDetector) {
        }

        private ScaleGestureListener() {
            this.mPrevSpanVector = new DHANVINE_Vector2D(0.0f, 0.0f);
        }

        public boolean onScaleBegin(View view, DHANVINE_ScaleGestureDetector dHANVINE_ScaleGestureDetector) {
            this.mPivotX = dHANVINE_ScaleGestureDetector.getFocusX();
            this.mPivotY = dHANVINE_ScaleGestureDetector.getFocusY();
            this.mPrevSpanVector.set(dHANVINE_ScaleGestureDetector.getCurrentSpanVector());
            return true;
        }

        public boolean onScale(View view, DHANVINE_ScaleGestureDetector dHANVINE_ScaleGestureDetector) {
            TransformInfo transformInfo = new TransformInfo();
            transformInfo.deltaScale = DHANVINE_MultiTouchListener.this.isScaleEnabled ? dHANVINE_ScaleGestureDetector.getScaleFactor() : 1.0f;
            float f = 0.0f;
            transformInfo.deltaAngle = DHANVINE_MultiTouchListener.this.isRotateEnabled ? DHANVINE_Vector2D.getAngle(this.mPrevSpanVector, dHANVINE_ScaleGestureDetector.getCurrentSpanVector()) : 0.0f;
            transformInfo.deltaX = DHANVINE_MultiTouchListener.this.isTranslateEnabled ? dHANVINE_ScaleGestureDetector.getFocusX() - this.mPivotX : 0.0f;
            if (DHANVINE_MultiTouchListener.this.isTranslateEnabled) {
                f = dHANVINE_ScaleGestureDetector.getFocusY() - this.mPivotY;
            }
            transformInfo.deltaY = f;
            transformInfo.pivotX = this.mPivotX;
            transformInfo.pivotY = this.mPivotY;
            transformInfo.minimumScale = DHANVINE_MultiTouchListener.this.minimumScale;
            transformInfo.maximumScale = DHANVINE_MultiTouchListener.this.maximumScale;
            DHANVINE_MultiTouchListener.this.move(view, transformInfo);
            return false;
        }
    }

    private class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;

        private TransformInfo() {
        }
    }

    private static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    public DHANVINE_MultiTouchListener(boolean z, boolean z2, boolean z3) {
        this.forspiral = Boolean.FALSE;
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.isRotateEnabled = z;
        this.isScaleEnabled = z3;
        this.isTranslateEnabled = z2;
        this.mScaleGestureDetector = new DHANVINE_ScaleGestureDetector(new ScaleGestureListener());
    }

    public DHANVINE_MultiTouchListener(boolean z, boolean z2, boolean z3, OnRotateListner onRotateListner) {
        this.forspiral = Boolean.FALSE;
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.isRotateEnabled = z;
        this.isScaleEnabled = z3;
        this.isTranslateEnabled = z2;
        this.mScaleGestureDetector = new DHANVINE_ScaleGestureDetector(new ScaleGestureListener());
        this.rotateListner = onRotateListner;
    }

    public DHANVINE_MultiTouchListener(Activity activity , Boolean bool) {
        this.mActivity = activity;
        this.forspiral = false;
        this.forspiral = bool;
        this.isRotateEnabled = true;
        this.isTranslateEnabled = true;
        this.isScaleEnabled = true;
        this.maximumScale = 10.0f;
        this.mActivePointerId = -1;
        this.mScaleGestureDetector = new DHANVINE_ScaleGestureDetector(new ScaleGestureListener());
    }
    Activity mActivity ;

    public void move(View view, TransformInfo transformInfo) {
        computeRenderOffset(view, transformInfo.pivotX, transformInfo.pivotY);
        adjustTranslation(view, transformInfo.deltaX, transformInfo.deltaY);
        float max = Math.max(transformInfo.minimumScale, Math.min(transformInfo.maximumScale, view.getScaleX() * transformInfo.deltaScale));
        view.setScaleX(max);
        view.setScaleY(max);
        view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
    }

    private static void adjustTranslation(View view, float f, float f2) {
        float[] fArr = {f, f2};
        view.getMatrix().mapVectors(fArr);
        view.setTranslationX(view.getTranslationX() + fArr[0]);
        view.setTranslationY(view.getTranslationY() + fArr[1]);
    }

    private static void computeRenderOffset(View view, float f, float f2) {
        if (view.getPivotX() != f || view.getPivotY() != f2) {
            float[] fArr = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr);
            view.setPivotX(f);
            view.setPivotY(f2);
            float[] fArr2 = {0.0f, 0.0f};
            view.getMatrix().mapPoints(fArr2);
            float f3 = fArr2[1] - fArr[1];
            view.setTranslationX(view.getTranslationX() - (fArr2[0] - fArr[0]));
            view.setTranslationY(view.getTranslationY() - f3);
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (forspiral) {
            if(mActivity instanceof NeonActivity){
                onTouch2(NeonActivity.setfront, motionEvent);
            }else if(mActivity instanceof WingsActivity){
                onTouch2(WingsActivity.setfront, motionEvent);
            }else if(mActivity instanceof FramesActivity){
                onTouch2(FramesActivity.setfront, motionEvent);
            }
        }
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (this.isTranslateEnabled) {
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            int i = 0;
            if (actionMasked == 6) {
                int i2 = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i2) == this.mActivePointerId) {
                    if (i2 == 0) {
                        i = 1;
                    }
                    this.mPrevX = motionEvent.getX(i);
                    this.mPrevY = motionEvent.getY(i);
                    this.mActivePointerId = motionEvent.getPointerId(i);
                }
            } else if (actionMasked == 0) {
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            }
        }
        return true;
    }

    public boolean onTouch2(View view, MotionEvent motionEvent) {
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        if (this.isTranslateEnabled) {
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            int i = 0;
            if (actionMasked == 6) {
                int i2 = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i2) == this.mActivePointerId) {
                    if (i2 == 0) {
                        i = 1;
                    }
                    this.mPrevX = motionEvent.getX(i);
                    this.mPrevY = motionEvent.getY(i);
                    this.mActivePointerId = motionEvent.getPointerId(i);
                }
            } else if (actionMasked == 0) {
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            }
        }
        return true;
    }
}
