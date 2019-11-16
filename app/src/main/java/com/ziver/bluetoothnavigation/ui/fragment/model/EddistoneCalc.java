package com.ziver.bluetoothnavigation.ui.fragment.model;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.math.Point;

public class EddistoneCalc {

    private int mId;
    private final Point mPoint;

    public EddistoneCalc(int id, @NonNull Point point) {
        mId = id;
        mPoint = point;
    }

    public int getId() {
        return mId;
    }

    public Point getPoint() {
        return mPoint;
    }
}
