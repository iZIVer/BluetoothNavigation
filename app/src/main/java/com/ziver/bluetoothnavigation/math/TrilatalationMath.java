package com.ziver.bluetoothnavigation.math;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class TrilatalationMath {

    public static Point trilatalation(@NonNull Circle c1, @NonNull Circle c2, @NonNull Circle c3) {
        Point[] points1 = crossCircles(c1, c2);
        Point[] points2 = crossCircles(c1, c3);

        List<Point> points = Arrays.asList(points1);
        try {
            if (!Arrays.asList(points2).isEmpty()) {
                if (points.isEmpty()) {
                    points = Arrays.asList(points2);
                } else
                    points.addAll(Arrays.asList(points2));
            }
        } catch (Exception e) {
            Logger.exception(e);
            return null;
        }

        int i = points1.length + points2.length;
        if (i == 0) {
            return null;
        }
        Point[] pS = new Point[i];
        double xs = 0;
        double ys = 0;
        i = 0;
        for (Point point : points) {
            if (point.getY() < 0 || point.getX() < 0 ||  point.getX() > 14 || point.getY() > 8) {
                continue;
            }
            i++;
            xs += point.getX();
            ys += point.getY();
        }
        return new Point(xs / i, ys / i);
    }

    private static Point[] crossCircles(@NonNull Circle c1, @NonNull Circle c2) {
        Point[] result = new Point[]{};

        Point center1 = c1.getCenter();
        Point center2 = c2.getCenter();
        double d = Math.sqrt(
                Math.pow(center2.getX() - center1.getX(), 2)
                        + Math.pow(center2.getY() - center1.getY(), 2)
        );

        if (d > c1.getRadius() + c2.getRadius()
                || d < Math.abs(c1.getRadius() - c2.getRadius())) {
            return result;
        }

        double a = (Math.pow(c1.getRadius(), 2) - Math.pow(c2.getRadius(), 2) + Math.pow(d, 2)) / (2 * d);
        double k = a / (d - a);
        double xc = (center1.getX() + k * center2.getX()) / (1 + k);
        double yc = (center1.getY() + k * center2.getY()) / (1 + k);

        double h = Math.sqrt(Math.pow(c1.getRadius(), 2) - Math.pow(a, 2));
        double crossX = xc + (h / d) * (center2.getY() - center1.getY());
        double crossY = yc - (h / d) * (center2.getX() - center1.getX());
        Point resultP1 = new Point(
                roundDecimal(crossX),
                roundDecimal(crossY));

        crossX = xc - (h / d) * (center2.getY() - center1.getY());
        crossY = yc + (h / d) * (center2.getX() - center1.getX());
        Point resultP2 = new Point(
                roundDecimal(crossX),
                roundDecimal(crossY));

        if (resultP1.equals(resultP2)) {
            result = new Point[]{resultP1};
        } else {
            result = new Point[]{resultP1, resultP2};
        }

        return result;
    }

    private static double roundDecimal(double num) {
        int decimal = 3;
        return new BigDecimal(num)
                .setScale(decimal, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
