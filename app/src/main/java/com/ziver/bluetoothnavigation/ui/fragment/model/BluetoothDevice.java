package com.ziver.bluetoothnavigation.ui.fragment.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.bluetooth.model.Device;
import com.ziver.bluetoothnavigation.math.RssiMath;

public class BluetoothDevice {

    private String mName;
    private String mAddress;
    private int mRSSI;
    private double mDistance;

    private final int TX_POWER = -41;

    private EddistoneCalc mEddistoneCalc;

    public BluetoothDevice(@NonNull String address, String name) {
        mName = name;
        mAddress = address;
    }

    public BluetoothDevice(@NonNull String address, String name, int RSSI) {
        this(address, name);
        mRSSI = RSSI;
    }

    public BluetoothDevice(@NonNull Device device) {
        this(device.getAddress(), device.getName(), device.getRSSI());
        mEddistoneCalc = device.getEddistoneCalc();
        mDistance = RssiMath.getDistanceByRssi(device.getRSSI(), TX_POWER);
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public int getRSSI() {
        return mRSSI;
    }

    public void setRSSI(int rssi) {
        int filterValue = (int) RssiMath.filterCalman(mRSSI, 0.35, rssi);
        mDistance = RssiMath.getDistanceByRssi(filterValue, TX_POWER);
        mRSSI = rssi;
    }


    public EddistoneCalc getEddistoneCalc() {
        return mEddistoneCalc;
    }

    public double getDistance() {
        return mDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothDevice btDevice = (BluetoothDevice) o;
        return TextUtils.equals(mAddress, btDevice.mAddress);
    }

    @Override
    public int hashCode() {
        return String.valueOf(mAddress).hashCode();
    }

    @Override
    public String toString() {
        return "BluetoothDevice{" +
                "Name='" + mName + '\'' +
                ", Address='" + mAddress + '\'' +
                ", RSSI=" + mRSSI +
                ", Distance=" + mDistance +
                '}';
    }
}
