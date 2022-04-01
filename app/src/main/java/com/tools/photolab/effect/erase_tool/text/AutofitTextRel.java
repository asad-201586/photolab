package com.tools.photolab.effect.erase_tool.text;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.tools.photolab.R;
import com.tools.photolab.effect.erase_tool.AutoResizeTextView;
import com.tools.photolab.effect.erase_tool.text.MultiTouchListener.TouchCallbackListener;


public class AutofitTextRel extends RelativeLayout implements TouchCallbackListener {
    private static final String TAG = "AutofitTextRel";
    double angle = 0.0d;

    public ImageView background_iv;
    int baseh;
    int basew;
    int basex;
    int basey;
    private int bgAlpha = 255;
    private int bgColor = 0;

    public String bgDrawable = "0";
    private ImageView border_iv;
    float cX = 0.0f;
    float cY = 0.0f;
    private int capitalFlage;
    private Context context;
    int currentState;
    double dAngle = 0.0d;
    private ImageView delete_iv;

    public int f27s;
    private String field_four;
    private int field_one;
    private String field_three;

    public String field_two;
    private String fontName;
    private GestureDetector gd;

    public int he;
    int height;
    float heightMain = 0.0f;
    private boolean isBold;
    private boolean isBorderVisible;
    private boolean isItalic;
    public boolean isMultiTouchEnabled = true;
    private boolean isUnderLine;
    boolean isUndoRedo;

    public int leftMargin;
    private float leftRightShadow;

    public TouchEventListener listener;
    private OnTouchListener mTouchListener1;
    int margl;
    int margt;
    private int outercolor;
    private int outersize;
    private int progress;
    private OnTouchListener rTouchListener;
    float ratio;
    private ImageView rotate_iv;
    private float rotation;
    Animation scale;
    private ImageView scale_iv;
    int sh = 1794;
    private int shadowColor;
    private int shadowColorProgress;
    private int shadowProg;
    int sw = 1080;
    private int tAlpha;
    double tAngle = 0.0d;
    private int tColor;
    private String text;
    private Path textPath;

    public AutoResizeTextView text_iv;
    private float topBottomShadow;

    public int topMargin;
    double vAngle = 0.0d;

    public int wi;
    int width;
    float widthMain = 0.0f;
    private int xRotateProg;
    private int yRotateProg;
    private int zRotateProg;
    Animation zoomInScale;
    Animation zoomOutScale;

    class SimpleListner extends SimpleOnGestureListener {
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return true;
        }

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        SimpleListner() {
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (listener != null) {
                listener.onDoubleTap();
            }
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            super.onLongPress(motionEvent);
        }
    }

    class Touch implements OnTouchListener {
        Touch() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            LayoutParams layoutParams = (LayoutParams) getLayoutParams();
            int action = motionEvent.getAction();
            if (action == 0) {
                if (autofitTextRel != null) {
                    autofitTextRel.requestDisallowInterceptTouchEvent(true);
                }
                if (listener != null) {
                    listener.onScaleDown(AutofitTextRel.this);
                }
                invalidate();
                AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
                autofitTextRel2.basex = rawX;
                autofitTextRel2.basey = rawY;
                autofitTextRel2.basew = autofitTextRel2.getWidth();
                AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
                autofitTextRel3.baseh = autofitTextRel3.getHeight();
                getLocationOnScreen(new int[2]);
                margl = layoutParams.leftMargin;
                margt = layoutParams.topMargin;
                currentState = 0;
            } else if (action == 1) {
                AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                autofitTextRel4.wi = autofitTextRel4.getLayoutParams().width;
                AutofitTextRel autofitTextRel5 = AutofitTextRel.this;
                autofitTextRel5.he = autofitTextRel5.getLayoutParams().height;
                AutofitTextRel autofitTextRel6 = AutofitTextRel.this;
                autofitTextRel6.leftMargin = ((LayoutParams) autofitTextRel6.getLayoutParams()).leftMargin;
                AutofitTextRel autofitTextRel7 = AutofitTextRel.this;
                autofitTextRel7.topMargin = ((LayoutParams) autofitTextRel7.getLayoutParams()).topMargin;
                AutofitTextRel autofitTextRel8 = AutofitTextRel.this;
                StringBuilder sb = new StringBuilder();
                sb.append(String.valueOf(leftMargin));
                sb.append(",");
                sb.append(String.valueOf(topMargin));
                autofitTextRel8.field_two = sb.toString();
                if (currentState == 3) {
                    clickToSaveWork();
                }
                AutofitTextRel autofitTextRel9 = AutofitTextRel.this;
                autofitTextRel9.currentState = 2;
                if (autofitTextRel9.listener != null) {
                    listener.onScaleUp(AutofitTextRel.this);
                }
            } else if (action == 2) {
                if (autofitTextRel != null) {
                    autofitTextRel.requestDisallowInterceptTouchEvent(true);
                }
                if (listener != null) {
                    listener.onScaleMove(AutofitTextRel.this);
                }
                float degrees = (float) Math.toDegrees(Math.atan2((double) (rawY - basey), (double) (rawX - basex)));
                if (degrees < 0.0f) {
                    degrees += 360.0f;
                }
                int i = rawX - basex;
                int i2 = rawY - basey;
                int i3 = i2 * i2;
                int sqrt = (int) (Math.sqrt((double) ((i * i) + i3)) * Math.cos(Math.toRadians((double) (degrees - getRotation()))));
                int sqrt2 = (int) (Math.sqrt((double) ((sqrt * sqrt) + i3)) * Math.sin(Math.toRadians((double) (degrees - getRotation()))));
                int i4 = (sqrt * 2) + basew;
                int i5 = (sqrt2 * 2) + baseh;
                if (i4 > f27s) {
                    layoutParams.width = i4;
                    layoutParams.leftMargin = margl - sqrt;
                }
                if (i5 > f27s) {
                    layoutParams.height = i5;
                    layoutParams.topMargin = margt - sqrt2;
                }
                setLayoutParams(layoutParams);
                AutofitTextRel autofitTextRel10 = AutofitTextRel.this;
                autofitTextRel10.currentState = 3;
                if (!autofitTextRel10.bgDrawable.equals("0")) {
                    AutofitTextRel autofitTextRel11 = AutofitTextRel.this;
                    autofitTextRel11.wi = autofitTextRel11.getLayoutParams().width;
                    AutofitTextRel autofitTextRel12 = AutofitTextRel.this;
                    autofitTextRel12.he = autofitTextRel12.getLayoutParams().height;
                    AutofitTextRel autofitTextRel13 = AutofitTextRel.this;
                    autofitTextRel13.setBgDrawable(autofitTextRel13.bgDrawable);
                }
            }
            return true;
        }
    }

    public interface TouchEventListener {
        void onDelete();

        void onDoubleTap();

        void onEdit(View view, Uri uri);

        void onRotateDown(View view);

        void onRotateMove(View view);

        void onRotateUp(View view);

        void onScaleDown(View view);

        void onScaleMove(View view);

        void onScaleUp(View view);

        void onTouchDown(View view);

        void onTouchMove(View view);

        void onTouchMoveUpClick(View view);

        void onTouchUp(View view);
    }

    public void setViewWH(float f, float f2) {
        this.widthMain = f;
        this.heightMain = f2;
    }

    public float getMainWidth() {
        return this.widthMain;
    }

    public float getMainHeight() {
        return this.heightMain;
    }

    public AutofitTextRel(Context context2) {
        super(context2);
        String str = "";
        this.field_four = str;
        this.field_one = 0;
        this.field_three = str;
        this.field_two = "0,0";
        this.fontName = str;
        this.gd = null;
        this.isBorderVisible = false;
        this.isItalic = false;
        this.isUnderLine = false;
        this.leftMargin = 0;
        this.leftRightShadow = 0.0f;
        this.listener = null;
        this.mTouchListener1 = new Touch();
        this.progress = 0;
        this.rTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
                int action = motionEvent.getAction();
                if (action == 0) {
                    if (autofitTextRel != null) {
                        autofitTextRel.requestDisallowInterceptTouchEvent(true);
                    }
                    if (listener != null) {
                        listener.onRotateDown(AutofitTextRel.this);
                    }
                    Rect rect = new Rect();
                    ((View) view.getParent()).getGlobalVisibleRect(rect);
                    cX = rect.exactCenterX();
                    cY = rect.exactCenterY();
                    vAngle = (double) ((View) view.getParent()).getRotation();
                    AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
                    autofitTextRel2.tAngle = (Math.atan2((double) (autofitTextRel2.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                    AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
                    autofitTextRel3.dAngle = autofitTextRel3.vAngle - tAngle;
                    currentState = 1;
                } else if (action != 1) {
                    if (action == 2) {
                        if (autofitTextRel != null) {
                            autofitTextRel.requestDisallowInterceptTouchEvent(true);
                        }
                        if (listener != null) {
                            listener.onRotateMove(AutofitTextRel.this);
                        }
                        AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                        autofitTextRel4.angle = (Math.atan2((double) (autofitTextRel4.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                        ((View) view.getParent()).setRotation((float) (angle + dAngle));
                        ((View) view.getParent()).invalidate();
                        ((View) view.getParent()).requestLayout();
                        currentState = 3;
                    }
                } else if (listener != null) {
                    listener.onRotateUp(AutofitTextRel.this);
                    if (currentState == 3) {
                        clickToSaveWork();
                    }
                    currentState = 2;
                }
                return true;
            }
        };
        this.shadowColor = 0;
        this.shadowColorProgress = 255;
        this.shadowProg = 0;
        this.tAlpha = 100;
        this.tColor = ViewCompat.MEASURED_STATE_MASK;
        this.text = str;
        this.topBottomShadow = 0.0f;
        this.topMargin = 0;
        this.xRotateProg = 0;
        this.yRotateProg = 0;
        this.zRotateProg = 0;
        this.outersize = 0;
        this.outercolor = 0;
        this.isUndoRedo = false;
        this.currentState = 0;
        init(context2);
    }

    public AutofitTextRel(Context context2, boolean z) {
        super(context2);
        String str = "";
        this.field_four = str;
        this.field_one = 0;
        this.field_three = str;
        this.field_two = "0,0";
        this.fontName = str;
        this.gd = null;
        this.isBorderVisible = false;
        this.isItalic = false;
        this.isUnderLine = false;
        this.leftMargin = 0;
        this.leftRightShadow = 0.0f;
        this.listener = null;
        this.mTouchListener1 = new Touch();
        this.progress = 0;
        this.rTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
                int action = motionEvent.getAction();
                if (action == 0) {
                    if (autofitTextRel != null) {
                        autofitTextRel.requestDisallowInterceptTouchEvent(true);
                    }
                    if (listener != null) {
                        listener.onRotateDown(AutofitTextRel.this);
                    }
                    Rect rect = new Rect();
                    ((View) view.getParent()).getGlobalVisibleRect(rect);
                    cX = rect.exactCenterX();
                    cY = rect.exactCenterY();
                    vAngle = (double) ((View) view.getParent()).getRotation();
                    AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
                    autofitTextRel2.tAngle = (Math.atan2((double) (autofitTextRel2.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                    AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
                    autofitTextRel3.dAngle = autofitTextRel3.vAngle - tAngle;
                    currentState = 1;
                } else if (action != 1) {
                    if (action == 2) {
                        if (autofitTextRel != null) {
                            autofitTextRel.requestDisallowInterceptTouchEvent(true);
                        }
                        if (listener != null) {
                            listener.onRotateMove(AutofitTextRel.this);
                        }
                        AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                        autofitTextRel4.angle = (Math.atan2((double) (autofitTextRel4.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                        ((View) view.getParent()).setRotation((float) (angle + dAngle));
                        ((View) view.getParent()).invalidate();
                        ((View) view.getParent()).requestLayout();
                        currentState = 3;
                    }
                } else if (listener != null) {
                    listener.onRotateUp(AutofitTextRel.this);
                    if (currentState == 3) {
                        clickToSaveWork();
                    }
                    currentState = 2;
                }
                return true;
            }
        };
        this.shadowColor = 0;
        this.shadowColorProgress = 255;
        this.shadowProg = 0;
        this.tAlpha = 100;
        this.tColor = ViewCompat.MEASURED_STATE_MASK;
        this.text = str;
        this.topBottomShadow = 0.0f;
        this.topMargin = 0;
        this.xRotateProg = 0;
        this.yRotateProg = 0;
        this.zRotateProg = 0;
        this.outersize = 0;
        this.outercolor = 0;
        this.isUndoRedo = false;
        this.currentState = 0;
        this.isUndoRedo = z;
        init(context2);
    }

    public AutofitTextRel(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        String str = "";
        this.field_four = str;
        this.field_one = 0;
        this.field_three = str;
        this.field_two = "0,0";
        this.fontName = str;
        this.gd = null;
        this.isBorderVisible = false;
        this.isItalic = false;
        this.isUnderLine = false;
        this.leftMargin = 0;
        this.leftRightShadow = 0.0f;
        this.listener = null;
        this.mTouchListener1 = new Touch();
        this.progress = 0;
        this.rTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
                int action = motionEvent.getAction();
                if (action == 0) {
                    if (autofitTextRel != null) {
                        autofitTextRel.requestDisallowInterceptTouchEvent(true);
                    }
                    if (listener != null) {
                        listener.onRotateDown(AutofitTextRel.this);
                    }
                    Rect rect = new Rect();
                    ((View) view.getParent()).getGlobalVisibleRect(rect);
                    cX = rect.exactCenterX();
                    cY = rect.exactCenterY();
                    vAngle = (double) ((View) view.getParent()).getRotation();
                    AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
                    autofitTextRel2.tAngle = (Math.atan2((double) (autofitTextRel2.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                    AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
                    autofitTextRel3.dAngle = autofitTextRel3.vAngle - tAngle;
                    currentState = 1;
                } else if (action != 1) {
                    if (action == 2) {
                        if (autofitTextRel != null) {
                            autofitTextRel.requestDisallowInterceptTouchEvent(true);
                        }
                        if (listener != null) {
                            listener.onRotateMove(AutofitTextRel.this);
                        }
                        AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                        autofitTextRel4.angle = (Math.atan2((double) (autofitTextRel4.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                        ((View) view.getParent()).setRotation((float) (angle + dAngle));
                        ((View) view.getParent()).invalidate();
                        ((View) view.getParent()).requestLayout();
                        currentState = 3;
                    }
                } else if (listener != null) {
                    listener.onRotateUp(AutofitTextRel.this);
                    if (currentState == 3) {
                        clickToSaveWork();
                    }
                    currentState = 2;
                }
                return true;
            }
        };
        this.shadowColor = 0;
        this.shadowColorProgress = 255;
        this.shadowProg = 0;
        this.tAlpha = 100;
        this.tColor = ViewCompat.MEASURED_STATE_MASK;
        this.text = str;
        this.topBottomShadow = 0.0f;
        this.topMargin = 0;
        this.xRotateProg = 0;
        this.yRotateProg = 0;
        this.zRotateProg = 0;
        this.outersize = 0;
        this.outercolor = 0;
        this.isUndoRedo = false;
        this.currentState = 0;
        init(context2);
    }

    public AutofitTextRel(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        String str = "";
        this.field_four = str;
        this.field_one = 0;
        this.field_three = str;
        this.field_two = "0,0";
        this.fontName = str;
        this.gd = null;
        this.isBorderVisible = false;
        this.isItalic = false;
        this.isUnderLine = false;
        this.leftMargin = 0;
        this.leftRightShadow = 0.0f;
        this.listener = null;
        this.mTouchListener1 = new Touch();
        this.progress = 0;
        this.rTouchListener = new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
                int action = motionEvent.getAction();
                if (action == 0) {
                    if (autofitTextRel != null) {
                        autofitTextRel.requestDisallowInterceptTouchEvent(true);
                    }
                    if (listener != null) {
                        listener.onRotateDown(AutofitTextRel.this);
                    }
                    Rect rect = new Rect();
                    ((View) view.getParent()).getGlobalVisibleRect(rect);
                    cX = rect.exactCenterX();
                    cY = rect.exactCenterY();
                    vAngle = (double) ((View) view.getParent()).getRotation();
                    AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
                    autofitTextRel2.tAngle = (Math.atan2((double) (autofitTextRel2.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                    AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
                    autofitTextRel3.dAngle = autofitTextRel3.vAngle - tAngle;
                    currentState = 1;
                } else if (action != 1) {
                    if (action == 2) {
                        if (autofitTextRel != null) {
                            autofitTextRel.requestDisallowInterceptTouchEvent(true);
                        }
                        if (listener != null) {
                            listener.onRotateMove(AutofitTextRel.this);
                        }
                        AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                        autofitTextRel4.angle = (Math.atan2((double) (autofitTextRel4.cY - motionEvent.getRawY()), (double) (cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                        ((View) view.getParent()).setRotation((float) (angle + dAngle));
                        ((View) view.getParent()).invalidate();
                        ((View) view.getParent()).requestLayout();
                        currentState = 3;
                    }
                } else if (listener != null) {
                    listener.onRotateUp(AutofitTextRel.this);
                    if (currentState == 3) {
                        clickToSaveWork();
                    }
                    currentState = 2;
                }
                return true;
            }
        };
        this.shadowColor = 0;
        this.shadowColorProgress = 255;
        this.shadowProg = 0;
        this.tAlpha = 100;
        this.tColor = ViewCompat.MEASURED_STATE_MASK;
        this.text = str;
        this.topBottomShadow = 0.0f;
        this.topMargin = 0;
        this.xRotateProg = 0;
        this.yRotateProg = 0;
        this.zRotateProg = 0;
        this.outersize = 0;
        this.outercolor = 0;
        this.isUndoRedo = false;
        this.currentState = 0;
        init(context2);
    }

    public AutofitTextRel setOnTouchCallbackListener(TouchEventListener touchEventListener) {
        this.listener = touchEventListener;
        return this;
    }

    public void setDrawParams() {
        invalidate();
    }

    public void init(Context context2) {
        try {
            this.context = context2;
            Display defaultDisplay = ((Activity) this.context).getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            this.width = point.x;
            this.height = point.y;
            this.ratio = ((float) this.width) / ((float) this.height);
            this.text_iv = new AutoResizeTextView(this.context);
            this.scale_iv = new ImageView(this.context);
            this.border_iv = new ImageView(this.context);
            this.background_iv = new ImageView(this.context);
            this.delete_iv = new ImageView(this.context);
            this.rotate_iv = new ImageView(this.context);
            this.f27s = dpToPx(this.context, 30);
            this.wi = dpToPx(this.context, 200);
            this.he = dpToPx(this.context, 200);
            this.scale_iv.setImageResource(R.drawable.sticker_scale);
            this.background_iv.setImageResource(0);
            this.rotate_iv.setImageResource(R.drawable.sticker_rotate);
            this.delete_iv.setImageResource(R.drawable.sticker_delete1);
            LayoutParams layoutParams = new LayoutParams(this.wi, this.he);
            LayoutParams layoutParams2 = new LayoutParams(this.f27s, this.f27s);
            layoutParams2.addRule(12);
            layoutParams2.addRule(11);
            LayoutParams layoutParams3 = new LayoutParams(this.f27s, this.f27s);
            layoutParams3.addRule(12);
            layoutParams3.addRule(9);
            LayoutParams layoutParams4 = new LayoutParams(-1, -1);
            if (VERSION.SDK_INT >= 17) {
                layoutParams4.addRule(17);
            } else {
                layoutParams4.addRule(1);
            }
            LayoutParams layoutParams5 = new LayoutParams(this.f27s, this.f27s);
            layoutParams5.addRule(10);
            layoutParams5.addRule(9);
            LayoutParams layoutParams6 = new LayoutParams(-1, -1);
            LayoutParams layoutParams7 = new LayoutParams(-1, -1);
            setLayoutParams(layoutParams);
            setBackgroundResource(R.drawable.border_gray);
            addView(this.background_iv);
            this.background_iv.setLayoutParams(layoutParams7);
            this.background_iv.setScaleType(ScaleType.FIT_XY);
            addView(this.border_iv);
            this.border_iv.setLayoutParams(layoutParams6);
            this.border_iv.setTag("border_iv");
            addView(this.text_iv);
            this.text_iv.setText(this.text);
            this.text_iv.setTextColor(this.tColor);
            this.text_iv.setOutlineSize(0);
            this.text_iv.setOutlineColor(0);
            this.text_iv.setShadowLayer(10.6f, 5.5f, 5.3f, ViewCompat.MEASURED_STATE_MASK);
            this.text_iv.setTextSize(400.0f);
            this.text_iv.setLayoutParams(layoutParams4);
            this.text_iv.setGravity(17);
            this.text_iv.setMinTextSize(10.0f);
            addView(this.delete_iv);
            this.delete_iv.setLayoutParams(layoutParams5);
            this.delete_iv.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    final ViewGroup viewGroup = (ViewGroup) getParent();
                    zoomInScale.setAnimationListener(new AnimationListener() {
                        public void onAnimationRepeat(Animation animation) {
                        }

                        public void onAnimationStart(Animation animation) {
                        }

                        public void onAnimationEnd(Animation animation) {
                            viewGroup.removeView(AutofitTextRel.this);
                        }
                    });
                    text_iv.startAnimation(zoomInScale);
                    background_iv.startAnimation(zoomInScale);
                    setBorderVisibility(false);
                    if (listener != null) {
                        listener.onDelete();
                    }
                }
            });
            addView(this.rotate_iv);
            this.rotate_iv.setLayoutParams(layoutParams3);
            this.rotate_iv.setOnTouchListener(this.rTouchListener);
            addView(this.scale_iv);
            this.scale_iv.setLayoutParams(layoutParams2);
            this.scale_iv.setTag("scale_iv");
            this.scale_iv.setOnTouchListener(this.mTouchListener1);
            this.rotation = getRotation();
            this.scale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_textlib_scale_anim);
            this.zoomOutScale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_textlib_scale_zoom_out);
            this.zoomInScale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_textlib_scale_zoom_in);
            initGD();
            this.isMultiTouchEnabled = setDefaultTouchListener(true);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void applyLetterSpacing(float f) {
        if (this.text != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.text.length()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                sb2.append(this.text.charAt(i));
                sb.append(sb2.toString());
                i++;
                if (i < this.text.length()) {
                    sb.append(" ");
                }
            }
            SpannableString spannableString = new SpannableString(sb.toString());
            if (sb.toString().length() > 1) {
                for (int i2 = 1; i2 < sb.toString().length(); i2 += 2) {
                    spannableString.setSpan(new ScaleXSpan((1.0f + f) / 10.0f), i2, i2 + 1, 33);
                }
            }
            this.text_iv.setText(spannableString, BufferType.SPANNABLE);
        }
    }

    public void applyLineSpacing(float f) {
        this.text_iv.setLineSpacing(f, 1.0f);
    }

    public void setBoldFont() {
        if (this.isBold) {
            this.isBold = false;
            this.text_iv.setTypeface(Typeface.DEFAULT);
            return;
        }
        this.isBold = true;
        this.text_iv.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void setCapitalFont() {
        if (this.capitalFlage == 0) {
            this.capitalFlage = 1;
            AutoResizeTextView autoResizeTextView = this.text_iv;
            autoResizeTextView.setText(autoResizeTextView.getText().toString().toUpperCase());
            return;
        }
        this.capitalFlage = 0;
        AutoResizeTextView autoResizeTextView2 = this.text_iv;
        autoResizeTextView2.setText(autoResizeTextView2.getText().toString().toLowerCase());
    }

    public void setUnderLineFont() {
        String str = "</u>";
        String str2 = "<u>";
        if (this.isUnderLine) {
            this.isUnderLine = false;
            String str3 = "";
            this.text_iv.setText(Html.fromHtml(this.text.replace(str2, str3).replace(str, str3)));
            return;
        }
        this.isUnderLine = true;
        AutoResizeTextView autoResizeTextView = this.text_iv;
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(this.text);
        sb.append(str);
        autoResizeTextView.setText(Html.fromHtml(sb.toString()));
    }

    public void setItalicFont() {
        if (this.isItalic) {
            this.isItalic = false;
            TextView textView = new TextView(this.context);
            textView.setText(this.text);
            if (this.isBold) {
                textView.setTypeface(textView.getTypeface(), 1);
            } else {
                textView.setTypeface(textView.getTypeface(), 0);
            }
            this.text_iv.setTypeface(textView.getTypeface());
            return;
        }
        this.isItalic = true;
        TextView textView2 = new TextView(this.context);
        textView2.setText(this.text);
        if (this.isBold) {
            textView2.setTypeface(textView2.getTypeface(), 3);
        } else {
            textView2.setTypeface(textView2.getTypeface(), 2);
        }
        this.text_iv.setTypeface(textView2.getTypeface());
    }

    public void setLeftAlignMent() {
        this.text_iv.setGravity(19);
    }

    public void setCenterAlignMent() {
        this.text_iv.setGravity(17);
    }

    public void setRightAlignMent() {
        this.text_iv.setGravity(21);
    }

    public boolean setDefaultTouchListener(boolean z) {
        if (z) {
            setOnTouchListener(new MultiTouchListener().enableRotation(true).setOnTouchCallbackListener(this).setGestureListener(this.gd));
            return true;
        }
        setOnTouchListener(null);
        return false;
    }

    public boolean getBorderVisibility() {
        return this.isBorderVisible;
    }

    public void setBorderVisibility(boolean z) {
        this.isBorderVisible = z;
        if (!z) {
            this.border_iv.setVisibility(View.GONE);
            this.scale_iv.setVisibility(View.GONE);
            this.delete_iv.setVisibility(View.GONE);
            this.rotate_iv.setVisibility(View.GONE);
            setBackgroundResource(0);
        } else if (this.border_iv.getVisibility() != View.VISIBLE) {
            this.border_iv.setVisibility(View.VISIBLE);
            this.scale_iv.setVisibility(View.VISIBLE);
            this.delete_iv.setVisibility(View.VISIBLE);
            this.rotate_iv.setVisibility(View.VISIBLE);
            setBackgroundResource(R.drawable.border_gray);
            this.text_iv.startAnimation(this.scale);
        }
    }

    public String getText() {
        return this.text_iv.getText().toString();
    }

    public void setText(String str) {
        try {
            this.text_iv.setText(str);
            this.text = str;
            if (!this.isUndoRedo) {
                this.text_iv.startAnimation(this.zoomOutScale);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void setTextFont(String str) {
        try {
            if (str.equals("default")) {
                this.text_iv.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "font/Default.ttf"));
                this.fontName = str;
                return;
            }
            AssetManager assets = this.context.getAssets();
            StringBuilder sb = new StringBuilder();
            sb.append("font/");
            sb.append(str);
            this.text_iv.setTypeface(Typeface.createFromAsset(assets, sb.toString()));
            this.fontName = str;
        } catch (Exception unused) {
            Log.e(TAG, "setTextFont: ");

//            StringBuilder sb = new StringBuilder();
//            sb.append(Configure.GetFileDir(MyApplicationClass.getInstance().getApplicationContext()).getPath());
//            sb.append(File.separator);
//            sb.append("font/");
//            File file = new File(sb.toString());
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            StringBuilder sb5 = new StringBuilder();
//            sb5.append(file.getPath());
//            sb5.append("/");
//            sb5.append(str);
//
//            Typeface typeface  = null;
//            try {
//                typeface = Typeface.createFromFile(String.valueOf(sb5));
//                if(sb5 != null){
//                    this.text_iv.setTypeface(typeface);
//                    this.fontName = str;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                this.text_iv.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "font/Default.ttf"));
//                this.fontName = str;
//            }

        }
    }

    public String getFontName() {
        return this.fontName;
    }

    public int getTextColor() {
        return this.tColor;
    }

    public void setTextColor(int i) {
        this.text_iv.setTextColor(i);
        this.tColor = i;
    }

    public void setTextOutlLine(int i) {
        this.outersize = i;
        this.text_iv.setOutlineSize(i);
    }

    public void setTextOutlineColor(int i) {
        this.outercolor = i;
        this.text_iv.setOutlineColor(i);
    }

    public int getTextAlpha() {
        return this.tAlpha;
    }

    public void setTextAlpha(int i) {
        this.text_iv.setAlpha(((float) i) / 100.0f);
        this.tAlpha = i;
    }

    public int getTextShadowColor() {
        return this.shadowColor;
    }

    public void setTextShadowColor(int i) {
        this.shadowColor = i;
        this.shadowColor = ColorUtils.setAlphaComponent(this.shadowColor, this.shadowColorProgress);
        this.text_iv.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public void setTextShadowOpacity(int i) {
        this.shadowColorProgress = i;
        this.shadowColor = ColorUtils.setAlphaComponent(this.shadowColor, i);
        this.text_iv.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public void setLeftRightShadow(float f) {
        this.leftRightShadow = f;
        this.text_iv.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public void setTopBottomShadow(float f) {
        this.topBottomShadow = f;
        this.text_iv.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public int getTextShadowProg() {
        return this.shadowProg;
    }

    public void setTextShadowProg(int i) {
        this.shadowProg = i;
        this.text_iv.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public String getBgDrawable() {
        return this.bgDrawable;
    }

    public void setBgDrawable(String str) {
        this.bgDrawable = str;
        this.bgColor = 0;
        this.background_iv.setImageBitmap(getTiledBitmap(this.context, getResources().getIdentifier(str, "drawable", this.context.getPackageName()), this.wi, this.he));
        this.background_iv.setBackgroundColor(this.bgColor);
    }

    public int getBgColor() {
        return this.bgColor;
    }

    public void setBgColor(int i) {
        this.bgDrawable = "0";
        this.bgColor = i;
        this.background_iv.setImageBitmap(null);
        this.background_iv.setBackgroundColor(i);
    }

    public int getLeftSadow() {
        return (int) this.leftRightShadow;
    }

    public int getTopBottomSadow() {
        return (int) this.topBottomShadow;
    }

    public int getOutercolor() {
        return this.outercolor;
    }

    public int getOutersize() {
        return this.outersize;
    }

    public int getBgAlpha() {
        return this.bgAlpha;
    }

    public void setBgAlpha(int i) {
        this.background_iv.setAlpha(((float) i) / 255.0f);
        this.bgAlpha = i;
    }

    public TextInfo getTextInfo() {
        TextInfo textInfo = new TextInfo();
        textInfo.setPOS_X(getX());
        textInfo.setPOS_Y(getY());
        textInfo.setWIDTH(this.wi);
        textInfo.setHEIGHT(this.he);
        textInfo.setTEXT(this.text);
        textInfo.setFONT_NAME(this.fontName);
        textInfo.setTEXT_COLOR(this.tColor);
        textInfo.setTEXT_ALPHA(this.tAlpha);
        textInfo.setSHADOW_COLOR(this.shadowColor);
        textInfo.setSHADOW_PROG(this.shadowProg);
        textInfo.setBG_COLOR(this.bgColor);
        textInfo.setBG_DRAWABLE(this.bgDrawable);
        textInfo.setBG_ALPHA(this.bgAlpha);
        textInfo.setROTATION(getRotation());
        textInfo.setXRotateProg(this.xRotateProg);
        textInfo.setYRotateProg(this.yRotateProg);
        textInfo.setZRotateProg(this.zRotateProg);
        textInfo.setCurveRotateProg(this.progress);
        textInfo.setFIELD_ONE(this.field_one);
        textInfo.setFIELD_TWO(this.field_two);
        textInfo.setFIELD_THREE(this.field_three);
        textInfo.setFIELD_FOUR(this.field_four);
        textInfo.setLeftRighShadow(this.leftRightShadow);
        textInfo.setTopBottomShadow(this.topBottomShadow);
        textInfo.setOutLineSize(this.outersize);
        textInfo.setOutLineColor(this.outercolor);
        return textInfo;
    }

    public void setTextInfo(TextInfo textInfo, boolean z) {
        StringBuilder sb = new StringBuilder();
        String str = "";
        sb.append(str);
        sb.append(textInfo.getPOS_X());
        String str2 = " ,";
        sb.append(str2);
        sb.append(textInfo.getPOS_Y());
        sb.append(str2);
        sb.append(textInfo.getWIDTH());
        sb.append(str2);
        sb.append(textInfo.getHEIGHT());
        sb.append(str2);
        sb.append(textInfo.getFIELD_TWO());
        Log.e("set Text value", sb.toString());
        this.wi = textInfo.getWIDTH();
        this.he = textInfo.getHEIGHT();
        this.text = textInfo.getTEXT();
        this.fontName = textInfo.getFONT_NAME();
        this.tColor = textInfo.getTEXT_COLOR();
        this.tAlpha = textInfo.getTEXT_ALPHA();
        this.shadowColor = textInfo.getSHADOW_COLOR();
        this.shadowProg = textInfo.getSHADOW_PROG();
        this.bgColor = textInfo.getBG_COLOR();
        this.bgDrawable = textInfo.getBG_DRAWABLE();
        this.bgAlpha = textInfo.getBG_ALPHA();
        this.rotation = textInfo.getROTATION();
        this.field_two = textInfo.getFIELD_TWO();
        setText(this.text);
        setTextFont(this.fontName);
        setTextColor(this.tColor);
        setTextAlpha(this.tAlpha);
        this.outersize = textInfo.getOutLineSize();
        setTextOutlLine(this.outersize);
        this.outercolor = textInfo.getOutLineColor();
        setTextOutlineColor(this.outercolor);
        setTextShadowColor(this.shadowColor);
        this.leftRightShadow = textInfo.getLeftRighShadow();
        this.topBottomShadow = textInfo.getTopBottomShadow();
        setTextShadowProg(this.shadowProg);
        int i = this.bgColor;
        if (i != 0) {
            setBgColor(i);
        } else {
            this.background_iv.setBackgroundColor(0);
        }
        if (this.bgDrawable.equals("0")) {
            this.background_iv.setImageBitmap(null);
        } else {
            setBgDrawable(this.bgDrawable);
        }
        setBgAlpha(this.bgAlpha);
        setRotation(textInfo.getROTATION());
        if (this.field_two.equals(str)) {
            getLayoutParams().width = this.wi;
            getLayoutParams().height = this.he;
            setX(textInfo.getPOS_X());
            setY(textInfo.getPOS_Y());
            return;
        }
        String[] split = this.field_two.split(",");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        ((LayoutParams) getLayoutParams()).leftMargin = parseInt;
        ((LayoutParams) getLayoutParams()).topMargin = parseInt2;
        getLayoutParams().width = this.wi;
        getLayoutParams().height = this.he;
        setX(textInfo.getPOS_X() + ((float) (parseInt * -1)));
        setY(textInfo.getPOS_Y() + ((float) (parseInt2 * -1)));
    }

    public void optimize(float f, float f2) {
        setX(getX() * f);
        setY(getY() * f2);
        getLayoutParams().width = (int) (((float) this.wi) * f);
        getLayoutParams().height = (int) (((float) this.he) * f2);
    }

    public void incrX() {
        setX(getX() + 2.0f);
    }

    public void decX() {
        setX(getX() - 2.0f);
    }

    public void incrY() {
        setY(getY() + 2.0f);
    }

    public void decY() {
        setY(getY() - 2.0f);
    }

    public int dpToPx(Context context2, int i) {
        float f = (float) i;
        context2.getResources();
        return (int) (Resources.getSystem().getDisplayMetrics().density * f);
    }

    private Bitmap getTiledBitmap(Context context2, int i, int i2, int i3) {
        Rect rect = new Rect(0, 0, i2, i3);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(BitmapFactory.decodeResource(context2.getResources(), i, new Options()), TileMode.REPEAT, TileMode.REPEAT));
        Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Config.ARGB_8888);
        new Canvas(createBitmap).drawRect(rect, paint);
        return createBitmap;
    }

    private void initGD() {
        this.gd = new GestureDetector(this.context, new SimpleListner());
    }

    public void onTouchCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchDown(view);
        }
    }

    public void onTouchUpCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchUp(view);
        }
    }

    public void onTouchMoveCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMove(view);
        }
    }

    public void onTouchUpClick(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(view);
        }
    }

    public float getNewX(float f) {
        return ((float) this.width) * (f / ((float) this.sw));
    }

    public float getNewY(float f) {
        return ((float) this.height) * (f / ((float) this.sh));
    }

    public void clickToSaveWork() {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(this);
        }
    }
}
