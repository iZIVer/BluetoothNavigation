package com.ziver.bluetoothnavigation.ui.fragment;

import com.ziver.bluetoothnavigation.math.Circle;
import com.ziver.bluetoothnavigation.math.Point;
import com.ziver.bluetoothnavigation.math.TrilatalationMath;
import com.ziver.bluetoothnavigation.ui.fragment.model.BluetoothDevice;

import java.util.List;

public class DistanceCalculation {

    public DistanceCalculation() {
    }

    public Point calculation(List<BluetoothDevice> list) {
        return TrilatalationMath.trilatalation(
                new Circle(list.get(0).getEddistoneCalc().getPoint(), (float) list.get(0).getDistance()),
                new Circle(list.get(1).getEddistoneCalc().getPoint(), (float) list.get(1).getDistance()),
                new Circle(list.get(2).getEddistoneCalc().getPoint(), (float) list.get(2).getDistance())
                );
    }
}
