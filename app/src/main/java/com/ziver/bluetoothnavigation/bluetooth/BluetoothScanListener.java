package com.ziver.bluetoothnavigation.bluetooth;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.bluetooth.model.Device;
import com.ziver.bluetoothnavigation.bluetooth.model.ScanState;

public interface BluetoothScanListener {
    void onChangeScanSate(@NonNull ScanState state);
    void onScanFind(@NonNull Device device);
}
