package com.ziver.bluetoothnavigation.bluetooth.model;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.ui.fragment.model.EddistoneCalc;

public class Device {

    private String mName;
    private String mAddress;
    private int mRSSI;

    private EddistoneCalc mEddistoneCalc;

    public Device(@NonNull String address, String name) {
        mName = name;
        mAddress = address;
    }

    public Device(@NonNull String address, String name, int RSSI) {
        this(address, name);
        mRSSI = RSSI;
    }

    public EddistoneCalc getEddistoneCalc() {
        return mEddistoneCalc;
    }

    public void setEddistoneCalc(EddistoneCalc eddistoneCalc) {
        mEddistoneCalc = eddistoneCalc;
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

    @NonNull
    @Override
    public String toString() {
        return "Device{" +
                "Name='" + mName + '\'' +
                ", Address='" + mAddress + '\'' +
                ", RSSI=" + mRSSI +
                '}';
    }
}
