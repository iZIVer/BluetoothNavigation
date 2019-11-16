package com.ziver.bluetoothnavigation.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RssiMath {

    /**
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)
     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */

    private static final float N = 2F;

    public static double getDistanceByRssi(int rssi, int txPower) {
        double distance = Math.pow(10d, ((double) txPower - rssi) / (10 * N));
        return roundDecimal(distance);
    }

    private static double roundDecimal(double num) {
        int decimal = 3;
        return new BigDecimal(num)
                .setScale(decimal, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static double filterCalman(double oldVlue, double k, double alt) {
        return k * alt + (1 - k) * oldVlue;
    }

}
