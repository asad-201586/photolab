package com.tools.photolab.effect.drip_tool.utils;

import android.content.res.Resources;
import android.content.res.TypedArray;

import androidx.annotation.ArrayRes;
import androidx.core.view.ViewCompat;

public class ColorUtils {


    public static int[] obtainColorArray(Resources resources, @ArrayRes int i) {
        TypedArray obtainTypedArray = resources.obtainTypedArray(i);
        int length = obtainTypedArray.length();
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = obtainTypedArray.getColor(i2, ViewCompat.MEASURED_STATE_MASK);
        }
        obtainTypedArray.recycle();
        return iArr;
    }
}
