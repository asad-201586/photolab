package com.tools.photolab.effect.erase_tool;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.internal.view.SupportMenu;

import com.tools.photolab.R;

public class AutoResizeTextView extends AppCompatTextView {
    private static final int DEFAULT_OUTLINE_COLOR = -256;
    private static final int DEFAULT_OUTLINE_SIZE = 5;
    public static float _minTextSize;
    private final RectF _availableSpaceRect;
    private boolean _initialized;
    private int _maxLines;
    private float _maxTextSize;

    public TextPaint _paint;
    private final SizeTester _sizeTester;

    public float _spacingAdd;

    public float _spacingMult;

    public int _widthLimit;
    private int mOutlineColor;
    private int mOutlineSize;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private int mTextColor;

    private interface SizeTester {
        int onTestSize(int i, RectF rectF);
    }

    class TextSize implements SizeTester {
        final RectF textRect = new RectF();

        TextSize() {
        }

        @TargetApi(16)
        public int onTestSize(int i, RectF rectF) {
            String str;
            AutoResizeTextView.this._paint.setTextSize((float) i);
            TransformationMethod transformationMethod = AutoResizeTextView.this.getTransformationMethod();
            if (transformationMethod != null) {
                str = transformationMethod.getTransformation(AutoResizeTextView.this.getText(), AutoResizeTextView.this).toString();
            } else {
                str = AutoResizeTextView.this.getText().toString();
            }
            if (AutoResizeTextView.this.getMaxLines() == 1) {
                this.textRect.bottom = AutoResizeTextView.this._paint.getFontSpacing();
                this.textRect.right = AutoResizeTextView.this._paint.measureText(str);
            } else {
                StaticLayout staticLayout = new StaticLayout(str, AutoResizeTextView.this._paint, AutoResizeTextView.this._widthLimit, Alignment.ALIGN_NORMAL, AutoResizeTextView.this._spacingMult, AutoResizeTextView.this._spacingAdd, true);
                if (AutoResizeTextView.this.getMaxLines() != -1 && staticLayout.getLineCount() > AutoResizeTextView.this.getMaxLines()) {
                    return 1;
                }
                this.textRect.bottom = (float) staticLayout.getHeight();
                int lineCount = staticLayout.getLineCount();
                int i2 = -1;
                for (int i3 = 0; i3 < lineCount; i3++) {
                    int lineEnd = staticLayout.getLineEnd(i3);
                    if (i3 < lineCount - 1 && lineEnd > 0 && !AutoResizeTextView.this.isValidWordWrap(str.charAt(lineEnd - 1), str.charAt(lineEnd))) {
                        return 1;
                    }
                    if (((float) i2) < staticLayout.getLineRight(i3) - staticLayout.getLineLeft(i3)) {
                        i2 = ((int) staticLayout.getLineRight(i3)) - ((int) staticLayout.getLineLeft(i3));
                    }
                }
                this.textRect.right = (float) i2;
            }
            this.textRect.offsetTo(0.0f, 0.0f);
            return rectF.contains(this.textRect) ? -1 : 1;
        }
    }

    public boolean isValidWordWrap(char c, char c2) {
        return c == ' ' || c == '-';
    }

    public AutoResizeTextView(Context context) {
        this(context, null, 16842884);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOutlineSize = 5;
        this.mOutlineColor = -256;
        this.mTextColor = getCurrentTextColor();
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.TextViewOutline);
            if (obtainStyledAttributes.hasValue(5)) {
                this.mOutlineSize = (int) obtainStyledAttributes.getDimension(5, 5.0f);
            }
            if (obtainStyledAttributes.hasValue(4)) {
                this.mOutlineColor = obtainStyledAttributes.getColor(4, -256);
            }
            if (obtainStyledAttributes.hasValue(3) || obtainStyledAttributes.hasValue(1) || obtainStyledAttributes.hasValue(2) || obtainStyledAttributes.hasValue(0)) {
                this.mShadowRadius = obtainStyledAttributes.getFloat(3, 0.0f);
                this.mShadowDx = obtainStyledAttributes.getFloat(1, 0.0f);
                this.mShadowDy = obtainStyledAttributes.getFloat(2, 0.0f);
                this.mShadowColor = obtainStyledAttributes.getColor(0, 0);
            }
            obtainStyledAttributes.recycle();
        }
        this._availableSpaceRect = new RectF();
        this._spacingMult = 1.0f;
        this._spacingAdd = 0.0f;
        this._initialized = false;
        _minTextSize = TypedValue.applyDimension(2, 12.0f, getResources().getDisplayMetrics());
        this._maxTextSize = getTextSize();
        this._paint = new TextPaint(getPaint());
        if (this._maxLines == 0) {
            this._maxLines = -1;
        }
        this._sizeTester = new TextSize();
        this._initialized = true;
    }

    @RequiresApi(api = 26)
    public void setJustify() {
        setJustificationMode(1);
    }

    public void setAllCaps(boolean z) {
        super.setAllCaps(z);
        adjustTextSize();
    }

    public void setTypeface(Typeface typeface) {
        super.setTypeface(typeface);
        adjustTextSize();
    }

    public void setTextSize(float f) {
        this._maxTextSize = f;
        adjustTextSize();
    }

    public int getMaxLines() {
        return this._maxLines;
    }

    public void setMaxLines(int i) {
        super.setMaxLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    public void setSingleLine() {
        super.setSingleLine();
        this._maxLines = 1;
        adjustTextSize();
    }

    public void setSingleLine(boolean z) {
        super.setSingleLine(z);
        if (z) {
            this._maxLines = 1;
        } else {
            this._maxLines = -1;
        }
        adjustTextSize();
    }

    public void setLines(int i) {
        super.setLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    public void setTextSize(int i, float f) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        this._maxTextSize = TypedValue.applyDimension(i, f, resources.getDisplayMetrics());
        adjustTextSize();
    }

    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this._spacingMult = f2;
        this._spacingAdd = f;
    }

    public void setMinTextSize(float f) {
        _minTextSize = f;
        adjustTextSize();
    }

    private void setPaintToOutline() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Join.ROUND);
        paint.setStrokeWidth((float) this.mOutlineSize);
        super.setTextColor(this.mOutlineColor);
        super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
    }

    private void setPaintToRegular() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(0.0f);
        super.setTextColor(this.mTextColor);
        if (this.mOutlineSize > 0) {
            super.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        } else {
            super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
        }
    }

    private void setShader() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.FILL);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), -16711936, SupportMenu.CATEGORY_MASK, TileMode.REPEAT);
        paint.setShader(linearGradient);
    }

    public void onMeasure(int i, int i2) {
        setPaintToOutline();
        super.onMeasure(i, i2);
    }

    public void setTextColor(int i) {
        super.setTextColor(i);
        this.mTextColor = i;
    }

    public void setShadowLayer(float f, float f2, float f3, int i) {
        super.setShadowLayer(f, f2, f3, i);
        this.mShadowRadius = f;
        this.mShadowDx = f2;
        this.mShadowDy = f3;
        this.mShadowColor = i;
    }

    public void setOutlineSize(int i) {
        this.mOutlineSize = i;
    }

    public void setOutlineColor(int i) {
        this.mOutlineColor = i;
    }

    public void onDraw(Canvas canvas) {
        setPaintToOutline();
        super.onDraw(canvas);
        setPaintToRegular();
        super.onDraw(canvas);
    }

    private void adjustTextSize() {
        if (this._initialized) {
            int i = (int) _minTextSize;
            int measuredHeight = (getMeasuredHeight() - getCompoundPaddingBottom()) - getCompoundPaddingTop();
            this._widthLimit = (getMeasuredWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            if (this._widthLimit > 0) {
                this._paint = new TextPaint(getPaint());
                RectF rectF = this._availableSpaceRect;
                rectF.right = (float) this._widthLimit;
                rectF.bottom = (float) measuredHeight;
                superSetTextSize(i);
            }
        }
    }

    private void superSetTextSize(int i) {
        super.setTextSize(0, (float) binarySearch(i, (int) this._maxTextSize, this._sizeTester, this._availableSpaceRect));
    }

    private int binarySearch(int i, int i2, SizeTester sizeTester, RectF rectF) {
        int i3 = i2 - 1;
        int i4 = i;
        while (i <= i3) {
            int i5 = (i + i3) >>> 1;
            int onTestSize = sizeTester.onTestSize(i5, rectF);
            if (onTestSize < 0) {
                int i6 = i5 + 1;
                i4 = i;
                i = i6;
            } else if (onTestSize <= 0) {
                return i5;
            } else {
                i4 = i5 - 1;
                i3 = i4;
            }
        }
        return i4;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        adjustTextSize();
    }

    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            adjustTextSize();
        }
    }
   /* private static final int DEFAULT_OUTLINE_COLOR = -256;
    private static final int DEFAULT_OUTLINE_SIZE = 5;
    public static float _minTextSize;
    private final RectF _availableSpaceRect;
    private boolean _initialized;
    private int _maxLines;
    private float _maxTextSize;

    public TextPaint _paint;
    private final SizeTester _sizeTester;

    public float _spacingAdd;

    public float _spacingMult;

    public int _widthLimit;
    private int mOutlineColor;
    private int mOutlineSize;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private int mTextColor;

    private interface SizeTester {
        int onTestSize(int i, RectF rectF);
    }

    class TextSize implements SizeTester {
        final RectF textRect = new RectF();

        TextSize() {
        }

        @TargetApi(16)
        public int onTestSize(int i, RectF rectF) {
            String str;
            _paint.setTextSize((float) i);
            TransformationMethod transformationMethod = getTransformationMethod();
            if (transformationMethod != null) {
                str = transformationMethod.getTransformation(getText(), AutoResizeTextView.this).toString();
            } else {
                str = getText().toString();
            }
            if (getMaxLines() == 1) {
                this.textRect.bottom = _paint.getFontSpacing();
                this.textRect.right = _paint.measureText(str);
            } else {
                StaticLayout staticLayout = new StaticLayout(str, _paint, _widthLimit, Alignment.ALIGN_NORMAL, _spacingMult, _spacingAdd, true);
                if (getMaxLines() != -1 && staticLayout.getLineCount() > getMaxLines()) {
                    return 1;
                }
                this.textRect.bottom = (float) staticLayout.getHeight();
                int lineCount = staticLayout.getLineCount();
                int i2 = -1;
                for (int i3 = 0; i3 < lineCount; i3++) {
                    int lineEnd = staticLayout.getLineEnd(i3);
                    if (i3 < lineCount - 1 && lineEnd > 0 && !isValidWordWrap(str.charAt(lineEnd - 1), str.charAt(lineEnd))) {
                        return 1;
                    }
                    if (((float) i2) < staticLayout.getLineRight(i3) - staticLayout.getLineLeft(i3)) {
                        i2 = ((int) staticLayout.getLineRight(i3)) - ((int) staticLayout.getLineLeft(i3));
                    }
                }
                this.textRect.right = (float) i2;
            }
            this.textRect.offsetTo(0.0f, 0.0f);
            return rectF.contains(this.textRect) ? -1 : 1;
        }
    }

    public boolean isValidWordWrap(char c, char c2) {
        return c == ' ' || c == '-';
    }

    public AutoResizeTextView(Context context) {
        this(context, null, 16842884);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOutlineSize = DEFAULT_OUTLINE_SIZE;
        this.mOutlineColor = DEFAULT_OUTLINE_COLOR;
        this.mTextColor = getCurrentTextColor();
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.TextViewOutline);
            if (obtainStyledAttributes.hasValue(DEFAULT_OUTLINE_SIZE)) {
                this.mOutlineSize = (int) obtainStyledAttributes.getDimension(5, 5.0f);
            }
            if (obtainStyledAttributes.hasValue(4)) {
                this.mOutlineColor = obtainStyledAttributes.getColor(4, DEFAULT_OUTLINE_COLOR);
            }
            if (obtainStyledAttributes.hasValue(3) || obtainStyledAttributes.hasValue(1) || obtainStyledAttributes.hasValue(2) || obtainStyledAttributes.hasValue(0)) {
                this.mShadowRadius = obtainStyledAttributes.getFloat(3, 0.0f);
                this.mShadowDx = obtainStyledAttributes.getFloat(1, 0.0f);
                this.mShadowDy = obtainStyledAttributes.getFloat(2, 0.0f);
                this.mShadowColor = obtainStyledAttributes.getColor(0, 0);
            }
            obtainStyledAttributes.recycle();
        }
        this._availableSpaceRect = new RectF();
        this._spacingMult = 1.0f;
        this._spacingAdd = 0.0f;
        this._initialized = false;
        _minTextSize = TypedValue.applyDimension(COMPLEX_UNIT_SP, 1.0f, getResources().getDisplayMetrics());
        this._maxTextSize = getTextSize();
        this._paint = new TextPaint(getPaint());
        if (this._maxLines == 0) {
            this._maxLines = -1;
        }
        this._sizeTester = new TextSize();
        this._initialized = true;
    }

    @RequiresApi(api = 26)
    public void setJustify() {
        setJustificationMode(1);
    }

    public void setAllCaps(boolean z) {
        super.setAllCaps(z);
        adjustTextSize();
    }

    public void setTypeface(Typeface typeface) {
        super.setTypeface(typeface);
        adjustTextSize();
    }

    public void setTextSize(float f) {
        this._maxTextSize = f;
        adjustTextSize();
    }

    public int getMaxLines() {
        return this._maxLines;
    }

    public void setMaxLines(int i) {
        super.setMaxLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    public void setSingleLine() {
        super.setSingleLine();
        this._maxLines = 1;
        adjustTextSize();
    }

    public void setSingleLine(boolean z) {
        super.setSingleLine(z);
        if (z) {
            this._maxLines = 1;
        } else {
            this._maxLines = -1;
        }
        adjustTextSize();
    }

    public void setLines(int i) {
        super.setLines(i);
        this._maxLines = i;
        adjustTextSize();
    }

    public void setTextSize(int i, float f) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        this._maxTextSize = TypedValue.applyDimension(i, f, resources.getDisplayMetrics());
        adjustTextSize();
    }

    public void setLineSpacing(float f, float f2) {
        super.setLineSpacing(f, f2);
        this._spacingMult = f2;
        this._spacingAdd = f;
    }

    public void setMinTextSize(float f) {
        _minTextSize = f;
        adjustTextSize();
    }

    private void setPaintToOutline() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Join.ROUND);
        paint.setStrokeWidth((float) this.mOutlineSize);
        super.setTextColor(this.mOutlineColor);
        super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
    }

    private void setPaintToRegular() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(0.0f);
        super.setTextColor(this.mTextColor);
        if (this.mOutlineSize > 0) {
            super.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        } else {
            super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
        }
    }

    private void setShader() {
        TextPaint paint = getPaint();
        paint.setStyle(Style.FILL);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), -16711936, SupportMenu.CATEGORY_MASK, TileMode.REPEAT);
        paint.setShader(linearGradient);
    }


    public void onMeasure(int i, int i2) {
        setPaintToOutline();
        super.onMeasure(i, i2);
    }

    public void setTextColor(int i) {
        super.setTextColor(i);
        this.mTextColor = i;
    }

    public void setShadowLayer(float f, float f2, float f3, int i) {
        super.setShadowLayer(f, f2, f3, i);
        this.mShadowRadius = f;
        this.mShadowDx = f2;
        this.mShadowDy = f3;
        this.mShadowColor = i;
    }

    public void setOutlineSize(int i) {
        this.mOutlineSize = i;
    }

    public void setOutlineColor(int i) {
        this.mOutlineColor = i;
    }


    public void onDraw(Canvas canvas) {
        setPaintToOutline();
        super.onDraw(canvas);
        setPaintToRegular();
        super.onDraw(canvas);
    }

    private void adjustTextSize() {
        if (this._initialized) {
            int i = (int) _minTextSize;
            int measuredHeight = (getMeasuredHeight() - getCompoundPaddingBottom()) - getCompoundPaddingTop();
            this._widthLimit = (getMeasuredWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
            if (this._widthLimit > 0) {
                this._paint = new TextPaint(getPaint());
                RectF rectF = this._availableSpaceRect;
                rectF.right = (float) this._widthLimit;
                rectF.bottom = (float) measuredHeight;
                superSetTextSize(i);
            }
        }
    }

    private void superSetTextSize(int i) {
        float size = (float) binarySearch(i, (int) this._maxTextSize, this._sizeTester, this._availableSpaceRect);
        super.setTextSize(0, size);
    }

    private int binarySearch(int i, int i2, SizeTester sizeTester, RectF rectF) {
        int i3 = i2 - 1;
        int i4 = i;
        while (i <= i3) {
            int i5 = (i + i3) >>> 1;
            int onTestSize = sizeTester.onTestSize(i5, rectF);
            if (onTestSize < 0) {
                int i6 = i5 + 1;
                i4 = i;
                i = i6;
            } else if (onTestSize <= 0) {
                return i5;
            } else {
                i4 = i5 - 1;
                i3 = i4;
            }
        }
        return i4;
    }


    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        super.onTextChanged(charSequence, i, i2, i3);
        adjustTextSize();
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            adjustTextSize();
        }
    }*/
}
