package com.tools.photolab.effect.color_splash_tool;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Iterator;

public class MyPath {
    int color;
    ArrayList<PointF> points;
    float r;

    MyPath(ArrayList<PointF> arrayList, int i, float f) {
        this.points = new ArrayList<>(arrayList);
        this.color = i;
        this.r = f;
    }

    public Path convertPath(float f) {
        Path path = new Path();
        Iterator it = this.points.iterator();
        while (it.hasNext()) {
            PointF pointF = (PointF) it.next();
            if (path.isEmpty()) {
                path.moveTo(pointF.x * f, pointF.y * f);
            } else {
                path.lineTo(pointF.x * f, pointF.y * f);
            }
        }
        return path;
    }
}
