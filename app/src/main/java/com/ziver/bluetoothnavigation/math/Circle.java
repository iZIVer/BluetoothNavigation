package com.ziver.bluetoothnavigation.math;

import androidx.annotation.NonNull;

public class Circle {

    private Point mCenter;
    private float mRadius;

    public Circle(@NonNull Point center, float radius) {
        mCenter = center;
        mRadius = radius;
    }

    public Point getCenter() {
        return mCenter;
    }

    public float getRadius() {
        return mRadius;
    }
}
