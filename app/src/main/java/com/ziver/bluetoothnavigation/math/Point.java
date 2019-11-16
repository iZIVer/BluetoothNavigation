package com.ziver.bluetoothnavigation.math;

public class Point {

    private double mX;
    private double mY;

    public Point(double x, double y) {
        mX = x;
        mY = y;
    }

    public double getX() {
        return mX;
    }

    public double getY() {
        return mY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.mX, mX) == 0 &&
                Double.compare(point.mY, mY) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(mX) + Double.hashCode(mY);
    }
}
